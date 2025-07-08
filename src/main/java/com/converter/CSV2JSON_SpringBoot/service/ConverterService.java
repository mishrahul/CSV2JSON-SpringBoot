package com.converter.CSV2JSON_SpringBoot.service;

import com.converter.CSV2JSON_SpringBoot.core.CSVFileValidator;
import com.converter.CSV2JSON_SpringBoot.core.DelimiterDetector;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class ConverterService {

    @Autowired
    CSVFileValidator validator;

    @Autowired
    DelimiterDetector detector;


    public void convert(boolean pretty, MultipartFile file) throws IOException {

        boolean isValid = validator.isValidCSVFile(file);

        //verify validity

        //define delimiter
        char delimiter = detector.detect(file);

        if(delimiter == 'x'); //handle error




    }

    private void buildJSON(MultipartFile file, boolean pretty, char delimiter) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        JsonGenerator output = new JsonFactory().createGenerator(OutputStream out, )

    }
}
