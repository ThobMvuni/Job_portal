package com.jobportal.service;

import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    @Autowired
    private ApplicationService applicationService;

    public Page<Job> searchJobs(String search, String location, String industry, 
                              Job.ExperienceLevel experience, Pageable pageable) {
        return jobRepository.searchJobs(search, location, industry, experience, pageable);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    @Transactional
    public Job createJob(Job job) {
        User currentUser = getCurrentUser();
        if (currentUser.getUserType() != User.UserType.EMPLOYER) {
            throw new IllegalStateException("Only employers can create job listings");
        }

        job.setPostedDate(LocalDateTime.now());
        job.setStatus(Job.JobStatus.ACTIVE);
        job.setCompany(currentUser.getCompany());
        
        return jobRepository.save(job);
    }

    @Transactional
    public Job updateJob(Long id, Job jobDetails) {
        Job job = getJobById(id);
        User currentUser = getCurrentUser();

        if (!job.getCompany().getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalStateException("You can only update your own job listings");
        }

        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setRequirements(jobDetails.getRequirements());
        job.setSalaryRange(jobDetails.getSalaryRange());
        job.setLocation(jobDetails.getLocation());
        job.setJobType(jobDetails.getJobType());
        job.setExperienceLevel(jobDetails.getExperienceLevel());
        job.setIndustry(jobDetails.getIndustry());
        job.setDeadlineDate(jobDetails.getDeadlineDate());
        job.setSkills(jobDetails.getSkills());

        return jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = getJobById(id);
        User currentUser = getCurrentUser();

        if (!job.getCompany().getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalStateException("You can only delete your own job listings");
        }

        job.setStatus(Job.JobStatus.CLOSED);
        jobRepository.save(job);
    }

    public List<Job> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyIdAndStatus(companyId, Job.JobStatus.ACTIVE);
    }

    @Transactional
    public void applyForJob(Long jobId) {
        Job job = getJobById(jobId);
        User currentUser = getCurrentUser();

        if (currentUser.getUserType() != User.UserType.JOBSEEKER) {
            throw new IllegalStateException("Only job seekers can apply for jobs");
        }

        if (job.getStatus() != Job.JobStatus.ACTIVE) {
            throw new IllegalStateException("This job is no longer accepting applications");
        }

        if (job.getDeadlineDate() != null && job.getDeadlineDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("The application deadline has passed");
        }

        // Check if already applied
        if (currentUser.getApplications().stream()
                .anyMatch(application -> application.getJob().getId().equals(jobId))) {
            throw new IllegalStateException("You have already applied for this job");
        }

        // Create application
        applicationService.createApplication(currentUser, job);
    }

    // Added methods for console application
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> searchJobs(String search, String location) {
        return jobRepository.searchJobs(search, location, null, null, Pageable.unpaged()).getContent();
    }

    public List<Job> getJobsByUser(User user) {
        return jobRepository.findByCompanyIdAndStatus(user.getCompany().getId(), Job.JobStatus.ACTIVE);
    }

    public Job save(Job job) {
        job.setPostedDate(LocalDateTime.now());
        job.setStatus(Job.JobStatus.ACTIVE);
        return jobRepository.save(job);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
