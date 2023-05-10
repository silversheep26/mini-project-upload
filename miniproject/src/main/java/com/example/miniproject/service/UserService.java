package com.example.miniproject.service;

import com.example.miniproject.dto.TokenDto;
import com.example.miniproject.dto.UserRequestDto;
import com.example.miniproject.dto.UserResponseDto;
import com.example.miniproject.entity.RefreshToken;
import com.example.miniproject.entity.User;
import com.example.miniproject.entity.UserRoleEnum;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.exception.ExceptionEnum;
import com.example.miniproject.jwt.JwtUtil;
import com.example.miniproject.redis.RedisUtil;
import com.example.miniproject.repository.BlackListRepository;
import com.example.miniproject.repository.RefreshTokenRepository;
import com.example.miniproject.repository.UserRepository;
import io.sentry.spring.tracing.SentrySpan;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    //    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;


    // 회원가입
    @SentrySpan
    public UserResponseDto singup(UserRequestDto userRequestDto){
        String userid = userRequestDto.getUserid();
        if(!userRequestDto.getPassword().matches("^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[$`~!@$!%*#^?&\\\\(\\\\)\\-_=+]).{8,15}$")){ // 비밀번호 정규식 체크
            throw new ApiException(ExceptionEnum.PASSWAORD_REGEX);
        }

        String password = passwordEncoder.encode(userRequestDto.getPassword());

        // 중복된 아이디값 체크
        if(userRepository.findByUserid(userRequestDto.getUserid()).isPresent()){
            throw new ApiException(ExceptionEnum.DUPLICATED_USER_NAME);
        }

        //관리자 권한 체크
        UserRoleEnum role = UserRoleEnum.USER;
        if(userRequestDto.isAdmin()){
            role = UserRoleEnum.ADMIN;
        }

        userRepository.save(new User(userid, password, role)); // 회원저장
        return new UserResponseDto("회원가입 성공");
    }


    // 로그인
    public UserResponseDto login(UserRequestDto userRequestDto, HttpServletResponse response){
        String userId = userRequestDto.getUserid();
        String password =  userRequestDto.getPassword();

        // 사용자 확인
        Optional<User> user = userRepository.findByUserid(userId);
        if(user.isEmpty()){
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        }

        // 비밀번호 확인
        if(!passwordEncoder.matches(password, user.get().getPassword())){
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        }

        // 아이디 정보로 토큰 생성
        TokenDto tokenDto = jwtUtil.creatAllToken(userId, user.get().getRole());

        // Refresh 토큰 있는지 확인
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserid(userId);

        if(refreshToken.isPresent()){
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        }else{
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), userId);
            refreshTokenRepository.save(newToken);
        }

        //response 헤더에 AccessToken / RefreshToken
        response.addHeader(JwtUtil.ACCESS_KEY, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_KEY, tokenDto.getRefreshToken());

        return new UserResponseDto("로그인 성공");
    }

    //로그아웃
    public UserResponseDto logout(User user, HttpServletRequest request){
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserid(user.getUserid());
        String accessToken = request.getHeader("ACCESS_KEY").substring(7);
        if(refreshToken.isPresent()){
            Long tokenTime = jwtUtil.getExpirationTime(accessToken);
            redisUtil.setBlackList(accessToken, "access_token", tokenTime);
            refreshTokenRepository.deleteByUserid(user.getUserid());
            return new UserResponseDto(user.getUserid());
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_USER);
    }

}