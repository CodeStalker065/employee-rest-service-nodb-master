package org.example.employeenodb.spring;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.example.employeenodb.EmployeeRestServiceNodbApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


@Getter
@SpringBootTest(classes = EmployeeRestServiceNodbApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
public class ContextLoader {

    protected String baseUrl;
    @LocalServerPort
    private int port;
    @Value("${employee.service.host}")
    private String employeeServiceHost;

    @PostConstruct
    private void init() {
        baseUrl = String.format("http://%s:%s", employeeServiceHost, port);
    }
}
