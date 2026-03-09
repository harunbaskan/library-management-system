package com.libraryapp.repository;

import com.libraryapp.model.Member;
import com.libraryapp.model.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    List<Member> findByMembershipType(MembershipType type);

    List<Member> findByActiveTrue();

    @Query("SELECT DISTINCT m FROM Member m JOIN m.borrowRecords br " +
            "WHERE br.returnDate IS NULL AND br.dueDate < CURRENT_DATE")
    List<Member> findMembersWithOverdueBooks();

    @Query("SELECT m FROM Member m WHERE " +
            "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(m.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Member> searchByName(@Param("name") String name);

    boolean existsByEmail(String email);
}