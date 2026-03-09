package com.libraryapp.controller;

import com.libraryapp.dto.MemberDto;
import com.libraryapp.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberDto.Response> createMember(@Valid @RequestBody MemberDto.Request request) {
        MemberDto.Response created = memberService.createMember(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto.Response> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @GetMapping
    public ResponseEntity<List<MemberDto.Response>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDto.Response> updateMember(@PathVariable Long id,
                                                           @Valid @RequestBody MemberDto.Request request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateMember(@PathVariable Long id) {
        memberService.deactivateMember(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateMember(@PathVariable Long id) {
        memberService.activateMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MemberDto.Response>> searchMembers(@RequestParam String name) {
        return ResponseEntity.ok(memberService.searchMembers(name));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<MemberDto.Response>> getMembersWithOverdueBooks() {
        return ResponseEntity.ok(memberService.getMembersWithOverdueBooks());
    }
}