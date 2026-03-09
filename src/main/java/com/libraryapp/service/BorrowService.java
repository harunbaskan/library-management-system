package com.libraryapp.service;

import com.libraryapp.dto.BorrowDto;
import com.libraryapp.exception.BookNotAvailableException;
import com.libraryapp.exception.BorrowLimitExceededException;
import com.libraryapp.exception.ResourceNotFoundException;
import com.libraryapp.model.Book;
import com.libraryapp.model.BorrowRecord;
import com.libraryapp.model.Member;
import com.libraryapp.repository.BorrowRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowService {

    private static final Logger log = LoggerFactory.getLogger(BorrowService.class);

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookService bookService;
    private final MemberService memberService;

    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                         BookService bookService,
                         MemberService memberService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @Transactional
    public BorrowDto.Response borrowBook(BorrowDto.Request request) {
        Book book = bookService.findBookOrThrow(request.getBookId());
        Member member = memberService.findMemberOrThrow(request.getMemberId());

        if (!book.isAvailable()) {
            throw new BookNotAvailableException(book.getTitle());
        }

        if (!member.canBorrow()) {
            throw new BorrowLimitExceededException(member.getFullName(), member.getBorrowLimit());
        }

        BorrowRecord record = BorrowRecord.createNewBorrow(book, member);
        record.setNotes(request.getNotes());
        book.borrowCopy();

        BorrowRecord savedRecord = borrowRecordRepository.save(record);
        log.info("Book '{}' borrowed by member '{}'. Due date: {}",
                book.getTitle(), member.getFullName(), record.getDueDate());

        return mapToResponse(savedRecord);
    }

    @Transactional
    public BorrowDto.Response returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", recordId));

        if (!record.isActive()) {
            throw new IllegalStateException("This book has already been returned");
        }

        record.setReturnDate(LocalDate.now());
        record.setFine(record.calculateFine());
        record.getBook().returnCopy();

        BorrowRecord updated = borrowRecordRepository.save(record);
        log.info("Book '{}' returned by '{}'. Fine: ${}",
                record.getBook().getTitle(),
                record.getMember().getFullName(),
                record.getFine());

        return mapToResponse(updated);
    }

    public BorrowDto.Response getBorrowRecord(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", id));
        return mapToResponse(record);
    }

    public List<BorrowDto.Response> getMemberBorrowHistory(Long memberId) {
        memberService.findMemberOrThrow(memberId);
        return borrowRecordRepository.findByMemberId(memberId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BorrowDto.Response> getActiveBorrowsByMember(Long memberId) {
        memberService.findMemberOrThrow(memberId);
        return borrowRecordRepository.findActiveBorrowsByMember(memberId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BorrowDto.Response> getOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BorrowDto.Response> getAllActiveBorrows() {
        return borrowRecordRepository.findAllActiveBorrows().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BorrowDto.Response mapToResponse(BorrowRecord record) {
        BorrowDto.Response response = new BorrowDto.Response();
        response.setId(record.getId());
        response.setBookId(record.getBook().getId());
        response.setBookTitle(record.getBook().getTitle());
        response.setMemberId(record.getMember().getId());
        response.setMemberName(record.getMember().getFullName());
        response.setBorrowDate(record.getBorrowDate());
        response.setDueDate(record.getDueDate());
        response.setReturnDate(record.getReturnDate());
        response.setFine(record.calculateFine());
        response.setOverdue(record.isOverdue());
        response.setNotes(record.getNotes());
        return response;
    }
}