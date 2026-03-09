package com.libraryapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.dto.BookDto;
import com.libraryapp.model.Genre;
import com.libraryapp.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDto.Response createSampleResponse() {
        BookDto.Response response = new BookDto.Response();
        response.setId(1L);
        response.setTitle("Clean Code");
        response.setAuthor("Robert C. Martin");
        response.setIsbn("9780132350884");
        response.setPublishedDate(LocalDate.of(2008, 8, 1));
        response.setGenre(Genre.TECHNOLOGY);
        response.setTotalCopies(3);
        response.setAvailableCopies(3);
        response.setAvailable(true);
        return response;
    }

    @Test
    @DisplayName("POST /api/books - Should create book and return 201")
    void createBook_ShouldReturn201() throws Exception {
        BookDto.Request request = new BookDto.Request();
        request.setTitle("Clean Code");
        request.setAuthor("Robert C. Martin");
        request.setIsbn("9780132350884");
        request.setGenre(Genre.TECHNOLOGY);
        request.setTotalCopies(3);

        when(bookService.createBook(any())).thenReturn(createSampleResponse());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("POST /api/books - Should return 400 for invalid data")
    void createBook_WithInvalidData_ShouldReturn400() throws Exception {
        BookDto.Request invalidRequest = new BookDto.Request();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/books/{id} - Should return book")
    void getBook_ShouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(createSampleResponse());

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    @DisplayName("GET /api/books - Should return list of books")
    void getAllBooks_ShouldReturnList() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(createSampleResponse()));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/books/search - Should search books by keyword")
    void searchBooks_ShouldReturnResults() throws Exception {
        when(bookService.searchBooks("clean")).thenReturn(List.of(createSampleResponse()));

        mockMvc.perform(get("/api/books/search")
                        .param("keyword", "clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - Should return 204 No Content")
    void deleteBook_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}