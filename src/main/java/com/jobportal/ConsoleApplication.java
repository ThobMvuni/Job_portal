package com.jobportal;

import com.jobportal.model.*;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleApplication implements CommandLineRunner {

    private final Scanner scanner = new Scanner(System.in);
    private final JobService jobService;
    private final UserService userService;
    private User currentUser = null;

    @Autowired
    public ConsoleApplication(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Welcome to Job Portal Console Application!");
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n=== Login Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.out.println("Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid option!");
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. View All Jobs");
        System.out.println("2. Search Jobs");
        if (currentUser.getUserType() == User.UserType.EMPLOYER) {
            System.out.println("3. Post a Job");
            System.out.println("4. View My Posted Jobs");
        }
        System.out.println("5. Logout");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                viewAllJobs();
                break;
            case 2:
                searchJobs();
                break;
            case 3:
                if (currentUser.getUserType() == User.UserType.EMPLOYER) {
                    postJob();
                } else {
                    System.out.println("Invalid option!");
                }
                break;
            case 4:
                if (currentUser.getUserType() == User.UserType.EMPLOYER) {
                    viewMyJobs();
                } else {
                    System.out.println("Invalid option!");
                }
                break;
            case 5:
                logout();
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    private void login() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = userService.findByEmail(email);
            if (user != null && userService.validatePassword(user, password)) {
                currentUser = user;
                System.out.println("Login successful!");
            } else {
                System.out.println("Invalid credentials!");
            }
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void register() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Are you an employer? (yes/no): ");
        boolean isEmployer = scanner.nextLine().toLowerCase().startsWith("y");

        try {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setUsername(email); // Using email as username
            user.setUserType(isEmployer ? User.UserType.EMPLOYER : User.UserType.JOBSEEKER);

            if (isEmployer) {
                System.out.print("Enter company name: ");
                String companyName = scanner.nextLine();
                Company company = new Company();
                company.setName(companyName);
                user.setCompany(company);
            }

            userService.save(user);
            System.out.println("Registration successful! Please login.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void viewAllJobs() {
        try {
            List<Job> jobs = jobService.getAllJobs();
            displayJobs(jobs);
        } catch (Exception e) {
            System.out.println("Error fetching jobs: " + e.getMessage());
        }
    }

    private void searchJobs() {
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();
        System.out.print("Enter location (or press enter to skip): ");
        String location = scanner.nextLine();

        try {
            List<Job> jobs = jobService.searchJobs(searchTerm, location);
            displayJobs(jobs);
        } catch (Exception e) {
            System.out.println("Error searching jobs: " + e.getMessage());
        }
    }

    private void postJob() {
        if (currentUser == null || currentUser.getUserType() != User.UserType.EMPLOYER) {
            System.out.println("Only employers can post jobs!");
            return;
        }

        System.out.print("Enter job title: ");
        String title = scanner.nextLine();
        System.out.print("Enter job description: ");
        String description = scanner.nextLine();
        System.out.print("Enter location: ");
        String location = scanner.nextLine();
        System.out.print("Enter industry: ");
        String industry = scanner.nextLine();

        try {
            Job job = new Job();
            job.setTitle(title);
            job.setDescription(description);
            job.setLocation(location);
            job.setIndustry(industry);
            job.setCompany(currentUser.getCompany());
            
            jobService.save(job);
            System.out.println("Job posted successfully!");
        } catch (Exception e) {
            System.out.println("Error posting job: " + e.getMessage());
        }
    }

    private void viewMyJobs() {
        if (currentUser == null || currentUser.getUserType() != User.UserType.EMPLOYER) {
            System.out.println("Only employers can view their posted jobs!");
            return;
        }

        try {
            List<Job> myJobs = jobService.getJobsByUser(currentUser);
            displayJobs(myJobs);
        } catch (Exception e) {
            System.out.println("Error fetching your jobs: " + e.getMessage());
        }
    }

    private void displayJobs(List<Job> jobs) {
        if (jobs.isEmpty()) {
            System.out.println("No jobs found.");
            return;
        }

        System.out.println("\n=== Jobs ===");
        for (Job job : jobs) {
            System.out.println("\nTitle: " + job.getTitle());
            System.out.println("Description: " + job.getDescription());
            System.out.println("Location: " + job.getLocation());
            System.out.println("Industry: " + job.getIndustry());
            System.out.println("Company: " + job.getCompany().getName());
            System.out.println("------------------------");
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }
}