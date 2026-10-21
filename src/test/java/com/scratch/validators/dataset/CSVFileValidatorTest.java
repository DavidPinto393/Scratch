package com.scratch.validators.dataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scratch.validators.field.FieldValidator;
import com.scratch.validators.field.ListValidator;
import com.scratch.validators.field.NumberValidator;
import com.scratch.validators.field.StringValidator;
import com.scratch.validators.models.ValidatorResponse;

public class CSVFileValidatorTest {
    private CSVFileValidator csvFileValidator;

    private HashMap<String,FieldValidator> validators = new HashMap<>();

    private String[] headers = { "SID","TYPE","Field1","Field2","New Value" };

    @BeforeEach 
    void setup() {
        validators.put("SID",StringValidator.builder().fieldName("SID").mandatory(true).fixedLength(7).alphaNumeric(true).build());
        validators.put("TYPE", ListValidator.builder().fieldName("TYPE").mandatory(true).isString(true).validStrings(List.of("ADD","REMOVE")).build());
        validators.put("Field1",StringValidator.builder().fieldName("Field1").mandatory(true).minLength(5).build());
        validators.put("Field2",StringValidator.builder().fieldName("Field2").mandatory(false).maxLength(6).build());
        validators.put("New Value",NumberValidator.builder().fieldName("New Value").mandatory(true).positive(true).irrational(false).build());
    }

    @Test
    public void testValidate_ValidFile() {
        csvFileValidator = CSVFileValidator.builder()
            .inputPath("src/test/resources")
            .outputPath("/tmp")
            .errorPath("/tmp")
            .fileName("validFile.csv")
            .headers(headers)
            .validators(validators)
            .build();

        ValidatorResponse actual = csvFileValidator.validate();
        ValidatorResponse expected = ValidatorResponse.builder()
            .errorCount(0)
            .errorDetails(List.of())
            .details(Map.of("outputFile", "/tmp/OUTPUT_validFile.csv", "errorFile", "/tmp/ERROR_validFile.csv"))
            .build();
        assertEquals(expected, actual, "Validation result does not match expected output.");
    }

    @Test
    public void testValidate_InvalidFile() {
        csvFileValidator = CSVFileValidator.builder()
            .inputPath("src/test/resources/")
            .outputPath("/tmp/")
            .errorPath("/tmp/")
            .fileName("invalidFile.csv")
            .headers(ArrayUtils.add(headers,"Dummy"))
            .validators(validators)
            .build();

        ValidatorResponse actual = csvFileValidator.validate();
        ValidatorResponse expected = ValidatorResponse.builder()
            .errorCount(5)
            .errorDetails(List.of(
                "Error(s) in Line 2: Value of SID is not Alphanumeric. ",
                "Error(s) in Line 3: Value of SID does not match length of 7. ",
                "Error(s) in Line 4: Value of TYPE is not in the permitted List( ADD, REMOVE ). ",
                "Error(s) in Line 5: Value of Field1 does not meet minimum length of 5. Value of Field2 exceeds maximum length of 6. ",
                "Error(s) in Line 6: Value of New Value must be Positive. "
            ))
            .details(Map.of("outputFile", "/tmp/OUTPUT_invalidFile.csv", "errorFile", "/tmp/ERROR_invalidFile.csv"))
            .build();
        assertEquals(expected, actual, "Validation result does not match expected output.");
        }
}
