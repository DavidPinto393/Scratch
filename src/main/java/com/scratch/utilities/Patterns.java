package com.scratch.utilities;

import java.util.regex.Pattern;

public interface Patterns{
   
    String ALPHANUMERIC_REGEX = "^[a-zA-Z0-9]*$";
    
    Pattern ALPHANUMERIC_PATTERN = Pattern.compile(ALPHANUMERIC_REGEX);

    String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
}