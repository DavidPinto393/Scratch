package com.scratch.service;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReaderHeaderAware;
import com.scratch.models.StatusResponse;
import com.scratch.validators.dataset.CSVFileValidator;
import com.scratch.validators.field.FieldValidator;
import com.scratch.validators.field.ListValidator;
import com.scratch.validators.field.StringValidator;
import com.scratch.validators.field.NumberValidator;
import com.scratch.validators.models.ValidatorResponse;

import lombok.SneakyThrows;

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
        validators.put("SID",StringValidator.builder().fieldName("SID").mandatory(true).fixedLength(7).alphaNumeric(true).build());
        validators.put("TYPE", ListValidator.builder().fieldName("TYPE").mandatory(true).isString(true).validStrings(List.of("ADD","REMOVE")).build());
        validators.put("Field1",StringValidator.builder().fieldName("Field1").mandatory(true).minLength(5).build());
        validators.put("Field2",StringValidator.builder().fieldName("Field2").mandatory(false).maxLength(6).build());
        validators.put("New Value",NumberValidator.builder().fieldName("New Value").mandatory(true).positive(true).irrational(false).build());
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

    public String uploadFile(String user,MultipartFile file){
        String uploadKey=user+System.currentTimeMillis();

        try{
            if(!file.getOriginalFilename().endsWith(".csv")){
                return "File is not a CSV. Please ensure you are using the template provided here.";
            }
            file.transferTo(new File(ROOT+ACTIVE + uploadKey+".csv"));
        }
        catch(Exception e){
            e.printStackTrace();
            return "File is Invalid. Please ensure you are using the template provided here.";
        }
        return uploadKey;
    }

    @SneakyThrows 
    public StatusResponse getFileStatus(String uploadKey){
        
        String status;
        
        if(new File(ROOT+ACTIVE+uploadKey+".csv").exists()) {
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            return StatusResponse.builder()
            .status("Pending Validation. ETA: "+(10-(minute%10))+" minutes")
            .build();
        }
        
        if(!new File(ROOT+ERROR+"ERROR_"+uploadKey+".csv").exists()){
            return StatusResponse.builder()
            .status("Upload Key does not exist")
            .build();
        }
        else{
            try (CSVReaderHeaderAware  reader = new CSVReaderHeaderAware(new FileReader(ROOT+ERROR+"ERROR_"+uploadKey+".csv"));){
                if(reader.readMap().size()==0){
                    status="Validation Failed";
                }
                else{
                    status="Success";
                }
            }
        }
        return StatusResponse.builder()
            .status(status)
            .outputUri(new URI(ROOT+OUTPUT+"OUTPUT_"+uploadKey+".csv"))
            .errorUri(new URI(ROOT+ERROR+"ERROR_"+uploadKey+".csv"))
            .build();
    }
}
