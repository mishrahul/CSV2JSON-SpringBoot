package com.converter.CSV2JSON_SpringBoot.serviceTest;

import com.converter.CSV2JSON_SpringBoot.core.Builder;
import com.converter.CSV2JSON_SpringBoot.core.DelimiterDetector;
import com.converter.CSV2JSON_SpringBoot.service.ConverterService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class ServiceTest {


    @Mock
    private Builder builder;

    @Mock
    private DelimiterDetector detector;

    @InjectMocks
    private ConverterService service;


}
