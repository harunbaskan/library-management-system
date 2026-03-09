package com.libraryapp.exception;

public class BorrowLimitExceededException extends RuntimeException {

    public BorrowLimitExceededException(String memberName, int limit) {
        super(String.format("Member '%s' has reached the borrowing limit of %d books", memberName, limit));
    }
}