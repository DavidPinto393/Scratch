package com.scratch.validators.field;

import com.scratch.validators.models.MandatoryValidation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=false)
public class ListValidator extends FieldValidator {

    @Builder.Default
    List<String> validStrings = new ArrayList<>();

    @Builder.Default
    List<Integer> validIntegers = new ArrayList<>();

    @Builder.Default
    boolean isString = true;

    @Override
    public String validate(Object value) {
        String errorString = "";
        MandatoryValidation mandatoryValidation= super.validateMandatory(value);
        if( mandatoryValidation.isSkip() ) {
            return "";
        }
        else if(!mandatoryValidation.isValid()){
            return String.format( "Value of %s is blank.", fieldName );
        }
        if( isString ) {
            if( !( value instanceof String ) ){
                return String.format( "Value of %s is not a String.", fieldName );
            }
            if( !( validStrings.contains( value ) ) ) {
                errorString = String.format( "Value of %s is not in the permitted List( %s ). ", fieldName, String.join(", ", validStrings) );
            }
        }
        else {
            if( !( value instanceof Integer ) ) {
                return String.format( "Value of %s is not a Number.", fieldName );
            }
            if( !( validIntegers.contains( value ) ) ) {
                errorString = String.format( "Value of %s is not in the permitted List( %s ). ", fieldName, validIntegers.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            }
        }

        return errorString;
    }
}