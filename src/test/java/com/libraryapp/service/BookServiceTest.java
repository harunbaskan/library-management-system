package com.libraryapp.service;

import com.libraryapp.dto.BookDto;
import com.libraryapp.exception.DuplicateResourceException;
import com.libraryapp.exception.ResourceNotFoundException;
import com.libraryapp.model.Book;
import com.libraryapp.model.Genre;
import com.libraryapp.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookDto.Request bookRequest;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Clean Code");
        testBook.setAuthor("Robert C. Martin");
        testBook.setIsbn("9780132350884");
        testBook.setPublishedDate(LocalDate.of(2008, 8, 1));
        testBook.setGenre(Genre.TECHNOLOGY);
        testBook.setTotalCopies(3);
        testBook.setAvailableCopies(3);
        testBook.setDescription("A handbook of agile software craftsmanship");

        bookRequest = new BookDto.Request();
        bookRequest.setTitle("Clean Code");
        bookRequest.setAuthor("Robert C. Martin");
        bookRequest.setIsbn("9780132350884");
        bookRequest.setPublishedDate(LocalDate.of(2008, 8, 1));
        bookRequest.setGenre(Genre.TECHNOLOGY);
        bookRequest.setTotalCopies(3);
        bookRequest.setDescription("A handbook of agile software craftsmanship");
    }

    @Test
    @DisplayName("Should create a new book successfully")
    void createBook_WithValidData_ShouldReturnCreatedBook() {
        when(bookRepository.existsByIsbn(bookRequest.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookDto.Response result = bookService.createBook(bookRequest);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        assertEquals("Robert C. Martin", result.getAuthor());
        assertEquals(3, result.getAvailableCopies());
        assertTrue(result.isAvailable());

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when creating book with duplicate ISBN")
    void createBook_WithDuplicateIsbn_ShouldThrowException() {
        when(bookRepository.existsByIsbn(bookRequest.getIsbn())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            bookService.createBook(bookRequest);
        });

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should return book when found by ID")
    void getBookById_WithExistingId_ShouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        BookDto.Response result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    @DisplayName("Should throw exception when book not found by ID")
    void getBookById_WithNonExistingId_ShouldThrowException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.getBookById(99L);
        });
    }

    @Test
    @DisplayName("Should return all books")
    void getAllBooks_ShouldReturnListOfBooks() {
        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Design Patterns");
        secondBook.setAuthor("Gang of Four");
        secondBook.setIsbn("9780201633610");
        secondBook.setGenre(Genre.TECHNOLOGY);
        secondBook.setTotalCopies(2);
        secondBook.setAvailableCopies(2);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook, secondBook));

        List<BookDto.Response> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
        assertEquals("Design Patterns", result.get(1).getTitle());
    }

    @Test
    @DisplayName("Should update book successfully")
    void updateBook_WithValidData_ShouldReturnUpdatedBook() {
        BookDto.Request updateRequest = new BookDto.Request();
        updateRequest.setTitle("Clean Code: Updated Edition");
        updateRequest.setAuthor("Robert C. Martin");
        updateRequest.setIsbn("9780132350884");
        updateRequest.setGenre(Genre.TECHNOLOGY);
        updateRequest.setTotalCopies(5);
        updateRequest.setDescription("Updated description");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookDto.Response result = bookService.updateBook(1L, updateRequest);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should delete book successfully")
    void deleteBook_WithExistingId_ShouldDeleteSuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        doNothing().when(bookRepository).delete(testBook);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));

        verify(bookRepository, times(1)).delete(testBook);
    }

    @Test
    @DisplayName("Should search books by keyword")
    void searchBooks_ShouldReturnMatchingBooks() {
        when(bookRepository.searchByKeyword("clean")).thenReturn(List.of(testBook));

        List<BookDto.Response> results = bookService.searchBooks("clean");

        assertEquals(1, results.size());
        assertEquals("Clean Code", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should return books by genre")
    void getBooksByGenre_ShouldReturnFilteredBooks() {
        when(bookRepository.findByGenre(Genre.TECHNOLOGY)).thenReturn(List.of(testBook));

        List<BookDto.Response> results = bookService.getBooksByGenre(Genre.TECHNOLOGY);

        assertEquals(1, results.size());
        assertEquals(Genre.TECHNOLOGY, results.get(0).getGenre());
    }
}