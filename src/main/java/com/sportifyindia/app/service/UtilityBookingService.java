package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.domain.UtilityBookings;
import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.domain.enumeration.BookingStatusEnum;
import com.sportifyindia.app.domain.enumeration.UtilitySlotStatusEnum;
import com.sportifyindia.app.repository.UserRepository;
import com.sportifyindia.app.repository.UtilityBookingsRepository;
import com.sportifyindia.app.repository.UtilityRepository;
import com.sportifyindia.app.repository.UtilitySlotsRepository;
import com.sportifyindia.app.security.SecurityUtils;
import com.sportifyindia.app.service.dto.BookingRequestDTO;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UtilityBookingService {

    private final Logger log = LoggerFactory.getLogger(UtilityBookingService.class);

    private final UtilityBookingsRepository utilityBookingsRepository;
    private final UtilityRepository utilityRepository;
    private final UserRepository userRepository;
    private final UtilitySlotsRepository utilitySlotsRepository;
    private final UtilitySlotService utilitySlotService;

    public UtilityBookingService(
        UtilityBookingsRepository utilityBookingsRepository,
        UtilityRepository utilityRepository,
        UserRepository userRepository,
        UtilitySlotsRepository utilitySlotsRepository,
        UtilitySlotService utilitySlotService
    ) {
        this.utilityBookingsRepository = utilityBookingsRepository;
        this.utilityRepository = utilityRepository;
        this.userRepository = userRepository;
        this.utilitySlotsRepository = utilitySlotsRepository;
        this.utilitySlotService = utilitySlotService;
    }

    public UtilityBookings createBooking(Long utilityId, Long slotId, Integer quantity) {
        log.debug("Request to create booking for Utility : {} and Slot : {} with quantity : {}", utilityId, slotId, quantity);

        Utility utility = utilityRepository.findById(utilityId).orElseThrow(() -> new IllegalArgumentException("Utility not found"));

        UtilitySlots slot = utilitySlotsRepository
            .findByIdWithLock(slotId)
            .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        if (slot.getStatus() != UtilitySlotStatusEnum.OPEN) {
            throw new IllegalArgumentException("Slot is not available for booking");
        }

        if (slot.getCurrentBookings() + quantity > slot.getMaxCapacity()) {
            throw new IllegalArgumentException("Not enough capacity available");
        }

        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalArgumentException("User not logged in"));
        User user = userRepository.findOneByLogin(userLogin).orElseThrow(() -> new IllegalArgumentException("User not found"));

        UtilityBookings booking = new UtilityBookings();
        booking.setUtility(utility);
        booking.setUtilitySlots(slot);
        booking.setBookedQuantity(quantity);
        booking.setStartTime(slot.getStartTime());
        booking.setEndTime(slot.getEndTime());
        booking.setStatus(BookingStatusEnum.CONFIRMED);
        booking.setAmountPaid(utility.getPricePerSlot().multiply(new java.math.BigDecimal(quantity)));

        // Update slot capacity
        utilitySlotService.updateAvailableCapacity(slot, quantity);

        return utilityBookingsRepository.save(booking);
    }

    public UtilityBookings confirmBooking(Long bookingId) {
        log.debug("Request to confirm booking : {}", bookingId);

        UtilityBookings booking = utilityBookingsRepository
            .findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatusEnum.CONFIRMED) {
            throw new IllegalArgumentException("Booking is not in a pending state");
        }

        booking.setStatus(BookingStatusEnum.CONFIRMED);
        return utilityBookingsRepository.save(booking);
    }

    public UtilityBookings cancelBooking(Long bookingId) {
        log.debug("Request to cancel booking : {}", bookingId);

        UtilityBookings booking = utilityBookingsRepository
            .findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() == BookingStatusEnum.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatusEnum.CANCELLED);

        // Update slot capacity
        UtilitySlots slot = booking.getUtilitySlots();
        slot.setCurrentBookings(slot.getCurrentBookings() - booking.getBookedQuantity());
        if (slot.getCurrentBookings() < slot.getMaxCapacity()) {
            slot.setStatus(UtilitySlotStatusEnum.OPEN);
        }
        utilitySlotsRepository.save(slot);

        return utilityBookingsRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public Page<UtilityBookings> getMyBookings(Pageable pageable) {
        log.debug("Request to get current user's bookings");
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalArgumentException("User not logged in"));
        return utilityBookingsRepository.findByUserLogin(userLogin, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<UtilityBookings> findOne(Long id) {
        log.debug("Request to get booking : {}", id);
        return utilityBookingsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<UtilityBookings> findByUtilityId(Long utilityId, Instant startDate, Instant endDate, Pageable pageable) {
        log.debug("Request to get bookings for utility : {} between dates : {} and {}", utilityId, startDate, endDate);
        return utilityBookingsRepository.findByUtilityIdAndDateRange(utilityId, startDate, endDate, pageable);
    }
}
