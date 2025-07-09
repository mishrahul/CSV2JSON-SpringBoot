package com.converter.CSV2JSON_SpringBoot.controller;

import com.converter.CSV2JSON_SpringBoot.service.ConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController("/")
public class ConverterController {

    @Autowired
    ConverterService service;


    @PostMapping("/convert")
    public ResponseEntity<String> convertCSV(@RequestParam(value = "pretty", defaultValue = "true") boolean pretty,
                                            @RequestParam("file") MultipartFile file) throws IOException {


        String json = service.convert(pretty, file);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(json);

    }
}
