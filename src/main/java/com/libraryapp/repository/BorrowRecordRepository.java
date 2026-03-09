package com.libraryapp.repository;

import com.libraryapp.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByMemberId(Long memberId);

    List<BorrowRecord> findByBookId(Long bookId);

    @Query("SELECT br FROM BorrowRecord br WHERE br.member.id = :memberId AND br.returnDate IS NULL")
    List<BorrowRecord> findActiveBorrowsByMember(@Param("memberId") Long memberId);

    @Query("SELECT br FROM BorrowRecord br WHERE br.returnDate IS NULL AND br.dueDate < CURRENT_DATE")
    List<BorrowRecord> findOverdueRecords();

    Optional<BorrowRecord> findByBookIdAndMemberIdAndReturnDateIsNull(Long bookId, Long memberId);

    @Query("SELECT br FROM BorrowRecord br WHERE br.returnDate IS NULL")
    List<BorrowRecord> findAllActiveBorrows();
}