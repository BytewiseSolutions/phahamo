package ls.co.bytewise.api.fee;

import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.academicyear.AcademicYearRepository;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;
    private final AnnualFeeRepository annualFeeRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final TermRepository termRepository;
    private final AcademicYearRepository academicYearRepository;

    public FeeStructureService(FeeStructureRepository feeStructureRepository,
                               AnnualFeeRepository annualFeeRepository,
                               SchoolClassRepository schoolClassRepository,
                               TermRepository termRepository,
                               AcademicYearRepository academicYearRepository) {
        this.feeStructureRepository = feeStructureRepository;
        this.annualFeeRepository = annualFeeRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.termRepository = termRepository;
        this.academicYearRepository = academicYearRepository;
    }

    public List<FeeStructureDto> listStructures(Long classId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        List<FeeStructure> structures;
        if (classId != null && termId != null) {
            structures = feeStructureRepository
                    .findAllBySchoolClassIdAndTermIdAndSchoolId(classId, termId, schoolId);
        } else if (classId != null) {
            structures = feeStructureRepository
                    .findAllBySchoolClassIdAndSchoolId(classId, schoolId);
        } else if (termId != null) {
            structures = feeStructureRepository
                    .findAllByTermIdAndSchoolId(termId, schoolId);
        } else {
            structures = feeStructureRepository.findAllBySchoolId(schoolId);
        }

        return structures.stream().map(FeeStructureDto::new).toList();
    }

    public FeeStructureDto getStructure(Long id) {
        return new FeeStructureDto(requireStructure(id));
    }

    @Transactional
    public FeeStructureDto createStructure(User creator, FeeStructureDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        if (dto.getClassId() == null || dto.getTermId() == null) {
            throw new FeeExceptions.InvalidFeeOperationException("Class and term are required");
        }

        SchoolClass schoolClass = schoolClassRepository.findById(dto.getClassId())
                .filter(c -> c.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new FeeExceptions.InvalidFeeOperationException("Class not found"));

        Term term = termRepository.findByIdAndSchoolId(dto.getTermId(), schoolId)
                .orElseThrow(() -> new FeeExceptions.InvalidFeeOperationException("Term not found"));

        boolean exists = !feeStructureRepository
                .findAllBySchoolClassIdAndTermIdAndSchoolId(dto.getClassId(), dto.getTermId(), schoolId)
                .isEmpty();
        if (exists) {
            throw new FeeExceptions.InvalidFeeOperationException(
                    "A fee structure already exists for this class and term");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new FeeExceptions.InvalidFeeOperationException("At least one fee item is required");
        }

        FeeStructure structure = new FeeStructure();
        structure.setSchool(schoolClass.getSchool());
        structure.setSchoolClass(schoolClass);
        structure.setTerm(term);
        structure.setCreatedBy(creator);
        structure.setUpdatedBy(creator);

        for (FeeItemDto itemDto : dto.getItems()) {
            validateItem(itemDto);
            FeeItem item = new FeeItem();
            item.setFeeStructure(structure);
            item.setName(itemDto.getName().trim());
            item.setAmount(itemDto.getAmount());
            item.setMandatory(itemDto.getMandatory() != null ? itemDto.getMandatory() : true);
            item.setDescription(itemDto.getDescription());
            item.setSchool(schoolClass.getSchool());
            item.setCreatedBy(creator);
            item.setUpdatedBy(creator);
            structure.getItems().add(item);
        }

        FeeStructure saved = feeStructureRepository.save(structure);
        return new FeeStructureDto(saved);
    }

    @Transactional
    public FeeStructureDto updateStructure(User editor, Long id, FeeStructureDto dto) {
        FeeStructure structure = requireStructure(id);

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new FeeExceptions.InvalidFeeOperationException("At least one fee item is required");
        }

        structure.getItems().clear();

        for (FeeItemDto itemDto : dto.getItems()) {
            validateItem(itemDto);
            FeeItem item = new FeeItem();
            item.setFeeStructure(structure);
            item.setName(itemDto.getName().trim());
            item.setAmount(itemDto.getAmount());
            item.setMandatory(itemDto.getMandatory() != null ? itemDto.getMandatory() : true);
            item.setDescription(itemDto.getDescription());
            item.setSchool(structure.getSchool());
            item.setCreatedBy(editor);
            item.setUpdatedBy(editor);
            structure.getItems().add(item);
        }

        structure.setUpdatedBy(editor);
        FeeStructure saved = feeStructureRepository.save(structure);
        return new FeeStructureDto(saved);
    }

    @Transactional
    public void deleteStructure(Long id) {
        FeeStructure structure = requireStructure(id);
        feeStructureRepository.delete(structure);
    }

    public List<AnnualFeeDto> listAnnualFees(Long classId, Long academicYearId) {
        Long schoolId = TenantContext.getSchoolId();

        List<AnnualFee> fees;
        if (classId != null && academicYearId != null) {
            fees = annualFeeRepository
                    .findAllBySchoolClassIdAndAcademicYearIdAndSchoolId(classId, academicYearId, schoolId);
        } else if (classId != null) {
            fees = annualFeeRepository
                    .findAllBySchoolClassIdAndSchoolId(classId, schoolId);
        } else if (academicYearId != null) {
            fees = annualFeeRepository
                    .findAllByAcademicYearIdAndSchoolId(academicYearId, schoolId);
        } else {
            fees = annualFeeRepository.findAllBySchoolId(schoolId);
        }

        return fees.stream().map(AnnualFeeDto::new).toList();
    }

    public AnnualFeeDto getAnnualFee(Long id) {
        return new AnnualFeeDto(requireAnnualFee(id));
    }

    @Transactional
    public AnnualFeeDto createAnnualFee(User creator, AnnualFeeDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        if (dto.getClassId() == null || dto.getAcademicYearId() == null) {
            throw new FeeExceptions.InvalidFeeOperationException("Class and academic year are required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new FeeExceptions.InvalidFeeOperationException("Name is required");
        }
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new FeeExceptions.InvalidFeeOperationException("Amount must be greater than 0");
        }

        SchoolClass schoolClass = schoolClassRepository.findById(dto.getClassId())
                .filter(c -> c.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new FeeExceptions.InvalidFeeOperationException("Class not found"));

        AcademicYear year = academicYearRepository.findById(dto.getAcademicYearId())
                .filter(y -> y.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new FeeExceptions.InvalidFeeOperationException("Academic year not found"));

        boolean exists = annualFeeRepository
                .findAllBySchoolClassIdAndAcademicYearIdAndSchoolId(
                        dto.getClassId(), dto.getAcademicYearId(), schoolId)
                .stream()
                .anyMatch(f -> f.getName().equalsIgnoreCase(dto.getName().trim()));
        if (exists) {
            throw new FeeExceptions.InvalidFeeOperationException(
                    "An annual fee with this name already exists for this class and year");
        }

        AnnualFee fee = new AnnualFee();
        fee.setSchool(schoolClass.getSchool());
        fee.setSchoolClass(schoolClass);
        fee.setAcademicYear(year);
        fee.setName(dto.getName().trim());
        fee.setAmount(dto.getAmount());
        fee.setDescription(dto.getDescription());
        fee.setCreatedBy(creator);
        fee.setUpdatedBy(creator);

        AnnualFee saved = annualFeeRepository.save(fee);
        return new AnnualFeeDto(saved);
    }

    @Transactional
    public AnnualFeeDto updateAnnualFee(User editor, Long id, AnnualFeeDto dto) {
        AnnualFee fee = requireAnnualFee(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            fee.setName(dto.getName().trim());
        }
        if (dto.getAmount() != null && dto.getAmount() > 0) {
            fee.setAmount(dto.getAmount());
        }
        fee.setDescription(dto.getDescription());
        fee.setUpdatedBy(editor);

        AnnualFee saved = annualFeeRepository.save(fee);
        return new AnnualFeeDto(saved);
    }

    @Transactional
    public void deleteAnnualFee(Long id) {
        AnnualFee fee = requireAnnualFee(id);
        annualFeeRepository.delete(fee);
    }

    private FeeStructure requireStructure(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return feeStructureRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new FeeExceptions.FeeStructureNotFoundException("Fee structure not found"));
    }

    private AnnualFee requireAnnualFee(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return annualFeeRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new FeeExceptions.AnnualFeeNotFoundException("Annual fee not found"));
    }

    private void validateItem(FeeItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new FeeExceptions.InvalidFeeOperationException("Fee item name is required");
        }
        if (item.getAmount() == null || item.getAmount() <= 0) {
            throw new FeeExceptions.InvalidFeeOperationException(
                    "Fee item amount must be greater than 0: " + item.getName());
        }
    }

    @Transactional
    public List<AnnualFeeDto> createAnnualFeeBulk(User creator, AnnualFeeBulkDto dto) {
        if (dto.getClassIds() == null || dto.getClassIds().isEmpty()) {
            throw new FeeExceptions.InvalidFeeOperationException("Select at least one class");
        }
        if (dto.getAcademicYearId() == null) {
            throw new FeeExceptions.InvalidFeeOperationException("Academic year is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new FeeExceptions.InvalidFeeOperationException("Name is required");
        }
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new FeeExceptions.InvalidFeeOperationException("Amount must be greater than 0");
        }

        List<AnnualFeeDto> created = new java.util.ArrayList<>();
        for (Long classId : dto.getClassIds()) {
            AnnualFeeDto single = new AnnualFeeDto();
            single.setClassId(classId);
            single.setAcademicYearId(dto.getAcademicYearId());
            single.setName(dto.getName());
            single.setAmount(dto.getAmount());
            single.setDescription(dto.getDescription());
            created.add(createAnnualFee(creator, single));
        }
        return created;
    }
}