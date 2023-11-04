package com.example.foodking.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface Reply extends JpaRepository<Reply,Long>,
        QuerydslPredicateExecutor<Reply> {

}
