package com.example.filmguide.config;

import com.example.filmguide.security.jwt.JwtSecurityConfigurer;
import com.example.filmguide.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth/signin").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .antMatchers(HttpMethod.GET, "/auth/users").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/auth/users").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/v1/comments").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/v1/comments").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/v1/comments").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/v1/comments").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/v1/votes").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/v1/votes").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/v1/votes").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/v1/votes").hasRole("USER")
                .anyRequest().authenticated()
            .and()
            .apply(new JwtSecurityConfigurer(jwtTokenProvider));
        //@formatter:on
    }


}

