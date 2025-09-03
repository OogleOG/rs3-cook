package net.botwithus.rs3cook.statistics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.botwithus.rs3cook.data.CookingUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Handles logging of cooking sessions and statistics
 */
public class SessionLogger {
    
    private static final String LOG_DIR = System.getProperty("user.home") + File.separator + "BotWithUs" + File.separator + "rs3cook" + File.separator + "logs";
    private static final String SESSION_LOG_FILE = "sessions.json";
    private static final String DAILY_LOG_FILE = "daily_log.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    
    private List<SessionRecord> sessionHistory = new ArrayList<>();
    private PrintWriter dailyLogWriter;
    private String currentLogDate;
    
    public SessionLogger() {
        loadSessionHistory();
        initializeDailyLog();
    }
    
    /**
     * Log the start of a new session
     */
    public void logSessionStart(String fishType, String location) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        logToDaily("=== SESSION START ===");
        logToDaily("Time: " + timestamp);
        logToDaily("Fish: " + fishType);
        logToDaily("Location: " + location);
        logToDaily("========================");
    }
    
    /**
     * Log a cooking action
     */
    public void logCookingAction(String fishType, boolean successful, boolean burned) {
        String result = successful ? "SUCCESS" : (burned ? "BURNED" : "FAILED");
        logToDaily("COOK: " + fishType + " - " + result);
    }
    
    /**
     * Log a banking action
     */
    public void logBankingAction(String action, long duration) {
        logToDaily("BANK: " + action + " (" + (duration / 1000) + "s)");
    }
    
    /**
     * Log a level up
     */
    public void logLevelUp(int oldLevel, int newLevel, long experience) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        logToDaily("*** LEVEL UP! ***");
        logToDaily("Time: " + timestamp);
        logToDaily("Level: " + oldLevel + " -> " + newLevel);
        logToDaily("Experience: " + CookingUtils.formatExperience(experience));
        logToDaily("*****************");
    }
    
    /**
     * Log session end and save session record
     */
    public void logSessionEnd(StatisticsManager.StatisticsReport report) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        
        // Log to daily file
        logToDaily("=== SESSION END ===");
        logToDaily("Time: " + timestamp);
        logToDaily("Duration: " + CookingUtils.formatTime(report.sessionDuration));
        logToDaily("Experience: " + CookingUtils.formatExperience(report.experienceGained));
        logToDaily("XP/Hour: " + CookingUtils.formatExperience(report.currentXpPerHour));
        logToDaily("Fish Cooked: " + report.fishCooked);
        logToDaily("Fish Burned: " + report.fishBurned);
        logToDaily("Success Rate: " + String.format("%.1f%%", report.successRate * 100));
        logToDaily("Banking Trips: " + report.bankingTrips);
        if (report.levelsGained > 0) {
            logToDaily("Levels Gained: " + report.levelsGained);
        }
        logToDaily("===================");
        
        // Create session record
        SessionRecord session = new SessionRecord(
            LocalDateTime.now(),
            report.sessionDuration,
            report.experienceGained,
            report.currentXpPerHour,
            report.fishCooked,
            report.fishBurned,
            report.successRate,
            report.bankingTrips,
            report.levelsGained,
            new HashMap<>(report.fishStats)
        );
        
        // Add to history and save
        sessionHistory.add(session);
        saveSessionHistory();
        
        // Close daily log
        closeDailyLog();
    }
    
    /**
     * Log an error or warning
     */
    public void logError(String message, Exception e) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        logToDaily("ERROR [" + timestamp + "]: " + message);
        if (e != null) {
            logToDaily("Exception: " + e.getMessage());
            // Log stack trace to daily log
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logToDaily(sw.toString());
        }
    }
    
    /**
     * Log a warning
     */
    public void logWarning(String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        logToDaily("WARNING [" + timestamp + "]: " + message);
    }
    
    /**
     * Log general information
     */
    public void logInfo(String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        logToDaily("INFO [" + timestamp + "]: " + message);
    }
    
    /**
     * Get session statistics for a date range
     */
    public SessionStatistics getSessionStatistics(LocalDateTime from, LocalDateTime to) {
        List<SessionRecord> filteredSessions = sessionHistory.stream()
                .filter(session -> session.timestamp.isAfter(from) && session.timestamp.isBefore(to))
                .toList();
        
        if (filteredSessions.isEmpty()) {
            return new SessionStatistics();
        }
        
        long totalDuration = filteredSessions.stream().mapToLong(s -> s.sessionDuration).sum();
        long totalExperience = filteredSessions.stream().mapToLong(s -> s.experienceGained).sum();
        int totalFishCooked = filteredSessions.stream().mapToInt(s -> s.fishCooked).sum();
        int totalFishBurned = filteredSessions.stream().mapToInt(s -> s.fishBurned).sum();
        int totalBankingTrips = filteredSessions.stream().mapToInt(s -> s.bankingTrips).sum();
        int totalLevelsGained = filteredSessions.stream().mapToInt(s -> s.levelsGained).sum();
        
        double averageSuccessRate = filteredSessions.stream()
                .mapToDouble(s -> s.successRate)
                .average()
                .orElse(0.0);
        
        long averageXpPerHour = totalDuration > 0 ? (totalExperience * 3600000) / totalDuration : 0;
        
        return new SessionStatistics(
            filteredSessions.size(),
            totalDuration,
            totalExperience,
            averageXpPerHour,
            totalFishCooked,
            totalFishBurned,
            averageSuccessRate,
            totalBankingTrips,
            totalLevelsGained
        );
    }
    
    /**
     * Get today's statistics
     */
    public SessionStatistics getTodayStatistics() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return getSessionStatistics(startOfDay, endOfDay);
    }
    
    /**
     * Get this week's statistics
     */
    public SessionStatistics getWeekStatistics() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();
        return getSessionStatistics(startOfWeek, now);
    }
    
    /**
     * Initialize daily log file
     */
    private void initializeDailyLog() {
        try {
            // Create log directory if it doesn't exist
            Path logDir = Paths.get(LOG_DIR);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            // Create daily log file
            currentLogDate = LocalDateTime.now().format(FILE_DATE_FORMAT);
            String dailyLogFileName = "cooking_" + currentLogDate + ".log";
            Path dailyLogPath = logDir.resolve(dailyLogFileName);
            
            dailyLogWriter = new PrintWriter(new FileWriter(dailyLogPath.toFile(), true));
            
        } catch (IOException e) {
            System.err.println("Failed to initialize daily log: " + e.getMessage());
        }
    }
    
    /**
     * Log message to daily log file
     */
    private void logToDaily(String message) {
        if (dailyLogWriter != null) {
            dailyLogWriter.println(message);
            dailyLogWriter.flush();
        }
        // Also print to console for debugging
        System.out.println(message);
    }
    
    /**
     * Close daily log file
     */
    private void closeDailyLog() {
        if (dailyLogWriter != null) {
            dailyLogWriter.close();
            dailyLogWriter = null;
        }
    }
    
    /**
     * Load session history from file
     */
    private void loadSessionHistory() {
        try {
            Path sessionFile = Paths.get(LOG_DIR, SESSION_LOG_FILE);
            if (Files.exists(sessionFile)) {
                try (FileReader reader = new FileReader(sessionFile.toFile())) {
                    SessionRecord[] sessions = gson.fromJson(reader, SessionRecord[].class);
                    if (sessions != null) {
                        sessionHistory = new ArrayList<>(Arrays.asList(sessions));
                        System.out.println("Loaded " + sessionHistory.size() + " session records");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load session history: " + e.getMessage());
        }
    }
    
    /**
     * Save session history to file
     */
    private void saveSessionHistory() {
        try {
            Path logDir = Paths.get(LOG_DIR);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            Path sessionFile = logDir.resolve(SESSION_LOG_FILE);
            try (FileWriter writer = new FileWriter(sessionFile.toFile())) {
                gson.toJson(sessionHistory, writer);
            }
            
        } catch (IOException e) {
            System.err.println("Failed to save session history: " + e.getMessage());
        }
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        closeDailyLog();
    }
    
    // Data classes
    public static class SessionRecord {
        public final LocalDateTime timestamp;
        public final long sessionDuration;
        public final long experienceGained;
        public final long xpPerHour;
        public final int fishCooked;
        public final int fishBurned;
        public final double successRate;
        public final int bankingTrips;
        public final int levelsGained;
        public final Map<String, StatisticsManager.FishStatistics> fishStats;
        
        public SessionRecord(LocalDateTime timestamp, long sessionDuration, long experienceGained,
                           long xpPerHour, int fishCooked, int fishBurned, double successRate,
                           int bankingTrips, int levelsGained, Map<String, StatisticsManager.FishStatistics> fishStats) {
            this.timestamp = timestamp;
            this.sessionDuration = sessionDuration;
            this.experienceGained = experienceGained;
            this.xpPerHour = xpPerHour;
            this.fishCooked = fishCooked;
            this.fishBurned = fishBurned;
            this.successRate = successRate;
            this.bankingTrips = bankingTrips;
            this.levelsGained = levelsGained;
            this.fishStats = fishStats;
        }
    }
    
    public static class SessionStatistics {
        public final int sessionCount;
        public final long totalDuration;
        public final long totalExperience;
        public final long averageXpPerHour;
        public final int totalFishCooked;
        public final int totalFishBurned;
        public final double averageSuccessRate;
        public final int totalBankingTrips;
        public final int totalLevelsGained;
        
        public SessionStatistics() {
            this(0, 0, 0, 0, 0, 0, 0.0, 0, 0);
        }
        
        public SessionStatistics(int sessionCount, long totalDuration, long totalExperience,
                               long averageXpPerHour, int totalFishCooked, int totalFishBurned,
                               double averageSuccessRate, int totalBankingTrips, int totalLevelsGained) {
            this.sessionCount = sessionCount;
            this.totalDuration = totalDuration;
            this.totalExperience = totalExperience;
            this.averageXpPerHour = averageXpPerHour;
            this.totalFishCooked = totalFishCooked;
            this.totalFishBurned = totalFishBurned;
            this.averageSuccessRate = averageSuccessRate;
            this.totalBankingTrips = totalBankingTrips;
            this.totalLevelsGained = totalLevelsGained;
        }
    }
}
