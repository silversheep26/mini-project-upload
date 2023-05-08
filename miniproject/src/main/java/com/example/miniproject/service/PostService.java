package com.example.miniproject.service;

import com.example.miniproject.dto.FileDto;
import com.example.miniproject.dto.PostRequestDto;
import com.example.miniproject.dto.PostResponseDto;
import com.example.miniproject.entity.Files;
import com.example.miniproject.entity.Post;
import com.example.miniproject.entity.User;
import com.example.miniproject.entity.UserRoleEnum;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.exception.ExceptionEnum;
import com.example.miniproject.jwt.JwtUtil;
import com.example.miniproject.repository.CommentRepository;
import com.example.miniproject.repository.FileRepostiory;
import com.example.miniproject.repository.PostRepository;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final FileRepostiory fileRepostiory;

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @Transactional(readOnly = true)
    // 게시글 전체 조회
    public List<PostResponseDto> getPosts(){
        List<Post> postList = postRepository.findAllByOrderByCreatedDateDesc();
        return postList.stream().map(PostResponseDto::new).collect(Collectors.toList());
    }

    // 게시글 작성
    public PostResponseDto writePost(PostRequestDto postRequestDto, User user){
        // 게시글 저장
        Post post = postRepository.saveAndFlush(new Post(postRequestDto,user));
       File file = new File(uploadPath);

        //경로에 폴더 있는지 확인
       if(!file.exists()){
           file.mkdirs(); //폴더 생성합니다.
       }

       // 받은 값에 파일 이름 추출 및 저장
        if(postRequestDto.getFiles() != null){
            postRequestDto.getFiles().forEach(multipartFile -> {
                String originalName = multipartFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String serverfileName = uuid+"_"+originalName;
                Path savepath = Paths.get(uploadPath, serverfileName);
                fileRepostiory.saveAndFlush( new Files(originalName, serverfileName));
                try{
                    multipartFile.transferTo(savepath); //파일 저장
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
        return new PostResponseDto(post);
    }

    @Transactional(readOnly = true)
    //게시글 하나 조회
    public PostResponseDto getPost(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );

//        if (post.getUser().getUserid().equals(user.getUserid())){
//            PostResponseDto postResponseDto = new PostResponseDto(true);
//        }
        return new PostResponseDto(post);
    }

    // 게시물 수정
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );
        if (user.getRole() != UserRoleEnum.ADMIN && !StringUtils.equals(post.getUser().getId(), user.getId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        post.updatePost(postRequestDto);
        return new PostResponseDto(post);
    }

    // 게시물 삭제
    public String deletePost(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );

        if (user.getRole() != UserRoleEnum.ADMIN && !StringUtils.equals(post.getUser().getId(), user.getId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        postRepository.deleteById(postId);
        return "삭제 완료";
    }

}
