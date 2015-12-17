Feature: storebatch

  Scenario: soak test to store a batch of vouchers
    Given 5 batches of 2 vouchers
    When Then 5 store batch voucher requests are sent after 0 ms
    Then All 2 vouchers must be visible in the repository
    
