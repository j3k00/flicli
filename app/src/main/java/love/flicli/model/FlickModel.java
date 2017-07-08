package love.flicli.model;

import android.graphics.Bitmap;

import net.jcip.annotations.Immutable;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Objects;

import static android.R.attr.mode;
import static android.R.attr.name;
import static android.R.attr.thickness;
import static android.R.attr.value;

/**
 * Created by tommaso on 19/05/17.
 */

/*
 * This class represent every single photo.
 * When we make a request and store the results, we managed the photo in this format.
 */

@Immutable
public class FlickModel {
    // Default attribues
    private String id;
    private String owner;
    private String secret;
    private String server;
    private String title;
    private int farm;
    private int ispublic;
    private int isfriend;
    private int isfamily;

    // Optional attributes
    private String description;
    private String license;
    private String date_upload;
    private String date_taken;
    private String owner_name;
    private String icon_server;
    private String original_format;
    private String last_update;
    private String geo;
    private String tags;
    private String machine_tags;
    private String o_dims;
    private String views;
    private String media;
    private String path_alias;
    private String url_sq;
    private String url_t;
    private String url_s;
    private String url_q;
    private String url_m;
    private String url_n;
    private String url_z;
    private String url_c;
    private String url_l;
    private String url_o;
    private Bitmap bitmap_url_s;
    private Bitmap bitmap_url_hd;
    private String favourities;
    private ArrayList<Comment> comments;

    public FlickModel(String id){
        this.id = id;
        owner = "";
        secret = "";
        server = "";
        title = "";
        description = "";
        license = "";
        date_upload = "";
        date_taken = "";
        owner_name = "";
        icon_server = "";
        original_format = "";
        last_update = "";
        geo = "";
        tags = "";
        machine_tags = "";
        o_dims = "";
        views = "";
        media = "";
        path_alias = "";
        url_sq = "";
        url_t = "";
        url_s = "";
        url_q = "";
        url_m = "";
        url_n = "";
        url_z = "";
        url_c = "";
        url_l = "";
        url_o = "";
        farm = 0;
        ispublic = 0;
        isfriend = 0;
        isfamily = 0;
        bitmap_url_s = null;
        bitmap_url_hd = null;
        comments = new ArrayList<Comment>();
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = _setAttribute(owner);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = _setAttribute(secret);
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = _setAttribute(server);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = _setAttribute(title);
    }

    public int getFarm() {
        return farm;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public int getIspublic() {
        return ispublic;
    }

    public void setIspublic(int ispublic) {
        this.ispublic = ispublic;
    }

    public int getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(int isfriend) {
        this.isfriend = isfriend;
    }

    public int getIsfamily() {
        return isfamily;
    }

    public void setIsfamily(int isfamily) {
        this.isfamily = isfamily;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = _setAttribute(description);
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = _setAttribute(license);
    }

    public String getDate_upload() {
        return date_upload;
    }

    public void setDate_upload(String date_upload) {
        this.date_upload = _setAttribute(date_upload);
    }

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = _setAttribute(date_taken);
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = _setAttribute(owner_name);
    }

    public String getIcon_server() {
        return icon_server;
    }

    public void setIcon_server(String icon_server) {
        this.icon_server = _setAttribute(icon_server);
    }

    public String getOriginal_format() {
        return original_format;
    }

    public void setOriginal_format(String original_format) {
        this.original_format = _setAttribute(original_format);
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = _setAttribute(last_update);
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = _setAttribute(geo);
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = _setAttribute(tags);
    }

    public String getMachine_tags() {
        return machine_tags;
    }

    public void setMachine_tags(String machine_tags) {
        this.machine_tags = _setAttribute(machine_tags);
    }

    public String getO_dims() {
        return o_dims;
    }

    public void setO_dims(String o_dims) {
        this.o_dims = _setAttribute(o_dims);
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = _setAttribute(views);
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = _setAttribute(media);
    }

    public String getPath_alias() {
        return path_alias;
    }

    public void setPath_alias(String path_alias) {
        this.path_alias = _setAttribute(path_alias);
    }

    public String getUrl_sq() {
        return url_sq;
    }

    public void setUrl_sq(String url_sq) {
        this.url_sq = _setAttribute(url_sq);
    }

    public String getUrl_t() {
        return url_t;
    }

    public void setUrl_t(String url_t) {
        this.url_t = _setAttribute(url_t);
    }

    public String getUrl_s() {
        return url_s;
    }

    public void setUrl_s(String url_s) {
        this.url_s = _setAttribute(url_s);
    }

    public String getUrl_q() {
        return url_q;
    }

    public void setUrl_q(String url_q) {
        this.url_q = _setAttribute(url_q);
    }

    public String getUrl_m() {
        return url_m;
    }

    public void setUrl_m(String url_m) {
        this.url_m = _setAttribute(url_m);
    }

    public String getUrl_n() {
        return url_n;
    }

    public void setUrl_n(String url_n) {
        this.url_n = _setAttribute(url_n);
    }

    public String getUrl_z() {
        return url_z;
    }

    public void setUrl_z(String url_z) {
        this.url_z = _setAttribute(url_z);
    }

    public String getUrl_c() {
        return url_c;
    }

    public void setUrl_c(String url_c) {
        this.url_c = _setAttribute(url_c);
    }

    public String getUrl_l() {
        return url_l;
    }

    public void setUrl_l(String url_l) {
        this.url_l = _setAttribute(url_l);
    }

    public String getUrl_o() {
        return url_o;
    }

    public void setUrl_o(String url_o) {
        this.url_o = _setAttribute(url_o);
    }

    private String _setAttribute(String param) {
        return (param != null) ? param : "";
    }

    public void setBitmap_url_s(Bitmap bitmap_url_s) { this.bitmap_url_s = bitmap_url_s; }

    public Bitmap getBitmap_url_s() {
        return this.bitmap_url_s;
    }

    public void setFavourities(String fav) { this.favourities = fav; }

    public String getFavourities() { return this.favourities; }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComments(Comment comment) { this.comments.add(comment); }

    public ArrayList<Comment> getComments() {
        if (this.comments.isEmpty()) {
            return Comment.emptyComment();
        } else {
            return this.comments;
        }
    }

    public void setBitmap_url_hd(Bitmap image) { this.bitmap_url_hd = image; }

    public  Bitmap getBitmap_url_hd() { return this.bitmap_url_hd;}


    public void reflectJson(String name, JSONObject obj, String variable) throws NoSuchFieldException, IllegalAccessException, JSONException {
        Field field = this.getClass().getDeclaredField(variable);
        field.set(this, (obj.isNull(name)) ? "" : obj.getString(name));
    }
}