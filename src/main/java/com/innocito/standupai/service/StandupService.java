package com.innocito.standupai.service;

import com.innocito.standupai.dto.StandUpResponseDTO;
import com.innocito.standupai.dto.StandupUpdateRequest;
import com.innocito.standupai.entity.StandupUpdate;
import com.innocito.standupai.entity.User;
import com.innocito.standupai.repository.StandupUpdateRepository;
import com.innocito.standupai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StandupService {

    @Autowired
    private StandupUpdateRepository standupUpdateRepository;

    @Autowired
    private GeminiAIService geminiAIService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    public StandupUpdate saveUpdate(StandupUpdate update) {
        return standupUpdateRepository.save(update);
    }

    public List<StandupUpdate> getTodaysUpdates() {
        return standupUpdateRepository.findByDate(LocalDate.now());
    }

    public String generateAndSendDailySummary() {
        List<StandupUpdate> updates = getTodaysUpdates();

        if (updates.isEmpty()) {
            return "No updates for today";
        }

        String summary = geminiAIService.generateSummary(updates);
        String insights = geminiAIService.generateInsights(updates);

        // Build email content
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Individual Updates:\n\n");

        for (StandupUpdate update : updates) {
            emailContent.append(update.getUser().getFullName()).append(":\n");
            emailContent.append("Yesterday: ").append(update.getYesterday()).append("\n");
            emailContent.append("Today: ").append(update.getToday()).append("\n");
            emailContent.append("Blockers: ").append(update.getBlockers() != null ? update.getBlockers() : "None").append("\n\n");
        }

        emailContent.append("Team Summary (AI-Generated):\n").append(summary).append("\n\n");
        emailContent.append("AI Insights:\n").append(insights).append("\n");

        // Send email to all team members
        for (StandupUpdate update : updates) {
            User user = update.getUser();
            emailService.sendDailySummary(
                    user.getEmail(),
                    "StandUp AI - Daily Summary - " + LocalDate.now(),
                    emailContent.toString()
            );
        }

        return emailContent.toString();
    }
    public List<StandUpResponseDTO> getAllStandupUpdates() {
        List<StandupUpdate> updates = standupUpdateRepository.findAll();

        return updates.stream().map(update -> {
            StandUpResponseDTO dto = new StandUpResponseDTO();
            dto.setYesterday(update.getYesterday());
            dto.setToday(update.getToday());
            dto.setBlockers(update.getBlockers());
            dto.setDate(update.getDate());
            dto.setTeamName(update.getTeamName());
            dto.setTodayMood(update.getTodayMood());
            dto.setFullName(update.getUser().getFullName());  // from User entity
            dto.setEmail(update.getUser().getEmail());
            return dto;
        }).collect(Collectors.toList());
    }
    public int getTotalStandupsByUser(Long userId) {
        return standupUpdateRepository.countByUserId(userId);
    }

    public boolean hasUserSubmittedToday(Long userId) {
        LocalDate today = LocalDate.now();
        return standupUpdateRepository.existsByUserIdAndDate(userId, today);
    }
    public StandupUpdate submitUpdate(StandupUpdateRequest request, String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        LocalDate today = LocalDate.now();

        // ✅ Check if user has already submitted today
        boolean alreadySubmitted = standupUpdateRepository.existsByUserIdAndDate(user.getId(), today);
        if (alreadySubmitted) {
            throw new IllegalArgumentException("You have already submitted your standup today.");
        }

        StandupUpdate update = new StandupUpdate();
        update.setYesterday(request.getYesterday());
        update.setToday(request.getToday());
        update.setBlockers(request.getBlockers());
        update.setDate(today);
        update.setTeamName(request.getTeamName());
        update.setTodayMood(request.getTodayMood());
        update.setUser(user);

        return standupUpdateRepository.save(update);
    }

    public int getTotalStandupsThisMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        return standupUpdateRepository.countByDateBetween(startDate, endDate);
    }

    //    public int getActiveTeamMembersCount() {
//        // Assuming "active participants" means users who submitted at least one standup update
//        List<Long> userIds = standupUpdateRepository.findDistinctUserIds();
//        return userIds.size();
//    }
    public int getTotalUsersCount() {
        return (int) userRepository.count();
    }
    public List<StandUpResponseDTO> getUserStandupHistory(Long userId) {
        List<StandupUpdate> updates = standupUpdateRepository.findByUserId(userId);

        return updates.stream().map(update -> {
            StandUpResponseDTO dto = new StandUpResponseDTO();
            dto.setYesterday(update.getYesterday());
            dto.setToday(update.getToday());
            dto.setBlockers(update.getBlockers());
            dto.setDate(update.getDate());
            dto.setTeamName(update.getTeamName());
            dto.setTodayMood(update.getTodayMood());
            dto.setFullName(update.getUser().getFullName());
            dto.setEmail(update.getUser().getEmail());
            return dto;
        }).collect(Collectors.toList());
    }




}
