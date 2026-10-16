package com.scratch.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import com.scratch.validators.dataset.CSVFileValidator;
import com.scratch.validators.field.FieldValidator;
import com.scratch.validators.field.ListValidator;
import com.scratch.validators.field.StringValidator;
import com.scratch.validators.field.NumberValidator;
import com.scratch.validators.models.ValidatorResponse;

@Service 
public class CAndOCSVValidationService {

    private static final String ROOT ="/workspaces/CandO/";
    private static final String ACTIVE ="active/";
    private static final String ARCHIVE ="archive/";
    private static final String OUTPUT ="output/";
    private static final String ERROR ="error/";

    private HashMap<String,FieldValidator> validators = new HashMap<>();

    private String[] headers = { "SID","TYPE","Field1","Field2","New Value" };

    public CAndOCSVValidationService(){
        validators.put("SID",StringValidator.builder().mandatory(true).fixedLength(7).alphaNumeric(true).build());
        validators.put("TYPE", ListValidator.builder().mandatory(true).validStrings(List.of("ADD","REMOVE")).build());
        validators.put("Field1",StringValidator.builder().mandatory(true).minLength(5).build());
        validators.put("Field2",StringValidator.builder().mandatory(false).maxLength(6).build());
        validators.put("New Value",NumberValidator.builder().mandatory(true).positive(true).irrational(false).build());
    }

    public void validateSingle(String file){

        CSVFileValidator csvFileValidator = CSVFileValidator.builder()
            .fileName(file)
            .inputPath(ROOT+ACTIVE)
            .outputPath(ROOT+OUTPUT)
            .errorPath(ROOT+ERROR)
            .headers(headers)
            .validators(validators)
            .build();
        ValidatorResponse response = csvFileValidator.validate();
        if(response.getErrorCount()>0) {
            System.out.println(String.join("\n",response.getErrorDetails()));
        }
        else{
            //TODO Link to publish module when available
        }
        File fileToMove = new File(ROOT+ACTIVE+file);
        fileToMove.renameTo(new File(ROOT+ARCHIVE+file));
    }
    
    public void validateBatch(List<String> files){
        files.forEach(file->validateSingle(file));
    }
    public void validateAllPending(){
        File directory = new File(ROOT+ACTIVE);
        File[] files = directory.listFiles();
        for (File file : files) {
            validateSingle(file.getName());
        }
    }
}
