package com.converter.CSV2JSON_SpringBoot.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CSVFileValidator {

    public boolean isValidCSVFile(Path tempFile) {
        String fileName = tempFile.toString();


        if(Files.exists(tempFile)) {
                return isCSV(fileName);
        }


        return false; //else throw new IOException();

    }

    private boolean isCSV(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        return extension.contains("csv");
    }
}
