# Procesy_projekt

***Running project:***

1. Have Maven installed and added to PATH
    a. Download Maven 3.9.9 from https://maven.apache.org/download.cgi
    b. Unzip it to a folder
    c. Copy the folder path
    d. Add the path to system variables
    e. Click on edit PATH
    f. Click on New
    g. Name: ```MAVEN_HOME``` value:  ```%MAVEN_HOME%\bin```
2. Have Java 21 installed
2. Type in console
   ```./mvnw clean package -DskipTests```
3. Open docker and run Dockerfile

***Database:***
1. install pgAdmin
2. create connection to database with port 5432 username: user and password: password
