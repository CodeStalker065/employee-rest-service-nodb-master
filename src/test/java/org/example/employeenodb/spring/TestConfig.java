package org.example.employeenodb.spring;

import org.assertj.core.api.SoftAssertions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.example.employeenodb")
public class TestConfig {

    @Bean
    protected SoftAssertions getSoftAssertions() {
        return new SoftAssertions();
    }
}
