package com.scratch.validators.field;

import com.scratch.validators.models.MandatoryValidation;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=false)
public class NumberValidator extends FieldValidator {

    String fieldName;

    @Builder.Default
    int min = 0;
    
    @Builder.Default
    int max = 0;
    
    @Builder.Default
    boolean irrational = false;
    
    @Builder.Default
    boolean positive = false;

    @Override
    public String validate(Object value) {
        String errorString = "";
        Number fieldValue;
        
        MandatoryValidation mandatoryValidation= super.validateMandatory(value);
        if( mandatoryValidation.isSkip() ) {
            return "";
        }
        else if(!mandatoryValidation.isValid()){
            return String.format( "Value of %s is blank.", fieldName );
        }
        if( value instanceof String ) {
            try{
                fieldValue = Double.valueOf((String)value);
            }
            catch(NumberFormatException e) {
                return String.format( "Value of %s is not a Number. ", fieldName );
            }
        }
        if( !(value instanceof Number) ) {
            return String.format( "Value of %s is not a Number. ", fieldName );
        }
        fieldValue = (Number)value;

        if( min>0 && fieldValue.doubleValue()<min ) {
            errorString += String.format( "Value of %s does not meet minimum value of %d. ", fieldName, min );
        }

        if( max>0 && fieldValue.doubleValue()>max ) {
            errorString += String.format( "Value of %s exceeds maximum length of %d. ", fieldName, max );
        }

        if( positive && fieldValue.doubleValue()<0 ) {
            errorString += String.format( "Value of %s must be Positive. ", fieldName );
        }

        if( !irrational && fieldValue.doubleValue() != fieldValue.intValue() ) {
            errorString +=  String.format( "Value of %s is not a Whole Number. ", fieldName );
        }
        return errorString;
    }
}