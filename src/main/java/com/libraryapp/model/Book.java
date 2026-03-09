package com.libraryapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    @Column(name = "total_copies", nullable = false)
    private int totalCopies;

    @Column(name = "available_copies", nullable = false)
    private int availableCopies;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public boolean isAvailable() {
        return this.availableCopies > 0;
    }

    public void borrowCopy() {
        if (!isAvailable()) {
            throw new IllegalStateException("No available copies of: " + this.title);
        }
        this.availableCopies--;
    }

    public void returnCopy() {
        if (this.availableCopies >= this.totalCopies) {
            throw new IllegalStateException("All copies already returned for: " + this.title);
        }
        this.availableCopies++;
    }
}