/*
 * Copyright 2024-2025 Martin Atanasov.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martinatanasov.computerstore.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//Enable logging only for test environment
@Profile(value = {"test", "benc"})
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.martinatanasov.computerstore.controllers.*.*(..))")
    private void forControllersPackage(){}

    @Pointcut("execution(* com.martinatanasov.computerstore.services.*.*(..))")
    private void forServicesPackage(){}

    @Pointcut("execution(* com.martinatanasov.computerstore.repositories.*.*(..))")
    private void forRepositoriesPackage(){}

    @Pointcut("forControllersPackage() || forServicesPackage() || forRepositoriesPackage()")
    private void forAppFlow(){}

    @Before("forAppFlow()")
    public void before(JoinPoint joinPoint){
        //Display the method we are calling
        String method = joinPoint.getSignature().toShortString();
        log.info("--->>> @Before calling the method: {}", method);
        //Get the arguments
        Object[] args = joinPoint.getArgs();
        for (Object index : args){
            log.info("--->>> argument: {}", index);
        }
    }

    @AfterReturning(
            pointcut = "forAppFlow()",
            returning = "objectResult"
    )
    public void afterReturning(JoinPoint joinPoint, Object objectResult){
        //Display the method we return
        String method = joinPoint.getSignature().toShortString();
        log.info("--->>> @AfterReturning calling the method: {}", method);
        //Display data returned
        log.info("--->>> Result: {}", objectResult);
    }

}
