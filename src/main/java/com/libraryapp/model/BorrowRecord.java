package com.libraryapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "borrow_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {

    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("0.50");
    private static final int STANDARD_BORROW_DAYS = 14;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal fine = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    public boolean isOverdue() {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public BigDecimal calculateFine() {
        if (!isOverdue()) {
            return BigDecimal.ZERO;
        }
        LocalDate endDate = (returnDate != null) ? returnDate : LocalDate.now();
        long overdueDays = ChronoUnit.DAYS.between(dueDate, endDate);
        return DAILY_FINE_RATE.multiply(BigDecimal.valueOf(overdueDays));
    }

    public boolean isActive() {
        return this.returnDate == null;
    }

    public static BorrowRecord createNewBorrow(Book book, Member member) {
        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setMember(member);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(STANDARD_BORROW_DAYS));
        record.setFine(BigDecimal.ZERO);
        return record;
    }
}