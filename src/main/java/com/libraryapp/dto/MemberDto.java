package com.libraryapp.dto;

import com.libraryapp.model.MembershipType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MemberDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        private String phoneNumber;

        @NotNull(message = "Membership type is required")
        private MembershipType membershipType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private LocalDate membershipDate;
        private MembershipType membershipType;
        private boolean active;
        private long activeBorrowCount;
        private int borrowLimit;
    }
}