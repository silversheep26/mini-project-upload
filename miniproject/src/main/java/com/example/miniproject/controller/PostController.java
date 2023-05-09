package com.example.miniproject.controller;

import com.example.miniproject.dto.PostRequestDto;
import com.example.miniproject.dto.PostResponseDto;
import com.example.miniproject.security.UserDetailsImpl;
import com.example.miniproject.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//변경 by ym
@CrossOrigin(origins="*", exposedHeaders = "ACCESS_KEY")
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시물 작성 및 파일 업로드
    @PostMapping(value = "/api/posts")
    public PostResponseDto writePost(PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
       return postService.writePost(postRequestDto, userDetails.getUser());
    }

    //게시물 전체 조회
    @GetMapping("/api/posts")
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    // 게시물 하나 보기
    @GetMapping("/api/posts/{postId}")
    public PostResponseDto getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    // 게시물 수정
    @PutMapping("/api/posts/{postId}")
    public PostResponseDto updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postrequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(postId, postrequestDto, userDetails.getUser());
    }

    // 게시물 삭제
    // 게시물 삭제 후 메인 페이지로 반환 해야함
    @DeleteMapping("/api/posts/{postId}")
    public String deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.deletePost(postId, userDetails.getUser());
    }

}
