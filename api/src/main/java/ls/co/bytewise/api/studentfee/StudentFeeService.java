package ls.co.bytewise.api.studentfee;

import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.fee.*;
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

    public StudentFeeService(StudentFeeAccountRepository accountRepository,
                             StudentRepository studentRepository,
                             TermRepository termRepository,
                             FeeStructureRepository feeStructureRepository,
                             AnnualFeeRepository annualFeeRepository) {
        this.accountRepository = accountRepository;
        this.studentRepository = studentRepository;
        this.termRepository = termRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.annualFeeRepository = annualFeeRepository;
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
}