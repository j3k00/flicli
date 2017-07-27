package love.flicli.model;

import net.jcip.annotations.Immutable;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by jeko on 15/06/17.
 */

@Immutable
public class CommentModel {
    // Default attribues
    private final String id;
    private final String author;
    private final String author_is_deleted;
    private final String authorname;
    private final String iconserver;
    private final String iconfarm;
    private final String datecreate;
    private final String permalink;
    private final String path_alias;
    private final String realname;
    private final String _content;

    public CommentModel(
        String id,
        String author,
        String author_is_deleted,
        String authorname,
        String iconserver,
        String iconfarm,
        String datecreate,
        String permalink,
        String path_alias,
        String realname,
        String _content
    ) {
        this.id = id;
        this.author = author;
        this.author_is_deleted = author_is_deleted;
        this.authorname = authorname;
        this.iconserver = iconserver;
        this.iconfarm = iconfarm;
        this.datecreate = datecreate;
        this.permalink = permalink;
        this.path_alias = path_alias;
        this.realname = realname;
        this._content = _content;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthor_is_deleted() {
        return author_is_deleted;
    }

    public String getAuthorname() {
        return authorname;
    }

    public String getIconserver() {
        return iconserver;
    }

    public String getIconfarm() {
        return iconfarm;
    }

    public String getDatecreate() {
        return datecreate;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getPath_alias() {
        return path_alias;
    }

    public String getRealname() {
        return realname;
    }

    public String get_content() {
        return _content;
    }

}
