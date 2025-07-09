package com.converter.CSV2JSON_SpringBoot.controllerTest;

import com.converter.CSV2JSON_SpringBoot.controller.ConverterController;
import com.converter.CSV2JSON_SpringBoot.service.ConverterService;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.multi.MultiLabelUI;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConverterController.class)
public class ControllerTest {


    @Autowired
    MockMvc mockMvc;


    @MockitoBean
    ConverterService converterService;

    @InjectMocks
    ConverterController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }



    @Test
    void testConvertCSV_prettyFalse() throws Exception {
        String sample = "Name,Age,City\nBob,35,Paris";
        boolean pretty = false;
        String out = "[{\"Name\":\"Bob\",\"Age\":\"35\",\"City\":\"Paris\"}]";

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.csv",MediaType.TEXT_PLAIN_VALUE, sample.getBytes());

        when(converterService.convert(eq(false), any(MultipartFile.class))).thenReturn(out);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/convert")
                                              .file(multipartFile)
                                              .param("pretty", String.valueOf(pretty)))
                .andExpect(status().isOk())
                //
                //andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("[{\"Name\":\"Bob\",\"Age\":\"35\",\"City\":\"Paris\"}]"));

        verify(converterService, times(1)).convert(eq(false), any(MultipartFile.class));
    }

    @Test
    void testConvert_prettyTrue() throws Exception {
        String sample = "Name,Age,City\nBob,35,Paris";
        boolean pretty = true;
        String out= "[ {\r\n" +
                "  \"Name\" : \"Bob\",\r\n" +
                "  \"Age\" : \"35\",\r\n" +
                "  \"City\" : \"Paris\"\r\n" +
                "} ]";

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.csv",MediaType.TEXT_PLAIN_VALUE, sample.getBytes());


        when(converterService.convert(eq(true), any(MultipartFile.class))).thenReturn(out);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/convert")
                        .file(multipartFile)
                        .param("pretty", String.valueOf(pretty)))
                .andExpect(status().isOk())

                .andExpect(MockMvcResultMatchers.content().string(out));

        verify(converterService, times(1)).convert(eq(true), any(MultipartFile.class));

    }


    @Test
    void testConvert_withDefaultPrettyPrint() throws Exception {
        String sample = "Name,Age,City\nBob,35,Paris";
        boolean pretty = true;
        String out= "[ {\r\n" +
                "  \"Name\" : \"Bob\",\r\n" +
                "  \"Age\" : \"35\",\r\n" +
                "  \"City\" : \"Paris\"\r\n" +
                "} ]";

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, sample.getBytes());

        when(converterService.convert(true, multipartFile)).thenReturn(out);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/convert")
                        .file(multipartFile)
                        .param("pretty", String.valueOf(pretty)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(out));

        verify(converterService, times(1)).convert(eq(pretty), any(MultipartFile.class));
    }


    @Test
    void testConvert_isNotCSV() throws Exception {
        String sample = "<html></html>";
        boolean pretty = true;

        MockMultipartFile multipartFile = new MockMultipartFile("file", "index.html", MediaType.TEXT_PLAIN_VALUE, sample.getBytes());

        when(converterService.convert(true, multipartFile)).thenThrow(new IllegalArgumentException("Attached file is not a valid CSV file"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/convert")
                .file(multipartFile).param("pretty", "true"))
                .andExpect(status().isBadRequest());
        verify(converterService, times(1)).convert(pretty, multipartFile);

    }


    @Test
    void testConvert_isEmptyFile_throwsIllegalArgsException() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "empty.csv", MediaType.TEXT_PLAIN_VALUE, "".getBytes());

        when(converterService.convert(true, multipartFile)).thenThrow(new IllegalArgumentException("Attached file might be empty"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/convert")
                .file(multipartFile).param("pretty", "true"))
                .andExpect(status().isBadRequest());

        verify(converterService, times(1)).convert(true, multipartFile);
    }
}
