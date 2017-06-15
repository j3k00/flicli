package love.flicli.model;

/**
 * Created by jeko on 15/06/17.
 */

public class Comment {
    // Default attribues
    private String id;
    private String author;
    private String  author_is_deleted;
    private String authorname;
    private String iconserver;
    private String iconfarm;
    private String datecreate;
    private String permalink;
    private String path_alias;
    private String realname;
    private String _content;

    public Comment(String id) {
        this.id = id;
        this.author = "";
        this.author_is_deleted = "";
        this.authorname = "";
        this.iconserver = "";
        this.iconfarm = "";
        this.datecreate = "";
        this.permalink = "";
        this.path_alias = "";
        this.realname = "";
        this._content = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_is_deleted() {
        return author_is_deleted;
    }

    public void setAuthor_is_deleted(String author_is_deleted) {
        this.author_is_deleted = author_is_deleted;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getIconserver() {
        return iconserver;
    }

    public void setIconserver(String iconserver) {
        this.iconserver = iconserver;
    }

    public String getIconfarm() {
        return iconfarm;
    }

    public void setIconfarm(String iconfarm) {
        this.iconfarm = iconfarm;
    }

    public String getDatecreate() {
        return datecreate;
    }

    public void setDatecreate(String datecreate) {
        this.datecreate = datecreate;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getPath_alias() {
        return path_alias;
    }

    public void setPath_alias(String path_alias) {
        this.path_alias = path_alias;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String get_content() {
        return _content;
    }

    public void set_content(String _content) {
        this._content = _content;
    }
}
