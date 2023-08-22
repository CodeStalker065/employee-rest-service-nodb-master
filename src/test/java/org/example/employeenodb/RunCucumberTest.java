package org.example.employeenodb;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features"
        , glue = "org.example.employeenodb"
        , tags = "@employee"
)
public class RunCucumberTest {
}
