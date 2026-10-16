package com.scratch.validators.models;

import lombok.Builder;
import lombok.Getter;

@Getter 
@Builder 
public class MandatoryValidation {
    private boolean skip;
    private boolean valid;
}
