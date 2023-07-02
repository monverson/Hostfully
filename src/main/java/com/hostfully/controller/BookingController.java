package com.hostfully.controller;

import com.hostfully.entity.Block;
import com.hostfully.entity.Booking;
import com.hostfully.repository.BlockRepository;
import com.hostfully.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;

    @Autowired
    public BookingController(BookingRepository bookingRepository, BlockRepository blockRepository) {
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        // Check for overlapping bookings or blocks before saving
        if (isBookingOverlap(booking) || isBlockOverlap(booking)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Booking createdBooking = bookingRepository.save(booking);
        return ResponseEntity.ok(createdBooking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking updatedBooking) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }

        // Check for overlapping bookings or blocks before updating
        if (isBookingOverlap(updatedBooking) || isBlockOverlap(updatedBooking)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        booking.setStartDate(updatedBooking.getStartDate());
        booking.setEndDate(updatedBooking.getEndDate());
        bookingRepository.save(booking);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        bookingRepository.delete(booking);
        return ResponseEntity.noContent().build();
    }


    private boolean isBookingOverlap(Booking booking) {
        List<Booking> existingBookings = bookingRepository.findAll();
        return existingBookings.stream().anyMatch(existingBooking -> !existingBooking.getId().equals(booking.getId())
                && booking.getStartDate().isBefore(existingBooking.getEndDate())
                && booking.getEndDate().isAfter(existingBooking.getStartDate()));
    }

    private boolean isBlockOverlap(Booking booking) {
        List<Block> blocks = blockRepository.findAll();
        return blocks.stream().anyMatch(block -> block.getStartDate().isBefore(booking.getEndDate())
                && block.getEndDate().isAfter(booking.getStartDate()));
    }
}