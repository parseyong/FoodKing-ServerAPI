package com.example.foodking.User;

import com.example.foodking.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User,Long>,
        QuerydslPredicateExecutor<User> {

}
