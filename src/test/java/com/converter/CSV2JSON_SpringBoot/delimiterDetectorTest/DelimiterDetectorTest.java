package com.converter.CSV2JSON_SpringBoot.delimiterDetectorTest;

import com.converter.CSV2JSON_SpringBoot.core.DelimiterDetector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class DelimiterDetectorTest {

    private DelimiterDetector detector;

    @TempDir
    Path tempDir;

    private Path tempFile;

    @BeforeEach
    void setUp() {
        detector = new DelimiterDetector();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    private void createTempFileWithContent(String content) throws IOException {
        tempFile = tempDir.resolve("test.csv");
        Files.writeString(tempFile, content);
    }

    @Test
    void testDetect_semicolonDelimiter() throws IOException {
        createTempFileWithContent("Name;Age;City\nJohn;30;New York");
        char delimiter = detector.detect(tempFile);
        assertEquals(';', delimiter);
    }

    @Test
    void testDetect_pipeDelimiter() throws IOException {
        createTempFileWithContent("Name|Age|City\nJane|25|Los Angeles");
        char delimiter = detector.detect(tempFile);
        assertEquals('|', delimiter);
    }

    @Test
    void testDetect_tabDelimiter() throws IOException {
        createTempFileWithContent("Name\tAge\tCity\nAlice\t35\tChicago");
        char delimiter = detector.detect(tempFile);
        assertEquals('\t', delimiter);
    }

    @Test
    void testDetect_commaDelimiter() throws IOException {
        createTempFileWithContent("Name,Age,City\nBob,40,Houston");
        char delimiter = detector.detect(tempFile);
        assertEquals(',', delimiter);
    }

    @Test
    void testDetect_noDelimiter() throws IOException {
        createTempFileWithContent("NameAgeCity\nEve50Miami");
        char delimiter = detector.detect(tempFile);
        assertEquals('x', delimiter);
    }

    @Test
    void testDetect_emptyFile() throws IOException {
        createTempFileWithContent("");
        char delimiter = detector.detect(tempFile);
        assertEquals('x', delimiter);
    }

    @Test
    void testDetect_singleLineNoDelimiter() throws IOException {
        createTempFileWithContent("JustASingleLineOfText");
        char delimiter = detector.detect(tempFile);
        assertEquals('x', delimiter);
    }

//    @Test
//    void testDetect_ioException() throws IOException {
//        createTempFileWithContent("Header,Value");
//        Files.delete(tempFile);
//
//        IOException thrown = assertThrows(IOException.class, () -> {
//            detector.detect(tempFile);
//        });
//
//        assertTrue(thrown.getMessage().contains("The system cannot find the file specified") || thrown.getMessage().contains("No such file or directory"));
//    }

    @Test
    void testDetect_multipleDelimiters_precedence() throws IOException {
        createTempFileWithContent("Name;Age,City\nJohn;30,New York");
        char delimiter = detector.detect(tempFile);
        assertEquals(';', delimiter);
    }
}
