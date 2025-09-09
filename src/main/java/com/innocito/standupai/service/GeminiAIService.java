package com.innocito.standupai.service;

import com.innocito.standupai.entity.StandupUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

@Service
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String generateSummary(List<StandupUpdate> updates) {
        String prompt = buildSummaryPrompt(updates);
        return callGeminiSimple(prompt);
    }

    public String generateInsights(List<StandupUpdate> updates) {
        String prompt = buildInsightsPrompt(updates);
        return callGeminiSimple(prompt);
    }

    private String callGeminiSimple(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Corrected URL using the latest Gemini model
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

            Map<String, Object> request = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();

            part.put("text", prompt);
            content.put("parts", new Object[]{part});
            request.put("contents", new Object[]{content});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Parse the actual response from Gemini API
            return parseGeminiResponse(response.getBody());

        } catch (Exception e) {
            System.out.println("Gemini API Error: " + e.getMessage());
            e.printStackTrace();
            return "AI service temporarily unavailable. Here's a basic summary:\n" +
                    generateBasicSummaryFromPrompt(prompt);
        }
    }

    private String parseGeminiResponse(Map<String, Object> response) {
        try {
            // Extract the text from the complex Gemini API response
            if (response != null && response.containsKey("candidates")) {
                ArrayList<Map<String, Object>> candidates = (ArrayList<Map<String, Object>>) response.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    if (candidate.containsKey("content")) {
                        Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                        if (content.containsKey("parts")) {
                            ArrayList<Map<String, Object>> parts = (ArrayList<Map<String, Object>>) content.get("parts");
                            if (parts != null && !parts.isEmpty()) {
                                Map<String, Object> firstPart = parts.get(0);
                                if (firstPart.containsKey("text")) {
                                    return (String) firstPart.get("text");
                                }
                            }
                        }
                    }
                }
            }
            return "Unable to parse AI response. Response structure: " + (response != null ? response.keySet() : "null");
        } catch (Exception e) {
            System.out.println("Error parsing Gemini response: " + e.getMessage());
            return "Error parsing AI response: " + e.getMessage();
        }
    }

    private String generateBasicSummaryFromPrompt(String prompt) {
        // Extract key information from the prompt and create a basic summary
        if (prompt.contains("Yesterday")) {
            return "Team completed various tasks yesterday and has plans for today. " +
                    "Some blockers may need attention.";
        }
        return "Team is progressing well with their tasks.";
    }

    private String buildSummaryPrompt(List<StandupUpdate> updates) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Generate a concise team summary based on these standup updates:\n\n");

        for (StandupUpdate update : updates) {
            promptBuilder.append(update.getUser().getFullName()).append(":\n");
            promptBuilder.append("Yesterday: ").append(update.getYesterday()).append("\n");
            promptBuilder.append("Today: ").append(update.getToday()).append("\n");
            if (update.getBlockers() != null && !update.getBlockers().isEmpty()) {
                promptBuilder.append("Blockers: ").append(update.getBlockers()).append("\n");
            }
            promptBuilder.append("TeamName: ").append(update.getTeamName()).append("\n");
            promptBuilder.append("\n");
        }

        promptBuilder.append("Please provide a concise team summary highlighting:" +
                "\n1. What the team accomplished yesterday" +
                "\n2. What the team plans to work on today" +
                "\n3. Any blockers or dependencies" +
                "\nKeep it professional and concise.");

        return promptBuilder.toString();
    }

    private String buildInsightsPrompt(List<StandupUpdate> updates) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Analyze these standup updates and provide insights:\n\n");

        for (StandupUpdate update : updates) {
            promptBuilder.append(update.getUser().getFullName()).append(":\n");
            promptBuilder.append("Yesterday: ").append(update.getYesterday()).append("\n");
            promptBuilder.append("Today: ").append(update.getToday()).append("\n");
            if (update.getBlockers() != null && !update.getBlockers().isEmpty()) {
                promptBuilder.append("Blockers: ").append(update.getBlockers()).append("\n");
            }
            promptBuilder.append("TeamName: ").append(update.getTeamName()).append("\n");
            promptBuilder.append("\n");
        }

        promptBuilder.append("Please analyze and provide insights on:" +
                "\n1. Repeated blockers or patterns" +
                "\n2. Trends in the work" +
                "\n3. Potential risks or dependencies" +
                "\n4. Any other notable observations" +
                "\n5. Team Name" +
                "\nFormat with emojis and be concise.");

        return promptBuilder.toString();
    }
}