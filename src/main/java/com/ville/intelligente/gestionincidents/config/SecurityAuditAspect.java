package com.ville.intelligente.gestionincidents.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class SecurityAuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditAspect.class);

    @AfterThrowing(pointcut = "within(com.ville.intelligente.gestionincidents.controller..*)", throwing = "exception")
    public void logSecurityException(JoinPoint joinPoint, Throwable exception) {
        if (exception instanceof AccessDeniedException) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                logger.warn(" TENTATIVE D'ACCÈS NON AUTORISÉ : " +
                        "IP={}, URL={}, User={}, Method={}",
                        request.getRemoteAddr(),
                        request.getRequestURI(),
                        request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonyme",
                        joinPoint.getSignature().getName());
            }
        }
    }
}