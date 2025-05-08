package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.domain.UtilityBookings;
import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.service.UtilityBookingService;
import com.sportifyindia.app.service.UtilitySlotService;
import com.sportifyindia.app.service.dto.BookingRequestDTO;
import com.sportifyindia.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api")
public class UtilityBookingResource {

    private final Logger log = LoggerFactory.getLogger(UtilityBookingResource.class);
    private static final String ENTITY_NAME = "utilityBooking";
    private static final int MAX_DAYS_RANGE = 7;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtilityBookingService utilityBookingService;
    private final UtilitySlotService utilitySlotService;

    public UtilityBookingResource(UtilityBookingService utilityBookingService, UtilitySlotService utilitySlotService) {
        this.utilityBookingService = utilityBookingService;
        this.utilitySlotService = utilitySlotService;
    }

    @GetMapping("/utilities/{utilityId}/slots")
    public ResponseEntity<List<UtilitySlots>> getUtilitySlots(
        @PathVariable Long utilityId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        log.debug("REST request to get slots for Utility : {} from date : {} to date : {}", utilityId, fromDate, toDate);

        // If toDate is not provided, use fromDate
        LocalDate endDate = toDate != null ? toDate : fromDate;

        // Validate date range
        long daysBetween = ChronoUnit.DAYS.between(fromDate, endDate);
        if (daysBetween < 0) {
            throw new BadRequestAlertException("End date cannot be before start date", ENTITY_NAME, "invalidDateRange");
        }
        if (daysBetween > MAX_DAYS_RANGE) {
            throw new BadRequestAlertException("Date range cannot exceed " + MAX_DAYS_RANGE + " days", ENTITY_NAME, "dateRangeTooLarge");
        }

        // Convert dates to Instant
        Instant startOfFromDate = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfToDate = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        // Check if slots exist for the given date range
        List<UtilitySlots> slots = utilitySlotService.findByUtilityIdAndDateRange(utilityId, startOfFromDate, endOfToDate);

        // Generate slots for any missing dates
        for (LocalDate date = fromDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            boolean hasSlotsForDate = slots
                .stream()
                .anyMatch(slot -> slot.getDate().equals(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            if (!hasSlotsForDate) {
                utilitySlotService.generateSlotsForUtility(utilitySlotService.getUtilityById(utilityId), currentDate);
            }
        }

        // Fetch all slots for the date range
        slots = utilitySlotService.findByUtilityIdAndDateRange(utilityId, startOfFromDate, endOfToDate);

        return ResponseEntity.ok().body(slots);
    }

    @GetMapping("/utilities/{utilityId}/bookings")
    public ResponseEntity<Page<UtilityBookings>> getBookingsByUtility(
        @PathVariable Long utilityId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Pageable pageable,
        UriComponentsBuilder uriBuilder
    ) {
        log.debug("REST request to get bookings for Utility : {}", utilityId);

        Instant start = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant end = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

        Page<UtilityBookings> page = utilityBookingService.findByUtilityId(utilityId, start, end, pageable);
        return ResponseEntity.ok().headers(PaginationUtil.generatePaginationHttpHeaders(uriBuilder, page)).body(page);
    }

    @PostMapping("/utility-bookings")
    public ResponseEntity<UtilityBookings> createBooking(@RequestBody BookingRequestDTO bookingRequest) throws URISyntaxException {
        log.debug("REST request to create a booking : {}", bookingRequest);
        if (bookingRequest.getUtilityId() == null || bookingRequest.getSlotId() == null || bookingRequest.getQuantity() == null) {
            throw new BadRequestAlertException("Invalid booking request", ENTITY_NAME, "idnull");
        }

        UtilityBookings result = utilityBookingService.createBooking(
            bookingRequest.getUtilityId(),
            bookingRequest.getSlotId(),
            bookingRequest.getQuantity()
        );

        return ResponseEntity
            .created(new URI("/api/utility-bookings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/utility-bookings/{id}/confirm")
    public ResponseEntity<UtilityBookings> confirmBooking(@PathVariable Long id) {
        log.debug("REST request to confirm booking : {}", id);
        UtilityBookings result = utilityBookingService.confirmBooking(id);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/utility-bookings/{id}/cancel")
    public ResponseEntity<UtilityBookings> cancelBooking(@PathVariable Long id) {
        log.debug("REST request to cancel booking : {}", id);
        UtilityBookings result = utilityBookingService.cancelBooking(id);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/utility-bookings")
    public ResponseEntity<Page<UtilityBookings>> getMyBookings(Pageable pageable, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get current user's bookings");
        Page<UtilityBookings> page = utilityBookingService.getMyBookings(pageable);
        return ResponseEntity.ok().headers(PaginationUtil.generatePaginationHttpHeaders(uriBuilder, page)).body(page);
    }

    @GetMapping("/utility-bookings/{id}")
    public ResponseEntity<UtilityBookings> getBooking(@PathVariable Long id) {
        log.debug("REST request to get booking : {}", id);
        Optional<UtilityBookings> booking = utilityBookingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(booking);
    }
}
