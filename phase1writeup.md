Group 10
Team Members: Rebecca Metzman, Sasha Nguyen , Matthieu Perez

Additional Tasks: 1.5, 1.8, 1.9
For each task you completed in this phase (Required or Additional), a brief description of the changes you made to the provided code, e.g. the names of any classes or methods that were changed, new methods that were created, etc.
1.1- added test cases / new test classes for the other methods
1.2- NA
1.3- changed the start function
1.4- added a new var to calculate the total amount of donations and display it
1.5- added test cases / new test files for each method
1.8- changed the createFund method
1.9- make attemptLogin throws an IllegalStateException and add a try catch block in the main() function of UI
A description of any bugs that you found and fixed in Task 1.2/1.5
1.5: We changed from direct get method calls in attemptLogin to optString and optInt methods.  This is so we can safely retrieve values from the JSON object.  This is to resolve the fact that it expected the method to handle missing fields gracefully, but the method did not do so.  Also, added a catch block specifically for JSONException to handle invalid JSON responses better.  When a JSONException is caught, the method returned null instead of propagating the exception.  In getFundName, we added a check to verify if the response is a valid JSON object before attempting to parse it. If the response is not valid JSON, the method now handles it appropriately by logging the error and returning null.  In getAllOrganizations, we added a check to verify if the response is a valid JSON object before attempting to parse it. This helps handle cases where the response is not valid JSON, preventing the JSONException.  In makeDonation we also added a check to verify if the response is a valid JSON object before attempting to parse it.
Any known bugs or other issues with the tasks you attempted in this phase.
Instructions on how to start each app, if you changed anything from the original version of the code, e.g. the name of the Java main class or JavaScript entry point, arguments to the programs, etc. If you did not change anything, you may omit this.
A brief but specific description of each team member’s contributions, including the task numbers that they worked on. Please do not simply write “all members contributed equally to all tasks” since we know that’s not really the case. 😁
Rebecca: 1.1, 1.2, 1.5
Sasha: 1.4, 1.9
Mathieu 1.3, 1.8
