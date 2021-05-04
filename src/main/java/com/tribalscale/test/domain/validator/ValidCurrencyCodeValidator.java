package com.tribalscale.test.domain.validator;

import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Currency;

public class ValidCurrencyCodeValidator implements ConstraintValidator<ValidCurrencyCode, String> {

    private Boolean isOptional;

    @Override
    public void initialize(ValidCurrencyCode validCurrencyCode) {
        this.isOptional = validCurrencyCode.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        try {
            boolean containsIsoCode = Currency.getAvailableCurrencies()
                    .contains(Currency.getInstance(value));

            return isOptional
                    ? (containsIsoCode || (!ObjectUtils.isEmpty(value)))
                    : containsIsoCode;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
