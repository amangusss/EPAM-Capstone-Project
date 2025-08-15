package com.github.amanguss.shopping_list_application.validation;

import com.github.amanguss.shopping_list_application.dto.listShare.ListShareCreateDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EitherUserIdOrEmailValidator implements ConstraintValidator<EitherUserIdOrEmail, ListShareCreateDto> {

    @Override
    public void initialize(EitherUserIdOrEmail constraintAnnotation) {}

    @Override
    public boolean isValid(ListShareCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        boolean hasUserId = dto.getSharedToUserId() != null;
        boolean hasEmail = dto.getSharedToEmail() != null && !dto.getSharedToEmail().trim().isEmpty();

        return hasUserId ^ hasEmail;
    }
}