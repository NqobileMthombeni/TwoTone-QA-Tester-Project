Feature: Job Request Tracker QA checks

  Scenario: A valid request can be saved
    Given the Job Request Tracker is open
    And the New Request form is open
    When I enter valid client information
    And I enter a valid job title
    And I enter a valid future due date
    And I enter a valid budget
    And I click Save
    Then exactly one new row should be added after the save delay

  Scenario: Total budget summary includes every job
    Given the Job Request Tracker is open
    When I add up the Budget of every job in the table
    Then the Total Budget summary should equal that sum

  Scenario: Selecting Done displays completed jobs
    Given the Job Request Tracker is open
    When I select "Done" from the status filter
    Then jobs with status "Done" should be displayed
