package com.scratch.validators.field;

import com.scratch.validators.models.MandatoryValidation;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import com.scratch.utilities.*;
import java.util.regex.Pattern;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=false)
public class StringValidator extends FieldValidator {

    String fieldName;

    @Builder.Default
    int minLength = 0;
    
    @Builder.Default
    int maxLength = 0;
    
    @Builder.Default
    int fixedLength = 0;
    
    @Builder.Default
    boolean alphaNumeric = false;
    
    @Builder.Default
    boolean email = false;

    String regex;

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
        if( !( value instanceof String ) ) {
            return String.format( "Value of %s is not a String.", fieldName );
        }

        String fieldValue = (String)value;

        if( minLength>0 && fieldValue.length()<minLength ) {
            errorString += String.format( "Value of %s does not meet minimum length of %d. ", fieldName, minLength );
        }

        if( maxLength>0 && fieldValue.length()>maxLength ) {
            errorString += String.format( "Value of %s exceeds maximum length of %d. ", fieldName, maxLength );
        }

        if( fixedLength>0 && fieldValue.length()!=fixedLength ) {
            errorString += String.format( "Value of %s does not match length of %d. ", fieldName, fixedLength );
        }

        if( alphaNumeric && !Patterns.ALPHANUMERIC_PATTERN.matcher(fieldValue).matches() ) {
            errorString += String.format( "Value of %s is not Alphanumeric. ", fieldName );
        }        

        if( email && !Patterns.EMAIL_PATTERN.matcher(fieldValue).matches() ) {
            errorString += String.format( "Value of %s is not an email. ", fieldName );
        }

        if( regex!=null && !Pattern.compile(regex).matcher(fieldValue).matches()) {
            errorString += String.format( "Value of %s does not match pattern %s. ", fieldName, regex );
        }

        return errorString;
    }
}