package com.example.foodking.Repository;

import com.example.foodking.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User,Long>,
        QuerydslPredicateExecutor<User> {

}
