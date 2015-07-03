1. Command to build the project
gradlew build

2. Command to start the application
java -jar build\libs\codetest-1.0.jar

3. Command to start the application in debug mode
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=7777,suspend=n -jar build\libs\codetest-1.0.jar