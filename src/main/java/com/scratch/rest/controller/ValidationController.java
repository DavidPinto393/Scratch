package com.scratch.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scratch.models.StatusResponse;
import com.scratch.service.CAndOCSVValidationService;


@RestController 
@RequestMapping("/") 
public class ValidationController {
    private final CAndOCSVValidationService cAndOCSVValidationService;

    ValidationController(CAndOCSVValidationService cAndOCSVValidationService) {
        this.cAndOCSVValidationService = cAndOCSVValidationService;
    }

    @PostMapping("/validate/{filename}")
    public void validateSingle(@PathVariable String filename){
        cAndOCSVValidationService.validateSingle(filename);
    }

    @GetMapping("checkStatus/{uploadKey}")
    public StatusResponse getFileStatus(@PathVariable String uploadKey){
        return cAndOCSVValidationService.getFileStatus(uploadKey);
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return cAndOCSVValidationService.uploadFile("dummy",file);
    }
}
