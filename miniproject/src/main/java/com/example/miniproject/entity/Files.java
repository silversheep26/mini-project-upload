package com.example.miniproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Files extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "files_id")
    private Long id;

    @Column(nullable = false)
    private String filename; // 사용자에게 보여줄 파일이름

    @Column(nullable = false)
    private String orangfilename; // DB에 저장될 파일 이름

    public Files(String filename, String orangfilename) {
        this.filename = filename;
        this.orangfilename = orangfilename;
    }
}
