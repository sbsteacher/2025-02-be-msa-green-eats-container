package com.green.eats.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
/* RestControllerAdvice가 선언된 클래스의 메서드들이 실행되기 직전에
   로그를 가로채서 기록하는 Aspect를 생성
*/
@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {
    // @RestControllerAdvice가 붙은 모든 클래스의 메서드를 타겟으로 함
    @Pointcut("within(@org.springframework.web.bind.annotation.RestControllerAdvice *)")
    public void restControllerAdvicePointcut() {}

    @Before("restControllerAdvicePointcut()")
    public void logException(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof Exception e) {
                // 에러 타입에 따라 로그 레벨 조정 가능 (비즈니스 예외는 INFO/WARN, 시스템 예외는 ERROR)
                if (isCriticalException(e)) {
                    log.error("[Exception Log] Type: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage(), e);
                } else {
                    log.info("[Exception Log] Type: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());
                }
                break;
            }
        }
    }

    // ERROR 레벨로 찍을 중대 예외 필터링 (필요에 따라 커스텀)
    private boolean isCriticalException(Exception e) {
        return !(e instanceof org.springframework.web.bind.MethodArgumentNotValidException ||
                e instanceof com.green.eats.common.exception.BusinessException);
    }
}
