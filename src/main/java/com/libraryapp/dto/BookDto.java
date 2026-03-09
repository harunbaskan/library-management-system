package com.libraryapp.dto;

import com.libraryapp.model.Genre;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class BookDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Author is required")
        private String author;

        @NotBlank(message = "ISBN is required")
        @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
        private String isbn;

        private LocalDate publishedDate;

        @NotNull(message = "Genre is required")
        private Genre genre;

        @Min(value = 1, message = "Total copies must be at least 1")
        private int totalCopies;

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private LocalDate publishedDate;
        private Genre genre;
        private int totalCopies;
        private int availableCopies;
        private String description;
        private boolean available;
    }
}