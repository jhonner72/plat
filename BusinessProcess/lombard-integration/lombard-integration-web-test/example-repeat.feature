Feature: Outclearings

  Scenario: soak test clean cheque voucher processing
    Given 100 zip files with 3 vouchers
    When The files are delivered every 10000 ms
    Then All 3 vouchers must be visible in the repository
