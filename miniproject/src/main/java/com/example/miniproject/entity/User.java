package com.example.miniproject.entity;

import com.example.miniproject.dto.UserRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false) // 고유번호
    private Long id;

    @Pattern(regexp = "[a-zA-z0-9]{4,15}$", message="4~15자리 숫자포함이여야 합니다")
    @Column(nullable = false) // 아이디
    private String userid;

    @Column(nullable = false) // 패드워드
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING) // 권한
    private UserRoleEnum role;

    public User(String userid, String password, UserRoleEnum role) {
        this.userid = userid;
        this.password = password;
        this.role = role;
    }

    public User(UserRequestDto userRequestDto) {
        this.userid = userRequestDto.getUserid();
        this.password = userRequestDto.getPassword();
    }
}
