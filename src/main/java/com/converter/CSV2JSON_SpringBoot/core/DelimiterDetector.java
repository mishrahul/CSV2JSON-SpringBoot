package com.converter.CSV2JSON_SpringBoot.core;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DelimiterDetector {

    public char detect(Path tempFile) throws IOException {

        char illegalDelimiter = 'x';
        BufferedReader reader = Files.newBufferedReader(tempFile);

        String line = reader.readLine();
        if(!(line==null)) {
            if (line.contains(";")) return  ';';
            else if (line.contains("|")) return '|';
            else if (line.contains("\t")) return '\t';
            else if (line.contains(",")) return ',';
        }

        return illegalDelimiter;
    }
}
