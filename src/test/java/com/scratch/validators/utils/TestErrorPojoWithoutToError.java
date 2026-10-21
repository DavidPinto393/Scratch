package com.scratch.validators.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class TestErrorPojoWithoutToError extends TestPojoWithoutToError {
    String errors;
}
