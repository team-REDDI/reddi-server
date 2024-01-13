package com.example.reddiserver.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 60*60*24*3)
public class RefreshToken {

    @Id
    private String refreshToken;

    @Indexed
    private String providerId;


    @Builder
    public RefreshToken(String providerId, String refreshToken) {
        this.providerId = providerId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}
