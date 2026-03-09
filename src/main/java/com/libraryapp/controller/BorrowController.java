package com.libraryapp.controller;

import com.libraryapp.dto.BorrowDto;
import com.libraryapp.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping
    public ResponseEntity<BorrowDto.Response> borrowBook(@Valid @RequestBody BorrowDto.Request request) {
        BorrowDto.Response response = borrowService.borrowBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<BorrowDto.Response> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.returnBook(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowDto.Response> getBorrowRecord(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.getBorrowRecord(id));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BorrowDto.Response>> getMemberHistory(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowService.getMemberBorrowHistory(memberId));
    }

    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<List<BorrowDto.Response>> getActiveBorrows(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowService.getActiveBorrowsByMember(memberId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowDto.Response>> getOverdueRecords() {
        return ResponseEntity.ok(borrowService.getOverdueRecords());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BorrowDto.Response>> getAllActiveBorrows() {
        return ResponseEntity.ok(borrowService.getAllActiveBorrows());
    }
}
