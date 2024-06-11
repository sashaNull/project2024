# Group 10

## Team Members:
- Rebecca Metzman
- Sasha Nguyen
- Matthieu Perez

## Additional Tasks:
- 1.5
- 1.8
- 1.9

## Descriptions of Changes:

### 1.1
- Added test cases / new test classes for the other methods.

### 1.2
- NA

### 1.3
- Changed the start function.

### 1.4
- Added a new variable to calculate the total amount of donations and display it.

### 1.5
- Added test cases / new test files for each method.

### 1.8
- Changed the `createFund` method.

### 1.9
- Made `attemptLogin` throw an `IllegalStateException` and added a try-catch block in the `main()` function of UI.

## Bugs Found and Fixed:

### 1.5:
- Changed from direct `get` method calls in `attemptLogin` to `optString` and `optInt` methods to safely retrieve values from the JSON object. This resolves the issue where the method was expected to handle missing fields gracefully but did not do so.
- Added a catch block specifically for `JSONException` to handle invalid JSON responses better. When a `JSONException` is caught, the method now returns null instead of propagating the exception.
- In `getFundName`, added a check to verify if the response is a valid JSON object before attempting to parse it. If the response is not valid JSON, the method now handles it appropriately by logging the error and returning null.
- In `getAllOrganizations`, added a check to verify if the response is a valid JSON object before attempting to parse it. This helps handle cases where the response is not valid JSON, preventing the `JSONException`.
- In `makeDonation`, added a check to verify if the response is a valid JSON object before attempting to parse it.

## Known Bugs or Issues:
- (Include any known bugs or issues here if applicable)

## Instructions:
- (Include instructions on how to start each app, if anything was changed from the original version of the code. If nothing was changed, this section can be omitted.)

## Team Member Contributions:

### Rebecca Metzman
- Tasks: 1.1, 1.2, 1.5

### Sasha Nguyen
- Tasks: 1.4, 1.9

### Matthieu Perez
- Tasks: 1.3, 1.8
