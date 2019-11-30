# Coding exercise

A single page application that talks to a RESTful end point to upload XML and store to a database, with another RESTful interface to download the XML document.

# Requirements

Create a single page front end with a form that lets a user upload an XML file to a database.

Data will be accepted from the front end via a RESTful end point. There should also be a RESTful end point that lets users download the file.

## Application

Use the following:

- HTML front end
  - Stretch goal: React or Angular
- Java 
- Maven build-system. based 
- SpringBoot 
- RESTful end points.
- MySQL

## Input

A single page with:

- a file input control to load an xml file.
  - Keep the original file name if possible.
- A text field - some description or note about the XML file.
- a submit button.

Validate that the file is a well formed XML document before storing it. Return an error and notify the user if the document is not valid.

## Output

Create a REST endpoint to allow users to download the XML document stored in the DB.

Request for this call should have one parameter - original file name. 

No user interface is required for this API call. 

## Tests

Write a unit/integration tests for the backend.

# How to build the app

## Pre-requisites

- MySQL version 8 or better. 
- JDK 11 or later
- Maven 3.6+

## Create database for use by the application

Command line instructions to create MySQL database that the application will use.

```bash
mysql --host=127.0.0.1 --port=3306 --user=root --password # then enter password 
```

Within the MySQL session, create the database with the following:

```sql
create database xml_doc_library; -- Creates the new database
create user 'xml_doc_user'@'%' identified by 'Se$curePas@@word'; -- Creates the user
grant all on xml_doc_library.* to 'xml_doc_user'@'%'; -- Gives all privileges to the new user on the newly created database
```

# Build instructions

# Future considerations

- XML documents can be validated against set XSDs.
- Large documents can be stored outside the database, which could just store file name.
  - Separate indexing mechanisms can be applied then.

# TODO

- [X] Start with https://spring.io/guides/gs/accessing-data-mysql/ and build up the project from https://start.spring.io/. [2019-11-30]
- [X] Create git repo with initial commit and push. [2019-11-30]
- [X] Create the empty database and configuration. [2019-11-30]
- [X] Create unit tests against entity model. [2019-11-30]
- [X] Create the entity model. [2019-11-30]
- [X] Create the repository. [2019-11-30]
- [X] Set up in-memory DB for tests. [2019-11-30]
- [ ] Create the controller skeleton with input and output end points.
- [ ] Create RESTful integration tests against the controller input function.
- [ ] Create XML validator skeleton, unit tests, then implementation.
  - See [creating custom validator](https://www.baeldung.com/spring-mvc-custom-validator).
  - See [XML validation](https://simonharrer.wordpress.com/2012/11/05/xml-validation-with-the-java-api/): well formed only, no XSD validation.
- [ ] Add validation to controller https://www.baeldung.com/spring-boot-bean-validation.
  - [ ] Mandatory inputs.
  - [ ] Max size of inputs.
  - [ ] XML validation: well formed only, no XSD validation (https://simonharrer.wordpress.com/2012/11/05/xml-validation-with-the-java-api/).
- [ ] Create RESTful integration tests against the controller output function.
- [ ] Create the application class.
- [ ] Create front end page.
- [ ] Run it.
- [ ] Update security.
- [ ] Run it again.