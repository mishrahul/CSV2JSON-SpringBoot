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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController("/")
public class ConverterController {

    @Autowired
    ConverterService service;

    public ConverterController(ConverterService service) {
        this.service = service;
    }


    @PostMapping(value = "/convert", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> convertCSV(@RequestParam(value = "pretty", defaultValue = "true") boolean pretty,
                                                            @RequestParam("file") MultipartFile file) throws IOException {

        Path tempOutput = service.convert(pretty, file);

        StreamingResponseBody responseStrema = outputStream -> {

            try (InputStream inputStream = Files.newInputStream(tempOutput)) {
                byte[] buffer = new byte[8192];
                int bytesRead;

                while((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            finally {
                Files.deleteIfExists(tempOutput);
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON).body(responseStrema);

    }
}
