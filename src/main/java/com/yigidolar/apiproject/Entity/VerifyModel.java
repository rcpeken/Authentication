package com.yigidolar.apiproject.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "verify")
public class VerifyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userID;

    private String email;

    private String code;

    private int type; // 1 -> Mail 2-> Reset Password

    public VerifyModel(Long id, long userID, String email, String code, int type) {
        this.id = id;
        this.userID = userID;
        this.email = email;
        this.code = code;
        this.type = type;
    }

    public VerifyModel() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}