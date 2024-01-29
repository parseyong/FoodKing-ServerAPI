package com.example.foodking.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>,
        QuerydslPredicateExecutor<User> {
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickName(String nickName);
    Optional<String> findEmailByPhoneNum(String phoneNum);

}
