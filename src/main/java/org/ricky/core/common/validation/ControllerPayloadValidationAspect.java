package org.ricky.core.common.validation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.ricky.common.domain.DTO;
import org.springframework.stereotype.Component;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className ControllerPayloadValidationAspect
 * @desc controller入参校验切面
 */
@Aspect
@Component
public class ControllerPayloadValidationAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointCut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void methodPointcut() {
    }

    @Before("controllerPointCut() && methodPointcut()")
    public void correctCommand(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof DTO dto) {
                dto.correctAndValidate();
            }
        }
    }

}
