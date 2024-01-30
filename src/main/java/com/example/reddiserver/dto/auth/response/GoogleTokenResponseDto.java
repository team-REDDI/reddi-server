package com.example.reddiserver.dto.auth.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleTokenResponseDto {
	public String access_token;
	public Integer expires_in;
	public String scope;
	public String token_type;
	public String id_token;
}
