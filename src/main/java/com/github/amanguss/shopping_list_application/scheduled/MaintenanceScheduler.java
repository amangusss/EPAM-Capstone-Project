package com.github.amanguss.shopping_list_application.scheduled;

import com.github.amanguss.shopping_list_application.service.ListShareService;
import com.github.amanguss.shopping_list_application.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaintenanceScheduler {

    private final UserSessionService userSessionService;
    private final ListShareService listShareService;

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredSessions() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
            userSessionService.deactivateExpiredSessions(cutoffTime);
            log.info("Expired sessions cleanup completed");
        } catch (Exception e) {
            log.error("Error during session cleanup", e);
        }
    }

    @Scheduled(fixedRate = 7200000)
    public void cleanupExpiredShares() {
        try {
            listShareService.cleanupExpiredShares();
            log.info("Expired shares cleanup completed");
        } catch (Exception e) {
            log.error("Error during shares cleanup", e);
        }
    }
}