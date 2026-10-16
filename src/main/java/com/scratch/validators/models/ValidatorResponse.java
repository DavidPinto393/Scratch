package com.scratch.validators.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data 
@Builder 
public class ValidatorResponse {
    int errorCount;
    List<String> errorDetails;
    Map<String,Object> details;
}
