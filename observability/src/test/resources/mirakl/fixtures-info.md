# Mirakl Test Fixtures

The files in this directory contains mocked response for AF-01 (return shop 
additional fields) Mirakl API:

| File                  | Purpose                                    |
|-----------------------|:-------------------------------------------|
| custom-fields-00.json | Empty file with no custom fields           |
| custom-fields-01.json | Correct Mirakl schema for kyc-scenario     |
| custom-fields-02.json | Correct Mirakl schema for non-kyc scenario |
| custom-fields-03.json | Unexpected field (based on non-kyc)        |
| custom-fields-04.json | Not found field (based on non-kyc)         |
| custom-fields-05.json | Incorrect label (based on non-kyc)         |
| custom-fields-06.json | Incorrect description (based on non-kyc)   |
| custom-fields-07.json | Incorrect type (based on non-kyc)          |
| custom-fields-08.json | Incorrect allowed values (based on kyc)    |
| custom-fields-09.json | Incorrect permissions (based on non-kyc)   |
| custom-fields-10.json | Multiple errors on same field              |
| custom-fields-11.json | Multiple errors on different fields        |
| custom-fields-12.json | Incorrect regex (based on non-kyc)         |
| custom-fields-13.json | Correct with warnings (based on kyc)       |
| custom-fields-13.json | Incorrect with fails (based on kyc)        |

> Some json entries contains a `_testComment` field detailing the changes
> made on the response for the test