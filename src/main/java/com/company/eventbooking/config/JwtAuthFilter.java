package com.company.eventbooking.config;

import com.company.eventbooking.entity.User;
import com.company.eventbooking.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@EnableMethodSecurity
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header=request.getHeader("Authorization");
        if(header!=null && header.startsWith("Bearer ")){
            String token=header.substring(7);
            String email=jwtUtil.extractEmail(token);

            User user=userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

            if(user !=null){
                UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(email,null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );
                MDC.put("authenticatedUser", user.getEmail());
                MDC.put("userRole", user.getRole());
                log.info("User : {}", user.getEmail());
                log.info("UserRole : {}", user.getRole());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request,response);

    }


}