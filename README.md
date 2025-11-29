# Record Keep

[![Java](https://img.shields.io/badge/Made%20with-Java%2017-f89820.svg?style=flat&logo=openjdk&logoColor=white)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Made%20with-Spring%20Boot-6DB33F.svg?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Thymeleaf](https://img.shields.io/badge/Made%20with-Thymeleaf-005C0F.svg?style=flat&logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![H2 Database](https://img.shields.io/badge/Database-H2-blue.svg?style=flat&logo=h2database&logoColor=white)](https://www.h2database.com)

A simple, web-based student attendance system built with Spring Boot. This application uses QR codes to track student attendance and provides a dashboard for managing records.

## Features

* **QR Code Scanning:** Real-time attendance scanning using a webcam or phone camera.
* **Student Management:** Full CRUD (Create, Read, Update, Delete) functionality for student records.
* **Attendance Reporting:** View and filter attendance logs by date, strand, grade, and section.
* **QR Code Generation:** Generate and print individual or batch QR codes for students.
* **CSV Import:** Easily import student lists from a CSV file.
* **Dashboard:** A quick overview of total students and today's attendance.

## Setup

### Prerequisites

* Java (OpenJDK) 17 or higher
* [Apache Maven](https://maven.apache.org/download.cgi) (or you can use the included Maven Wrapper)
* [Git](https://git-scm.com/downloads) for version control

### Installation

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/keiaa-75/recordkeep
    cd recordkeep
    ```

2.  **Run the application using the Maven Wrapper:**

    On Linux/macOS:

    ```bash
    ./mvnw spring-boot:run
    ```

    On Windows:

    ```pwsh
    .\mvnw.cmd spring-boot:run
    ```

## Usage

Once the application is running, open your web browser and go to:

```
http://localhost:8080
```

You will be greeted by the dashboard. From there, you can navigate to the other pages to manage students or scan QR codes.

## License

This was made by **Marco Bacolto** for a school project, and is licensed under the MIT License.