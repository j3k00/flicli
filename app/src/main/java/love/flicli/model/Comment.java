package love.flicli.model;

import net.jcip.annotations.Immutable;

/**
 * Created by tommaso on 19/05/17.
 */

/*
 * This class represent every single comment.
 * When we make a request and store the results, we managed every comments in an object of this format.
 */

@Immutable
public class Comment {
    private String author = "";
    private String comment = "";

    public Comment(String author, String comment) {
        this.author = author;
        this.comment = comment;
    }

    public String getAuthor() { return this.author; }

    public String getComment() { return this.comment; }
}
