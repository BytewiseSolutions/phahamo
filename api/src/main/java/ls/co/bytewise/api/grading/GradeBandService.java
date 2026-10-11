package ls.co.bytewise.api.grading;

import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GradeBandService {

    private final GradeBandRepository gradeBandRepository;

    public GradeBandService(GradeBandRepository gradeBandRepository) {
        this.gradeBandRepository = gradeBandRepository;
    }

    public List<GradeBandDto> listAll() {
        Long schoolId = TenantContext.getSchoolId();
        return gradeBandRepository.findAllBySchoolIdOrderByMinScoreDesc(schoolId).stream()
                .map(GradeBandDto::new)
                .toList();
    }

    @Transactional
    public List<GradeBandDto> replaceAll(User currentUser, List<GradeBandDto> bands) {
        Long schoolId = TenantContext.getSchoolId();

        if (bands == null || bands.isEmpty()) {
            throw new GradeBandExceptions.InvalidGradeBandException("At least one band is required");
        }

        List<GradeBandDto> sorted = new ArrayList<>(bands);
        sorted.sort(Comparator.comparing(GradeBandDto::getMinScore));

        validateNoOverlap(sorted);
        validateUniqueGrades(sorted);

        gradeBandRepository.deleteAll(
                gradeBandRepository.findAllBySchoolIdOrderByMinScoreDesc(schoolId)
        );
        gradeBandRepository.flush();

        List<GradeBand> toSave = new ArrayList<>();
        for (GradeBandDto dto : sorted) {
            GradeBand band = new GradeBand();
            band.setSchool(currentUser.getSchools().stream()
                    .filter(s -> s.getId().equals(schoolId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Active school not found")));
            band.setMinScore(dto.getMinScore());
            band.setMaxScore(dto.getMaxScore());
            band.setGrade(dto.getGrade().trim());
            band.setRemark(dto.getRemark().trim());
            band.setCreatedBy(currentUser);
            band.setUpdatedBy(currentUser);
            toSave.add(band);
        }

        List<GradeBand> saved = gradeBandRepository.saveAll(toSave);
        saved.sort(Comparator.comparing(GradeBand::getMinScore).reversed());
        return saved.stream().map(GradeBandDto::new).toList();
    }

    private void validateNoOverlap(List<GradeBandDto> sorted) {
        for (int i = 0; i < sorted.size(); i++) {
            GradeBandDto b = sorted.get(i);

            if (b.getMinScore() == null || b.getMaxScore() == null) {
                throw new GradeBandExceptions.InvalidGradeBandException(
                        "Every band must have a min and max score");
            }
            if (b.getMinScore() < 0 || b.getMaxScore() > 100) {
                throw new GradeBandExceptions.InvalidGradeBandException(
                        "Scores must be between 0 and 100");
            }
            if (b.getMaxScore() <= b.getMinScore()) {
                throw new GradeBandExceptions.InvalidGradeBandException(
                        "Band \"" + b.getGrade() + "\" has max <= min");
            }
            if (b.getGrade() == null || b.getGrade().isBlank()) {
                throw new GradeBandExceptions.InvalidGradeBandException("Grade is required");
            }
            if (b.getRemark() == null || b.getRemark().isBlank()) {
                throw new GradeBandExceptions.InvalidGradeBandException(
                        "Remark is required for grade \"" + b.getGrade() + "\"");
            }
        }

        for (int i = 0; i < sorted.size(); i++) {
            for (int j = i + 1; j < sorted.size(); j++) {
                GradeBandDto a = sorted.get(i);
                GradeBandDto c = sorted.get(j);
                boolean overlap = a.getMinScore() <= c.getMaxScore()
                        && c.getMinScore() <= a.getMaxScore();
                if (overlap) {
                    throw new GradeBandExceptions.InvalidGradeBandException(
                            "Bands \"" + a.getGrade() + "\" and \"" + c.getGrade() + "\" overlap");
                }
            }
        }
    }

    public GradeResolveDto resolve(Double score) {
        if (score == null || score < 0 || score > 100) {
            throw new GradeBandExceptions.InvalidGradeBandException("Score must be between 0 and 100");
        }

        Long schoolId = TenantContext.getSchoolId();
        List<GradeBand> bands = gradeBandRepository.findAllBySchoolIdOrderByMinScoreDesc(schoolId);

        for (GradeBand band : bands) {
            if (score >= band.getMinScore() && score <= band.getMaxScore()) {
                return new GradeResolveDto(score, band.getGrade(), band.getRemark());
            }
        }

        throw new GradeBandExceptions.InvalidGradeBandException(
                "No grade band covers score " + score);
    }

    private void validateUniqueGrades(List<GradeBandDto> bands) {
        List<String> seen = new ArrayList<>();
        for (GradeBandDto dto : bands) {
            String g = dto.getGrade().trim().toUpperCase();
            if (seen.contains(g)) {
                throw new GradeBandExceptions.DuplicateGradeException(
                        "Duplicate grade: " + dto.getGrade());
            }
            seen.add(g);
        }
    }
}