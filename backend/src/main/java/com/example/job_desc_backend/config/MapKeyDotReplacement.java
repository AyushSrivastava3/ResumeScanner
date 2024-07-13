package com.example.job_desc_backend.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MapKeyDotReplacement {

    @WritingConverter
    @Component
    public static class DotToUnderscoreConverter implements Converter<Map<String, Object>, Map<String, Object>> {
        @Override
        public Map<String, Object> convert(Map<String, Object> source) {
            return source.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().replace(".", "_"),
                            Map.Entry::getValue
                    ));
        }
    }
    @Component
    @ReadingConverter
    public static class UnderscoreToDotConverter implements Converter<Map<String, Object>, Map<String, Object>> {
        @Override
        public Map<String, Object> convert(Map<String, Object> source) {
            return source.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().replace("_", "."),
                            Map.Entry::getValue
                    ));
        }
    }
}
