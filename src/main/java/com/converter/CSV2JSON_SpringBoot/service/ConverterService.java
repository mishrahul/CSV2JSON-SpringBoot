package com.converter.CSV2JSON_SpringBoot.service;

import com.converter.CSV2JSON_SpringBoot.core.CSVFileValidator;
import com.converter.CSV2JSON_SpringBoot.core.DelimiterDetector;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class ConverterService {

    @Autowired
    CSVFileValidator validator;

    @Autowired
    DelimiterDetector detector;


    public String convert(boolean pretty, MultipartFile file) throws IOException {


        Path tempInputFile = Files.createTempFile("input", ".csv");
        file.transferTo(tempInputFile.toFile());

        Path tempOutputFile = Files.createTempFile("output", ".json");



        String json;

        boolean isValid = validator.isValidCSVFile(tempInputFile);

        if(isValid) {
            char delimiter = detector.detect(tempInputFile);
            if(delimiter == 'x') throw new IllegalArgumentException();


            buildJSON(tempInputFile, tempOutputFile, pretty, delimiter);

            json = Files.readString(tempOutputFile);

            Files.delete(tempInputFile);
            Files.delete(tempOutputFile);

            return json;



        } else {
         //   System.out.println(isValid);
            throw new RuntimeException("The input file is not a valid .csv file");
        }




    }

    private void buildJSON(Path tempInputFile, Path tempOutputFile, boolean pretty, char delimiter) throws IOException {


        BufferedReader reader = Files.newBufferedReader(tempInputFile);
        JsonGenerator output = new JsonFactory().createGenerator(Files.newBufferedWriter(tempOutputFile));

        if (pretty) output.useDefaultPrettyPrinter();


        CSVParser parser = CSVFormat.DEFAULT
                                    .builder()
                                    .setDelimiter(delimiter)
                                    .setHeader()
                                    .setSkipHeaderRecord(true)
                                    .build()
                                    .parse(reader);

        output.writeStartArray();
        for (CSVRecord rec : parser) {
            output.writeStartObject();
            for (Map.Entry<String, String> e : rec.toMap().entrySet()) {
                output.writeStringField(e.getKey(), e.getValue());
            }
            output.writeEndObject();
        }
        output.writeEndArray();
        reader.close();
        output.close();
    }
}
