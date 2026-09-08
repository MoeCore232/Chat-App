package com.chat_app.chat_app.Shared.Config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(
            JwtHelper jwtHelper,
            UserDetailsService userDetailsService
    ) {
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend (Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authorization =
                    accessor.getFirstNativeHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ")) {

                throw new IllegalArgumentException(
                        "Missing or invalid Authorization header"
                );
            }

            String token = authorization.substring(7);

            String username = jwtHelper.extraUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            boolean isTokenValid = jwtHelper.isTokenValid(token, userDetails);

            if (!isTokenValid) {
                throw new IllegalArgumentException(
                        "Invalid JWT"
                );
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            accessor.setUser(authentication);
        }

        return message;
    }
}