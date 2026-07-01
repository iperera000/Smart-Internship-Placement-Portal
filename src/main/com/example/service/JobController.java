package com.example.internship.controller;

import com.example.internship.dto.CvRankResult;
import com.example.internship.model.Company;
import com.example.internship.model.JobPosting;
import com.example.internship.model.Student;
import com.example.internship.repository.CompanyRepository;
import com.example.internship.repository.JobPostingRepository;
import com.example.internship.repository.StudentRepository;
import com.example.internship.repository.UserRepository;
import com.example.internship.service.CvFilterService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/company/jobs")
public class JobController {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CvFilterService cvFilterService;

    public JobController(JobPostingRepository jobPostingRepository, CompanyRepository companyRepository,
                         UserRepository userRepository, StudentRepository studentRepository,
                         CvFilterService cvFilterService) {
        this.jobPostingRepository = jobPostingRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.cvFilterService = cvFilterService;
    }

    @GetMapping("/post")
    public String showPostJobForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();
        Optional<Company> companyOpt = companyRepository.findByUserId(userId);

        model.addAttribute("job", new JobPosting());
        return "company/post-job";
    }

    @PostMapping("/post")
    public String saveJob(@ModelAttribute JobPosting job, Authentication authentication, Model model) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();
        Company company = companyRepository.findByUserId(userId).orElseThrow();
        job.setCompany(company);
        jobPostingRepository.save(job);
        model.addAttribute("msg", "Internship Job posted successfully!");
        model.addAttribute("job", new JobPosting());
        return "company/post-job";
    }

    @GetMapping("/manage")
    public String manageJobs(Authentication authentication, Model model) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();
        Company company = companyRepository.findByUserId(userId).orElseThrow();
        List<JobPosting> jobs = jobPostingRepository.findByCompanyId(company.getId());
        model.addAttribute("jobs", jobs);
        return "company/manage-jobs";
    }

    @PostMapping("/{id}/close")
    public String closeJob(@PathVariable Long id) {
        JobPosting job = jobPostingRepository.findById(id).orElseThrow();
        job.setStatus("CLOSED");
        jobPostingRepository.save(job);
        return "redirect:/company/jobs/manage?msg=Job successfully closed.";
    }

    // --- MEMBER 6 ADDITION: CV RANKING ENDPOINT ---
    @GetMapping("/{id}/ranked-applicants")
    public String showRankedApplicants(@PathVariable Long id, Model model) {
        JobPosting job = jobPostingRepository.findById(id).orElseThrow();

        List<Student> allStudents = studentRepository.findAll();
        List<CvRankResult> rankedResults = cvFilterService.rankApplicants(job, allStudents);

        model.addAttribute("job", job);
        model.addAttribute("rankedResults", rankedResults);
        return "company/applicants";
    }
}
