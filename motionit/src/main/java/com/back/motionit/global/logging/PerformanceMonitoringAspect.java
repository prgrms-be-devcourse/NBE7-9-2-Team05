package com.back.motionit.global.logging;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PerformanceMonitoringAspect {

	private final MeterRegistry meterRegistry;

	@Around("execution(* com.back.motionit.domain.challenge..controller..*(..)) || "
		+ "execution(* com.back.motionit.domain.challenge..service..*(..)) || "
		+ "execution(* com.back.motionit.domain.challenge..repository..*(..))")
	public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.nanoTime();
		Object result = joinPoint.proceed();
		long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		String className = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();
		String layer = getLayer(className);

		Timer timer = Timer.builder("method.execution.time")
			.description("Method execution time in milliseconds")
			.tags("layer", layer, "class", className, "method", methodName)
			.register(meterRegistry);
		timer.record(durationMs, TimeUnit.MILLISECONDS);

		if (log.isInfoEnabled()) {
			log.info("[PERF][{}] {}.{} executed in {} ms", layer, className, methodName, durationMs);
		}
		return result;
	}

	private String getLayer(String className) {
		if (className.toLowerCase().contains("controller")) {
			return "controller";
		}
		if (className.toLowerCase().contains("service")) {
			return "service";
		}
		if (className.toLowerCase().contains("repository")) {
			return "repository";
		}
		return "other";
	}
}
