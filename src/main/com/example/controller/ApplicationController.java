package com.portal.internship.controller;

import com.portal.internship.entity.JobApplication;
import com.portal.internship.entity.ApplicationStatus;
import com.portal.internship.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }


    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(@RequestBody JobApplication application) {
        try {
            JobApplication savedApp = service.applyForJob(application);
            return ResponseEntity.ok(savedApp);
        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<JobApplication> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(service.updateApplicationStatus(id, status));
    }
}