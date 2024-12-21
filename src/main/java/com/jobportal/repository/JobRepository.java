package com.jobportal.repository;

import com.jobportal.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j WHERE " +
           "(:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%',:search,'%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%',:search,'%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%',:location,'%'))) AND " +
           "(:industry IS NULL OR j.industry = :industry) AND " +
           "(:experience IS NULL OR j.experienceLevel = :experience) AND " +
           "j.status = 'ACTIVE'")
    Page<Job> searchJobs(@Param("search") String search,
                        @Param("location") String location,
                        @Param("industry") String industry,
                        @Param("experience") Job.ExperienceLevel experience,
                        Pageable pageable);

    List<Job> findByCompanyIdAndStatus(Long companyId, Job.JobStatus status);

    @Query("SELECT DISTINCT j.industry FROM Job j WHERE j.industry IS NOT NULL")
    List<String> findAllIndustries();

    @Query("SELECT DISTINCT j.location FROM Job j WHERE j.location IS NOT NULL")
    List<String> findAllLocations();
}
