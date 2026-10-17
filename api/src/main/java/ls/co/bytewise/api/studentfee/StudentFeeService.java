package ls.co.bytewise.api.studentfee;

import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.fee.*;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentFeeService {

    private final StudentFeeAccountRepository accountRepository;
    private final StudentRepository studentRepository;
    private final TermRepository termRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final AnnualFeeRepository annualFeeRepository;
    private final StudentFeePaymentRepository paymentRepository;

    public StudentFeeService(StudentFeeAccountRepository accountRepository,
                             StudentRepository studentRepository,
                             TermRepository termRepository,
                             FeeStructureRepository feeStructureRepository,
                             AnnualFeeRepository annualFeeRepository,
                             StudentFeePaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.studentRepository = studentRepository;
        this.termRepository = termRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.annualFeeRepository = annualFeeRepository;
        this.paymentRepository = paymentRepository;
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
        return new StudentFeeAccountDto(saved);
    }

    @Transactional
    public List<StudentFeeAccountDto> generateForClass(Long classId, Long termId, User creator) {
        Long schoolId = TenantContext.getSchoolId();

        List<Student> students = studentRepository
                .findAllBySchoolIdAndCurrentClassId(schoolId, classId);

        List<StudentFeeAccountDto> results = new ArrayList<>();
        for (Student s : students) {
            try {
                results.add(generateForStudent(s.getId(), termId, creator));
            } catch (StudentFeeExceptions.InvalidFeeOperationException ignored) {

            }
        }
        return results;
    }

    public StudentFeeAccountDto getAccount(Long studentId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        StudentFeeAccount account = accountRepository
                .findByStudentIdAndTermIdAndSchoolId(studentId, termId, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException(
                        "No fee account exists for this student and term. Generate first."));
        return new StudentFeeAccountDto(account);
    }

    public List<StudentFeeAccountDto> listForTerm(Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return accountRepository.findAllByTermIdAndSchoolId(termId, schoolId)
                .stream().map(StudentFeeAccountDto::new).toList();
    }

    public List<StudentFeeAccountDto> listForClass(Long classId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return accountRepository
                .findAllBySchoolClassIdAndTermIdAndSchoolId(classId, termId, schoolId)
                .stream().map(StudentFeeAccountDto::new).toList();
    }

    @Transactional
    public StudentFeeAccountDto addAdjustment(Long accountId, AdjustmentPayloadDto dto, User approver) {
        StudentFeeAccount account = requireAccount(accountId);

        if (dto.getType() == null) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Adjustment type is required");
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
        return new StudentFeeAccountDto(saved);
    }

    @Transactional
    public StudentFeeAccountDto removeAdjustment(Long adjustmentId) {
        Long schoolId = TenantContext.getSchoolId();

        List<StudentFeeAccount> accounts = accountRepository.findAllBySchoolId(schoolId);

        for (StudentFeeAccount account : accounts) {
            StudentFeeAdjustment found = account.getAdjustments().stream()
                    .filter(a -> a.getId().equals(adjustmentId))
                    .findFirst()
                    .orElse(null);

            if (found != null) {
                account.getAdjustments().remove(found);
                account.setTotalAdjustments(account.getAdjustments().stream()
                        .mapToDouble(StudentFeeAdjustment::getAmount).sum());
                StudentFeeAccount saved = accountRepository.save(account);
                return new StudentFeeAccountDto(saved);
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
        return new StudentFeeAccountDto(requireAccount(id));
    }

    @Transactional
    public StudentFeeAccountDto recordPayment(Long accountId, PaymentPayloadDto dto, User receivedBy) {
        StudentFeeAccount account = requireAccount(accountId);

        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Amount must be greater than 0");
        }
        if (dto.getMethod() == null) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("Payment method is required");
        }
        if (account.getBalance() <= 0) {
            throw new StudentFeeExceptions.InvalidFeeOperationException("This account is already fully paid");
        }
        if (dto.getAmount() > account.getBalance()) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "Payment exceeds remaining balance (M " + account.getBalance() + ")");
        }

        StudentFeePayment payment = new StudentFeePayment();
        payment.setAccount(account);
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod());
        payment.setReference(dto.getReference());
        payment.setPaidAt(dto.getPaidAt() != null ? dto.getPaidAt() : Instant.now());
        payment.setReceivedBy(receivedBy);
        payment.setSchool(account.getSchool());
        payment.setCreatedBy(receivedBy);
        payment.setBalanceAfter(account.getBalance() - dto.getAmount());
        account.setAmountPaid(account.getAmountPaid() + dto.getAmount());
        account.setUpdatedBy(receivedBy);
        account.getPayments().add(payment);

        StudentFeeAccount saved = accountRepository.save(account);
        return new StudentFeeAccountDto(saved);
    }

    public StudentFeeAccountDto getAccountForStudent(User user, Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByUserId(user.getId())
                .filter(s -> s.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException(
                        "No student record linked to this account"));

        return accountRepository.findByStudentIdAndTermIdAndSchoolId(student.getId(), termId, schoolId)
                .map(StudentFeeAccountDto::new)
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException(
                        "Your fee account hasn't been generated yet. Please contact the school."));
    }

    public byte[] generateReceipt(Long paymentId, User currentUser) {
        Long schoolId = TenantContext.getSchoolId();

        StudentFeePayment payment = paymentRepository.findByIdAndAccount_SchoolId(paymentId, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Payment not found"));

        if (currentUser.hasRole(ls.co.bytewise.api.common.UserRole.STUDENT)) {
            Student student = studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Student not found"));
            if (!payment.getAccount().getStudent().getId().equals(student.getId())) {
                throw new StudentFeeExceptions.AccountNotFoundException("Payment not found");
            }
        }

        StudentFeeAccount account = payment.getAccount();
        ls.co.bytewise.api.school.School school = account.getSchool();

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

        if (payment.getReceivedBy() != null) {
            dto.setReceivedBy(payment.getReceivedBy().getFullName());
        }
        dto.setGeneratedDate(java.time.LocalDate.now());

        return PaymentReceiptRenderer.render(dto);
    }
}