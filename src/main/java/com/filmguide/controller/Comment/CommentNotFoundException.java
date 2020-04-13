package com.filmguide.controller.Comment;

public class CommentNotFoundException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -4815767088197731740L;

    public CommentNotFoundException() {
    }

    public CommentNotFoundException(Long commentId ) {
        super("Comment: " +commentId +" not found.");
    }
}
