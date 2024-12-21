package com.jobportal.service;

import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserService userService;

    public Application createApplication(User applicant, Job job) {
        Application application = new Application();
        application.setUser(applicant);
        application.setJob(job);
        application.setStatus(Application.Status.PENDING);
        return applicationRepository.save(application);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
    }

    public Page<Application> getApplicationsByUser(User user, Pageable pageable) {
        return applicationRepository.findByUser(user, pageable);
    }

    public Page<Application> getApplicationsByJob(Job job, Pageable pageable) {
        return applicationRepository.findByJob(job, pageable);
    }

    public Application updateApplicationStatus(Long id, Application.Status status) {
        Application application = getApplicationById(id);
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        Application application = getApplicationById(id);
        applicationRepository.delete(application);
    }
}
