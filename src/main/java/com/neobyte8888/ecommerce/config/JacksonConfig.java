package com.neobyte8888.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary // Ưu tiên dùng bean này khi có nhiều ObjectMapper
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Hỗ trợ parse các kiểu dữ liệu Java 8+ như LocalDateTime (Sprint 5)
        mapper.registerModule(new JavaTimeModule());
        
        // Không biến ngày tháng thành mảng số (VD: [2026,3,27]) mà giữ dạng String chuẩn ISO
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}