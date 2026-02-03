package com.company.eventbooking.service;

import com.company.eventbooking.config.JwtUtil;
import com.company.eventbooking.dto.AuthResponse;
import com.company.eventbooking.dto.LoginRequest;
import com.company.eventbooking.dto.RegisterRequest;
import com.company.eventbooking.entity.Admin;
import com.company.eventbooking.entity.User;
import com.company.eventbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void register(RegisterRequest request){

        if(userRepository.findByEmail(request.email()).isPresent()){
            throw new UsernameNotFoundException("Email Already Exists");
        }
        User user=User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

    }

    public AuthResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.email()).orElseThrow(()->new RuntimeException("Invalid Credentials"));
        if(!passwordEncoder.matches(request.password(),user.getPassword())){
            throw new BadCredentialsException("Incorrect Credentials");
        }

        String token=jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

}
