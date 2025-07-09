package com.converter.CSV2JSON_SpringBoot.builderTest;

import com.converter.CSV2JSON_SpringBoot.core.Builder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderTest {

    private Builder builder;

    @TempDir // JUnit 5 annotation to create a temporary directory
    Path tempDir;

    private Path tempInputFile;
    private Path tempOutputFile;

    @BeforeEach
    void setUp() throws IOException {
        builder = new Builder();
        tempInputFile = tempDir.resolve("input.csv");
        tempOutputFile = tempDir.resolve("output.json");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempInputFile);
        Files.deleteIfExists(tempOutputFile);
    }

    @Test
    void testBuildJSON_prettyTrue() throws IOException {
        String csvContent = "Name,Age\nJohn,30";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, true, ',');

        String jsonOutput = Files.readString(tempOutputFile);
        String expectedJson = "[ {\r\n  \"Name\" : \"John\",\r\n  \"Age\" : \"30\"\r\n} ]";
        assertEquals(expectedJson, jsonOutput);
    }

    @Test
    void testBuildJSON_prettyFalse() throws IOException {
        String csvContent = "Name,Age\nJane,25";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, false, ',');

        String jsonOutput = Files.readString(tempOutputFile);
        String expectedJson = "[{\"Name\":\"Jane\",\"Age\":\"25\"}]";
        assertEquals(expectedJson, jsonOutput);
    }

    @Test
    void testBuildJSON_withCommaDelimiter() throws IOException {
        String csvContent = "Header1,Header2\nValue1,Value2";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, false, ',');

        String jsonOutput = Files.readString(tempOutputFile);
        assertEquals("[{\"Header1\":\"Value1\",\"Header2\":\"Value2\"}]", jsonOutput);
    }

    @Test
    void testBuildJSON_withSemicolonDelimiter() throws IOException {
        String csvContent = "Header1;Header2\nValue1;Value2";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, false, ';');

        String jsonOutput = Files.readString(tempOutputFile);
        assertEquals("[{\"Header1\":\"Value1\",\"Header2\":\"Value2\"}]", jsonOutput);
    }

    @Test
    void testBuildJSON_withPipeDelimiter() throws IOException {
        String csvContent = "Header1|Header2\nValue1|Value2";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, false, '|');

        String jsonOutput = Files.readString(tempOutputFile);
        assertEquals("[{\"Header1\":\"Value1\",\"Header2\":\"Value2\"}]", jsonOutput);
    }

    @Test
    void testBuildJSON_withTabDelimiter() throws IOException {
        String csvContent = "Header1\tHeader2\nValue1\tValue2";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, false, '\t');

        String jsonOutput = Files.readString(tempOutputFile);
        assertEquals("[{\"Header1\":\"Value1\",\"Header2\":\"Value2\"}]", jsonOutput);
    }

    @Test
    void testBuildJSON_emptyCSV() throws IOException {
        String csvContent = "Header1,Header2";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, false, ',');

        String jsonOutput = Files.readString(tempOutputFile);
        assertEquals("[]", jsonOutput);
    }

    @Test
    void testBuildJSON_singleRecordCSV() throws IOException {
        String csvContent = "Name,Age,City\nAlice,30,New York";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, false, ',');

        String jsonOutput = Files.readString(tempOutputFile);
        assertEquals("[{\"Name\":\"Alice\",\"Age\":\"30\",\"City\":\"New York\"}]", jsonOutput);
    }

    @Test
    void testBuildJSON_multipleRecordsCSV() throws IOException {
        String csvContent = "Name,Age\nBob,25\nCharlie,35";
        Files.writeString(tempInputFile, csvContent);

        builder.buildJSON(tempInputFile, tempOutputFile, false, ',');

        String jsonOutput = Files.readString(tempOutputFile);
        assertEquals("[{\"Name\":\"Bob\",\"Age\":\"25\"},{\"Name\":\"Charlie\",\"Age\":\"35\"}]", jsonOutput);
    }

    //not working
//    @Test
//    void testBuildJSON_ioExceptionDuringRead() throws IOException {
//        Files.writeString(tempInputFile, "Header\nValue");
//        Files.delete(tempInputFile);
//
//        IOException thrown = assertThrows(IOException.class, () -> {
//            builder.buildJSON(tempInputFile, tempOutputFile, false, ',');
//        });
//
//        assertTrue(thrown.getMessage().contains("The system cannot find the file specified") || thrown.getMessage().contains("No such file or directory"));
//    }

}
