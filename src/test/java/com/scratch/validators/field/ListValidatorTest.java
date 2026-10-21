package com.scratch.validators.field;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ListValidatorTest {
    private ListValidator listValidator;

    //Generic String validation tests
    @ParameterizedTest
    @CsvSource({
        "ADD,''",
        "REMOVE,''",
        "'','Value of TestField is blank.'",
        "INVALID,'Value of TestField is not in the permitted List( ADD, REMOVE ). '"
    })
    void validateString(String input,String expectedMessage) {
        listValidator = ListValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .validStrings(List.of("ADD","REMOVE"))
            .isString(true)
            .build();

        String result = listValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Generic Integer validation tests
    @ParameterizedTest
    @CsvSource({
        "1,''",
        "2,''",
        ",'Value of TestField is blank.'",
        "3,'Value of TestField is not in the permitted List( 1, 2 ). '"
    })
    void validateInteger(Integer input,String expectedMessage) {
        listValidator = ListValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .validIntegers(List.of(1, 2))
            .isString(false)
            .build();

        String result = listValidator.validate(input);
        assertEquals(expectedMessage, result, "Unexpected validation result.");
    }

    //Edge case tests
    //Test to ensure expected blanks pass validation when mandatory is false
    @Test 
    void validateNotMandatory() {
        listValidator = ListValidator.builder()
            .mandatory(false)
            .fieldName("TestField")
            .validStrings(List.of("ADD","REMOVE"))
            .isString(true)
            .build();

        String result = listValidator.validate(null);
        assertEquals("", result, "Unexpected validation result.");

        result = listValidator.validate("");
        assertEquals("", result, "Unexpected validation result.");
    }

    //Test to ensure that the validator correctly identifies incorrect types
    @Test 
    void validateIncorrectTypes() {
        listValidator = ListValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .validStrings(List.of("ADD","REMOVE"))
            .isString(true)
            .build();
        String result = listValidator.validate(Integer.valueOf(1));
        assertEquals("Value of TestField is not a String.", result, "Unexpected validation result.");

        listValidator = ListValidator.builder()
            .mandatory(true)
            .fieldName("TestField")
            .validIntegers(List.of(1, 2))
            .isString(false)
            .build();
        result = listValidator.validate("Test");
        assertEquals("Value of TestField is not a Number.", result, "Unexpected validation result.");
    }
}
