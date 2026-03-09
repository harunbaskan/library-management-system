package com.libraryapp.service;

import com.libraryapp.dto.MemberDto;
import com.libraryapp.exception.DuplicateResourceException;
import com.libraryapp.exception.ResourceNotFoundException;
import com.libraryapp.model.Member;
import com.libraryapp.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberDto.Response createMember(MemberDto.Request request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Member", "email", request.getEmail());
        }

        Member member = mapToEntity(request);
        member.setMembershipDate(LocalDate.now());
        member.setActive(true);

        Member savedMember = memberRepository.save(member);
        return mapToResponse(savedMember);
    }

    public MemberDto.Response getMemberById(Long id) {
        Member member = findMemberOrThrow(id);
        return mapToResponse(member);
    }

    public List<MemberDto.Response> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberDto.Response updateMember(Long id, MemberDto.Request request) {
        Member existingMember = findMemberOrThrow(id);

        if (!existingMember.getEmail().equals(request.getEmail())
                && memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Member", "email", request.getEmail());
        }

        existingMember.setFirstName(request.getFirstName());
        existingMember.setLastName(request.getLastName());
        existingMember.setEmail(request.getEmail());
        existingMember.setPhoneNumber(request.getPhoneNumber());
        existingMember.setMembershipType(request.getMembershipType());

        Member updatedMember = memberRepository.save(existingMember);
        return mapToResponse(updatedMember);
    }

    @Transactional
    public void deactivateMember(Long id) {
        Member member = findMemberOrThrow(id);
        member.setActive(false);
        memberRepository.save(member);
    }

    @Transactional
    public void activateMember(Long id) {
        Member member = findMemberOrThrow(id);
        member.setActive(true);
        memberRepository.save(member);
    }

    public List<MemberDto.Response> searchMembers(String name) {
        return memberRepository.searchByName(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MemberDto.Response> getMembersWithOverdueBooks() {
        return memberRepository.findMembersWithOverdueBooks().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    Member findMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
    }

    private Member mapToEntity(MemberDto.Request request) {
        Member member = new Member();
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setMembershipType(request.getMembershipType());
        return member;
    }

    MemberDto.Response mapToResponse(Member member) {
        MemberDto.Response response = new MemberDto.Response();
        response.setId(member.getId());
        response.setFirstName(member.getFirstName());
        response.setLastName(member.getLastName());
        response.setEmail(member.getEmail());
        response.setPhoneNumber(member.getPhoneNumber());
        response.setMembershipDate(member.getMembershipDate());
        response.setMembershipType(member.getMembershipType());
        response.setActive(member.isActive());
        response.setActiveBorrowCount(member.getActiveBorrowCount());
        response.setBorrowLimit(member.getBorrowLimit());
        return response;
    }
}