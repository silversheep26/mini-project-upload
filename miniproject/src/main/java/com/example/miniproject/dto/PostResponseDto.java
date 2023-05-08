package com.example.miniproject.dto;

import com.example.miniproject.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String userid;
    private String title;
    private String contents;
    private LocalDateTime lastmodifiedDate;
    private List<CommentResponseDto> commentList;

    private boolean matchUser;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.userid = post.getUser().getUserid();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.lastmodifiedDate = post.getLastmodifiedDate();
        this.commentList = post.getCommentList().stream().map(CommentResponseDto::new).collect(Collectors.toList());
    }

    public PostResponseDto(boolean matchUser) {
        this.matchUser = matchUser;
    }
}
