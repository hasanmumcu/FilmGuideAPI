package com.filmguide.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="votes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends AbstractPersistableEntity<Long>{
    /**
    *
    */
    private static final long serialVersionUID = 4985769437763246732L;

    @Column(nullable = false)
    private int vote;

    @Column(nullable = false)
    private String movieId;

    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = true)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;
    
    public void setVote(int vote){
        if(vote < 0) this.vote = 0;
        else if(vote > 10) this.vote = 10;
        else this.vote = vote;
    }

    @Override
    public String toString(){

        return "Vote(vote: " + this.getVote() + 
                ", movieId: " + this.getMovieId() +
                ", user: " + this.getUser() + ")";
    }
}