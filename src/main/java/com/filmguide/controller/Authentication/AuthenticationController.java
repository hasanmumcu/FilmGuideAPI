package com.filmguide.controller.Authentication;

import com.filmguide.model.User;
import com.filmguide.repository.CommentRepository;
import com.filmguide.repository.UserRepository;
import com.filmguide.security.jwt.JwtTokenProvider;

import org.apache.http.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final String recaptchaSecretKey = "6Leyv-gUAAAAAF_KpeBeTc8jUHl7ammZnoJ4wsaQ";

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
            String token = jwtTokenProvider.createToken(username, this.users.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest data) {

        Map<Object, Object> model = new HashMap<>();

        try {
            String username = data.getUsername();
            String password = data.getPassword();
            String email = data.getEmail();
            String recaptchaToken = data.getRecaptchaToken();

            final CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
         
            List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
            requestBody.add(new BasicNameValuePair("secret", recaptchaSecretKey));
            requestBody.add(new BasicNameValuePair("response", recaptchaToken));
            request.setEntity(new UrlEncodedFormEntity(requestBody, "UTF-8"));
            
            HttpResponse response = httpClient.execute(request);
            HttpEntity respEntity = response.getEntity();

            if(respEntity != null){
                String content = EntityUtils.toString(respEntity);
                Map<String, Object> entities = JsonParserFactory.getJsonParser().parseMap(content);
                if(!(boolean)(entities.get("success"))){
                    model.put("message", "Recaptcha is not accepted!");
                    model.put("success", false);
                    return ResponseEntity.badRequest().body(model);
                }
            }
            else{
                model.put("message", "Something went wrong!");
                model.put("success", false);
                return ResponseEntity.badRequest().body(model);
            }

            if (username == null || password == null || email == null || recaptchaToken == null) {
                model.put("message", "Missing credentials!");
                model.put("success", false);
                return ResponseEntity.badRequest().body(model);
            }

            Optional<User> userList = this.users.findByUsername(username);
            if (userList.isPresent()) {
                model.put("message", "Username is already in use!");
                model.put("success", false);
                return ResponseEntity.badRequest().body(model);
            }

            userList = this.users.findByEmail(email);
            if (userList.isPresent()) {
                model.put("message", "Email is already in use!");
                model.put("success", false);
                return ResponseEntity.badRequest().body(model);
            }

            User user = User.builder().username(username).password(passwordEncoder.encode(password)).email(email)
                    .roles(Arrays.asList("ROLE_USER")).build();

            this.users.save(user);
         
            model.put("message", "Registration successful!");
            model.put("success", true);

            return ResponseEntity.ok().body(model);
        } catch (AuthenticationException e) {
            model.put("success", false);
            return ResponseEntity.status(500).body(model);
        } catch (UnsupportedEncodingException e1) {
            model.put("success", false);
            return ResponseEntity.status(500).body(model);
        } catch (ClientProtocolException e1) {
            model.put("success", false);
            return ResponseEntity.status(500).body(model);
        } catch (IOException e1) {
            model.put("success", false);
            return ResponseEntity.status(500).body(model);
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
