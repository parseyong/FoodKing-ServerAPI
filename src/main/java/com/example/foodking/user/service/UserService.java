package com.example.foodking.user.service;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.dto.request.*;
import com.example.foodking.user.dto.response.ReadUserInfoResDTO;
import com.example.foodking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final CoolSmsService coolSmsService;

    public String login(LoginReqDTO loginReqDTO){
        User user = userRepository.findUserByEmail(loginReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.LOGIN_FAIL));

        isMatchPassword(loginReqDTO.getPassword(), user.getPassword(), ExceptionCode.LOGIN_FAIL);
        return jwtProvider.createToken(user.getUserId(), user.getAuthorities());
    }
    @Transactional
    public void addUser(AddUserReqDTO addUserReqDTO){

        coolSmsService.isAuthenticatedNum(addUserReqDTO.getPhoneNum());
        if(emailDuplicatedChecking(addUserReqDTO.getEmail()))
           throw new CommondException(ExceptionCode.EMAIL_DUPLICATED);
        if(nickNameDuplicatedChecking(addUserReqDTO.getNickName()))
            throw new CommondException(ExceptionCode.NICKNAME_DUPLICATED);
        if(!addUserReqDTO.getPassword().equals(addUserReqDTO.getPasswordRepeat()))
            throw new CommondException(ExceptionCode.PASSWORD_NOT_COLLECT);

        // passwordEncoder를 통한 비밀번호 암호화 및 salt처리
        addUserReqDTO.setPassword(passwordEncoder.encode(addUserReqDTO.getPassword()));
        User user = AddUserReqDTO.toEntity(addUserReqDTO);
        userRepository.save(user);
        coolSmsService.deleteAuthInfo(addUserReqDTO.getPhoneNum());
    }

    public boolean emailDuplicatedChecking(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean nickNameDuplicatedChecking(String nickName){
        return userRepository.existsByNickName(nickName);
    }

    public String findEmail(PhoneAuthReqDTO phoneAuthReqDTO){
        coolSmsService.authNumCheck(phoneAuthReqDTO);
        return userRepository.findEmailByPhoneNum(phoneAuthReqDTO.getPhoneNum())
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
    }

    @Transactional
    // passwordEncoder는 단방향 해시이기때문에 인코딩된 데이터의 원래값을 역추적하기 어렵다. 따라서 새로운 비밀번호를 생성하고 반환한다.
    public String findPassword(FindPwdReqDTO findPwdReqDTO){
        coolSmsService.authNumCheck(PhoneAuthReqDTO.builder()
                .phoneNum(findPwdReqDTO.getPhoneNum())
                .authenticationNumber(findPwdReqDTO.getAuthenticationNumber())
                .build());
        User user = userRepository.findUserByEmail(findPwdReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        if(user.getPhoneNum() != findPwdReqDTO.getPhoneNum())
            throw new CommondException(ExceptionCode.ACCESS_FAIL_USER);

        String tmpPassword = RandomStringUtils.randomAlphanumeric(15);
        user.changePassword(passwordEncoder.encode(tmpPassword));
        return tmpPassword;
    }

    public ReadUserInfoResDTO readUser(Long userId){
        User user = findUserById(userId);

        return ReadUserInfoResDTO.toDTO(user);
    }
    @Transactional
    public void updateUser(UpdateUserInfoReqDTO updateUserInfoReqDTO, Long userId){
        User user = findUserById(userId);

        isMatchPassword(updateUserInfoReqDTO.getOldPassword(),user.getPassword(),ExceptionCode.PASSWORD_NOT_COLLECT);
        user.changeNickName(updateUserInfoReqDTO.getNickName());
        user.changePhoneNum(updateUserInfoReqDTO.getPhoneNum());
        user.changePassword(passwordEncoder.encode(updateUserInfoReqDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(DeleteUserReqDTO deleteUserReqDTO){
        User user = userRepository.findUserByEmail(deleteUserReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        isMatchPassword(deleteUserReqDTO.getPassword(), user.getPassword(), ExceptionCode.PASSWORD_NOT_COLLECT);
        userRepository.delete(user);
    }

    //rawPassword에는 인코딩 되지않은 값을, encodedPassword에는 인코딩이 되어있는 값을 넣어야한다.
    public void isMatchPassword(String rawPassword, String encodedPassword, ExceptionCode exceptionCode){

        if(!passwordEncoder.matches(rawPassword,encodedPassword))
            throw new CommondException(exceptionCode);
    }

    public User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
    }

}
