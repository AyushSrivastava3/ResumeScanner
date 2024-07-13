package com.example.job_desc_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Configuration
public class CustomConversionsConfig {

    @Autowired
    private MapKeyDotReplacement.DotToUnderscoreConverter dotToUnderscoreConverter;

    @Autowired
    private MapKeyDotReplacement.UnderscoreToDotConverter underscoreToDotConverter;

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(dotToUnderscoreConverter, underscoreToDotConverter));
    }
}

