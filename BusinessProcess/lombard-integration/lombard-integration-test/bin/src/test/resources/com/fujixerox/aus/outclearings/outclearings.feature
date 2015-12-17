Feature: Outclearings

  Scenario: clean cheque voucher processing
    Given A zip file with 3 vouchers
    When The file is delivered
    Then All 3 vouchers must be visible in the repository
