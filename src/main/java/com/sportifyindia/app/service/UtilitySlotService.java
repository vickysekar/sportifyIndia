package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.domain.enumeration.UtilitySlotStatusEnum;
import com.sportifyindia.app.repository.TimeSlotsRepository;
import com.sportifyindia.app.repository.UtilityAvailableDaysRepository;
import com.sportifyindia.app.repository.UtilityRepository;
import com.sportifyindia.app.repository.UtilitySlotsRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UtilitySlotService {

    private final Logger log = LoggerFactory.getLogger(UtilitySlotService.class);

    private final UtilitySlotsRepository utilitySlotsRepository;
    private final UtilityRepository utilityRepository;
    private final UtilityAvailableDaysRepository utilityAvailableDaysRepository;
    private final TimeSlotsRepository timeSlotsRepository;

    public UtilitySlotService(
        UtilitySlotsRepository utilitySlotsRepository,
        UtilityRepository utilityRepository,
        UtilityAvailableDaysRepository utilityAvailableDaysRepository,
        TimeSlotsRepository timeSlotsRepository
    ) {
        this.utilitySlotsRepository = utilitySlotsRepository;
        this.utilityRepository = utilityRepository;
        this.utilityAvailableDaysRepository = utilityAvailableDaysRepository;
        this.timeSlotsRepository = timeSlotsRepository;
    }

    public void generateSlotsForUtility(Utility utility, LocalDate date) {
        log.debug("Generating slots for Utility : {} on date : {}", utility.getId(), date);
        List<UtilityAvailableDays> availableDays = utilityAvailableDaysRepository.findByUtilityId(utility.getId());
        for (UtilityAvailableDays availableDay : availableDays) {
            if (availableDay.getDaysOfWeek().name().equals(date.getDayOfWeek().name())) {
                TimeSlots timeSlot = availableDay.getTimeSlots();
                Instant startTime = date
                    .atTime(timeSlot.getStartTime().atZone(ZoneId.systemDefault()).toLocalTime())
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
                Instant endTime = date
                    .atTime(timeSlot.getEndTime().atZone(ZoneId.systemDefault()).toLocalTime())
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

                UtilitySlots slot = new UtilitySlots();
                slot.setUtility(utility);
                slot.setTimeSlots(timeSlot);
                slot.setDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                slot.setStartTime(startTime);
                slot.setEndTime(endTime);
                slot.setMaxCapacity(utility.getMaxCapacity());
                slot.setCurrentBookings(0);
                slot.setStatus(UtilitySlotStatusEnum.OPEN);

                utilitySlotsRepository.save(slot);
            }
        }
    }

    public void updateAvailableCapacity(UtilitySlots slot, Integer quantity) {
        log.debug("Request to update available capacity for slot : {} with quantity : {}", slot.getId(), quantity);
        slot.setCurrentBookings(slot.getCurrentBookings() + quantity);
        if (slot.getCurrentBookings() >= slot.getMaxCapacity()) {
            slot.setStatus(UtilitySlotStatusEnum.BOOKED);
        }
        utilitySlotsRepository.save(slot);
    }

    @Transactional(readOnly = true)
    public List<UtilitySlots> findByUtilityIdAndDateRange(Long utilityId, Instant startDate, Instant endDate) {
        log.debug("Request to get slots for Utility : {} between dates : {} and {}", utilityId, startDate, endDate);
        return utilitySlotsRepository.findByUtilityIdAndDateRange(utilityId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Utility getUtilityById(Long utilityId) {
        log.debug("Request to get Utility : {}", utilityId);
        return utilityRepository.findById(utilityId).orElseThrow(() -> new IllegalArgumentException("Utility not found"));
    }
}
