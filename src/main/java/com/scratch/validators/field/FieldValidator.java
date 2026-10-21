package com.scratch.validators.field;

import com.scratch.validators.models.MandatoryValidation;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter 
@SuperBuilder
public abstract class FieldValidator {

    protected boolean mandatory;

    protected String fieldName;

    public abstract String validate(Object value);

    protected MandatoryValidation validateMandatory(Object value) {
        if(value==null || "".equals(value.toString())) {
            if(!mandatory) {            
                return MandatoryValidation.builder().skip(true).valid(true).build();
            }
            return MandatoryValidation.builder().skip(false).valid(false).build();
        }
        return MandatoryValidation.builder().skip(false).valid(true).build();
    }
}