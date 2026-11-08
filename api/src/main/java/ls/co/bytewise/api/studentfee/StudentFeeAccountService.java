package ls.co.bytewise.api.studentfee;

import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.fee.*;
import ls.co.bytewise.api.mail.MailService;
import ls.co.bytewise.api.parent.Parent;
import ls.co.bytewise.api.parent.ParentRepository;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentClassFeePreviewDto;
import ls.co.bytewise.api.student.StudentClassFeePreviewLineDto;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.student.StudentStatus;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.common.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

@Service
public class StudentFeeAccountService {

    private final StudentFeeAccountRepository accountRepository;
    private final StudentRepository studentRepository;
    private final TermRepository termRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final AnnualFeeRepository annualFeeRepository;
    private final StudentFeePaymentRepository paymentRepository;
    private final MailService mailService;
    private final StudentFeeBalanceService balanceService;
    private final AuditLogService auditLogService;
    private final ParentRepository parentRepository;

    public StudentFeeAccountService(StudentFeeAccountRepository accountRepository,
                                    StudentRepository studentRepository,
                                    TermRepository termRepository,
                                    FeeStructureRepository feeStructureRepository,
                                    AnnualFeeRepository annualFeeRepository,
                                    StudentFeePaymentRepository paymentRepository,
                                    MailService mailService,
                                    StudentFeeBalanceService balanceService,
                                    AuditLogService auditLogService,
                                    ParentRepository parentRepository) {
        this.accountRepository = accountRepository;
        this.studentRepository = studentRepository;
        this.termRepository = termRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.annualFeeRepository = annualFeeRepository;
        this.paymentRepository = paymentRepository;
        this.mailService = mailService;
        this.balanceService = balanceService;
        this.auditLogService = auditLogService;
        this.parentRepository = parentRepository;
    }

    @Transactional
    public StudentFeeAccountDto generateForStudent(Long studentId, Long termId, User creator) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.InvalidFeeOperationException("Student not found"));

        Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.InvalidFeeOperationException("Term not found"));

        SchoolClass schoolClass = student.getCurrentClass();
        if (schoolClass == null) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "Student " + student.getAdmissionNumber() + " has no current class");
        }

        if (feeStructureRepository
                .findAllBySchoolClassIdAndTermIdAndSchoolId(schoolClass.getId(), termId, schoolId)
                .isEmpty()) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "No fee structure is configured for this class and term");
        }

        accountRepository.findByStudentIdAndTermIdAndSchoolId(studentId, termId, schoolId)
                .ifPresent(existing -> {
                    throw new StudentFeeExceptions.InvalidFeeOperationException(
                            "Fee account already exists for this student and term");
                });

        StudentFeeAccount account = new StudentFeeAccount();
        account.setSchool(schoolClass.getSchool());
        account.setStudent(student);
        account.setTerm(term);
        account.setSchoolClass(schoolClass);
        account.setCreatedBy(creator);
        account.setUpdatedBy(creator);

        List<FeeStructure> structures = feeStructureRepository
                .findAllBySchoolClassIdAndTermIdAndSchoolId(schoolClass.getId(), termId, schoolId);

        if (!structures.isEmpty()) {
            FeeStructure structure = structures.get(0);
            for (FeeItem item : structure.getItems()) {
                StudentFeeLine line = new StudentFeeLine();
                line.setAccount(account);
                line.setName(item.getName());
                line.setAmount(item.getAmount());
                line.setMandatory(item.getMandatory());
                line.setDescription(item.getDescription());
                line.setFromAnnualFee(false);
                line.setSchool(account.getSchool());
                line.setCreatedBy(creator);
                line.setUpdatedBy(creator);
                account.getLines().add(line);
            }
        }

        AcademicYear year = term.getAcademicYear();
        if (year != null && isFirstTermOfYear(term, year, schoolId)) {
            List<AnnualFee> annualFees = annualFeeRepository
                    .findAllBySchoolClassIdAndAcademicYearIdAndSchoolId(
                            schoolClass.getId(), year.getId(), schoolId);

            for (AnnualFee annual : annualFees) {
                StudentFeeLine line = new StudentFeeLine();
                line.setAccount(account);
                line.setName(annual.getName());
                line.setAmount(annual.getAmount());
                line.setMandatory(true);
                line.setDescription(annual.getDescription());
                line.setFromAnnualFee(true);
                line.setSchool(account.getSchool());
                line.setCreatedBy(creator);
                line.setUpdatedBy(creator);
                account.getLines().add(line);
            }
        }

        account.setTotalFees(account.getLines().stream()
                .mapToDouble(StudentFeeLine::getAmount).sum());
        account.setTotalAdjustments(0.0);
        account.setAmountPaid(0.0);

        StudentFeeAccount saved = accountRepository.save(account);
        return toDto(saved);
    }

    @Transactional
    public List<StudentFeeAccountDto> generateForClass(Long classId, Long termId, User creator) {
        synchronizeClassTerm(classId, termId, creator);
        return listForClass(classId, termId);
    }

    @Transactional
    public void synchronizeStudentAccounts(Student student, User creator) {
        if (student.getStatus() != ls.co.bytewise.api.student.StudentStatus.ACTIVE
                || student.getCurrentClass() == null) {
            return;
        }

        Long schoolId = TenantContext.getSchoolId();
        feeStructureRepository
                .findAllBySchoolClassIdAndSchoolId(student.getCurrentClass().getId(), schoolId)
                .forEach(structure -> synchronizeStudentTerm(student, structure, creator));
    }

    @Transactional
    public void synchronizeClassTerm(Long classId, Long termId, User creator) {
        Long schoolId = TenantContext.getSchoolId();
        FeeStructure structure = feeStructureRepository
                .findAllBySchoolClassIdAndTermIdAndSchoolId(classId, termId, schoolId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new StudentFeeExceptions.InvalidFeeOperationException(
                        "No fee structure is configured for this class and term"));

        studentRepository.findAllBySchoolIdAndCurrentClassId(schoolId, classId).stream()
                .filter(student -> student.getStatus() == ls.co.bytewise.api.student.StudentStatus.ACTIVE)
                .forEach(student -> synchronizeStudentTerm(student, structure, creator));
    }

    @Transactional(readOnly = true)
    public StudentClassFeePreviewDto previewClassChange(Student student, SchoolClass newClass) {
        Long schoolId = TenantContext.getSchoolId();
        List<StudentClassFeePreviewLineDto> lines = accountRepository
                .findAllByStudentIdAndSchoolId(student.getId(), schoolId)
                .stream()
                .map(account -> previewLine(account, newClass, schoolId))
                .filter(java.util.Objects::nonNull)
                .toList();

        boolean changed = lines.stream().anyMatch(StudentClassFeePreviewLineDto::isWillUpdate);
        return new StudentClassFeePreviewDto(
                student.getId(),
                student.getCurrentClass() == null ? null : student.getCurrentClass().getName(),
                newClass.getName(),
                changed,
                lines);
    }

    @Transactional
    public void applyClassChange(Student student, SchoolClass newClass, User editor) {
        Long schoolId = TenantContext.getSchoolId();
        List<StudentFeeAccount> accounts = accountRepository
                .findAllByStudentIdAndSchoolId(student.getId(), schoolId);

        for (StudentFeeAccount account : accounts) {
            FeeStructure structure = feeStructureRepository
                    .findAllBySchoolClassIdAndTermIdAndSchoolId(
                            newClass.getId(), account.getTerm().getId(), schoolId)
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (structure == null || !account.getPayments().isEmpty()) {
                continue;
            }

            double oldTotal = account.getTotalFees();
            replaceAccountFees(account, structure, student, newClass, editor, schoolId);
            double newTotal = account.getTotalFees();

            if (Double.compare(oldTotal, newTotal) != 0) {
                auditLogService.record(account.getSchool(), editor,
                        AuditAction.STUDENT_FEE_CLASS_CHANGED,
                        "STUDENT_FEE_ACCOUNT", account.getId(),
                        "Class fee changed for " + student.getUser().getFullName()
                                + " in " + account.getTerm().getName()
                                + " from M " + String.format("%.2f", oldTotal)
                                + " to M " + String.format("%.2f", newTotal));
            }
        }

        synchronizeStudentAccounts(student, editor);
    }

    private StudentClassFeePreviewLineDto previewLine(StudentFeeAccount account,
                                                       SchoolClass newClass,
                                                       Long schoolId) {
        FeeStructure structure = feeStructureRepository
                .findAllBySchoolClassIdAndTermIdAndSchoolId(
                        newClass.getId(), account.getTerm().getId(), schoolId)
                .stream()
                .findFirst()
                .orElse(null);
        if (structure == null) return null;

        double oldTotal = account.getTotalFees();
        double newTotal = account.getPayments().isEmpty()
                ? calculateStructureTotal(structure, newClass, schoolId)
                : oldTotal;
        double amountPaid = account.getAmountPaid();
        double oldBalance = oldTotal - account.getTotalAdjustments() - amountPaid;
        double newBalance = newTotal - account.getTotalAdjustments() - amountPaid;
        boolean willUpdate = account.getPayments().isEmpty()
                && Double.compare(oldTotal, newTotal) != 0;

        return new StudentClassFeePreviewLineDto(
                account.getTerm().getName(), oldTotal, newTotal, amountPaid,
                oldBalance, newBalance, willUpdate);
    }

    private double calculateStructureTotal(FeeStructure structure,
                                           SchoolClass schoolClass,
                                           Long schoolId) {
        double total = structure.getItems().stream()
                .mapToDouble(FeeItem::getAmount)
                .sum();
        AcademicYear academicYear = structure.getTerm().getAcademicYear();
        if (academicYear != null && isFirstTermOfYear(structure.getTerm(), academicYear, schoolId)) {
            total += annualFeeRepository
                    .findAllBySchoolClassIdAndAcademicYearIdAndSchoolId(
                            schoolClass.getId(), academicYear.getId(), schoolId)
                    .stream()
                    .mapToDouble(AnnualFee::getAmount)
                    .sum();
        }
        return total;
    }

    private void replaceAccountFees(StudentFeeAccount account,
                                     FeeStructure structure,
                                     Student student,
                                     SchoolClass schoolClass,
                                     User editor,
                                     Long schoolId) {
        account.setSchoolClass(schoolClass);
        account.getLines().clear();
        for (FeeItem item : structure.getItems()) {
            StudentFeeLine line = new StudentFeeLine();
            line.setAccount(account);
            line.setName(item.getName());
            line.setAmount(item.getAmount());
            line.setMandatory(item.getMandatory());
            line.setDescription(item.getDescription());
            line.setFromAnnualFee(false);
            line.setSchool(account.getSchool());
            line.setCreatedBy(editor);
            line.setUpdatedBy(editor);
            account.getLines().add(line);
        }

        AcademicYear academicYear = structure.getTerm().getAcademicYear();
        if (academicYear != null && isFirstTermOfYear(structure.getTerm(), academicYear, schoolId)) {
            annualFeeRepository
                    .findAllBySchoolClassIdAndAcademicYearIdAndSchoolId(
                            schoolClass.getId(), academicYear.getId(), schoolId)
                    .forEach(annualFee -> {
                        StudentFeeLine line = new StudentFeeLine();
                        line.setAccount(account);
                        line.setName(annualFee.getName());
                        line.setAmount(annualFee.getAmount());
                        line.setMandatory(true);
                        line.setDescription(annualFee.getDescription());
                        line.setFromAnnualFee(true);
                        line.setSchool(account.getSchool());
                        line.setCreatedBy(editor);
                        line.setUpdatedBy(editor);
                        account.getLines().add(line);
                    });
        }

        account.setTotalFees(account.getLines().stream()
                .mapToDouble(StudentFeeLine::getAmount).sum());
        account.setUpdatedBy(editor);
        accountRepository.save(account);
    }

    private void synchronizeStudentTerm(Student student, FeeStructure structure, User creator) {
        Long schoolId = TenantContext.getSchoolId();
        StudentFeeAccount account = accountRepository
                .findByStudentIdAndTermIdAndSchoolId(
                        student.getId(), structure.getTerm().getId(), schoolId)
                .orElse(null);

        if (account == null) {
            account = new StudentFeeAccount();
            account.setSchool(student.getSchool());
            account.setStudent(student);
            account.setTerm(structure.getTerm());
            account.setSchoolClass(student.getCurrentClass());
            account.setCreatedBy(creator);
        } else if (!account.getPayments().isEmpty()) {
            return;
        }

        account.setUpdatedBy(creator);
        account.getLines().clear();
        for (FeeItem item : structure.getItems()) {
            StudentFeeLine line = new StudentFeeLine();
            line.setAccount(account);
            line.setName(item.getName());
            line.setAmount(item.getAmount());
            line.setMandatory(item.getMandatory());
            line.setDescription(item.getDescription());
            line.setFromAnnualFee(false);
            line.setSchool(account.getSchool());
            line.setCreatedBy(creator);
            line.setUpdatedBy(creator);
            account.getLines().add(line);
        }

        AcademicYear academicYear = structure.getTerm().getAcademicYear();
        if (academicYear != null && isFirstTermOfYear(
                structure.getTerm(), academicYear, schoolId)) {
            List<AnnualFee> annualFees = annualFeeRepository
                    .findAllBySchoolClassIdAndAcademicYearIdAndSchoolId(
                            student.getCurrentClass().getId(), academicYear.getId(), schoolId);
            for (AnnualFee annualFee : annualFees) {
                StudentFeeLine line = new StudentFeeLine();
                line.setAccount(account);
                line.setName(annualFee.getName());
                line.setAmount(annualFee.getAmount());
                line.setMandatory(true);
                line.setDescription(annualFee.getDescription());
                line.setFromAnnualFee(true);
                line.setSchool(account.getSchool());
                line.setCreatedBy(creator);
                line.setUpdatedBy(creator);
                account.getLines().add(line);
            }
        }

        account.setTotalFees(account.getLines().stream()
                .mapToDouble(StudentFeeLine::getAmount).sum());
        account.setTotalAdjustments(account.getAdjustments().stream()
                .mapToDouble(StudentFeeAdjustment::getAmount).sum());
        accountRepository.save(account);
    }

    public StudentFeeAccountDto getAccount(Long studentId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        StudentFeeAccount account = accountRepository
                .findByStudentIdAndTermIdAndSchoolId(studentId, termId, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException(
                        "No fee account exists for this student and term. Generate first."));
        return toDto(account);
    }

    public List<StudentFeeAccountDto> listForTerm(Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return accountRepository.findAllByTermIdAndSchoolId(termId, schoolId)
                .stream().map(this::toDto).toList();
    }

    public Page<StudentFeeAccountDto> listForClass(Long classId, Long termId, String status, Pageable pageable) {
        Long schoolId = TenantContext.getSchoolId();
        return accountRepository.searchClass(classId, termId, schoolId,
                status == null ? "all" : status, pageable).map(this::toDto);
    }

    public List<StudentFeeAccountDto> listForClass(Long classId, Long termId) {
        return listForClass(classId, termId, "all", Pageable.unpaged()).getContent();
    }

    @Transactional
    public StudentFeeAccountDto addAdjustment(Long accountId, StudentFeeAdjustmentDto dto, User approver) {
        StudentFeeAccount account = requireAccount(accountId);

        if (dto.getType() == null) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Adjustment type is required");
        }
        if (dto.getType() == AdjustmentType.WRITEOFF) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "Write-offs must use the write-off operation");
        }
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Amount must be greater than 0");
        }
        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Reason is required");
        }

        double existingAdjustments = account.getAdjustments().stream()
                .mapToDouble(StudentFeeAdjustment::getAmount).sum();
        double newTotal = existingAdjustments + dto.getAmount();
        if (newTotal > account.getTotalFees()) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "Total adjustments cannot exceed total fees (M " + account.getTotalFees() + ")");
        }

        StudentFeeAdjustment adj = new StudentFeeAdjustment();
        adj.setAccount(account);
        adj.setType(dto.getType());
        adj.setAmount(dto.getAmount());
        adj.setReason(dto.getReason().trim());
        adj.setApprovedBy(approver);
        adj.setAppliedAt(Instant.now());
        adj.setSchool(account.getSchool());
        adj.setCreatedBy(approver);
        adj.setUpdatedBy(approver);
        account.getAdjustments().add(adj);

        account.setTotalAdjustments(newTotal);
        account.setUpdatedBy(approver);

        StudentFeeAccount saved = accountRepository.save(account);
        return toDto(saved);
    }

    @Transactional
    public StudentFeeAccountDto writeOff(Long accountId, StudentFeeAdjustmentDto dto, User approver) {
        StudentFeeAccount account = requireAccount(accountId);
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Write-off amount must be greater than 0");
        }
        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Write-off reason is required");
        }

        double outstanding = balanceService.currentTermBalance(account);
        if (dto.getAmount() > outstanding) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "Write-off cannot exceed this term's outstanding amount (M " + outstanding + ")");
        }

        StudentFeeAdjustment writeOff = new StudentFeeAdjustment();
        writeOff.setAccount(account);
        writeOff.setType(AdjustmentType.WRITEOFF);
        writeOff.setAmount(dto.getAmount());
        writeOff.setReason(dto.getReason().trim());
        writeOff.setApprovedBy(approver);
        writeOff.setAppliedAt(Instant.now());
        writeOff.setSchool(account.getSchool());
        writeOff.setCreatedBy(approver);
        writeOff.setUpdatedBy(approver);
        account.getAdjustments().add(writeOff);
        account.setTotalAdjustments(account.getAdjustments().stream()
                .mapToDouble(StudentFeeAdjustment::getAmount).sum());
        account.setUpdatedBy(approver);

        StudentFeeAccount saved = accountRepository.save(account);
        return toDto(saved);
    }

    @Transactional
    public StudentFeeAccountDto removeAdjustment(Long adjustmentId, User remover) {
        Long schoolId = TenantContext.getSchoolId();

        List<StudentFeeAccount> accounts = accountRepository.findAllBySchoolId(schoolId);

        for (StudentFeeAccount account : accounts) {
            StudentFeeAdjustment found = account.getAdjustments().stream()
                    .filter(a -> a.getId().equals(adjustmentId))
                    .findFirst()
                    .orElse(null);

            if (found != null) {
                if (found.getType() == AdjustmentType.WRITEOFF
                        && !remover.hasRole(ls.co.bytewise.api.common.UserRole.SCHOOL_ADMIN)
                        && !remover.hasRole(ls.co.bytewise.api.common.UserRole.SYSTEM_ADMIN)) {
                    throw new StudentFeeExceptions.InvalidFeeOperationException(
                            "Only a school admin can remove a write-off");
                }
                account.getAdjustments().remove(found);
                account.setTotalAdjustments(account.getAdjustments().stream()
                        .mapToDouble(StudentFeeAdjustment::getAmount).sum());
                StudentFeeAccount saved = accountRepository.save(account);
                return toDto(saved);
            }
        }

        throw new StudentFeeExceptions.AccountNotFoundException("Adjustment not found");
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        StudentFeeAccount account = requireAccount(accountId);
        accountRepository.delete(account);
    }

    private StudentFeeAccount requireAccount(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return accountRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Account not found"));
    }

    private boolean isFirstTermOfYear(Term term, AcademicYear year, Long schoolId) {
        List<Term> yearTerms = termRepository.findByAcademicYearId(year.getId());
        if (yearTerms.isEmpty()) return false;
        Term earliest = yearTerms.stream()
                .min((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
                .orElse(null);
        return earliest != null && earliest.getId().equals(term.getId());
    }

    public StudentFeeAccountDto getById(Long id) {
        return toDto(requireAccount(id));
    }

    @Transactional(readOnly = true)
    public Page<StudentFeePaymentDto> listPayments(String search, Pageable pageable) {
        Long schoolId = TenantContext.getSchoolId();
        return paymentRepository.searchBySchoolId(schoolId,
                search == null ? null : search.trim(), pageable)
                .map(StudentFeePaymentDto::new);
    }

    @Transactional
    public StudentFeeAccountDto recordPayment(Long accountId, StudentFeePaymentDto dto, User receivedBy) {
        StudentFeeAccount account = accountRepository.findByIdAndSchoolIdForUpdate(
                        accountId, TenantContext.getSchoolId())
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Fee account not found"));
        Student student = account.getStudent();

        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Amount must be greater than 0");
        }
        if (dto.getMethod() == null) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Payment method is required");
        }

        StudentFeePayment duplicate = findDuplicatePayment(account, dto);
        if (duplicate != null && !dto.isConfirmDuplicate()) {
            throw new StudentFeeExceptions.DuplicatePaymentException(new StudentFeePaymentDto(duplicate));
        }

        double balance = balanceService.totalBalance(account);
        if (balance <= 0) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("This account is already fully paid");
        }
        if (dto.getAmount() > balance) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "Payment exceeds remaining balance (M " + balance + ")");
        }

        StudentFeePayment payment = new StudentFeePayment();
        payment.setAccount(account);
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod());
        payment.setReference(dto.getReference());
        payment.setPaidAt(dto.getPaidAt() != null ? dto.getPaidAt() : Instant.now());
        payment.setReceivedBy(receivedBy);
        payment.setStatus(PaymentStatus.ACTIVE);
        payment.setSchool(account.getSchool());
        payment.setCreatedBy(receivedBy);
        payment.setBalanceAfter(balance - dto.getAmount());
        account.setAmountPaid(account.getAmountPaid() + dto.getAmount());
        account.setUpdatedBy(receivedBy);
        account.getPayments().add(payment);

        StudentFeeAccount saved = accountRepository.save(account);
        auditLogService.record(saved.getSchool(), receivedBy, AuditAction.PAYMENT_RECORDED,
                "PAYMENT", payment.getId(),
                "Recorded payment of M " + String.format("%.2f", payment.getAmount())
                        + " for " + student.getUser().getFullName()
                        + "; balance changed from M " + String.format("%.2f", balance)
                        + " to M " + String.format("%.2f", balance - payment.getAmount()));

        String recipientEmail = null;
        String recipientName = null;

        if (student.getParents() != null && !student.getParents().isEmpty()) {
            Parent parent = student.getParents().iterator().next();
            if (parent.getUser() != null) {
                recipientEmail = parent.getUser().getEmail();
                recipientName = parent.getUser().getFirstName();
            }
        } else if (student.getUser() != null) {
            recipientEmail = student.getUser().getEmail();
            recipientName = student.getUser().getFirstName();
        }

        if (recipientEmail != null) {
            String subject = "Payment Received - M " + String.format("%.2f", dto.getAmount());
            String body = "Dear " + recipientName + ",\n\n"
                    + "We have received a payment of M " + String.format("%.2f", dto.getAmount()) + " for "
                    + student.getUser().getFullName() + " on " + java.time.LocalDate.now() + ".\n\n"
                    + "Receipt Number: RCPT-" + String.format("%05d", saved.getPayments().get(saved.getPayments().size() - 1).getId()) + "\n"
                    + "Payment Method: " + dto.getMethod() + "\n"
                    + "Remaining Balance: M " + String.format("%.2f", balance - dto.getAmount()) + "\n\n"
                    + "Thank you for your payment.\n\n"
                    + account.getSchool().getName();
            mailService.send(recipientEmail, subject, body, account.getSchool());
        }

        return toDto(saved);
    }

    private StudentFeePayment findDuplicatePayment(StudentFeeAccount account, StudentFeePaymentDto dto) {
        return paymentRepository.findAllByAccountIdAndAmountOrderByPaidAtDesc(account.getId(), dto.getAmount())
                .stream()
                .filter(payment -> {
                    if (dto.getReference() != null && !dto.getReference().isBlank()) {
                        return payment.getReference() != null
                                && payment.getReference().trim().equalsIgnoreCase(dto.getReference().trim());
                    }
                    if (dto.getPaidAt() == null || payment.getPaidAt() == null) return false;
                    return payment.getPaidAt().atZone(java.time.ZoneOffset.UTC).toLocalDate()
                            .equals(dto.getPaidAt().atZone(java.time.ZoneOffset.UTC).toLocalDate());
                })
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public StudentFeeAccountDto reversePayment(Long paymentId, PaymentReversalDto dto, User reversedBy) {
        if (dto == null || dto.getReason() == null || dto.getReason().isBlank()) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("A reversal reason is required");
        }

        Long schoolId = TenantContext.getSchoolId();
        StudentFeePayment payment = paymentRepository.findByIdAndAccount_SchoolId(paymentId, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.REVERSED) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("This payment has already been reversed");
        }

        StudentFeeAccount account = payment.getAccount();
        account.setAmountPaid(Math.max(0.0, account.getAmountPaid() - payment.getAmount()));
        account.setUpdatedBy(reversedBy);

        payment.setStatus(PaymentStatus.REVERSED);
        payment.setReversalReason(dto.getReason().trim());
        payment.setReversedAt(Instant.now());
        payment.setReversedBy(reversedBy);

        StudentFeeAccount saved = accountRepository.save(account);
        auditLogService.record(saved.getSchool(), reversedBy, AuditAction.PAYMENT_REVERSED,
                "PAYMENT", payment.getId(),
                "Reversed payment of M " + String.format("%.2f", payment.getAmount())
                        + " for " + account.getStudent().getUser().getFullName()
                        + "; reason: " + payment.getReversalReason());
        return toDto(saved);
    }

    public StudentFeeAccountDto getAccountForStudent(User user, Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByUserId(user.getId())
                .filter(s -> s.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException(
                        "No student record linked to this account"));

        return accountRepository.findByStudentIdAndTermIdAndSchoolId(student.getId(), termId, schoolId)
                .map(StudentFeeAccountDto::new)
                .orElse(null);
    }

    private StudentFeeAccountDto toDto(StudentFeeAccount account) {
        return new StudentFeeAccountDto(
                account,
                balanceService.previousBalances(account),
                balanceService.currentTermBalance(account),
                balanceService.totalBalance(account));
    }

    public byte[] generateReceipt(Long paymentId, User currentUser) {
        Long schoolId = TenantContext.getSchoolId();

        StudentFeePayment payment = paymentRepository.findByIdAndAccount_SchoolId(paymentId, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Payment not found"));

        if (currentUser.hasRole(UserRole.STUDENT)) {
            Student student = studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Student not found"));
            if (!payment.getAccount().getStudent().getId().equals(student.getId())) {
                throw new StudentFeeExceptions.AccountNotFoundException("Payment not found");
            }
        }

        if (currentUser.hasRole(UserRole.PARENT)) {
            Parent parent = parentRepository.findByUserId(currentUser.getId())
                    .filter(p -> p.isLoginEnabled()
                            && p.getSchool().getId().equals(schoolId))
                    .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Payment not found"));
            boolean linked = payment.getAccount().getStudent().getParents().stream()
                    .anyMatch(parentLink -> parentLink.getId().equals(parent.getId()));
            if (!linked) {
                throw new StudentFeeExceptions.AccountNotFoundException("Payment not found");
            }
        }

        StudentFeeAccount account = payment.getAccount();
        School school = account.getSchool();

        PaymentReceiptDto dto = new PaymentReceiptDto();
        dto.setSchoolName(school.getName());
        dto.setSchoolAddress(school.getAddress());
        dto.setSchoolPhone(school.getContactPhone());
        dto.setSchoolEmail(school.getContactEmail());
        if (school.getLogo() != null) {
            dto.setSchoolLogo(school.getLogo().getContents());
            dto.setSchoolLogoMimeType(school.getLogo().getType());
        }

        dto.setReceiptNumber(String.format("RCPT-%05d", payment.getId()));
        dto.setStudentName(account.getStudent().getUser().getFullName());
        dto.setAdmissionNumber(account.getStudent().getAdmissionNumber());
        dto.setClassName(account.getSchoolClass().getName());

        dto.setAmountPaid(payment.getAmount());
        dto.setPaymentMethod(payment.getMethod().name());
        dto.setReference(payment.getReference());
        dto.setPaymentDate(payment.getPaidAt().atZone(java.time.ZoneOffset.UTC).toLocalDate());
        dto.setBalanceAfter(payment.getBalanceAfter());
        dto.setCancelled(payment.getStatus() == PaymentStatus.REVERSED);
        dto.setCancellationReason(payment.getReversalReason());
        if (payment.getReversedBy() != null) {
            dto.setCancelledBy(payment.getReversedBy().getFullName());
        }
        if (payment.getReversedAt() != null) {
            dto.setCancelledAt(payment.getReversedAt().atZone(java.time.ZoneOffset.UTC).toLocalDate());
        }

        if (payment.getReceivedBy() != null) {
            dto.setReceivedBy(payment.getReceivedBy().getFullName());
        }
        dto.setGeneratedDate(java.time.LocalDate.now());

        return PaymentReceiptRenderer.render(dto);
    }
}
