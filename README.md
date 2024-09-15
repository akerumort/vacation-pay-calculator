<h1 align="center" id="title">vacation-pay-calculator</h1>

<p align="center"><img src="https://socialify.git.ci/akerumort/vacation-pay-calculator/image?description=1&font=Jost&language=1&name=1&owner=1&pattern=Signal&theme=Light"></p>

This is a simple microservice for calculating vacation pay based on average salary and the number of vacation days. The project is built with Java 17 and Spring Boot.

## ⚙ Features

- Calculate Gross and Net vacation pay
- Detailed and simple responses
- Custom validation & error handling
- Detailed logging
- Unit testing for services
- Docker support

## 🌐 API Endpoint & description

- **URL**: `/calculate`
- **Method**: `GET`
- **Query Parameters**:
  - `averageSalary` (required): Average salary for the last 12 months.
  - `vacationDays` (required): Number of vacation days.
  - `vacationDates` (optional): Specific vacation dates to consider holidays and weekends.
  - `vacationStartDate` (optional): Start date of the vacation period.
  - `vacationEndDate` (optional): End date of the vacation period.

- **Responses**:
  - If `vacationDates` are not provided:
    - **SimpleVacationPayResponseDto**:
      ```json
      {
        "vacationPay": "BigDecimal",
        "message": "String"
      }
      ```
  - If `vacationDates` are provided:
    - **DetailedVacationPayResponseDto**:
      ```json
      {
        "vacationPay": "BigDecimal",
        "weekendsAndHolidays": "int",
        "paidVacationDays": "int",
        "message": "String"
      }
      ```

### Error Handling

The API handles various exceptions and returns appropriate HTTP status codes and error messages:

- **400 Bad Request**: For invalid input, missing parameters, or validation errors.
- **500 Internal Server Error**: For unexpected errors.

## 💻 Used technologies

- **Java** 17
- **Spring Boot**
- **Spring Web**
- **Maven**
- **Hibernate Validator**: 8.0.0
- **MapStruct**: 1.5.2
- **JUnit 5**
- **Mockito**
- **Log4j**: 2.23.1
- **SpringDoc OpenAPI**: 2.5.0
- **Docker**

## 🐋 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/akerumort/vacation-pay-calculator.git
   cd vacation-pay-calculator
2. Build and start the containers:
   ```bash
   docker build -t vacation-pay-calculator .
3. Run the Docker container

    ```bash
    docker run -p 8080:8080 vacation-pay-calculator
    ```
4. The application will start on http://localhost:8080

## ⌨️ Testing

- Run the tests using Maven:
    ```bash
    mvn test
## 📝 API Documentation

- Available on:
   ```bash
   http://localhost:8080/swagger-ui/index.html

## 🛡️ License
This project is licensed under the MIT License. See the `LICENSE` file for more details.

## ✉️ Contact
For any questions or inquiries, please contact `akerumort404@gmail.com`.
