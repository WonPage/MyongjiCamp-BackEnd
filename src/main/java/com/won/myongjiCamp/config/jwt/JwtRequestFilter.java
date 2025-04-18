package com.won.myongjiCamp.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.config.security.auth.PrincipalDetailService;
import com.won.myongjiCamp.dto.response.ResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PrincipalDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!requestTokenHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "잘못된 토큰 형식");
            return;
        }

        String jwtToken = requestTokenHeader.substring(7);

        if (jwtTokenUtil.isTokenExpired(jwtToken)) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰");
            return;
        }

        String username = jwtTokenUtil.extractUsername(jwtToken);
        PrincipalDetail userDetails = (PrincipalDetail) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ResponseDto responseDto = new ResponseDto(status.value(), message);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), responseDto);
    }
}
