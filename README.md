# SplitEase

SplitEase is a Spring Boot application designed to resolve the issue of money splitting between groups. It uses an enhanced algorithm to calculate debt settlement, making it easier to manage and settle group expenses.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites
Before you begin, ensure you have met the following requirements:
- Java 17 or higher installed on your machine.
- Maven 3.6.3 or higher installed on your machine.
- A running SMTP server for sending emails (or use a service like Gmail).

## Installation
To install SplitEase, follow these steps:

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/SplitEase.git
    cd SplitEase
    ```

2. Build the project using Maven:
    ```bash
    mvn clean install
    ```

## Configuration
1. **Environment Variables:**

   Set the following environment variables:

    - `SPLITEASE_SMTP_PASSWORD`: SMTP server password.

   On Windows:
    ```cmd
    setx SPLITEASE_SMTP_PASSWORD "your_password_here"
    ```

   On macOS/Linux:
    ```bash
    export SPLITEASE_SMTP_PASSWORD="your_password_here"
    ```

2. **Application Configuration:**

   Configure your SMTP settings and other necessary configurations in `src/main/resources/application.yml`:

    ```yaml
    spring:
      mail:
        host: smtp.example.com
        port: 587
        username: your_email@example.com
        password: ${SPLITEASE_SMTP_PASSWORD}
        properties:
          mail:
            smtp:
              auth: true
              starttls:
                enable: true
    ```

## Running the Application
To run the application, use the following command:
```bash
mvn spring-boot:run
