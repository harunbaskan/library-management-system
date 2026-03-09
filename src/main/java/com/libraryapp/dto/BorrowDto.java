package com.libraryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BorrowDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotNull(message = "Book ID is required")
        private Long bookId;

        @NotNull(message = "Member ID is required")
        private Long memberId;

        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long bookId;
        private String bookTitle;
        private Long memberId;
        private String memberName;
        private LocalDate borrowDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private BigDecimal fine;
        private boolean overdue;
        private String notes;
    }
}