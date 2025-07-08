package com.converter.CSV2JSON_SpringBoot.core;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DelimiterDetector {

    public char detect(MultipartFile file) throws IOException {

        char illegalDelimiter = 'x';
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

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
