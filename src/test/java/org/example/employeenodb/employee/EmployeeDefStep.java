package org.example.employeenodb.employee;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.example.employeenodb.exception.CustomRuntimeException;
import org.example.employeenodb.model.Employee;
import org.example.employeenodb.spring.ContextLoader;
import org.example.employeenodb.util.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.example.employeenodb.util.session.SessionKey.*;

@Slf4j
public class EmployeeDefStep {

    @Autowired
    protected ContextLoader contextLoader;

    @Autowired
    protected Session session;

    @Autowired
    protected SoftAssertions softAssertions;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Before
    public void doSetup(Scenario scenario) {

        //Was added to test the parallel execution:
//        long threadId = Thread.currentThread().getId();
//        String processName = ManagementFactory.getRuntimeMXBean().getName();
//        log.info(String.format("Started in thread: %s, in JVM: %s", threadId, processName));
        System.out.format("Thread ID - %2d - %s .\n",
                Thread.currentThread().getId(), scenario);
    }

    @After
    public void cleanUp() {
        restTemplate.delete(String.format("%s/employee", contextLoader.getBaseUrl()));
        softAssertions.assertAll();
        session.clear();
    }

    @DataTableType
    public Employee createEmployee(Map<String, String> data) {
        return new Employee(
                Integer.valueOf(data.get("id")),
                data.get("name"),
                data.get("passportNumber"),
                data.get("education"));
    }

    @Given("employees added to Employee rest service repository:")
    public void addListOfEmployees(List<Employee> employees) {

        restTemplate.postForLocation(String.format("%s/employee/list", contextLoader.getBaseUrl()), employees);
        List<Employee> expectedList = employees.stream().sorted().collect(Collectors.toList());
        session.put(EXPECTED_LIST, expectedList);
    }


    @When("we send {string} request to the {string} endpoint")
    public void weSendGETRequestToTheEmployeeEndpoint(String methodName, String endpoint) {
        ResponseEntity<List<Employee>> responseEntity = restTemplate.exchange(
                String.format("%s/%s", contextLoader.getBaseUrl(), endpoint),
                HttpMethod.valueOf(methodName),
                null,
                new ParameterizedTypeReference<List<Employee>>() {
                }
        );

        List<Employee> actualList = Objects.requireNonNull(responseEntity.getBody()).stream().sorted().collect(Collectors.toList());
        session.put(ACTUAL_LIST, actualList);
    }

    @When("we send {string} request to the {string} endpoint with {int} id")
    public void weSendGETRequestToTheEmployeeEndpointWithId(String methodName, String endpoint, int id) {
        ResponseEntity<Employee> responseEntity = restTemplate.exchange(
                String.format("%s/%s/%s", contextLoader.getBaseUrl(), endpoint, id),
                HttpMethod.valueOf(methodName),
                null,
                Employee.class
        );

        Employee actual = Objects.requireNonNull(responseEntity.getBody());

        session.put(ACTUAL_RESULT, actual);

        List<Employee> expectedList = (List<Employee>) session.get(EXPECTED_LIST, Object.class);

        Employee expected = expectedList.stream().filter(employee -> employee.getId() == id).findFirst()
                .orElseThrow(() -> new CustomRuntimeException(String.format("Expected employee not found with id = %s", id)));

        session.put(EXPECTED_RESULT, expected);
    }

    @Then("retrieved data is equal to added data")
    public void retrievedDataIsEqualToAddedData() {
        softAssertions
                .assertThat(session.get(ACTUAL_LIST, Object.class))
                .isEqualTo(session.get(EXPECTED_LIST, Object.class));
    }

    @Then("retrieved data is equal to added data for specified id")
    public void retrievedDataIsEqualToAddedDataForSpecifiedId() {
        softAssertions
                .assertThat(session.get(ACTUAL_RESULT, Employee.class))
                .isEqualTo(session.get(EXPECTED_RESULT, Employee.class));
    }
}
