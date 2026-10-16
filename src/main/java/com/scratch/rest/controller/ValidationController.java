package com.scratch.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scratch.service.CAndOCSVValidationService;


@RestController 
@RequestMapping("/validate") 
public class ValidationController {
    @Autowired 
    private CAndOCSVValidationService cAndOCSVValidationService;

    @PostMapping("/{filename}")
    public void validateSingle(@PathVariable String filename){
        cAndOCSVValidationService.validateSingle(filename);
    }
}
