package com.scratch.validators.dataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scratch.validators.field.FieldValidator;
import com.scratch.validators.field.NumberValidator;
import com.scratch.validators.field.StringValidator;
import com.scratch.validators.field.ListValidator;
import com.scratch.validators.utils.*;

public class PojoValidatorTest {
    private PojoValidator<TestPojo, TestErrorPojo> pojoValidator;

    private HashMap<String,FieldValidator> validators = new HashMap<>();

    @BeforeEach 
    public void setup() {
        validators.put("name", StringValidator.builder().fieldName("name").mandatory(true).minLength(3).build());
        validators.put("age", NumberValidator.builder().fieldName("age").mandatory(true).min(1).build());
        validators.put("email", StringValidator.builder().fieldName("email").mandatory(true).email(true).build());
        validators.put("location", ListValidator.builder().fieldName("location").mandatory(true).validStrings(List.of("ABC","XYZ")).build());

    }

    //Test for list of 3 valid Pojos
    @SuppressWarnings("unchecked")
    @Test
    public void testValidPojos() {
        List<TestPojo> dataset = List.of(
            new TestPojo("John Doe", 30, "john.doe@example.com", "ABC"),
            new TestPojo("Jane Smith", 25, "jane.smith@example.com", "XYZ"),
            new TestPojo("Bob Johnson", 35, "bob.johnson@example.com", "ABC")
        );
        
        pojoValidator = PojoValidator.<TestPojo, TestErrorPojo>builder()
            .pojoClass(TestPojo.class)
            .errorClass(TestErrorPojo.class)
            .validators(validators)
            .dataset(dataset)
            .build();
        var response = pojoValidator.validate();
        assert(response.getErrorCount() == 0);
        assert(response.getErrorDetails().isEmpty());
        assertEquals(response.getDetails().get("validRecords"), dataset);
        assert(((List<TestErrorPojo>)response.getDetails().get("invalidRecords")).size() == 0);
    }

    //Test list of 3 Pojos with 2 invalid Pojos
    @SuppressWarnings("unchecked")
    @Test
    public void testInvalidPojos() {
        List<TestPojo> dataset = List.of(
            new TestPojo("John Doe", 30, "john.doe@example.com", "ABC"),
            new TestPojo("Jane Smith", -5, "jane.smith@example.com", "XYZ"),
            new TestPojo("Bob Johnson", 35, "bob.johnson@example.com", "DEF")
        );
        validators.remove("email"); // Remove email validator to test logic branch for unvalidated fields
        pojoValidator = PojoValidator.<TestPojo, TestErrorPojo>builder()
            .pojoClass(TestPojo.class)
            .errorClass(TestErrorPojo.class)
            .validators(validators)
            .dataset(dataset)
            .build();
        var response = pojoValidator.validate();
        assert(response.getErrorCount() == 2);
        assert(!response.getErrorDetails().isEmpty());
        assert(((List<TestErrorPojo>)response.getDetails().get("invalidRecords")).size() == 2);
    }

    //Test Exception when no toError method is present in the Pojo class
    @Test
    public void testPojoWithoutToErrorMethod() {
        List<TestPojoWithoutToError> dataset = List.of(
            new TestPojoWithoutToError("John Doe", -30, "john.doe@example.com", "ABC")
        );
        
        PojoValidator<TestPojoWithoutToError, TestErrorPojoWithoutToError> pojoValidator_invalidPojo = PojoValidator.<TestPojoWithoutToError, TestErrorPojoWithoutToError>builder()
            .pojoClass(TestPojoWithoutToError.class)
            .errorClass(TestErrorPojoWithoutToError.class)
            .validators(validators)
            .dataset(dataset)
            .build();
        assertThrows(UnsupportedOperationException.class, () -> pojoValidator_invalidPojo.validate(),
            "The POJO class TestPojoWithoutToError does not have a toError() method. Please implement the toError() method in the POJO class to convert it to the error class.");
    }

    //Test Exception when no Getter method is present in the Pojo class
    @Test
    public void testPojoWithoutGetterMethod() {
        List<TestPojoWithoutGetter> dataset = List.of(
            new TestPojoWithoutGetter("John Doe", -30, "john.doe@example.com", "ABC")
        );
        
        PojoValidator<TestPojoWithoutGetter, TestErrorPojoWithoutGetter> pojoValidator_invalidPojo = PojoValidator.<TestPojoWithoutGetter, TestErrorPojoWithoutGetter>builder()
            .pojoClass(TestPojoWithoutGetter.class)
            .errorClass(TestErrorPojoWithoutGetter.class)
            .validators(validators)
            .dataset(dataset)
            .build();
        assertThrows(UnsupportedOperationException.class, () -> pojoValidator_invalidPojo.validate(),
            "The POJO class TestPojoWithoutGetter does not have a Getter method for name. Please implement the Getter method for name in the POJO class.");
    }

}