package com.libraryapp.exception;

public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(String bookTitle) {
        super(String.format("Book '%s' is not available for borrowing", bookTitle));
    }
}