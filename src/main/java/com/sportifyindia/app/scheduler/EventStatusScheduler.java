package com.sportifyindia.app.scheduler;

import com.sportifyindia.app.service.OneTimeEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for updating event statuses.
 */
@Component
public class EventStatusScheduler {

    private final Logger log = LoggerFactory.getLogger(EventStatusScheduler.class);

    private final OneTimeEventService oneTimeEventService;

    public EventStatusScheduler(OneTimeEventService oneTimeEventService) {
        this.oneTimeEventService = oneTimeEventService;
    }

    /**
     * Update status of expired events to COMPLETED.
     * Runs at 00:01 AM every day.
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void updateExpiredEventsStatus() {
        log.info("Starting scheduled task to update expired events status");
        try {
            oneTimeEventService.updateExpiredEventsStatus();
            log.info("Successfully completed scheduled task to update expired events status");
        } catch (Exception e) {
            log.error("Error occurred while updating expired events status", e);
        }
    }
}
