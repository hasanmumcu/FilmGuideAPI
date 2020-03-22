package com.example.filmguide.controller.Authentication;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = -8006220798066154264L;

    private String username;
    private String password;
    private String email;
}