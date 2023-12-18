package com.example.foodking.User;

import com.example.foodking.RecipeInfo.RecipeInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "nick_name")
    private String nickName;

    @Column(nullable = false, name = "phone_num")
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
