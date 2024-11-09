package com.khanh.labeling_management.entity.information_management;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Document(collection = "User")
@Setter
@Getter
@RequiredArgsConstructor
public class User implements UserDetails {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private Status status;
    private int active = 1;
    private String roleId;
    private String roleName;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private boolean deleted;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return active == 1;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return active == 1;
    }

    public enum Status {
        Active
    }
}
