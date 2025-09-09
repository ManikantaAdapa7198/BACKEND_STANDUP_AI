package com.innocito.standupai.controller;

import com.innocito.standupai.dto.StandUpResponseDTO;
import com.innocito.standupai.dto.StandupUpdateRequest;
import com.innocito.standupai.entity.StandupUpdate;
import com.innocito.standupai.entity.User;
import com.innocito.standupai.repository.StandupUpdateRepository;
import com.innocito.standupai.repository.UserRepository;
import com.innocito.standupai.service.StandupService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/standup")
public class StandupController {


    @Autowired
    private StandupService standupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StandupUpdateRepository standupUpdateRepository;
    @PostMapping("/update")
    public ResponseEntity<?> submitUpdate(@RequestBody StandupUpdateRequest request, Authentication authentication) {
        try {
            StandupUpdate savedUpdate = standupService.submitUpdate(request, authentication.getName());
            return ResponseEntity.ok(savedUpdate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate-summary")
    public ResponseEntity<?> generateSummary() {
        String summary = standupService.generateAndSendDailySummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/updates/today")
    public ResponseEntity<?> getTodaysUpdates()
    {
        return ResponseEntity.ok(standupService.getTodaysUpdates());
    }
    @PostMapping("/send-daily-summary")
    public ResponseEntity<String> sendDailySummaryEmail() {
        String emailResult = standupService.generateAndSendDailySummary();
        return ResponseEntity.ok(emailResult);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all/updates")
    public ResponseEntity<List<StandUpResponseDTO>> getAllStandups() {
        List<StandUpResponseDTO> updates = standupService.getAllStandupUpdates();
        return ResponseEntity.ok(updates);
    }
    @GetMapping("/total/{userId}")
    public ResponseEntity<Integer> getTotalStandups(@PathVariable Long userId) {
        int total = standupService.getTotalStandupsByUser(userId);
        return ResponseEntity.ok(total);
    }

    // API 2: Check if user has submitted today's standup
    @GetMapping("/status/{userId}")
    public ResponseEntity<String> getTodaysStatus(@PathVariable Long userId) {
        boolean submitted = standupService.hasUserSubmittedToday(userId);
        String status = submitted ? "Complete" : "Incomplete";
        return ResponseEntity.ok(status);
    }
    @GetMapping("/total/this-month")
    public ResponseEntity<Integer> getTotalStandupsThisMonth() {
        int total = standupService.getTotalStandupsThisMonth();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/team-members/count")
    public ResponseEntity<Integer> getTotalUsersCount() {
        int totalUsers = standupService.getTotalUsersCount();
        return ResponseEntity.ok(totalUsers);
    }
    @GetMapping("/my-history")
    public ResponseEntity<?> getMyHistory() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Unauthorized");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var optionalUser = userRepository.findByUsername(userDetails.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("User not found");
        }

        var user = optionalUser.get();
        var updates = standupService.getAllStandupUpdates()
                .stream()
                .filter(dto -> dto.getEmail().equals(user.getEmail())) // filter by current user
                .toList();

        return ResponseEntity.ok(updates);
    }


}