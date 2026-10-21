package com.scratch.validators.utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class TestPojoWithoutToError {
    String name;
    Integer age;
    String email;
    String location;
}
