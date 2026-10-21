package com.scratch.validators.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class TestPojo {
    String name;
    Integer age;
    String email;
    String location;

    public TestErrorPojo toError() {
        TestErrorPojo errorPojo = new TestErrorPojo();
        errorPojo.setName(this.name);
        errorPojo.setAge(this.age);
        errorPojo.setEmail(this.email);
        errorPojo.setLocation(this.location);
        return errorPojo;
    }
}
