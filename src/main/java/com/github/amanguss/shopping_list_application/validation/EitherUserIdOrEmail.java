package com.github.amanguss.shopping_list_application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EitherUserIdOrEmailValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EitherUserIdOrEmail {

    String message() default "Either sharedToUserId or sharedToEmail must be provided, but not both";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}