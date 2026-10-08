# Job Request Tracker QA Automation

## Overview

This project contains a Java-based Selenium and Cucumber test suite for the Job Request Tracker demo application. The automation verifies key business rules in the browser UI, including saving a valid request, calculating the total budget correctly, and filtering jobs by status.

The suite is designed to detect functional issues in the current demo app and provide reproducible QA evidence for them.

## Technology stack

* Java 17
* Maven
* Selenium WebDriver
* Cucumber JVM
* JUnit 4
* Allure
* Google Chrome

## Prerequisites

* Java 17 or later installed
* Maven installed and available on your PATH
* Google Chrome installed locally
* A desktop environment with access to Chrome

If Chrome is installed in a non-default location, update the browser path in the test setup before running the suite.

## Project structure

* `job-tracker-demo.html` — the web application under test
* `src/test/resources/features/job_tracker.feature` — feature scenarios
* `src/test/java/com/twotone/qa/RunCucumberTest.java` — Cucumber runner
* `src/test/java/com/twotone/qa/pages/JobTrackerPage.java` — page object for browser interactions
* `src/test/java/com/twotone/qa/steps/JobRequestTrackerSteps.java` — step definitions and assertions
* `target/` — generated build and test reports

## How to run the demo app

The application is a local HTML file, so it is opened directly in the browser rather than through a web server.

From the project root, open `job-tracker-demo.html` in Chrome, or use a file URL that points to the project location on your machine.

## How to run the automated tests

From the project root, run:

```
mvn test
```

To perform a clean run:

```
mvn clean test
```

## What the automation checks

The current feature file covers these scenarios:

1. A valid request can be saved
   * Opens the tracker
   * Opens the New Request form
   * Enters valid data
   * Clicks Save
   * Verifies exactly one new job row is added
2. Total budget summary includes every job
   * Opens the tracker
   * Checks the Total Budget summary value
   * Verifies that all jobs are included in the total
3. Selecting Done displays completed jobs
   * Opens the tracker
   * Selects the Done filter
   * Verifies matching jobs are shown

## Expected results against the supplied build

The supplied `job-tracker-demo.html` contains known bugs, so **two of the three tests are expected to FAIL**. This is intentional: the tests reproduce the bugs.

| Scenario | Expected result now | Related bug |
|---|---|---|
| A valid request can be saved | PASS | - |
| Total budget summary includes every job | FAIL (shows R87,800, expected R100,300) | BUG-01 |
| Selecting Done displays completed jobs | FAIL (no rows shown) | BUG-02 |

`mvn test` will therefore report 2 failures and end with `BUILD FAILURE`. This is the correct outcome for the current build. Once the bugs are fixed, all three tests should pass.

## Reporting

The project is configured to generate:

* Cucumber HTML report in `target/cucumber-reports/cucumber.html`
* Allure results in `target/allure-results`

You can also generate the Allure report with:

```
mvn allure:report
```

## Notes

This project is intended as a QA automation exercise for validating key user flows in the tracker demo. It is a browser-driven, behavior-focused suite and is best used as a regression and defect-detection tool for the web app under test.
