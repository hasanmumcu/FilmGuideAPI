package com.example.filmguide.controller.Authentication;

import com.example.filmguide.model.Comment;
import com.example.filmguide.model.User;
import com.example.filmguide.repository.CommentRepository;
import com.example.filmguide.repository.UserRepository;
import com.example.filmguide.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository users;

    @Autowired
    CommentRepository comments;

    @Autowired
    PasswordEncoder passwordEncoder;
    

    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@RequestBody AuthenticationRequest data) {

        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest data){

        try{
            String username = data.getUsername();
            String password = data.getPassword();
            String email = data.getEmail();

            Map<Object, Object> model = new HashMap<>();

            if(username == null || password == null || email == null){
                model.put("message", "Missing credentials!");
                model.put("success", false);
                return ResponseEntity.badRequest().body(model);
            }

            Optional<User> userList = this.users.findByUsername(username);
            if(userList.isPresent()){
                model.put("message", "Username is already in use!");
                model.put("success", false); 
                return ResponseEntity.badRequest().body(model);
            }

            userList = this.users.findByEmail(email);
            if(userList.isPresent()){
                model.put("message", "Email is already in use!");
                model.put("success", false);
                return ResponseEntity.badRequest().body(model);
            }

            User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .roles(Arrays.asList("ROLE_USER"))
                .build();
            
            Comment comment = Comment.builder().body("test").user(user).movieId("22").build();
            Comment comment2 = Comment.builder().body("asdadsa").user(user).movieId("22").build();
            this.users.save(user);
            this.comments.save(comment);
            this.comments.save(comment2);
            

            model.put("message", "Registration successful!");
            model.put("success", true);
           
            return ResponseEntity.ok().body(model);
        }
        catch (AuthenticationException e){
            throw new BadCredentialsException("adsada");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Object> userList(){

        try{
            List<User> userList = this.users.findAll();
            return ResponseEntity.ok().body(userList);
        }
        catch(Exception ex){
            Map<Object, Object> model = new HashMap<Object, Object>();
            model.put("error","Database connection has been lost!");
            return ResponseEntity.status(500).body(model);
        }
    }
}
