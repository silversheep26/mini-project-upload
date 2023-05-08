package com.example.miniproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class BlackList extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userid;

    @Column
    private String token;

    public BlackList(String userid, String token) {
        this.userid = userid;
        this.token = token;
    }
}