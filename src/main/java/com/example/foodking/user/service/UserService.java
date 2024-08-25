package com.example.foodking.user.service;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.dto.request.*;
import com.example.foodking.user.dto.response.LoginTokenRes;
import com.example.foodking.user.dto.response.UserFindRes;
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


    @Transactional
    public LoginTokenRes login(LoginReq loginReq){
        User user = userRepository.findUserByEmail(loginReq.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.LOGIN_FAIL));

        isMatchPassword(loginReq.getPassword(), user.getPassword(), ExceptionCode.LOGIN_FAIL);

        return LoginTokenRes.builder()
                .accessToken(jwtProvider.createAccessToken(user.getUserId(), user.getAuthorities()))
                .refreshToken(jwtProvider.createRefreshToken(user.getUserId(),user.getAuthorities()))
                .build();
    }

    @Transactional
    public void addUser(UserAddReq userAddReq){
        // 인증여부 확인
        coolSmsService.isAuthenticatedNum(userAddReq.getPhoneNum());

        // 이메일 중복 체크
        if(checkEmailDuplication(userAddReq.getEmail()))
           throw new CommondException(ExceptionCode.EMAIL_DUPLICATED);

        /*
            닉네임 중복여부 체크, unique무결성이 깨진다면 늦게 커밋된 트랜잭션은 롤백된다.
            unique로 설정하여 동시성 문제가 발생하지 않고 늦은 트랜잭션은 락을 다시 회수할 필요가 없기때문에 분산락을 적용하지 않는다.
            unique무결성 체크를 DB단에서 체크한다 해도 비즈니스로직으로 중복여부를 한번 더 체크해주는게 좋다.
        */
        if(checkNickNameDuplication(userAddReq.getNickName()))
            throw new CommondException(ExceptionCode.NICKNAME_DUPLICATED);

        // 휴대폰번호로 가입된 계정이 있는 지 체크, unique로 등록했더라도 서버단에서 한번 검증을 한 뒤 db에 넘긴다.
        if(userRepository.existsByPhoneNum(userAddReq.getPhoneNum()))
            throw new CommondException(ExceptionCode.PHONE_NUMBER_DUPLICATED);
        
        // 회원가입 시 입력하는 두개의 비밀번호 일치여부 체크
        if(!userAddReq.getPassword().equals(userAddReq.getPasswordRepeat()))
            throw new CommondException(ExceptionCode.PASSWORD_NOT_COLLECT);

        // passwordEncoder를 통한 비밀번호 암호화 및 salt처리
        userAddReq.setPassword(passwordEncoder.encode(userAddReq.getPassword()));

        // 유저 저장
        userRepository.save(UserAddReq.toEntity(userAddReq));

        //레디스에 저장된 인증정보 삭제
        coolSmsService.deleteAuthInfo(userAddReq.getPhoneNum());
    }

    public boolean checkEmailDuplication(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean checkNickNameDuplication(String nickName){
        return userRepository.existsByNickName(nickName);
    }

    @Transactional
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
    /*
        passwordEncoder는 단방향 해시이기때문에 인코딩된 데이터의 원래값을 역추적하기 어렵다.
        따라서 새로운 비밀번호를 생성하고 반환한다.
    */
    public String findPassword(PasswordFindReq passwordFindReq){
        // 인증여부 확인
        coolSmsService.isAuthenticatedNum(passwordFindReq.getPhoneNum());

        User user = userRepository.findUserByEmail(passwordFindReq.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        // 이메일과 연결된 유저의 휴대폰번호와 입력받은 휴대폰 번호가 일치하는 지 체크
        if(!user.getPhoneNum().equals(passwordFindReq.getPhoneNum()))
            throw new CommondException(ExceptionCode.ACCESS_FAIL_USER);

        // 임시 비밀번호 생성
        String tempPassword = RandomStringUtils.randomAlphanumeric(15);

        // 유저비밀번호 변경내용 저장
        user.updatePassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // 인증정보 삭제
        coolSmsService.deleteAuthInfo(passwordFindReq.getPhoneNum());

        return tempPassword;
    }

    public UserFindRes findUser(Long userId){
        User user = findUserById(userId);
        return UserFindRes.toDTO(user);
    }

    @Transactional
    public void updateUser(UserUpdateReq userUpdateReq, Long userId){
        User user = findUserById(userId);

        // 비밀번호 일치여부 체크, 유저정보를 변경할 때 이전 비밀번호를 입력해야한다.
        isMatchPassword(userUpdateReq.getOldPassword(),user.getPassword(),ExceptionCode.PASSWORD_NOT_COLLECT);

        /*
            닉네임 중복여부 체크, unique무결성이 깨진다면 늦게 커밋된 트랜잭션은 롤백된다.
            unique로 설정하여 동시성 문제가 발생하지 않고 늦은 트랜잭션은 락을 다시 회수할 필요가 없기때문에 분산락을 적용하지 않는다.
            unique무결성 체크를 DB단에서 체크한다 해도 비즈니스로직으로 중복여부를 한번 더 체크해주는게 좋다.
        */
        if(checkNickNameDuplication(userUpdateReq.getNickName()))
            throw new CommondException(ExceptionCode.NICKNAME_DUPLICATED);

        user.updateNickName(userUpdateReq.getNickName());

        // 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(userUpdateReq.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UserDeleteReq userDeleteReq, String accessToken){
        User user = userRepository.findUserByEmail(userDeleteReq.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        //회원탈퇴 시 비밀번호를 다시 입력해야한다.
        isMatchPassword(userDeleteReq.getPassword(), user.getPassword(), ExceptionCode.PASSWORD_NOT_COLLECT);

        //Bearer 제거
        accessToken = accessToken.substring(7);

        //해당 유저에 발급된 토큰으로 회원인증이 불가능하도록 로그아웃 진행
        jwtProvider.logOut(user.getUserId(), accessToken);
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
