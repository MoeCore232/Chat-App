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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(c -> c.disable())
                .authorizeHttpRequests(auth -> { auth.requestMatchers(
                        "/",
                        "/api/user/sigh-up",
                        "/api/user/sigh-in",
                        "/api/user/confirmation-code/{userId}",
                        "/api/user/resend-confirmation-code/{userId}"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // User
                        .requestMatchers(HttpMethod.POST, "/api/user/sigh-up").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.POST, "/api/user/sigh-in").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.GET, "/api/user/get-all-users").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/user/get-user-by-id/{employeeId}").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/user/update-user").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.DELETE, "/api/user/delete-user/{userId}").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.GET, "/api/user/search-by-username/{username}/{currentUsername}").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.GET, "/api/user/get-user-chat-info/{conversationId}/{userId}").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.PUT, "/api/user/save-expo-push-token/{userId}").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.POST, "/api/user/confirmation-code/{userId}").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.PUT, "/api/user/resend-confirmation-code/{userId}").hasAnyRole(ADMIN, USER)

                        // Conversation
                        .requestMatchers(HttpMethod.GET, "/api/conversation/get-all-conversations").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/conversation/create-conversations").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.GET, "/api/conversation/get-all-user-conversations/{userId}").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.PUT, "/api/conversation/mark-as-read/{conversationId}/{userId}").hasAnyRole(ADMIN, USER)

                        // Message
                        .requestMatchers(HttpMethod.GET, "/api/message/get-all-messages").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/message/create-message").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.DELETE, "/api/message/delete-message/{messageId}").hasAnyRole(ADMIN, USER)
                        .requestMatchers("/ws/**").permitAll()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}




