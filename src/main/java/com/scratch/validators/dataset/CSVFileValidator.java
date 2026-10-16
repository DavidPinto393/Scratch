package com.scratch.validators.dataset;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVWriter;
import com.scratch.validators.models.ValidatorResponse;
import com.scratch.validators.field.FieldValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;

@Builder 
@Getter 
public class CSVFileValidator implements DatasetValidator{
    
    private String inputPath;
    private String outputPath;
    private String errorPath;

    private String fileName;

    private HashMap<String,FieldValidator> validators;

    private String[] headers;

    @Override 
    @SneakyThrows
    public ValidatorResponse validate(){
        
        if( !inputPath.endsWith("/") ) {
            inputPath += "/";
        }
        if( !outputPath.endsWith("/") ) {
            outputPath += "/";
        }
        if( !errorPath.endsWith("/") ) {
            errorPath += "/";
        }

        String inputFile = inputPath+fileName;
        String outputFile = outputPath+"OUTPUT_"+fileName;
        String errorFile = errorPath+"ERROR_"+fileName;
        int errorCount = 0;
        int recordCount = 0;
        List<String> errorDetails = new ArrayList<>();

        try (CSVReaderHeaderAware  reader = new CSVReaderHeaderAware(new FileReader(inputFile));
        CSVWriter errorWriter = new CSVWriter(new FileWriter(errorFile));
        CSVWriter validWriter = new CSVWriter(new FileWriter(outputFile));) {
            
            Map<String, String> row;
            validWriter.writeNext(headers);
            errorWriter.writeNext(ArrayUtils.add(headers,"Errors"));
            while ((row = reader.readMap()) != null) {
                recordCount++;
                String errors = "";
                List<String> orderedOutput = new ArrayList<>();
                for( String header:headers ){
                    FieldValidator validator = validators.get(header);
                    orderedOutput.add(row.get(header));
                    if(validator != null) {
                        String message = validator.validate(row.get(header));
                        if( !"".equals(message) ){
                            errors += message;
                            errorCount++;
                            errorDetails.add(String.format("Error(s) in Line %d: %s",recordCount,message));
                        }
                    }
                }
                if( !"".equals(errors) ){
                    errorCount++;
                    errorDetails.add(String.format("Error(s) in Line %d: %s",recordCount,errors));
                    orderedOutput.add(errors);
                    errorWriter.writeNext(orderedOutput.toArray(new String[orderedOutput.size()]));
                }
                else {
                    validWriter.writeNext(orderedOutput.toArray(new String[orderedOutput.size()]));
                }
            }            
        }

        return ValidatorResponse.builder()
                    .errorCount(errorCount)
                    .errorDetails(errorDetails)
                    .details(Map.of("outputFile",outputFile,"errorFile",errorFile))
                    .build();
    }
}
