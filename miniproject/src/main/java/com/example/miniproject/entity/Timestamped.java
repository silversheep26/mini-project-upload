package com.example.miniproject.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Getter
@EnableJpaAuditing
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Timestamped {
    //생성시간
    @CreatedDate
    private LocalDateTime createdDate;

    //수정시간
    @LastModifiedDate
    private LocalDateTime lastmodifiedDate;
}
