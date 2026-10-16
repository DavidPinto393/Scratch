package com.scratch.validators.field;

import com.scratch.validators.models.MandatoryValidation;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter 
@SuperBuilder
public abstract class FieldValidator {

    protected boolean mandatory;

    public abstract String validate(Object value);

    protected MandatoryValidation validateMandatory(Object value) {
        if(!mandatory) {
            return MandatoryValidation.builder().skip(true).valid(true).build();
        }
        if(value==null || "".equals((String)value)) {
            return MandatoryValidation.builder().skip(false).valid(false).build();
        }
            return MandatoryValidation.builder().skip(false).valid(true).build();
    }
}