  Feature: storebatch

  Scenario: store a batch of vouchers
    Given A batch of 2 vouchers
    When The store batch voucher request is sent
    Then All 2 vouchers must be visible in the repository
