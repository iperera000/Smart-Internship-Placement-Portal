package com.example.internship.service;

import com.example.internship.model.InterviewSession;
import com.example.internship.repository.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewSessionRepository sessionRepository;

    public AnalyticsService(StudentRepository studentRepository, CompanyRepository companyRepository,
                            JobPostingRepository jobPostingRepository, ApplicationRepository applicationRepository,
                            InterviewSessionRepository sessionRepository) {
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.applicationRepository = applicationRepository;
        this.sessionRepository = sessionRepository;
    }

    @Cacheable("passRate")
    public double getPassRate() {
        long totalSessions = sessionRepository.count();
        long passedSessions = sessionRepository.findAll().stream().filter(InterviewSession::isPassed).count();
        double passRate = totalSessions > 0 ? ((double) passedSessions / totalSessions) * 100.0 : 0.0;
        return Math.round(passRate * 10.0) / 10.0;
    }

    @Cacheable("topSkills")
    public Map<String, Integer> getTopSkillsInDemand(int limit) {
        return new HashMap<>();
    }

    @Cacheable("trends")
    public Map<String, Integer> getApplicationTrendByMonth(int months) {
        return new HashMap<>();
    }

    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("totalStudents", studentRepository.count());
        metrics.put("totalCompanies", companyRepository.count());
        metrics.put("totalJobs", jobPostingRepository.count());
        metrics.put("totalApplications", applicationRepository.count());

        metrics.put("passRate", getPassRate());

        return metrics;
    }
}