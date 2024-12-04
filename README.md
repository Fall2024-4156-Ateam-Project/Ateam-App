# Ateam-App

## Deployment:
The service has been deployed on: http://34.138.31.157:9000/

## Setup and Running

JDK 17: This application uses JDK 17: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html  
Installing maven: https://maven.apache.org/download.cgi  
Build with maven: ```mvn -B package --file ClientApp/pom.xml```  
Running the test: ```mvn clean test```  
Check the test coverage report inside teamproject folder: ```mvn jacoco:report```. The report path will be ./target/site/jacoco/index.html  
Running the application: ```mvn spring-boot:run```  

## This app is simulating a hospital app to use the scheduling service api

## End to end testing

### Register
1. When the user is on the home page, click on the "Register" button and they will be directed to the register form page
2. Type in the Username, email, password and choose the role and click on register buttton.  
   A. If the email is already registered, there will be a warning saying "User already exists"  
   B. If successfully registered they will be directed to their personal home page.  
### Log in
1. When the user is on the home page, click on the "Login" button and they will be directed to the Login form page.
2. Type in their email, password and choose their role and click on login buttton.  
   A. If the credentails is incorrect, there will be a warning saying "Invalid credential"  
   B. If successfully logged in they will be directed to their personal home page.  
### Log out
1. When the user is logged in and on the personal home page, click on the "Login" button and they will be directed to the home page to choose login or register.
### Search Doctors
1. When the user is logged in and on the personal home page, click on the "Search Doctor" button and they will be directed to the search doctor page.
2. This page is by default showing all doctors with their name, specialty and email.
3. The user can search the doctors by their name or their specialty in the search bar at the top of the page. There is a drop-down menu to select search by name or by specialty.
4. If there is no doctor satisfies the search string, the page will show "No doctors found."
5. Otherwise the page will show all doctors satisfy the search condition.
### Timeslots and Requests related to it
1. When the user on the search doctor page and click on the "show timeslots" button, they will be directed to the page showing the doctor email and all their corresponding timeslots.
2. Click on any timeslots on the page, the user will be directed to the timeslot details page.
3. On the timeslot details page the user can see the info related to this timeslot.  
   A. If this timeslot was created by the current user, below the timeslot the user can update the timeslot details or remove the timeslot. Then they will be directed to all timeslots schedule page.  
4. If the user role is a doctor, on the timeslot details page click the "view requests", they can see all the requests sent to this timeslot and the details.  
   A. If this timeslot was created by the current user, on the request list, they are able to update the request status (approved or denied).  
5. If the user role is a patient, on the timeslot details page click the "create a request" button, they will be directed to the create request form. Then they can enter the request descriptions and submit the request. Then they will be redirected to the timeslot detail page.
6. If the user role is a doctor, when the user is logged in and on the personal home page, click on the "view time slots" button, they will be directed to the page showing all their timeslots. The other features are the same as above doctor timeslot features.
7. If the user role is a doctor, when the user is logged in and on the personal home page, click on the "create time slot" button, they will be directed to the create timeslot form. Then they can fill in the timeslot information and click "create timeslot" button to create. Then they will be directed to the home page.
8. When the user is logged in and on the personal home page, click on the "My requests" button and they will be directed to the requests list page containing all requests they have created and the related information. For each request, the user can update the description of the request or delete it.
### Meeting
1. If the user role is a doctor, on the home page there is a "Create Meeting" button. Clicking on that button, the user will be directed to a page asking the detailed information of the new meeting, including description, recurrence (daily, weekly, monthly, or none), type (group or one-on-one), participant email, start day, start time, end day, end time, and status (valid, invalid). If any field is missing, the user will be asked to enter that field. If the participant email is not found in the database, the user will be notified "Participant does not exist". If the meeting is created succesfully, then the user will be directed to a page showing meetings associated with this user.
2. No matter the user is a doctor or patient, on the home page there is a "My Meetings" button. Clicking on that button, the user will see the list of meetings associated with the user. Each meeting contains information of the organizer, type, description, start day, end day, start time, end time, recurrence, status, role, and participants (names and emails). However, for different role, either participant or organizer, the content of participant will be presented distinctly. As organizer, doctor is able to view the name and email of patients. As participants, they will be patients or doctors, are not allowed to view that information due to the privacy and security concern. They are only able to see "As a participants, you are not allowed to see other participants' information."
3. When viewing meetings, the user can use at most 3 filters: type, status, or recurrence. After clicking on the "Apply Filters" button, they will only see the filterd meetings. They can cancel the filter and view all meetings again by clicking on the "Reset Filters" button.
4. Under the view meeting page, if the user is a organizer, the doctor can delete a meeting using the "Delete" button, and they will be notified "Meeting deleted successfully." However, as participants, either patients or doctors, they are prohibited to make any change to that meetings they are attending.


## Tools used
This section includes notes on tools and technologies used in building this project, as well as any additional details if applicable.

1. Firebase DB
2. Maven Package Manager
3. GitHub Actions CI
4. Checkstyle
5. JUnit
6. JaCoCo
7. Postman