package com.yigidolar.apiproject.security;

import com.yigidolar.apiproject.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Kullanıcı bulunamadı"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .authorities("USER")
                .build();
    }
}
