package com.converter.CSV2JSON_SpringBoot.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component
public class Builder {

    public void buildJSON (Path tempInputFile, Path tempOutputFile, boolean pretty, char delimiter) throws IOException {

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

