package com.filmguide.model;

import org.springframework.security.core.userdetails.UserDetails;

public interface IUser extends UserDetails{

    public String getFirstName();
    public String getLastName();
    public String getEmail();
    public void setFirstName(String firstName);
    public void setLastName(String lastName);
    public void setEmail(String email);
}