package com.example.miniproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.miniproject.dto.PostRequestDto;
import com.example.miniproject.dto.PostResponseDto;
import com.example.miniproject.entity.Post;
import com.example.miniproject.entity.User;
import com.example.miniproject.entity.UserRoleEnum;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.exception.ExceptionEnum;
import com.example.miniproject.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private static final String S3_BUCKET_PREFIX = "S3";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;
    Post post;

    @Transactional(readOnly = true)
    // 게시글 전체 조회
    public List<PostResponseDto> getPosts(){
        List<Post> postList = postRepository.findAllByOrderByCreatedDateDesc();
        return postList.stream().map(PostResponseDto::new).collect(Collectors.toList());
    }

    // 게시글 작성
    public PostResponseDto writePost(PostRequestDto postRequestDto, MultipartFile image, User user) throws IOException {
        // 게시글 저장
        // 파일명 새로 부여를 위한 현재 시간 알아내기
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);

        String imageUrl = null;

        // 새로 부여한 이미지명
        String newFileName = "image" + hour + minute + second + millis;
        String fileExtension = '.' + image.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");
        String imageName =S3_BUCKET_PREFIX + newFileName + fileExtension;

        // 메타데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(image.getContentType());
        objectMetadata.setContentLength(image.getSize());

        InputStream inputStream = image.getInputStream();

        amazonS3.putObject(new PutObjectRequest(bucketName, imageName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        imageUrl = amazonS3.getUrl(bucketName, imageName).toString();
        post = post.builder()
                .postRequestDto(postRequestDto)
                .imageUrl(imageUrl)
                .user(user)
                .build();
        postRepository.saveAndFlush(post);
        return new PostResponseDto(post);
    }

    @Transactional(readOnly = true)
    //게시글 하나 조회
    public PostResponseDto getPost(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );
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