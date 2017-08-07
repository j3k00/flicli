package love.flicli.model;

import android.graphics.Bitmap;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Objects;

import static android.R.attr.icon;
import static android.R.attr.mode;
import static android.R.attr.name;
import static android.R.attr.syncable;
import static android.R.attr.tag;
import static android.R.attr.thickness;
import static android.R.attr.value;


/*
 * This class represent every single photo.
 * When we make a request and store the results, we managed the photo in this format.
 */

@ThreadSafe
public class FlickModel {
    // Default attribues
    private final String id;
    private final String owner;
    private final String secret;
    private final String server;
    private final String title;
    private final int farm;
    private final int ispublic;
    private final int isfriend;
    private final int isfamily;

    // Optional attributes
    private final String description;
    private final String license;
    private final String date_upload;
    private final String date_taken;
    private final String owner_name;
    private final String icon_server;
    private final String original_format;
    private final String last_update;
    private final String geo;
    private final String tags;
    private final String machine_tags;
    private final String o_dims;
    private final String views;
    private final String media;
    private final String path_alias;
    private final String url_sq;
    private final String url_t;
    private final String url_s;
    private final String url_q;
    private final String url_m;
    private final String url_n;
    private final String url_z;
    private final String url_c;
    private final String url_l;
    private final String url_o;

    //sincronizzati

    @GuardedBy("Itself")
    private Bitmap bitmap_url_h;

    @GuardedBy("Itself")
    private ArrayList<CommentModel> comments;

    @GuardedBy("Itself")
    private String favourities;

    @GuardedBy("Itself")
    private Bitmap bitmap_url_s;


    public FlickModel(
        String id,
        String owner,
        String secret,
        String server,
        String title,
        String description,
        String license,
        String date_upload,
        String date_taken,
        String owner_name,
        String icon_server,
        String original_format,
        String last_update,
        String geo,
        String tags,
        String machine_tags,
        String o_dims,
        String views,
        String media,
        String path_alias,
        String url_sq,
        String url_t,
        String url_s,
        String url_q,
        String url_m,
        String url_n,
        String url_z,
        String url_c,
        String url_l,
        String url_o,
        int farm,
        int ispublic,
        int isfriend,
        int isfamily
    ){
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.title = title;
        this.description = description;
        this.license = license;
        this.date_upload = date_upload;
        this.date_taken = date_taken;
        this.owner_name = owner_name;
        this.icon_server = icon_server;
        this.original_format = original_format;
        this.last_update = last_update;
        this.geo = geo;
        this.tags = tags;
        this.machine_tags = machine_tags;
        this.o_dims = o_dims;
        this.views = views;
        this.media = media;
        this.path_alias = path_alias;
        this.url_sq = url_sq;
        this.url_t = url_t;
        this.url_s = url_s;
        this.url_q = url_q;
        this.url_m = url_m;
        this.url_n = url_n;
        this.url_z = url_z;
        this.url_c = url_c;
        this.url_l = url_l;
        this.url_o = url_o;
        this.farm = farm;
        this.ispublic = ispublic;
        this.isfriend = isfriend;
        this.isfamily = isfamily;

        //Detail Image
        this.bitmap_url_s = null;
        this.bitmap_url_h = null;
        this.comments = new ArrayList<CommentModel>();
        this.favourities = "";
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public String getTitle() {
        return title;
    }

    public int getFarm() {
        return farm;
    }

    public int getIspublic() {
        return ispublic;
    }

    public int getIsfriend() {
        return isfriend;
    }

    public int getIsfamily() {
        return isfamily;
    }

    public String getDescription() {
        return description;
    }

    public String getLicense() {
        return license;
    }

    public String getDate_upload() {
        return date_upload;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public String getIcon_server() {
        return icon_server;
    }

    public String getOriginal_format() {
        return original_format;
    }

    public String getLast_update() {
        return last_update;
    }

    public String getGeo() {
        return geo;
    }

    public String getTags() {
        return tags;
    }

    public String getMachine_tags() {
        return machine_tags;
    }

    public String getO_dims() {
        return o_dims;
    }

    public String getViews() {
        return views;
    }

    public String getMedia() {
        return media;
    }

    public String getUrl_q() {
        return url_q;
    }

    public String getUrl_sq() {
        return url_sq;
    }

    public String getUrl_m() {
        return url_m;
    }

    public String getUrl_n() {
        return url_n;
    }

    public String getUrl_z() {
        return url_z;
    }

    public String getUrl_c() {
        return url_c;
    }

    public String getUrl_l() {
        return url_l;
    }

    public String getUrl_o() {
        return url_o;
    }

    public String getUrl_h() {
        return (getUrl_h() == "") ? "" : getUrl_z().replace("_z", "_h");
    }

    public Bitmap getBitmap_url_s() {
        return this.bitmap_url_s;
    }

    public synchronized void freeComment() {
        this.comments = new ArrayList<CommentModel>();
    }

    public synchronized void freeBitMapHD() {
        this.bitmap_url_h = null;
    }

    public synchronized ArrayList<CommentModel> getComments() {
            return this.comments;
    }

    public synchronized void setComments(ArrayList<CommentModel> comments) {
            this.comments = comments;
    }

    public synchronized void setBitmap_url_h(Bitmap image) {
            this.bitmap_url_h = image;
    }

    public synchronized void setBitmap_url_s(Bitmap image) {
       this.bitmap_url_s = image;
    }

    public synchronized Bitmap getBitmap_url_hd() {
            return this.bitmap_url_h;
    }

    public synchronized void setFavourities(String param) {
       this.favourities = param;
    }

    public synchronized String getFavourities() { return this.favourities; }

    private String _setAttribute(String param) {
        return (param != null) ? param : "";
    }

    public void reflectJson(String name, String object) throws NoSuchFieldException, IllegalAccessException, JSONException {
        Field field = this.getClass().getDeclaredField(name);
        field.set(this, object);
    }
}