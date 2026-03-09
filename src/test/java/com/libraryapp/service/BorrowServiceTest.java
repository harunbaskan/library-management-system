package com.libraryapp.service;

import com.libraryapp.dto.BorrowDto;
import com.libraryapp.exception.BookNotAvailableException;
import com.libraryapp.exception.BorrowLimitExceededException;
import com.libraryapp.model.*;
import com.libraryapp.repository.BorrowRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BookService bookService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private BorrowService borrowService;

    private Book testBook;
    private Member testMember;
    private BorrowDto.Request borrowRequest;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Clean Code");
        testBook.setAuthor("Robert C. Martin");
        testBook.setIsbn("9780132350884");
        testBook.setGenre(Genre.TECHNOLOGY);
        testBook.setTotalCopies(3);
        testBook.setAvailableCopies(2);

        testMember = new Member();
        testMember.setId(1L);
        testMember.setFirstName("John");
        testMember.setLastName("Doe");
        testMember.setEmail("john@example.com");
        testMember.setMembershipType(MembershipType.STANDARD);
        testMember.setActive(true);
        testMember.setBorrowRecords(new ArrayList<>());

        borrowRequest = new BorrowDto.Request();
        borrowRequest.setBookId(1L);
        borrowRequest.setMemberId(1L);
        borrowRequest.setNotes("First borrow");
    }

    @Test
    @DisplayName("Should successfully borrow a book")
    void borrowBook_WithValidRequest_ShouldCreateBorrowRecord() {
        when(bookService.findBookOrThrow(1L)).thenReturn(testBook);
        when(memberService.findMemberOrThrow(1L)).thenReturn(testMember);
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(invocation -> {
            BorrowRecord record = invocation.getArgument(0);
            record.setId(1L);
            return record;
        });

        BorrowDto.Response result = borrowService.borrowBook(borrowRequest);

        assertNotNull(result);
        assertEquals("Clean Code", result.getBookTitle());
        assertEquals("John Doe", result.getMemberName());
        assertNotNull(result.getBorrowDate());
        assertNotNull(result.getDueDate());
        assertNull(result.getReturnDate());
        assertFalse(result.isOverdue());

        assertEquals(1, testBook.getAvailableCopies());
    }

    @Test
    @DisplayName("Should throw exception when book is not available")
    void borrowBook_WhenBookNotAvailable_ShouldThrowException() {
        testBook.setAvailableCopies(0);

        when(bookService.findBookOrThrow(1L)).thenReturn(testBook);
        when(memberService.findMemberOrThrow(1L)).thenReturn(testMember);

        assertThrows(BookNotAvailableException.class, () -> {
            borrowService.borrowBook(borrowRequest);
        });

        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when member exceeds borrow limit")
    void borrowBook_WhenLimitExceeded_ShouldThrowException() {
        for (int i = 0; i < 3; i++) {
            BorrowRecord activeRecord = new BorrowRecord();
            activeRecord.setBorrowDate(LocalDate.now());
            activeRecord.setDueDate(LocalDate.now().plusDays(14));
            testMember.getBorrowRecords().add(activeRecord);
        }

        when(bookService.findBookOrThrow(1L)).thenReturn(testBook);
        when(memberService.findMemberOrThrow(1L)).thenReturn(testMember);

        assertThrows(BorrowLimitExceededException.class, () -> {
            borrowService.borrowBook(borrowRequest);
        });
    }

    @Test
    @DisplayName("Should return a book successfully")
    void returnBook_ShouldSetReturnDateAndCalculateFine() {
        BorrowRecord record = new BorrowRecord();
        record.setId(1L);
        record.setBook(testBook);
        record.setMember(testMember);
        record.setBorrowDate(LocalDate.now().minusDays(10));
        record.setDueDate(LocalDate.now().plusDays(4));
        record.setFine(BigDecimal.ZERO);

        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(record);

        BorrowDto.Response result = borrowService.returnBook(1L);

        assertNotNull(result);
        assertNotNull(result.getReturnDate());
        assertEquals(BigDecimal.ZERO.compareTo(result.getFine()), 0);
    }

    @Test
    @DisplayName("Should calculate fine for overdue return")
    void returnBook_WhenOverdue_ShouldCalculateFine() {
        BorrowRecord overdueRecord = new BorrowRecord();
        overdueRecord.setId(2L);
        overdueRecord.setBook(testBook);
        overdueRecord.setMember(testMember);
        overdueRecord.setBorrowDate(LocalDate.now().minusDays(20));
        overdueRecord.setDueDate(LocalDate.now().minusDays(6));
        overdueRecord.setFine(BigDecimal.ZERO);

        when(borrowRecordRepository.findById(2L)).thenReturn(Optional.of(overdueRecord));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(overdueRecord);

        BorrowDto.Response result = borrowService.returnBook(2L);

        assertTrue(result.getFine().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.isOverdue());
    }

    @Test
    @DisplayName("Should throw exception when returning already returned book")
    void returnBook_WhenAlreadyReturned_ShouldThrowException() {
        BorrowRecord returnedRecord = new BorrowRecord();
        returnedRecord.setId(1L);
        returnedRecord.setBook(testBook);
        returnedRecord.setMember(testMember);
        returnedRecord.setReturnDate(LocalDate.now().minusDays(1));

        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(returnedRecord));

        assertThrows(IllegalStateException.class, () -> {
            borrowService.returnBook(1L);
        });
    }

    @Test
    @DisplayName("Should return overdue records")
    void getOverdueRecords_ShouldReturnOverdueList() {
        BorrowRecord overdueRecord = new BorrowRecord();
        overdueRecord.setId(1L);
        overdueRecord.setBook(testBook);
        overdueRecord.setMember(testMember);
        overdueRecord.setBorrowDate(LocalDate.now().minusDays(20));
        overdueRecord.setDueDate(LocalDate.now().minusDays(6));

        when(borrowRecordRepository.findOverdueRecords()).thenReturn(List.of(overdueRecord));

        List<BorrowDto.Response> results = borrowService.getOverdueRecords();

        assertEquals(1, results.size());
        assertTrue(results.get(0).isOverdue());
    }
}