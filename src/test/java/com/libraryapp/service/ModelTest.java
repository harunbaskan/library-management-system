package com.libraryapp.service;

import com.libraryapp.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    @DisplayName("Book - should track availability correctly")
    void book_AvailabilityTracking() {
        Book book = new Book();
        book.setTotalCopies(2);
        book.setAvailableCopies(2);

        assertTrue(book.isAvailable());

        book.borrowCopy();
        assertEquals(1, book.getAvailableCopies());
        assertTrue(book.isAvailable());

        book.borrowCopy();
        assertEquals(0, book.getAvailableCopies());
        assertFalse(book.isAvailable());
    }

    @Test
    @DisplayName("Book - should throw when no copies available to borrow")
    void book_BorrowWhenNoneAvailable_ShouldThrow() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setTotalCopies(1);
        book.setAvailableCopies(0);

        assertThrows(IllegalStateException.class, book::borrowCopy);
    }

    @Test
    @DisplayName("Book - should throw when returning more than total copies")
    void book_ReturnWhenAllReturned_ShouldThrow() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setTotalCopies(2);
        book.setAvailableCopies(2);

        assertThrows(IllegalStateException.class, book::returnCopy);
    }

    @Test
    @DisplayName("Member - borrow limits based on membership type")
    void member_BorrowLimitsByType() {
        Member standard = new Member();
        standard.setMembershipType(MembershipType.STANDARD);
        assertEquals(3, standard.getBorrowLimit());

        Member premium = new Member();
        premium.setMembershipType(MembershipType.PREMIUM);
        assertEquals(7, premium.getBorrowLimit());

        Member student = new Member();
        student.setMembershipType(MembershipType.STUDENT);
        assertEquals(5, student.getBorrowLimit());
    }

    @Test
    @DisplayName("Member - canBorrow respects limit and active status")
    void member_CanBorrowLogic() {
        Member member = new Member();
        member.setMembershipType(MembershipType.STANDARD);
        member.setActive(true);
        member.setBorrowRecords(new ArrayList<>());

        assertTrue(member.canBorrow());

        for (int i = 0; i < 3; i++) {
            BorrowRecord record = new BorrowRecord();
            record.setBorrowDate(LocalDate.now());
            record.setDueDate(LocalDate.now().plusDays(14));
            member.getBorrowRecords().add(record);
        }

        assertFalse(member.canBorrow());
    }

    @Test
    @DisplayName("Member - inactive member cannot borrow")
    void member_InactiveMemberCannotBorrow() {
        Member member = new Member();
        member.setMembershipType(MembershipType.STANDARD);
        member.setActive(false);
        member.setBorrowRecords(new ArrayList<>());

        assertFalse(member.canBorrow());
    }

    @Test
    @DisplayName("BorrowRecord - overdue detection")
    void borrowRecord_OverdueDetection() {
        BorrowRecord record = new BorrowRecord();
        record.setDueDate(LocalDate.now().plusDays(5));
        assertFalse(record.isOverdue());

        BorrowRecord overdueRecord = new BorrowRecord();
        overdueRecord.setDueDate(LocalDate.now().minusDays(3));
        assertTrue(overdueRecord.isOverdue());
    }

    @Test
    @DisplayName("BorrowRecord - fine calculation for overdue")
    void borrowRecord_FineCalculation() {
        BorrowRecord record = new BorrowRecord();
        record.setDueDate(LocalDate.now().minusDays(5));

        BigDecimal expectedFine = new BigDecimal("2.50");
        assertEquals(0, expectedFine.compareTo(record.calculateFine()));
    }

    @Test
    @DisplayName("BorrowRecord - no fine when not overdue")
    void borrowRecord_NoFineWhenNotOverdue() {
        BorrowRecord record = new BorrowRecord();
        record.setDueDate(LocalDate.now().plusDays(3));

        assertEquals(0, BigDecimal.ZERO.compareTo(record.calculateFine()));
    }

    @Test
    @DisplayName("BorrowRecord - factory method sets correct defaults")
    void borrowRecord_CreateNewBorrow() {
        Book book = new Book();
        book.setTitle("Test");
        Member member = new Member();
        member.setFirstName("Test");

        BorrowRecord record = BorrowRecord.createNewBorrow(book, member);

        assertEquals(LocalDate.now(), record.getBorrowDate());
        assertEquals(LocalDate.now().plusDays(14), record.getDueDate());
        assertNull(record.getReturnDate());
        assertTrue(record.isActive());
        assertEquals(0, BigDecimal.ZERO.compareTo(record.getFine()));
    }
}