package com.twotone.qa;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.twotone.qa.steps",
        plugin = {"pretty", "html:target/cucumber-reports/cucumber.html", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
        monochrome = true
)
public class RunCucumberTest {
}
