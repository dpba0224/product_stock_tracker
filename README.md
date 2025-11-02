# Product Stock Tracker
This repository contiains all the codes and files to build a web application that tracks the product stock levels from various vendors using Spring Boot

## Prerequisites
Project used: *Maven*

Java Version: *21*

Spring Boot Version: *3.5.7*

Database: *H2 In-memory database*

CSV library used: *OpenCSV v.5.7.1*

Other Dependencies:
*Spring Boot Dev Tools,*
*Spring Boot Web,*
*Spring Boot Starter Data JPA,*
*Spring Boot Starter Test,*
*Mockito Core,*
*Lombok*


## How the application was built:
1. Input of prerequisites and dependencies using Spring Initializr and pom.xml file
2. Addition of configurations for H2 in-memory database using application.properties file
3. Creation of packages for controller, entity, service and repository for cleaner and organized project structure
4. Creation of FileController that includes the following requests and dependency injection:
  - uploadFileToDb method (POST) to create products along with their respective information and quantity
  - getAllProducts method (GET) to retrieve all products present in the application as stored in H2 database
  - fileService is used as the dependency injection to achieve the implemented methods in the said controller
5. Creation of the File entity that defines the id, sku, name and stock quantity of the products based on the uploaded csv file
6. Creation of the FileRepository that serves as an abstraction layer for the data access logic of an application
7. Creation of the FileService that contains the business logic of the application
  - fileRepository is used as the dependency injection to help implement the methods for FileService
  - saveFile method to create and save the products included in the CSV file
  - addition of validations under saveFile method to filter out all possible errors and exceptions based on the uploaded file, prior saving the data to the database
  - getAllProducts method that returns a list of products saved in the app's in-built database
8. Creation of FileServiceTest to apply the Unit Testing of the application
  - AAA Pattern (Arrange-Act-Assert) was used to test the business logic of the application in a much organized and efficient manner.
9. Built the project using maven clean and install under Maven tools
10. Creation of JAR file and other files located in target folder

## How to run the application
**Upload CSV file and Save data to the Database**
1. By using the Postman API, create a new request and input the following URL: localhost:8080/api/import
2. Change the request method to POST
3. Select the Body tab, select the form-data, and create the following key and value:
  - Key: file (checked)
  - Key Type: File (as selected)
  - Value: Upload the .csv file containing the products to be added in the web application
4. Click Send once done. If the Body prints "Success", it means the upload was successfully made, and data is stored in the database.
5. Check the messages made from the console of the IDE of your choice. This will be based on the validations made in the FileService.

**Retrieve the products present in the application**
1. By using the Postman API, create a new request and input the following URL: localhost:8080/api/products
2. Change the request method to GET
3. Click Send
4. Check if all information from the products (as objects) are present in the JSON file, such as the SKU, name, and stockQuantity as key value pairs.

## CSV file used
stock.csv was used as the test csv file, which is located at the src folder of the project

## Improvements if given more time
1. Creation of business logic for updating a specific product if ever there are any adjustments to be made in its SKU, name, and stockQuantity
2. Creation of front-end application to have a user interface using either React or Angular
