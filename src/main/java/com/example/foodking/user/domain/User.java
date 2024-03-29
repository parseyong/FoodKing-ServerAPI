package com.example.foodking.user.domain;

import com.example.foodking.recipe.domain.RecipeInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "nick_name", unique = true)
    private String nickName;

    @Column(nullable = false, name = "phone_num",unique = true)
    private String phoneNum;

    @Column(nullable = false,name = "role_name")
    private String roleName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<RecipeInfo> recipeInfoList;

    @Builder
    public User(String email, String password, String nickName, String phoneNum){
        this.email=email;
        this.password=password;
        this.nickName=nickName;
        this.phoneNum=phoneNum;
        this.roleName="ROLE_USER";
    }
    public void changePhoneNum(String phoneNum){
        this.phoneNum=phoneNum;
    }
    public void changeNickName(String nickName){
        this.nickName=nickName;
    }
    public void changePassword(String password){
        this.password=password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roleName));
        return authorities;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
