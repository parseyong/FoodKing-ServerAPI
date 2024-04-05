package com.example.foodking.user.service;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.common.RedissonPrefix;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.dto.request.*;
import com.example.foodking.user.dto.response.LoginTokenResDTO;
import com.example.foodking.user.dto.response.ReadUserInfoResDTO;
import com.example.foodking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final CoolSmsService coolSmsService;

    @Qualifier("tokenRedis")
    private final RedisTemplate<String,String> tokenRedis;

    @Qualifier("blackListRedis")
    private final RedisTemplate<String,String> blackListRedis;


    public LoginTokenResDTO login(LoginReqDTO loginReqDTO){
        User user = userRepository.findUserByEmail(loginReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.LOGIN_FAIL));

        isMatchPassword(loginReqDTO.getPassword(), user.getPassword(), ExceptionCode.LOGIN_FAIL);

        return LoginTokenResDTO.builder()
                .accessToken(jwtProvider.createAccessToken(user.getUserId(), user.getAuthorities()))
                .refreshToken(jwtProvider.createRefreshToken(user.getUserId(),user.getAuthorities()))
                .build();
    }

    @Transactional
    public void logOut(Long userId, String accessToken){
        accessToken = accessToken.substring(7);

        blackListRedis.opsForValue().set(
                RedissonPrefix.BLACK_LIST_REDIS + accessToken,
                String.valueOf(userId),
                jwtProvider.validAccessTokenTime,
                TimeUnit.MILLISECONDS);

        tokenRedis.delete(RedissonPrefix.TOKEN_REDIS + String.valueOf(userId));
    }

    @Transactional
    public void addUser(AddUserReqDTO addUserReqDTO){
        // 인증여부 확인
        coolSmsService.isAuthenticatedNum(addUserReqDTO.getPhoneNum());

        // 이메일 중복 체크
        if(emailDuplicatedChecking(addUserReqDTO.getEmail()))
           throw new CommondException(ExceptionCode.EMAIL_DUPLICATED);

        // 닉네임 중복체크
        if(nickNameDuplicatedChecking(addUserReqDTO.getNickName()))
            throw new CommondException(ExceptionCode.NICKNAME_DUPLICATED);

        // 휴대폰번호로 가입된 계정이 있는 지 체크
        if(userRepository.existsByPhoneNum(addUserReqDTO.getPhoneNum()))
            throw new CommondException(ExceptionCode.PHONE_NUMBER_DUPLICATED);
        
        // 회원가입 시 입력하는 두개의 비밀번호 일치여부 체크
        if(!addUserReqDTO.getPassword().equals(addUserReqDTO.getPasswordRepeat()))
            throw new CommondException(ExceptionCode.PASSWORD_NOT_COLLECT);

        // passwordEncoder를 통한 비밀번호 암호화 및 salt처리
        addUserReqDTO.setPassword(passwordEncoder.encode(addUserReqDTO.getPassword()));

        // 유저 저장
        userRepository.save(AddUserReqDTO.toEntity(addUserReqDTO));

        //레디스에 저장된 인증정보 삭제
        coolSmsService.deleteAuthInfo(addUserReqDTO.getPhoneNum());
    }

    public boolean emailDuplicatedChecking(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean nickNameDuplicatedChecking(String nickName){
        return userRepository.existsByNickName(nickName);
    }

    public String findEmail(String phoneNum){
        // 인증여부 확인
        coolSmsService.isAuthenticatedNum(phoneNum);

        // 이메일 조회
        String email = userRepository.findEmailByPhoneNum(phoneNum)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        // 인정정보 삭제
        coolSmsService.deleteAuthInfo(phoneNum);
        
        return email;
    }

    @Transactional
    // passwordEncoder는 단방향 해시이기때문에 인코딩된 데이터의 원래값을 역추적하기 어렵다. 따라서 새로운 비밀번호를 생성하고 반환한다.
    public String findPassword(FindPwdReqDTO findPwdReqDTO){
        // 인증여부 확인
        coolSmsService.isAuthenticatedNum(findPwdReqDTO.getPhoneNum());

        User user = userRepository.findUserByEmail(findPwdReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        if(user.getPhoneNum() != findPwdReqDTO.getPhoneNum())
            throw new CommondException(ExceptionCode.ACCESS_FAIL_USER);

        // 임시 비밀번호 생성
        String tmpPassword = RandomStringUtils.randomAlphanumeric(15);

        // 유저비밀번호 변경내용 저장
        user.changePassword(passwordEncoder.encode(tmpPassword));
        userRepository.save(user);

        // 인증정보 삭제
        coolSmsService.deleteAuthInfo(findPwdReqDTO.getPhoneNum());

        return tmpPassword;
    }

    public ReadUserInfoResDTO readUser(Long userId){
        User user = findUserById(userId);
        return ReadUserInfoResDTO.toDTO(user);
    }

    @Transactional
    public void updateUser(UpdateUserInfoReqDTO updateUserInfoReqDTO, Long userId){
        User user = findUserById(userId);

        // 비밀번호 일치여부 체크, 유저정보를 변경할 때 이전 비밀번호를 입력해야한다.
        isMatchPassword(updateUserInfoReqDTO.getOldPassword(),user.getPassword(),ExceptionCode.PASSWORD_NOT_COLLECT);

        // 닉네임 중복여부 체크, unique무결성이 깨진다면 늦게 커밋된 트랜잭션은 롤백된다.
        // unique로 설정해놓았고 늦은 트랜잭션은 락을 다시 회수할 필요가 없기때문에 분산락을 적용하지 않는다.
        // unique무결성 체크를 DB단에서 체크한다 해도 비즈니스로직으로 중복여부를 한번 더 체크해주는게 좋다.
        if(nickNameDuplicatedChecking(updateUserInfoReqDTO.getNickName()))
            throw new CommondException(ExceptionCode.NICKNAME_DUPLICATED);

        user.changeNickName(updateUserInfoReqDTO.getNickName());

        // 비밀번호 변경
        user.changePassword(passwordEncoder.encode(updateUserInfoReqDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(DeleteUserReqDTO deleteUserReqDTO){
        User user = userRepository.findUserByEmail(deleteUserReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        // 로그인여부와 상관없이 회원탈퇴 시 비밀번호를 입력해야한다.
        isMatchPassword(deleteUserReqDTO.getPassword(), user.getPassword(), ExceptionCode.PASSWORD_NOT_COLLECT);
        userRepository.delete(user);
    }

    //rawPassword에는 인코딩 되지않은 값을, encodedPassword에는 인코딩이 되어있는 값을 넣어야한다.
    private void isMatchPassword(String rawPassword, String encodedPassword, ExceptionCode exceptionCode){
        if(!passwordEncoder.matches(rawPassword,encodedPassword))
            throw new CommondException(exceptionCode);
    }

    private User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
    }

}
