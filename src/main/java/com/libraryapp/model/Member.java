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
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "membership_date", nullable = false)
    private LocalDate membershipDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType membershipType;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public int getBorrowLimit() {
        return switch (this.membershipType) {
            case STANDARD -> 3;
            case PREMIUM -> 7;
            case STUDENT -> 5;
        };
    }

    public long getActiveBorrowCount() {
        return this.borrowRecords.stream()
                .filter(record -> record.getReturnDate() == null)
                .count();
    }

    public boolean canBorrow() {
        return this.active && getActiveBorrowCount() < getBorrowLimit();
    }
}