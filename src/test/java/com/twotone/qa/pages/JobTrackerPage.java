package com.twotone.qa.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JobTrackerPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By totalBudgetLocator = By.id("totalBudget");
    private final By statusFilterLocator = By.id("statusFilter");
    private final By showingLocator = By.id("showing");
    private final By newRequestButton = By.id("newBtn");
    private final By dialogLocator = By.id("dlg");
    private final By clientNameField = By.id("fClient");
    private final By jobTitleField = By.id("fTitle");
    private final By dueDateField = By.id("fDue");
    private final By budgetField = By.id("fBudget");
    private final By statusField = By.id("fStatus");
    private final By saveButton = By.id("saveBtn");
    private final By cancelButton = By.id("cancelBtn");
    private final By rowsLocator = By.cssSelector("#rows tr");
    private final By errorLocator = By.id("err");

    public JobTrackerPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String url) {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(totalBudgetLocator));
        wait.until(ExpectedConditions.visibilityOfElementLocated(statusFilterLocator));
        wait.until(ExpectedConditions.visibilityOfElementLocated(showingLocator));
        wait.until(ExpectedConditions.visibilityOfElementLocated(rowsLocator));
        demoPause();
    }

    private void demoPause() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getTotalBudgetText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(totalBudgetLocator)).getText();
    }

    public List<WebElement> getJobRows() {
        return driver.findElements(rowsLocator).stream()
                .filter(row -> row.findElements(By.tagName("td")).size() > 1)
                .filter(row -> !row.getText().contains("No jobs match your filters"))
                .toList();
    }

    public int getJobCount() {
        return getJobRows().size();
    }

    public void openNewRequestForm() {
        wait.until(ExpectedConditions.elementToBeClickable(newRequestButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(dialogLocator));
        wait.until(ExpectedConditions.visibilityOfElementLocated(clientNameField));
        demoPause();
    }

    public void selectStatusByLabel(String label) {
        WebElement select = wait.until(ExpectedConditions.visibilityOfElementLocated(statusFilterLocator));
        Select dropdown = new Select(select);
        dropdown.selectByVisibleText(label);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                select
        );

        wait.until(d -> label.equals(new Select(d.findElement(statusFilterLocator)).getFirstSelectedOption().getText()));
        demoPause();
    }

    public void enterClient(String client) {
        setInputValue(clientNameField, client);
        demoPause();
    }

    public void enterJobTitle(String jobTitle) {
        setInputValue(jobTitleField, jobTitle);
        demoPause();
    }

    public void enterDueDate(String dueDate) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(dueDateField));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                field,
                dueDate
        );
        wait.until(driver -> dueDate.equals(field.getAttribute("value")));
        demoPause();
    }

    public void enterBudget(String budget) {
        setInputValue(budgetField, budget);
        demoPause();
    }

    public void clearFilters() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("clearBtn"))).click();
    }

    public String getInputValue(String fieldId) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(fieldId)));
        return field.getAttribute("value");
    }

    public void verifyFieldValue(String fieldId, String expectedValue) {
        wait.until(driver -> expectedValue.equals(getInputValue(fieldId)));
    }

    private void setInputValue(By locator, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.clear();
        field.sendKeys(value);
        wait.until(driver -> value.equals(field.getAttribute("value")));
    }

    public void ensureValidRequestFormIsReady() {
        verifyFieldValue("fClient", getInputValue("fClient"));
        verifyFieldValue("fTitle", getInputValue("fTitle"));
        verifyFieldValue("fDue", getInputValue("fDue"));
        verifyFieldValue("fBudget", getInputValue("fBudget"));
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
    }

    public void clickSave() {
        WebElement save = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
                save
        );
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        save.click();
        demoPause();
    }

    public void saveTwiceQuickly() {
        WebElement save = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
                save
        );
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        save.click();
        save.click();
    }

    public void waitForRowCountToIncreaseBy(int startingCount, int expectedIncrease) {
        wait.until(driver -> getJobCount() == startingCount + expectedIncrease);
    }

    public String getErrorText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator)).getText();
    }

    public void waitForSaveToComplete(int startingCount) {
        wait.until(driver -> driver.findElements(rowsLocator).size() > startingCount);
    }
}
