package com.twotone.qa.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.twotone.qa.pages.JobTrackerPage;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;

public class JobRequestTrackerSteps {
    private static final String APP_URL = Path.of(System.getProperty("user.dir"), "job-tracker-demo.html")
            .toAbsolutePath()
            .toUri()
            .toString();

    private WebDriver driver;
    private JobTrackerPage page;
    private int startingJobCount;
    private int budgetTotalFromTable;

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1280,800");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        page = new JobTrackerPage(driver);
    }

    @After
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Scenario Screenshot", "image/png", new ByteArrayInputStream(screenshot), ".png");
            driver.quit();
        }
    }

    @Given("the Job Request Tracker is open")
    public void theJobRequestTrackerIsOpen() {
        page.open(APP_URL);
    }

    @When("I add up the Budget of every job in the table")
    public void iAddUpTheBudgetOfEveryJobInTheTable() {
        budgetTotalFromTable = page.getJobRows().stream()
                .map(row -> row.findElements(By.tagName("td")).get(5).getText())
                .mapToInt(this::parseCurrencyValue)
                .sum();
    }

    @Then("the Total Budget summary should equal that sum")
    public void theTotalBudgetSummaryShouldEqualThatSum() {
        int actualTotal = parseCurrencyValue(page.getTotalBudgetText());
        assertEquals(
                "Expected the Total Budget summary to match the sum of every job budget. " +
                        "Expected R" + formatNumber(budgetTotalFromTable) +
                        " but the card showed R" + formatNumber(actualTotal) + ".",
                budgetTotalFromTable,
                actualTotal
        );
    }

    private int parseCurrencyValue(String rawValue) {
        String digitsOnly = rawValue.replaceAll("\\D+", "");
        if (digitsOnly.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(digitsOnly);
    }

    private String formatNumber(int value) {
        return String.format("%,d", value);
    }

    @When("I select \"Done\" from the status filter")
    public void iSelectDoneFromTheStatusFilter() {
        page.selectStatusByLabel("Done");
    }

    @Then("jobs with status \"Done\" should be displayed")
    public void jobsWithStatusDoneShouldBeDisplayed() {
        int count = page.getJobCount();
        assertTrue("Expected at least one job with status Done, but the app returns zero matching rows after selecting Done.", count > 0);
    }

    @Then("the filtered result should not be empty")
    public void theFilteredResultShouldNotBeEmpty() {
        int count = page.getJobCount();
        assertTrue("The Done filter is returning zero rows because the option value is mismatched to the status data.", count > 0);
    }

    @Given("the New Request form is open")
    public void theNewRequestFormIsOpen() {
        page.open(APP_URL);
        page.openNewRequestForm();
    }

    @When("I enter valid client information")
    public void iEnterValidClientInformation() {
        String client = "Automation Test Client";
        page.enterClient(client);
        assertEquals(client, page.getInputValue("fClient"));
    }

    @When("I enter a valid job title")
    public void iEnterAValidJobTitle() {
        String title = "Automation Test Job";
        page.enterJobTitle(title);
        assertEquals(title, page.getInputValue("fTitle"));
    }

    @When("I enter a valid future due date")
    public void iEnterAValidFutureDueDate() {
        String dueDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE);
        page.enterDueDate(dueDate);
        assertEquals(dueDate, page.getInputValue("fDue"));
    }

    @When("I enter a valid budget")
    public void iEnterAValidBudget() {
        String budget = "10000";
        page.enterBudget(budget);
        assertEquals(budget, page.getInputValue("fBudget"));
    }

    @When("I click Save")
    public void iClickSave() {
        startingJobCount = page.getJobCount();
        page.ensureValidRequestFormIsReady();
        page.clickSave();
    }

    @Then("exactly one new row should be added after the save delay")
    public void exactlyOneNewRowShouldBeAddedAfterTheSaveDelay() {
        page.waitForRowCountToIncreaseBy(startingJobCount, 1);
        assertEquals(startingJobCount + 1, page.getJobCount());
    }

    @When("I click Save twice quickly")
    public void iClickSaveTwiceQuickly() {
        startingJobCount = page.getJobCount();
        page.ensureValidRequestFormIsReady();
        page.saveTwiceQuickly();
    }

    @When("I wait for the save operation to complete")
    public void iWaitForTheSaveOperationToComplete() {
        page.waitForRowCountToIncreaseBy(startingJobCount, 1);
    }

    @Then("only one new job request should exist")
    public void onlyOneNewJobRequestShouldExist() {
        int actualCount = page.getJobCount();
        assertTrue("Expected only one new request to be created, but the app created duplicates. Actual count: " + actualCount, actualCount == startingJobCount + 1);
    }
}
