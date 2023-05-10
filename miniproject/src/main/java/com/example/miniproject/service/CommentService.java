package com.example.miniproject.service;
import com.example.miniproject.dto.CommentResponseDto;
import com.example.miniproject.entity.Post;
import com.example.miniproject.entity.User;
import com.example.miniproject.dto.CommentRequestDto;
import com.example.miniproject.entity.Comment;
import com.example.miniproject.entity.UserRoleEnum;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.exception.ExceptionEnum;
import com.example.miniproject.repository.CommentRepository;
import com.example.miniproject.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    //댓글 작성
    @Transactional
    public CommentResponseDto writeComment(Long postId, CommentRequestDto commentRequestDto, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );

        Comment comment = new Comment(commentRequestDto, user, post);
        comment.setPost(post);
        comment.setUser(user);

        commentRepository.saveAndFlush(comment);
        return new CommentResponseDto(comment);
    }

    //댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_COMMENT)
        );
        if (user.getRole() != UserRoleEnum.ADMIN && !StringUtils.equals(comment.getUser().getId(), user.getId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        comment.updateComment(commentRequestDto, user);
        return new CommentResponseDto(comment);
    }

    //댓글 삭제
    @Transactional
    public String deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_COMMENT)
        );
        if (user.getRole() != UserRoleEnum.ADMIN && !StringUtils.equals(comment.getUser().getId(), user.getId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        commentRepository.deleteById(commentId);
        return "삭제 완료";
    }


}
