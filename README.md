# CSV To JSON Converter Spring Boot App

## Overview

A simple Spring Boot application which provides RESTful API for receiving, handling and converting user-provided CSV file into its JSON equivalent.


## Technologies
* Java 17
* Spring Boot
* Apache commons CSV
* Jackson core
* Maven
* JUnit & Mockito for testing


## Features
* Converts uploaded CSV data into a well-structured array of JSON objects
* Accepts CSV file through multipart/form-data with POST request along with an optional boolean parameter "pretty" for pretty-printing, true by default
* Preprocessing file alidation to ensure the expected file is not missing, is a CSV file and is not empty.
* Auto-detects delimiters from the input file
* Uses Apache commons CSV for parsing and writing the output
* Can handle large input data due to response streaming 



## Gettiing Started
### Requirements
* Java 17+
* Maven

### Installation
1. **Clone the repository**

    ```
    git clone https://github.com/mishrahul/CSV2JSON-SpringBoot.git
    cd CSV2JSON-SpringBoot
    ```

2. **Building the project**
    ```
    mvn clean install
    ```

3. **Running the application**
    ```
    mvn spring-boot:run
    ```

The applicatiion runs on the port **8080** by default and can be accessed at **https://localhost:8080/convert**


## API Endpoints
### To update an expense entry
1. Endpoint: **POST /convert**

2. Request Body
  ```
     {
      "description": "Lunch",
      "amount": 10.00,
      "category": "FOOD",
      "date": "2025-06-01"
    }
   ```

### To retrieve expense data between two dates for a particular category
1. Endpoint: **POST /convert**

2. Request parametes

   **file** : (required) CSV file in the form of multipart/form-data

   **pretty** : (optional) boolean, enables pretty-printing if true, raw JSON if false. Is true by default.
   
3. Example URL
   ```
   https://localhost:8080/convert?pretty=false
   ```

4. Response
   * content-type: application/json

