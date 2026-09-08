package ls.co.bytewise.api.config;

import jakarta.persistence.EntityManager;
import ls.co.bytewise.api.security.TenantContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantFilterAspect {

    private final EntityManager entityManager;

    public TenantFilterAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Around("execution(* ls.co.bytewise.api..*Repository.*(..))")
    public Object applyTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Long schoolId = TenantContext.getSchoolId();

        if (schoolId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("schoolFilter").setParameter("schoolId", schoolId);
        }

        return joinPoint.proceed();
    }
}