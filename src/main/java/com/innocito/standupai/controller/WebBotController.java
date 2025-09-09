//package com.innocito.standupai.controller;
//
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import java.time.LocalTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//public class WebBotController {
//
//    // WebSocket endpoint for real-time chat
//    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
//    public Map<String, String> handleChatMessage(Map<String, String> message) {
//        String text = message.get("text").toLowerCase();
//        String response;
//
//        if (text.contains("standup") || text.contains("update")) {
//            response = "📝 To submit your standup, please visit: http://localhost:8080\n\n" +
//                    "Or use this format:\n" +
//                    "• Yesterday: [what you did]\n" +
//                    "• Today: [what you'll do]\n" +
//                    "• Blockers: [any issues]";
//        }
//        else if (text.contains("summary") || text.contains("report")) {
//            response = "📊 Daily summaries are available at: http://localhost:8080\n\n" +
//                    "I'll notify you when summary is ready!";
//        }
//        else if (text.contains("help")) {
//            response = "🤖 StandUp AI Bot Help:\n\n" +
//                    "• Type 'standup' - Submit daily update\n" +
//                    "• Type 'summary' - Get team summary\n" +
//                    "• Visit http://localhost:8080 - Full web app";
//        }
//        else {
//            response = "👋 Hello! I'm StandUp AI Bot!\n\n" +
//                    "I can help with:\n" +
//                    "• Daily standup updates\n" +
//                    "• Team summaries\n" +
//                    "• Blockers tracking\n\n" +
//                    "Type 'help' for options or visit our web app!";
//        }
//
//        Map<String, String> botResponse = new HashMap<>();
//        botResponse.put("text", response);
//        botResponse.put("timestamp", LocalTime.now().toString());
//        botResponse.put("from", "StandUp-Bot");
//
//        return botResponse;
//    }
//
//    // REST endpoint for simple HTTP requests
//    @PostMapping("/api/bot/message")
//    @ResponseBody
//    public Map<String, String> handleBotMessage(@RequestBody Map<String, String> request) {
//        String text = request.get("text").toLowerCase();
//        Map<String, String> response = new HashMap<>();
//
//        if (text.contains("standup")) {
//            response.put("response", "Please submit standups at: http://localhost:8080/standup");
//        } else {
//            response.put("response", "StandUp AI Bot is here! Use our web app for full features.");
//        }
//
//        return response;
//    }
//}
//package com.innocito.standupai.controller;
//
//import com.innocito.standupai.dto.StandupUpdateRequest;
//import com.innocito.standupai.entity.StandupUpdate;
//import com.innocito.standupai.service.StandupService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//import java.time.LocalTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//public class WebBotController {
//
//    @Autowired
//    private StandupService standupService;
//
//    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
//    public Map<String, String> handleChatMessage(@Payload Map<String, String> message) {
//        String text = message.get("text").toLowerCase();
//        String from = message.getOrDefault("from", "User");
//
//
//        String response;
//
//        // 🔍 Try parsing standup format
//        if (text.contains("yesterday:") && text.contains("today:")) {
//            try {
//                StandupUpdateRequest updateRequest = parseUpdateFromText(text);
//                StandupUpdate saved = standupService.submitUpdate(updateRequest, from);
//
//                response = "✅ Your standup has been saved successfully!";
//            } catch (Exception e) {
//                response = "❌ Failed to save your update. Make sure the format is correct and username exists.";
//            }
//        }
//        // 🔍 Help commands
//        else if (text.contains("standup") || text.contains("update")) {
//            response = "📝 To submit your standup, use this format:\n" +
//                    "• Yesterday: [what you did]\n" +
//                    "• Today: [what you'll do]\n" +
//                    "• Blockers: [any issues]";
//        } else if (text.contains("summary")) {
//            response = "📊 Summaries are available at: http://localhost:8080";
//        } else if (text.contains("help")) {
//            response = "🤖 Help Menu:\n" +
//                    "• Type your standup using format\n" +
//                    "• Type 'summary' - Get team summary\n" +
//                    "• Visit http://localhost:8080 - Full web app";
//        } else {
//            response = "👋 Hello! I'm StandUp Bot. Type 'help' to get started.";
//        }
//
//        Map<String, String> botResponse = new HashMap<>();
//        botResponse.put("text", response);
//        botResponse.put("timestamp", LocalTime.now().toString());
//        botResponse.put("from", "StandUp-Bot");
//        return botResponse;
//    }
//
//    private StandupUpdateRequest parseUpdateFromText(String text) {
//        StandupUpdateRequest request = new StandupUpdateRequest();
//
//        String[] lines = text.split("\n");
//        for (String line : lines) {
//            if (line.toLowerCase().startsWith("yesterday:")) {
//                request.setYesterday(line.substring(10).trim());
//            } else if (line.toLowerCase().startsWith("today:")) {
//                request.setToday(line.substring(6).trim());
//            } else if (line.toLowerCase().startsWith("blockers:")) {
//                request.setBlockers(line.substring(9).trim());
//            }
//        }
//
//        request.setTeamName("DefaultTeam"); // or parse from text
//        request.setTodayMood("🙂"); // optional default mood
//
//
//        return request;
//    }
//}
package com.innocito.standupai.controller;

import com.innocito.standupai.dto.StandupUpdateRequest;
import com.innocito.standupai.entity.StandupUpdate;
import com.innocito.standupai.service.StandupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebBotController {

    @Autowired
    private StandupService standupService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Map<String, String> handleChatMessage(@Payload Map<String, String> message) {
        String text = message.get("text").toLowerCase();
        String from = message.getOrDefault("from", "User");
        String mood = message.getOrDefault("mood", "🙂");
        String team = message.getOrDefault("team", "DefaultTeam");

        String response;

        if (text.contains("yesterday:") && text.contains("today:")) {
            try {
                StandupUpdateRequest updateRequest = parseUpdateFromText(text);
                updateRequest.setTodayMood(mood);  // 👈 Set mood
                updateRequest.setTeamName(team);   // 👈 Set team
                StandupUpdate saved = standupService.submitUpdate(updateRequest, from);

                response = "✅ Your standup has been saved successfully for team: " + team;
            } catch (Exception e) {
                response = "❌ Failed to save your update. Ensure the format is correct and username exists.";
            }
        }
        else if (text.contains("standup") || text.contains("update")) {
            response = "📝 Use this format:\n" +
                    "• Yesterday: [what you did]\n" +
                    "• Today: [what you'll do]\n" +
                    "• Blockers: [any issues]";
        }
        else if (text.contains("summary")) {
            response = "📊 Team summaries available at: StandUp AI Application";
        }
        else if (text.contains("help")) {
            response = "🤖 Help:\n" +
                    "• Type your standup in the given format\n" +
                    "• Choose mood & team\n" +
                    "• Visit http://localhost:8080";
        }
        else {
            response = "👋 Hello! I'm StandUp Bot. Type 'help' to begin.";
        }

        Map<String, String> botResponse = new HashMap<>();
        botResponse.put("text", response);
        botResponse.put("timestamp", LocalTime.now().toString());
        botResponse.put("from", "StandUp-Bot");
        return botResponse;
    }

    private StandupUpdateRequest parseUpdateFromText(String text) {
        StandupUpdateRequest request = new StandupUpdateRequest();

        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().startsWith("yesterday:")) {
                request.setYesterday(line.substring(10).trim());
            } else if (line.toLowerCase().startsWith("today:")) {
                request.setToday(line.substring(6).trim());
            } else if (line.toLowerCase().startsWith("blockers:")) {
                request.setBlockers(line.substring(9).trim());
            }
        }

        return request;
    }
}

