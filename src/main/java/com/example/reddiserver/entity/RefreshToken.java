package com.example.reddiserver.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@RedisHash(value = "refreshToken", timeToLive = 60*60*24*3)
public class RefreshToken {

    @Id
    private Long userId;

    private String refreshToken;

}
