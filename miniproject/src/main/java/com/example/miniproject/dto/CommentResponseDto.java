package com.example.miniproject.dto;

import com.example.miniproject.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String userid;
    private String contents;
    private LocalDateTime lastmodifiedDate;


    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.userid = comment.getUser().getUserid();
        this.contents = comment.getContents();
        this.lastmodifiedDate = comment.getLastmodifiedDate();

    }
}

