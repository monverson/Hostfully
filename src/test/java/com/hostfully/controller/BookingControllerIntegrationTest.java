package com.hostfully.controller;

import com.hostfully.entity.Booking;
import com.hostfully.repository.BlockRepository;
import com.hostfully.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private BlockRepository blockRepository;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDate.now());
        booking.setEndDate(LocalDate.now().plusDays(3));
    }

    @Test
    void createBooking_ValidBooking_ReturnsOk() throws Exception {
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2023-07-01\", \"endDate\": \"2023-07-04\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    void createBooking_OverlapBooking_ReturnsConflict() throws Exception {
        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2023-07-02\", \"endDate\": \"2023-07-05\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllBookings_ReturnsOk() throws Exception {
        List<Booking> bookings = Collections.singletonList(booking);
        when(bookingRepository.findAll()).thenReturn(bookings);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId()));
    }

    @Test
    void getBookingById_ExistingId_ReturnsOk() throws Exception {
        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(java.util.Optional.ofNullable(booking));

        mockMvc.perform(get("/bookings/{id}", booking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    void getBookingById_NonExistingId_ReturnsNotFound() throws Exception {
        when(bookingRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/bookings/{id}", 100L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBooking_ExistingId_ValidBooking_ReturnsOk() throws Exception {
        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(java.util.Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(put("/bookings/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2023-07-01\", \"endDate\": \"2023-07-04\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    void updateBooking_NonExistingId_ReturnsNotFound() throws Exception {
        when(bookingRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(put("/bookings/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2023-07-01\", \"endDate\": \"2023-07-04\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBooking_OverlapBooking_ReturnsConflict() throws Exception {
        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(java.util.Optional.ofNullable(booking));
        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(put("/bookings/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2023-07-02\", \"endDate\": \"2023-07-05\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteBooking_ExistingId_ReturnsNoContent() throws Exception {
        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(java.util.Optional.ofNullable(booking));

        mockMvc.perform(delete("/bookings/{id}", booking.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBooking_NonExistingId_ReturnsNotFound() throws Exception {
        when(bookingRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(delete("/bookings/{id}", 100L))
                .andExpect(status().isNotFound());
    }
}
