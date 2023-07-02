package com.hostfully.controller;

import com.hostfully.entity.Block;
import com.hostfully.entity.Booking;
import com.hostfully.repository.BlockRepository;
import com.hostfully.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookingControllerTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BlockRepository blockRepository;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_ValidBooking_ReturnsOkResponse() {
        Booking booking = new Booking();
        booking.setStartDate(LocalDate.of(2023, 1, 15));
        booking.setEndDate(LocalDate.of(2023, 1, 20));
        when(bookingRepository.save(booking)).thenReturn(booking);

        ResponseEntity<Booking> response = bookingController.createBooking(booking);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createBooking_OverlappingBooking_ReturnsConflictResponse() {
        Booking booking = new Booking();
        booking.setStartDate(LocalDate.of(2023, 1, 10));
        booking.setEndDate(LocalDate.of(2023, 1, 11));
        when(bookingRepository.findAll()).thenReturn(createBookingList());
        when(blockRepository.findAll()).thenReturn(createBlockList());

        ResponseEntity<Booking> response = bookingController.createBooking(booking);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getAllBookings_ReturnsAllBookings() {
        List<Booking> bookings = createBookingList();
        when(bookingRepository.findAll()).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getAllBookings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    @Test
    void getBookingById_ValidId_ReturnsBooking() {
        Booking booking = new Booking();
        Long id = 1L;
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        ResponseEntity<Booking> response = bookingController.getBookingById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
    }

    @Test
    void getBookingById_InvalidId_ReturnsNotFoundResponse() {
        Long id = 1L;
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Booking> response = bookingController.getBookingById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void updateBooking_ValidIdAndBooking_ReturnsUpdatedBooking() {
        Booking booking = new Booking();
        Booking updatedBooking = new Booking();
        Long id = 1L;
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        ResponseEntity<Booking> response = bookingController.updateBooking(id, updatedBooking);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBooking_InvalidId_ReturnsNotFoundResponse() {
        Long id = 1L;
        Booking updatedBooking = new Booking();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Booking> response = bookingController.updateBooking(id, updatedBooking);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(bookingRepository, never()).save(updatedBooking);
    }

    @Test
    void updateBooking_OverlappingBooking_ReturnsConflictResponse() {
        Booking booking = new Booking();
        booking.setStartDate(LocalDate.of(2023, 1, 10));
        booking.setEndDate(LocalDate.of(2023, 1, 11));
        Booking updatedBooking = new Booking();
        updatedBooking.setStartDate(LocalDate.of(2023, 1, 10));
        updatedBooking.setEndDate(LocalDate.of(2023, 1, 11));
        Long id = 1L;
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(bookingRepository.findAll()).thenReturn(createBookingList());
        when(blockRepository.findAll()).thenReturn(createBlockList());

        ResponseEntity<Booking> response = bookingController.updateBooking(id, updatedBooking);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(bookingRepository, never()).save(updatedBooking);
    }

    @Test
    void deleteBooking_ValidId_ReturnsNoContentResponse() {
        Booking booking = new Booking();
        Long id = 1L;
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        ResponseEntity<Void> response = bookingController.deleteBooking(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookingRepository, times(1)).delete(booking);
    }

    @Test
    void deleteBooking_InvalidId_ReturnsNotFoundResponse() {
        Long id = 1L;
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = bookingController.deleteBooking(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookingRepository, never()).delete(any());
    }

    private List<Booking> createBookingList() {
        // Create and return a list of bookings for testing
        List<Booking> bookings = new ArrayList<>();
        // Add some bookings to the list
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStartDate(LocalDate.of(2023, 1, 1));
        booking1.setEndDate(LocalDate.of(2023, 1, 10));
        bookings.add(booking1);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStartDate(LocalDate.of(2023, 2, 1));
        booking2.setEndDate(LocalDate.of(2023, 2, 10));
        bookings.add(booking2);

        return bookings;
    }

    private List<Block> createBlockList() {
        // Create and return a list of blocks for testing
        List<Block> blocks = new ArrayList<>();
        // Add some blocks to the list
        Block block1 = new Block();
        block1.setId(1L);
        block1.setStartDate(LocalDate.of(2023, 1, 5));
        block1.setEndDate(LocalDate.of(2023, 1, 15));
        blocks.add(block1);

        Block block2 = new Block();
        block2.setId(2L);
        block2.setStartDate(LocalDate.of(2023, 2, 5));
        block2.setEndDate(LocalDate.of(2023, 2, 15));
        blocks.add(block2);

        return blocks;
    }
}