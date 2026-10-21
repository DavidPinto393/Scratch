package com.scratch.validators.field;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class NumberValidatorTest {
    private NumberValidator numberValidator;

    //Generic Number validation tests
    @ParameterizedTest
    @CsvSource({
        "5,''",
        "10,''",
        "4,'Value of TestField does not meet minimum value of 5. '",
        "31,'Value of TestField exceeds maximum length of 30. '",
        "-1,'Value of TestField does not meet minimum value of 5. Value of TestField must be Positive. '",
        ",'Value of TestField is blank.'"
    })
    void validateInteger(Integer input,String expectedMessage) {
        numberValidator = NumberValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .positive(true)
            .irrational(false)
            .min(5)
            .max(30)
            .build();

        String result = numberValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Tests for Valid Irrational Numbers
    @ParameterizedTest
    @CsvSource({    
        "6.14,''",
        "21.71,''",
        "11.41,''",
        "20,''",
        "4.0,'Value of TestField does not meet minimum value of 5. '",
        "31.0,'Value of TestField exceeds maximum length of 30. '",
        "-1.0,'Value of TestField does not meet minimum value of 5. Value of TestField must be Positive. '",
        ",'Value of TestField is blank.'"
    })
    void validateIrrational(Double input,String expectedMessage) {
        numberValidator = NumberValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .positive(true)
            .irrational(true)
            .min(5)
            .max(30)
            .build();

        String result = numberValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Tests for no validation criteria
    @ParameterizedTest
    @CsvSource({
        "5,''",
        "10,''",
        "4,''",
        "31,''",
        "-1,''",
        ",''"
    })
    void validateNoCriteria(Integer input,String expectedMessage) {
        numberValidator = NumberValidator.builder()
            .mandatory(false)
            .fieldName("TestField")
            .build();
        String result = numberValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Tests for Types being Mismatched
    @Test 
    void validateIncorrectTypes() {
        numberValidator = NumberValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .positive(true)
            .irrational(false)
            .min(5)
            .max(30)
            .build();

        //Non-numeric String input
        String result = numberValidator.validate("StringValue");
        assertEquals("Value of TestField is not a Number. ", result, "Unexpected validation result.");

        //Numeric String input
        result = numberValidator.validate("10");
        assertEquals("", result, "Unexpected validation result.");

        //Non-numeric Object input
        result = numberValidator.validate(new Object());
        assertEquals("Value of TestField is not a Number. ", result, "Unexpected validation result.");

        //Double input that is irrational
        result = numberValidator.validate(Double.valueOf("8.14159"));
        assertEquals("Value of TestField is not a Whole Number. ", result, "Unexpected validation result.");
    }
}
