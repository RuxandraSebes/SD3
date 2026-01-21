package com.example.demo.dtos.validators;


import com.example.demo.dtos.validators.annotation.ConsumptionLimit;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConsumptionValidator implements ConstraintValidator<ConsumptionLimit, Integer> {
    private int max;
    @Override public void initialize(ConsumptionLimit ann) { this.max = ann.value(); }
    @Override public boolean isValid(Integer rate, ConstraintValidatorContext ctx) {
        if (rate == null) return true;               // let @NotNull enforce presence
        return rate <= max;
    }
}


