# 🧑‍💼 Job Portal Console Application

A console-based job portal built with **Java** and **Spring Boot**, allowing users to register as **job seekers or employers**, log in, and manage or search job listings through a simple command-line interface.

![Job Portal Console Screenshot](image\job_portal.png)

---

## 📌 Features

- ✅ User registration (Job Seeker or Employer)
- 🔐 Secure login with Spring Security
- 📝 Post new jobs (Employers only)
- 🔍 Search jobs by title and location
- 🗂️ View all available jobs
- 🏢 Company-specific job listings
- 🧼 Clean in-memory database using H2

---

## 🛠️ Technologies Used

- Java 17+
- Spring Boot 2.7.0
- Spring Security
- Hibernate / JPA
- H2 In-Memory Database
- Maven

---

## 🚀 Getting Started

### 📦 Prerequisites

- Java 8 or higher
- Maven 3.6 or higher

### 🔧 Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/ThobMvuni/job_portal.git
   cd job_portal

2. **Run the application**
   ```bash
   mvn clean spring-boot:run

3. **Interact with the CLI menu**
   
   === Login Menu ===
1. Login
2. Register
3. Exit

=== Job Seeker Menu ===
1. Search Jobs
2. View All Jobs
3. Apply for a Job
4. Exit

=== Employer Menu ===
1. Post a Job
2. View Your Jobs
3. Delete a Job
4. Exit

4. **Project Structure**

```
job-portal/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/jobportal/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── JobPortalApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│  
├── pom.xml
└── README.md
```

