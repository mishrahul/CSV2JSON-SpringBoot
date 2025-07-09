package com.converter.CSV2JSON_SpringBoot.serviceTest;

import com.converter.CSV2JSON_SpringBoot.core.Builder;
import com.converter.CSV2JSON_SpringBoot.core.DelimiterDetector;
import com.converter.CSV2JSON_SpringBoot.service.ConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConverterServiceTest {

    @Mock
    private DelimiterDetector detector;

    @Mock
    private Builder builder;

    @InjectMocks
    private ConverterService service;
//
//    @TempDir
//    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void convert_successful() throws Exception {
        String csv = "Name,Age\nJohn,30";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes());

        when(detector.detect(any())).thenReturn(',');
        doAnswer(invocation -> {
            Path out = invocation.getArgument(1);
            Files.writeString(out, "[{\"Name\":\"John\",\"Age\":\"30\"}]");
            return null;
        }).when(builder).buildJSON(any(), any(), eq(false), eq(','));

        String result = service.convert(false, file);

        assertEquals("[{\"Name\":\"John\",\"Age\":\"30\"}]", result);
        verify(detector).detect(any());
        verify(builder).buildJSON(any(), any(), eq(false), eq(','));
    }

    @Test
    void convert_prettyPrint() throws Exception {
        String csv = "Name,Age\nJane,25";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes());

        when(detector.detect(any())).thenReturn(',');
        doAnswer(invocation -> {
            Path out = invocation.getArgument(1);
            Files.writeString(out, "[\n  {\"Name\": \"Jane\", \"Age\": \"25\"}\n]");
            return null;
        }).when(builder).buildJSON(any(), any(), eq(true), eq(','));

        String result = service.convert(true, file);

        assertTrue(result.contains("Jane"));
        verify(detector).detect(any());
        verify(builder).buildJSON(any(), any(), eq(true), eq(','));
    }

    @Test
    void convert_noFile_throws() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", null, new byte[0]);
        Exception ex = assertThrows(NoSuchFileException.class, () -> service.convert(true, file));
        assertTrue(ex.getMessage().contains("No input file attached"));
    }

    @Test
    void convert_notCsvFile_throws() {
        MockMultipartFile file = new MockMultipartFile("file", "test.html", "text/html", "<html></html>".getBytes());
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.convert(true, file));
        assertTrue(ex.getMessage().contains("not a valid CSV"));
    }

    @Test
    void convert_emptyFile_throws() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", new byte[0]);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.convert(true, file));
        assertTrue(ex.getMessage().contains("empty"));
    }

    @Test
    void convert_invalidDelimiter_throws() throws Exception {
        String csv = "Name.Age\nJohn.30";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes());
        when(detector.detect(any())).thenReturn('x');
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.convert(true, file));
        assertTrue(ex.getMessage().contains("Invalid delimiter"));
    }

    @Test
    void convert_builderThrowsIOException() throws Exception {
        String csv = "Name,Age\nJohn,30";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes());
        when(detector.detect(any())).thenReturn(',');
        doThrow(new IOException("builder error")).when(builder).buildJSON(any(), any(), anyBoolean(), anyChar());
        Exception ex = assertThrows(IOException.class, () -> service.convert(true, file));
        assertTrue(ex.getMessage().contains("builder error"));
    }
}
