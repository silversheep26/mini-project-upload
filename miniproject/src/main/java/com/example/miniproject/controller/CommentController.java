package com.example.miniproject.controller;

import com.example.miniproject.dto.CommentRequestDto;
import com.example.miniproject.dto.CommentResponseDto;
import com.example.miniproject.security.UserDetailsImpl;
import com.example.miniproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

//변경 by ym
@CrossOrigin(origins="*")
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //댓글 작성
    @PostMapping("/api/posts/{postId}/comments")
    public CommentResponseDto writeComment(@PathVariable Long postId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.writeComment(postId, commentRequestDto, userDetails.getUser());
    }

    //댓글 수정
    @PutMapping("/api/posts/{postId}/comments/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(commentId, commentRequestDto, userDetails.getUser());
    }

    //댓글 삭제
    @DeleteMapping("/api/posts/{postId}/comments/{commentId}")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.deleteComment(commentId, userDetails.getUser());
    }

}
