package com.example.reddiserver.security.jwt;

import com.example.reddiserver.dto.security.response.JwtErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            throw new JwtException("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("Jwt 토큰이 잘못되었습니다.", e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable e) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        response.getWriter().write(
                JwtErrorResponse.of(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        e.getMessage()
                ).convertToJson()
        );
    }
}