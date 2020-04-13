package com.filmguide.controller.Comment;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.filmguide.Utility;
import com.filmguide.model.Comment;
import com.filmguide.model.User;
import com.filmguide.repository.CommentRepository;
import com.filmguide.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class CommentController{

    @Autowired
    CommentRepository comments;

    @Autowired
    UserRepository users;

    @GetMapping("/comments")
    public ResponseEntity<Object> getComments(@RequestParam(value ="movieId", required = false) String movieId){

        try{
            List<Comment> comments;
            if(movieId == null){
                comments = this.comments.findAll();
            }
            else{
                comments = this.comments.findByMovieId(movieId);
            }
            return ResponseEntity.ok(comments);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/comments")
    public ResponseEntity<Map<String, Object>> postComment(@RequestBody CommentRequest commentRequest, @AuthenticationPrincipal UserDetails userDetails){
        
        final String movieId = commentRequest.getMovieId();
        final String body = commentRequest.getBody();

        if(movieId == null || body == null){     
            return Utility.buildErrorResponse(400, "Bad Request", "Missing request body", "/v1/comments", HttpStatus.BAD_REQUEST);          
        }

        try{
            Optional<User> users = this.users.findByUsername(userDetails.getUsername());
            if(!users.isPresent()){
                throw new Exception();
            }
            User user = users.get();

            this.comments.save(Comment.builder()
                                .body(body)
                                .movieId(movieId)
                                .user(user)
                                .createdAt(new Date())
                                .build()
            );
        }
        catch(Exception ex){
           
            return Utility.buildErrorResponse(500, "Internal Server Error", "Database problem", "/v1/comments", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
       return Utility.buildSuccessResponse(200, "Comment has been created successfully", HttpStatus.OK);
    }

    @PutMapping("/comments")
    public ResponseEntity<Map<String, Object>> putComment(@RequestParam(value = "commentId", required = true) Long id, @RequestBody CommentRequest requestBody, @AuthenticationPrincipal UserDetails userDetails){
        
        final String movieId = requestBody.getMovieId();
        final String body = requestBody.getBody();
        
        if(movieId == null || body == null){
            return Utility.buildErrorResponse(400, "Bad Request", "Missing request body", "/v1/comments", HttpStatus.BAD_REQUEST);
        }

        try{
            Optional<Comment> comments = this.comments.findById(id);
            if(!comments.isPresent()){
                return Utility.buildErrorResponse(404, "Not Found", "Comment not found", "/v1/comments", HttpStatus.NOT_FOUND);
            }

            Comment comment = comments.get();
            
            if(!comment.getUser().getUsername().equals(userDetails.getUsername())){
                return Utility.buildErrorResponse(403, "Forbidden", "Can not update comment", "/v1/comments", HttpStatus.FORBIDDEN);
            }

            comment.setBody(body);
            comment.setMovieId(movieId);
            comment.setUpdatedAt(new Date());
            this.comments.save(comment);
        }
        catch(Exception ex){
            return Utility.buildErrorResponse(500, "Internal Server Error", "Database problem", "/v1/comments", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Utility.buildSuccessResponse(200, "Comment has been updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/comments")
    public ResponseEntity<Map<String, Object>> deleteComment(@RequestParam(value = "commentId", required = true) Long id, @AuthenticationPrincipal UserDetails userDetails){

        try{
            Optional<Comment> comments = this.comments.findById(id);
            if(!comments.isPresent()){
                return Utility.buildErrorResponse(404, "Not Found", "Comment not found", "/v1/comments", HttpStatus.NOT_FOUND);
            }

            Comment comment = comments.get();
            
            if(!comment.getUser().getUsername().equals(userDetails.getUsername())){
                return Utility.buildErrorResponse(403, "Forbidden", "Can not delete comment", "/v1/comments", HttpStatus.FORBIDDEN);
            }

            this.comments.delete(comment);
        }
        catch(Exception ex){
            return Utility.buildErrorResponse(500, "Internal Server Error", "Database problem", "/v1/comments", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Utility.buildSuccessResponse(200, "Comment has been deleted successfully", HttpStatus.OK);
    }


    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getCommentsById(@PathVariable("id") Long id){
 
        Comment comment = this.comments.findById(id).orElseThrow(() -> new CommentNotFoundException());
        return ResponseEntity.ok(comment);
    }

}