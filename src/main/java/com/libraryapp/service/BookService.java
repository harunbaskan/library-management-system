package com.libraryapp.service;

import com.libraryapp.dto.BookDto;
import com.libraryapp.exception.DuplicateResourceException;
import com.libraryapp.exception.ResourceNotFoundException;
import com.libraryapp.model.Book;
import com.libraryapp.model.Genre;
import com.libraryapp.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookDto.Response createBook(BookDto.Request request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book", "ISBN", request.getIsbn());
        }

        Book book = mapToEntity(request);
        book.setAvailableCopies(request.getTotalCopies());
        Book savedBook = bookRepository.save(book);

        return mapToResponse(savedBook);
    }

    public BookDto.Response getBookById(Long id) {
        Book book = findBookOrThrow(id);
        return mapToResponse(book);
    }

    public List<BookDto.Response> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDto.Response updateBook(Long id, BookDto.Request request) {
        Book existingBook = findBookOrThrow(id);

        if (!existingBook.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book", "ISBN", request.getIsbn());
        }

        int copyDifference = request.getTotalCopies() - existingBook.getTotalCopies();
        int newAvailable = existingBook.getAvailableCopies() + copyDifference;

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setIsbn(request.getIsbn());
        existingBook.setPublishedDate(request.getPublishedDate());
        existingBook.setGenre(request.getGenre());
        existingBook.setTotalCopies(request.getTotalCopies());
        existingBook.setAvailableCopies(Math.max(0, newAvailable));
        existingBook.setDescription(request.getDescription());

        Book updatedBook = bookRepository.save(existingBook);
        return mapToResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = findBookOrThrow(id);
        bookRepository.delete(book);
    }

    public List<BookDto.Response> searchBooks(String keyword) {
        return bookRepository.searchByKeyword(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookDto.Response> getBooksByGenre(Genre genre) {
        return bookRepository.findByGenre(genre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookDto.Response> getAvailableBooks() {
        return bookRepository.findAvailableBooks().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    private Book mapToEntity(BookDto.Request request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublishedDate(request.getPublishedDate());
        book.setGenre(request.getGenre());
        book.setTotalCopies(request.getTotalCopies());
        book.setDescription(request.getDescription());
        return book;
    }

    BookDto.Response mapToResponse(Book book) {
        BookDto.Response response = new BookDto.Response();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setPublishedDate(book.getPublishedDate());
        response.setGenre(book.getGenre());
        response.setTotalCopies(book.getTotalCopies());
        response.setAvailableCopies(book.getAvailableCopies());
        response.setDescription(book.getDescription());
        response.setAvailable(book.isAvailable());
        return response;
    }
}