package com.back.motionit.standard.ut;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	public static class Jwt {
		public static String toString(String secret, long expireSeconds, Map<String, Object> body) {
			ClaimsBuilder claimsBuilder = Jwts.claims();

			for (Map.Entry<String, Object> entry : body.entrySet()) {
				claimsBuilder.add(entry.getKey(), entry.getValue());
			}

			Claims claims = claimsBuilder.build();

			Date issuedAt = new Date();
			Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

			Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret.trim()));

			String jwt = Jwts.builder()
				.claims(claims)
				.issuedAt(issuedAt)
				.expiration(expiration)
				.signWith(secretKey)
				.compact();

			return jwt;
		}

		public static boolean isValid(String jwt, String secret) {

			SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret.trim()));

			try {
				Jwts
					.parser()
					.verifyWith(secretKey)
					.build()
					.parse(jwt);

			} catch (Exception e) {
				return false;
			}

			return true;
		}

		public static Map<String, Object> payloadOrNull(String jwt, String secret) {

			SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret.trim()));

			if (isValid(jwt, secret)) {
				return (Map<String, Object>)Jwts
					.parser()
					.verifyWith(secretKey)
					.build()
					.parse(jwt)
					.getPayload();
			}

			return null;
		}

		public static boolean isExpired(String jwt, String secret) {
			SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret.trim()));

			try {
				Jwts
					.parser()
					.verifyWith(secretKey)
					.build()
					.parse(jwt);
				return false;

			} catch (ExpiredJwtException e) {
				return true;

			} catch (Exception e) {
				return true;
			}
		}
	}
}
