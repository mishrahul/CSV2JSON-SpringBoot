package com.converter.CSV2JSON_SpringBoot.controllerTest;

import com.converter.CSV2JSON_SpringBoot.controller.ConverterController;
import com.converter.CSV2JSON_SpringBoot.controller.GlobalExceptionHandler;
import com.converter.CSV2JSON_SpringBoot.service.ConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ConverterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConverterService converterService;

    @InjectMocks
    private ConverterController converterController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(converterController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testConvertCSV_prettyTrue() throws Exception {
        String csvContent = "Name,Age\nJohn,30";
        String expectedJson = "[\n  {\n    \"Name\" : \"John\",\n    \"Age\" : \"30\"\n  }\n]";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, csvContent.getBytes());

        when(converterService.convert(eq(true), any(MultipartFile.class))).thenReturn(expectedJson);

        mockMvc.perform(multipart("/convert")
                        .file(file)
                        .param("pretty", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedJson));

        verify(converterService, times(1)).convert(eq(true), any(MultipartFile.class));
    }

    @Test
    void testConvertCSV_prettyFalse() throws Exception {
        String csvContent = "Name,Age\nJane,25";
        String expectedJson = "[{\"Name\":\"Jane\",\"Age\":\"25\"}]";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, csvContent.getBytes());

        when(converterService.convert(eq(false), any(MultipartFile.class))).thenReturn(expectedJson);

        mockMvc.perform(multipart("/convert")
                        .file(file)
                        .param("pretty", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedJson));

        verify(converterService, times(1)).convert(eq(false), any(MultipartFile.class));
    }

    @Test
    void testConvertCSV_defaultPretty() throws Exception {
        String csvContent = "Name,Age\nAlice,35";
        String expectedJson = "[\n  {\n    \"Name\" : \"Alice\",\n    \"Age\" : \"35\"\n  }\n]";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, csvContent.getBytes());

        when(converterService.convert(eq(true), any(MultipartFile.class))).thenReturn(expectedJson);

        mockMvc.perform(multipart("/convert")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedJson));

        verify(converterService, times(1)).convert(eq(true), any(MultipartFile.class));
    }

//    @Test
//    void testConvertCSV_noFileAttached() throws Exception {
//        when(converterService.convert(anyBoolean(), any(MultipartFile.class)))
//                .thenThrow(new NoSuchFileException("No input file attached"));
//
//        mockMvc.perform(multipart("/convert")
//                        .param("pretty", "true"))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("No input file attached"));
//
//        verify(converterService, times(1)).convert(eq(true), any(MultipartFile.class));
//    }


    @Test
    void testConvertCSV_notCsvFile() throws Exception {
        String htmlContent = "<html><body>Hello</body></html>";
        MockMultipartFile file = new MockMultipartFile("file", "test.html", MediaType.TEXT_HTML_VALUE, htmlContent.getBytes());

        when(converterService.convert(anyBoolean(), any(MultipartFile.class)))
                .thenThrow(new IllegalArgumentException("Attached file is not a valid CSV file"));

        mockMvc.perform(multipart("/convert")
                        .file(file)
                        .param("pretty", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Attached file is not a valid CSV file"));

        verify(converterService, times(1)).convert(eq(true), any(MultipartFile.class));
    }

    @Test
    void testConvertCSV_emptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        when(converterService.convert(anyBoolean(), any(MultipartFile.class)))
                .thenThrow(new IllegalArgumentException("Attached file might be empty"));

        mockMvc.perform(multipart("/convert")
                        .file(file)
                        .param("pretty", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Attached file might be empty"));

        verify(converterService, times(1)).convert(eq(true), any(MultipartFile.class));
    }

    @Test
    void testConvertCSV_ioException() throws Exception {
        String csvContent = "Name,Age\nBob,40";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, csvContent.getBytes());

        when(converterService.convert(anyBoolean(), any(MultipartFile.class)))
                .thenThrow(new IOException("Simulated IO error"));

        mockMvc.perform(multipart("/convert")
                        .file(file)
                        .param("pretty", "true"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Simulated IO error"));

        verify(converterService, times(1)).convert(eq(true), any(MultipartFile.class));
    }

    @Test
    void testConvertCSV_invalidDelimiter() throws Exception {
        String csvContent = "Name.Age\nCharlie.50";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, csvContent.getBytes());

        when(converterService.convert(anyBoolean(), any(MultipartFile.class)))
                .thenThrow(new IllegalArgumentException("Invalid delimiter found in the input file"));

        mockMvc.perform(multipart("/convert")
                        .file(file)
                        .param("pretty", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid delimiter found in the input file"));

        verify(converterService, times(1)).convert(eq(true), any(MultipartFile.class));
    }
}
