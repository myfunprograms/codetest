##############################
#  Run the application       #
##############################
1. Install gradle if needed
2. Add <gradle_installed_path>/bin to path
3. Build the project
   gradle build
4. Start the application
   java -jar build\libs\codetest-1.0.jar

   Start the application in debug mode
   java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=7777,suspend=n -jar build\libs\codetest-1.0.jar

##############################
#  Tools                     #
##############################
Environment
- Spring boot

Presentation layer: 
- Thymeleaf
- SpringMVC
- Jquery-UI
- bootstrap

Persistence layer:
- HSQLDB
- Spring JdBC

##############################
#  Test                      #
##############################
1. Type of tests
Unit test
- Service layer
- DOmain objects
Integration test
- Presentation layer
- DB layer

2. Test cases
- Empty file
- Invalid file header
- File with valid header in different order
- Create or update courses
- Create or update students
- Save student with invalid course number
- Load active courses when exists active courses
- Load active courses when exists no active courses
- Load active courses when exists no active students
- Load active courses when exists active students

##############################
#  TODO                      #
##############################
- Add UI test
- Move the integration test to its own folder
- Make the UI looks better

##############################
#  Questions                 #
##############################
1. What to do with record that a student is active but a course is inactive?
- Currently save the record. 
2. What to do when an active course doesn't have active student?
- CUrrently return the course with no student record
3. The sample record has something like "a and b". While the Excel doesn't add quotes for text records. What to save, "a and b" or without quote?
- Currently save with quote. It doesn't remove the quotes if exists.  