package com.greenblat.vktesttask.audit;

import com.greenblat.vktesttask.model.AuditEntity;
import com.greenblat.vktesttask.repository.AuditEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditEntityRepository auditEntityRepository;

    @Before("@annotation(audit)")
    public void beforeAudit(JoinPoint joinPoint, Audit audit) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();

        Instant modifiedAt = Instant.now();
        String username = getUsername(authentication);
        String method = request.getMethod();
        String endpoint = getEndpoint(joinPoint, request);
        String requestParams = Arrays.toString(joinPoint.getArgs());

        log.info("Audit at {}: User {} invoked endpoint {} & method {} with parameters {}",
                modifiedAt, username, endpoint, method, requestParams);

        var auditEntity = AuditEntity.builder()
                .username(username)
                .endPoint(endpoint)
                .params(requestParams)
                .method(method)
                .modifiedAt(modifiedAt)
                .build();
        auditEntityRepository.save(auditEntity);
    }

    private String getEndpoint(JoinPoint joinPoint, HttpServletRequest httpServletRequest) {
        Class<?> controllerClass = joinPoint.getTarget().getClass();

        if (AnnotationUtils.findAnnotation(controllerClass, RestController.class) != null) {
            return httpServletRequest.getRequestURI();
        }

        return "Unknown endpoint";
    }

    private String getUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "Anonymous";
    }


}
