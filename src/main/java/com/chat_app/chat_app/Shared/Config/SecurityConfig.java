package com.chat_app.chat_app.Shared.Config;

import com.chat_app.chat_app.Core_System.User.User;
import com.chat_app.chat_app.Core_System.User.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    private final String ADMIN = User.Roles.ADMIN_ROLE.name();
    private final String USER = User.Roles.USER_ROLE.name();

    UserDetailsServiceImpl userDetailsService;
    JwtAuthFilter jwtAuthFilter;

    public SecurityConfig (UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .cors(c -> c.disable())
                .csrf(c -> c.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                                    //Auth
                                    "/api/user/sigh-up",
                                    "/api/user/sigh-in"
                            ).permitAll()
                            // User
                            .requestMatchers(HttpMethod.GET, "/api/user/get-all-users").hasRole(ADMIN)
                            .requestMatchers(HttpMethod.GET, "/api/user/get-user-by-id/{employeeId}").hasRole(ADMIN)
                            .requestMatchers(HttpMethod.PUT, "/api/user/update-user").hasAnyRole(ADMIN, USER)
                            .requestMatchers(HttpMethod.DELETE, "/api/user/delete-user/{userId}").hasAnyRole(ADMIN, USER)

                            // Conversation
                            .requestMatchers(HttpMethod.GET, "/api/conversation/get-all-conversations").hasRole(ADMIN)
                            .requestMatchers(HttpMethod.POST, "/api/conversation/create-conversations").hasAnyRole(ADMIN, USER)

                            // Message
                            .requestMatchers(HttpMethod.GET, "/api/message/get-all-messages").hasRole(ADMIN)
                            .requestMatchers(HttpMethod.POST, "/api/message/create-message").hasAnyRole(ADMIN, USER)
                            .requestMatchers(HttpMethod.DELETE, "/api/message/delete-message/{messageId}").hasAnyRole(ADMIN, USER)
                            .anyRequest()
                            .authenticated();
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager(httpSecurity));

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity){
        var authBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(
                passwordEncoder()
        );
        return authBuilder.build();
    }
}
