package com.scratch.validators.field;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class StringValidatorTest {
    private StringValidator stringValidator;

    //Tests for Min and Max Length
    @ParameterizedTest
    @CsvSource({
        "ValidString,10,20,''",
        "Short,10,20,'Value of TestField does not meet minimum length of 10. '",
        "ThisStringIsWayTooLongToBeValid,10,20,'Value of TestField exceeds maximum length of 20. '",
        "'',10,20,'Value of TestField is blank.'"
    })
    void validateStringLength(String input,int minLength,int maxLength,String expectedMessage) {
        stringValidator = StringValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .minLength(minLength)
            .maxLength(maxLength)
            .build();
        String result = stringValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Test for Fixed Length AlphaNumeric String
    @ParameterizedTest
    @CsvSource({
        "InvalidString,10,true,'Value of TestField does not match length of 10. '",
        "InvalidString!,10,true,'Value of TestField does not match length of 10. Value of TestField is not Alphanumeric. '",
        "ValidValue,10,true,''"
    })
    void validateFixedLengthAlphaNumeric(String input,int fixedLength,boolean alphaNumeric ,String expectedMessage) {
        stringValidator = StringValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .fixedLength(fixedLength)
            .alphaNumeric(alphaNumeric)
            .build();
        String result = stringValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Test for Email Validation
    @ParameterizedTest
    @CsvSource({    
        "invalidemail.com,true,'Value of TestField is not a valid email address. '",
        "valid.email@example.com,true,''"
    })
    void validateEmail(String input, boolean mandatory, String expectedMessage) {
        stringValidator = StringValidator.builder()
            .mandatory(mandatory)
            .fieldName("TestField")
            .email(true)
            .build();
        String result = stringValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Test for Non-String Input
    @Test 
    void validateNonStringInput() {
        stringValidator = StringValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .build();
        String result = stringValidator.validate(Integer.valueOf(12345));
        assertEquals("Value of TestField is not a String.", result, "Unexpected validation result.");
    }

    //Test for Optional Field with Null Input
    @ParameterizedTest
    @CsvSource({
        "'',false,''"
    })
    void validateOptionalFieldWithNullInput(String input, boolean mandatory, String expectedMessage) {
        stringValidator = StringValidator.builder()
            .mandatory(mandatory)
            .fieldName("TestField")
            .build();
        String result = stringValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Test for Custom Regex Validation
    @ParameterizedTest
    @CsvSource({
        "abc123,true,'Value of TestField does not match the required pattern ^[a-zA-Z]+$. '",
        "abcXYZ,true,''"
    })
    void validateCustomRegex(String input, boolean mandatory, String expectedMessage) {
        stringValidator = StringValidator.builder()
            .mandatory(mandatory)
            .fieldName("TestField")
            .regex("^[a-zA-Z]+$")
            .build();
        String result = stringValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }
}
