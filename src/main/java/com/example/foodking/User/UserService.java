package com.example.foodking.User;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.User.DTO.LoginReqDTO;
import lombok.RequiredArgsConstructor;
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

    public String login(LoginReqDTO loginReqDTO){

        User user = userRepository.findUserByEmail(loginReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.LOGIN_FAIL));

        isMatchPassword(loginReqDTO.getPassword(), user.getPassword(), ExceptionCode.LOGIN_FAIL);
        return jwtProvider.createToken(user.getUserId(), user.getAuthorities());
    }

    public void isMatchPassword(String password1, String password2,ExceptionCode exceptionCode){

        if(!passwordEncoder.matches(password1,password2))
            throw new CommondException(exceptionCode);
    }
}
