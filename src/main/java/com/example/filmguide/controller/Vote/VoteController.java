package com.example.filmguide.controller.Vote;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.filmguide.Utility;
import com.example.filmguide.model.User;
import com.example.filmguide.model.Vote;
import com.example.filmguide.repository.UserRepository;
import com.example.filmguide.repository.VoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class VoteController{

    @Autowired
    UserRepository users;

    @Autowired
    VoteRepository votes;

    @GetMapping("/votes")
    public ResponseEntity<Object> getVotes(@RequestParam(value = "movieId", required = false) String movieId ){
        
        try{
            List<Vote> votes;
            if(movieId == null){
                votes = this.votes.findAll();
            }
            else{
                votes = this.votes.findByMovieId(movieId);
            }

            return ResponseEntity.ok(votes);
        }
        catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/votes")
    public ResponseEntity<Map<String, Object>> postVote(@RequestBody VoteRequest voteRequest, @AuthenticationPrincipal UserDetails userDetails){
        
        final String movieId = voteRequest.getMovieId();
        final Integer vote = voteRequest.getVote();

        System.out.println(vote);

        if(movieId == null || vote == null){
            return Utility.buildErrorResponse(400, "Bad Request", "Missing or invalid data", "/v1/votes", HttpStatus.BAD_REQUEST);
        }
        
        try{
            Optional<User> users = this.users.findByUsername(userDetails.getUsername());
            
            if(!users.isPresent()){
                throw new Exception();
            }

            User user = users.get();
            this.votes.save(Vote.builder()
                            .movieId(movieId)
                            .vote(vote.intValue())
                            .user(user)
                            .createdAt(new Date())
                            .build()
            );
        }
        catch(Exception ex){
            return Utility.buildErrorResponse(500, "Internal Server Error", "Database problem", "/v1/votes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
        return Utility.buildSuccessResponse(200, "Vote has been created successfully", HttpStatus.OK);
    }

    @PutMapping("/votes")
    public ResponseEntity<Map<String, Object>> putVote(@RequestParam(value = "voteId", required = true) Long id, @RequestBody VoteRequest voteRequest, @AuthenticationPrincipal UserDetails userDetails){
        
        final String movieId = voteRequest.getMovieId();
        final Integer vote = voteRequest.getVote();

        if(movieId == null || vote == null){
            return Utility.buildErrorResponse(400, "Bad Request", "Missing movieId or vote", "/v1/votes", HttpStatus.BAD_REQUEST);
        }

        try{
            Optional<Vote> votes = this.votes.findById(id);
            if(!votes.isPresent()){
                return Utility.buildErrorResponse(404, "Not Found", "Vote not found", "/v1/votes", HttpStatus.NOT_FOUND);
            }

            Vote voteObj = votes.get();

            if(!voteObj.getUser().getUsername().equals(userDetails.getUsername())){
                return Utility.buildErrorResponse(403, "Forbidden", "Can not update vote", "/v1/votes", HttpStatus.FORBIDDEN);
            }

            voteObj.setMovieId(movieId);
            voteObj.setVote(vote);
            voteObj.setUpdatedAt(new Date());
            this.votes.save(voteObj);
        }
        catch(Exception ex){
            return Utility.buildErrorResponse(500, "Internal Server Error", "Database problem", "/v1/votes", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Utility.buildSuccessResponse(200, "Vote has been updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/votes")
    public ResponseEntity<Map<String, Object>> deleteVote(@RequestParam(value = "voteId", required = true) Long id, @AuthenticationPrincipal UserDetails userDetails){
    
        try{
            Optional<Vote> votes = this.votes.findById(id);
            
            if(!votes.isPresent()){
                throw new Exception();
            }

            Vote vote = votes.get();

            if(!vote.getUser().getUsername().equals(userDetails.getUsername())){
                return Utility.buildErrorResponse(403, "Forbidden", "Can not delete vote", "/v1/votes", HttpStatus.FORBIDDEN);
            }

            this.votes.delete(vote);
        }
        catch(Exception ex){
            return Utility.buildErrorResponse(500, "Internal Server Error", "Database problem", "/v1/votes", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Utility.buildSuccessResponse(200, "Vote has been deleted successfully", HttpStatus.OK);
    }
}