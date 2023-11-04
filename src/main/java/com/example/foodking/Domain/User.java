package com.example.foodking.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String phoneNum;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<RecipeInfo> recipeInfoList;

    @Builder
    public User(String email, String password, String nickName, String phoneNum){
        this.email=email;
        this.password=password;
        this.nickName=nickName;
        this.phoneNum=phoneNum;
    }
    public void changePhoneNum(String phoneNum){
        this.phoneNum=phoneNum;
    }
    public void changeNickName(String nickName){
        this.nickName=nickName;
    }
    public void changePassword(String password){
        this.phoneNum=password;
    }
}
