package com.filmguide.security.jwt;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtAuthenticationException extends AuthenticationException {
  
	/**
     *
     */
    private static final long serialVersionUID = -8835554321838080626L;

    public InvalidJwtAuthenticationException(String e) {
        super(e);
    }
}
