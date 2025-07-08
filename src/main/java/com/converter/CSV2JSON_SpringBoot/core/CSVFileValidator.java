package com.converter.CSV2JSON_SpringBoot.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;

public class CSVFileValidator {

    public boolean isValidCSVFile(MultipartFile file) {
        String fileName = file.getName();
        //Path filePath = Path.of(fileName);

        if(!file.isEmpty())
            if(file.getContentType().contains("text")) {
                return isCSV(file);
            }

        //else throw new IOException();

        return false;
    }

    private boolean isCSV(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getName());
        return extension.contains(".csv");
    }
}
