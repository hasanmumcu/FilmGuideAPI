package com.example.filmguide;

import com.example.filmguide.model.Comment;
import com.example.filmguide.model.User;
import com.example.filmguide.repository.CommentRepository;
import com.example.filmguide.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {


    @Autowired
    UserRepository users;

    @Autowired
    CommentRepository comments;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {

        User user = User.builder()
            .username("user")
            .password(this.passwordEncoder.encode("password"))
            .email("user@user.gmail")
            .firstName("first_name")
            .lastName("last_name")
            .roles(Arrays.asList( "ROLE_USER"))
            .build();
 

        Comment comment = Comment.builder().body("test").user(user).movieId("22").createdAt(new Date()).build();
        this.users.save(user);
        this.comments.save(comment);

        this.users.save(User.builder()
            .username("admin")
            .password(this.passwordEncoder.encode("password"))
            .email("admin@user.gmail")
            .firstName("first_name")
            .lastName("last_name")
            .roles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"))
            .build()
        );

        log.debug("printing all users...");
        this.users.findAll().forEach(v -> log.debug(" User :" + v.toString()));
    }
}
