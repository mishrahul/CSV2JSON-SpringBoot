package com.converter.CSV2JSON_SpringBoot.service;

import com.converter.CSV2JSON_SpringBoot.core.Builder;
import com.converter.CSV2JSON_SpringBoot.core.DelimiterDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

@Service
public class ConverterService {


    @Autowired
    DelimiterDetector detector;

    @Autowired
    Builder converter;



    public ConverterService(DelimiterDetector detector, Builder converter) {
        this.detector = detector;
        this.converter = converter;
    }


    public String convert(boolean pretty, MultipartFile file) throws IOException {

        if(file.getContentType()==null) throw  new NoSuchFileException("No input file attached");


        if(!(file.getContentType().equals("text/csv"))) throw new IllegalArgumentException("Attached file is not a valid CSV file");


        if(file.isEmpty()) throw new IllegalArgumentException("Attached file might be empty");

        Path tempInputFile = Files.createTempFile("input", ".csv");
        file.transferTo(tempInputFile.toFile());
        Path tempOutputFile = Files.createTempFile("output", ".json");

        String json;

            char delimiter = detector.detect(tempInputFile);
            if(delimiter == 'x') throw new IllegalArgumentException("Invalid delimiter found in the input file");

            converter.buildJSON(tempInputFile, tempOutputFile, pretty, delimiter);

            json = Files.readString(tempOutputFile);

            Files.delete(tempInputFile);
            Files.delete(tempOutputFile);

            return json;
    }

}
