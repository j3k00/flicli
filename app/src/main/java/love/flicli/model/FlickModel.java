package love.flicli.model;

import net.jcip.annotations.Immutable;

/**
 * Created by tommaso on 19/05/17.
 */

/*
 * This class represent every single photo.
 * When we make a request and store the results, we managed the photo in this format.
 */

@Immutable
public class FlickModel {
    private String image_thb = "";
    private String id = "";
    private String author = "";
    private String imageUrl = "";
    private String title = "";

    public FlickModel(String image_thb, String id, String author, String imageUrl, String title){
        this.image_thb = image_thb;
        this.id = id;
        this.author = author;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getImage_thb() { return this.image_thb; }

    public String getAuthor() { return this.author; }

    public String getTitle() { return this.title; }

    public String getImageUrl() { return this.imageUrl; }

    public String getId() { return this.id; }

    //public String getComment() { return this.comment; }

}
