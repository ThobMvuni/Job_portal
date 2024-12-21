# Job Portal Console Application

A console-based job portal application built with Spring Boot where employers can post jobs and job seekers can search and view job listings.

## Prerequisites

- Java 8 or higher
- Maven 3.6 or higher

## Getting Started

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:
   ```bash
   mvn clean spring-boot:run


Features
User registration (Employer/Job Seeker)
User login
Post jobs (Employers only)
View all jobs
Search jobs by title and location
View company-specific jobs
Using the Application
When you start the application, you'll see the main menu:
Login
Register
Exit
If you're a new user, choose Register and:
Enter your email
Enter your password
Enter your full name
Choose if you're an employer or job seeker
If you're an employer, enter your company name
After logging in:
View all jobs
Search for specific jobs
If you're an employer:
Post new jobs
View your company's jobs
To exit the application:
Choose Logout from the main menu
Or press Ctrl+C
Technical Details
Built with Spring Boot 2.7.0
Uses H2 in-memory database
Spring Security for authentication
JPA/Hibernate for data persistence
Database
The application uses H2 in-memory database, which means:

Data is stored only during runtime
Data is cleared when the application stops
H2 console is available at http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:job_portal
Username: sa
Password: (leave empty)


This README provides clear instructions on how to run and use the application, along with information about its features and technical details. Would you like me to explain any part of it in more detail?