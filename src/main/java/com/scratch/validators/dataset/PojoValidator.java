package com.scratch.validators.dataset;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.scratch.validators.field.FieldValidator;
import com.scratch.validators.models.ValidatorResponse;

import lombok.Builder;
import lombok.SneakyThrows;

@Builder 
public class PojoValidator<T,E extends T> implements DatasetValidator {

    private Class<T> pojoClass;
    private Class<E> errorClass;

    private List<T> dataset;

    private HashMap<String,FieldValidator> validators;

    @Override
    @SneakyThrows 
    public ValidatorResponse validate() {
        
        AtomicInteger recordCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<T> validRecords = new ArrayList<>();
        List<E> invalidRecords = new ArrayList<>();
        List<String> errorDetails = new ArrayList<>();
        dataset.forEach(data->{
            recordCount.incrementAndGet();
            String errors = "";
            for( Field f: pojoClass.getDeclaredFields()){
                String fieldName = f.getName();
                
                FieldValidator validator = validators.get(fieldName);
                if( validator != null ) {
                    try {
                        Method getter = pojoClass.getMethod("get"+Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), new Class[] {});
                        Object value = getter.invoke(data);
                        String errorMessage = validator.validate(value);
                        if( !errorMessage.isEmpty() ) {                            
                            errors += errorMessage;                            
                        }
                    }
                    catch(NoSuchMethodException nsm) {
                        throw new UnsupportedOperationException("The POJO class "+pojoClass.getName()+" does not have a Getter method for "+fieldName+". Please implement the Getter method for "+fieldName+" in the POJO class.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
               }
            }
            if( !"".equals(errors) ){
                errorCount.incrementAndGet();
                errorDetails.add(String.format("Record %d: %s", recordCount.get(), errors));
                try{
                    Method toErrorMethod = pojoClass.getDeclaredMethod("toError", new Class[] {});
                    @SuppressWarnings("unchecked")
                    E invalidRecord = (E) toErrorMethod.invoke(data);
                    Method setErrorsMethod = errorClass.getDeclaredMethod("setErrors", String.class);
                    setErrorsMethod.invoke(invalidRecord, errors);
                    invalidRecords.add(invalidRecord);
                } 
                catch(NoSuchMethodException nsm) {
                    throw new UnsupportedOperationException("The POJO class "+pojoClass.getName()+" does not have a toError() method. Please implement the toError() method in the POJO class to convert it to the error class.");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error creating invalid record: "+e.getMessage());
                }
            }
            else {
                validRecords.add(data);
            }            
        });
        return ValidatorResponse.builder()
                    .errorCount(errorCount.get())
                    .errorDetails(errorDetails)
                    .details(Map.of("validRecords", validRecords,
                                 "invalidRecords", invalidRecords))
                    .build();
    }
    
}
