package com.neobyte8888.ecommerce.modules.auth.dto;

public class JwtAuthResponse {
	private String accessToken;
	private String tokenType = "Bearer"; // Chuẩn OAuth2/JWT luôn có chữ Bearer đi kèm

	public JwtAuthResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
}
