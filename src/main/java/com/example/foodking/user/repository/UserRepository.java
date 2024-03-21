package com.example.foodking.user.repository;

import com.example.foodking.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickName(String nickName);
    Optional<String> findEmailByPhoneNum(String phoneNum);

}
