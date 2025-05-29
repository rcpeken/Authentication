package com.yigidolar.apiproject.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "userTable")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String username;
    @Column
    private String password;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private boolean verified = false;
}
