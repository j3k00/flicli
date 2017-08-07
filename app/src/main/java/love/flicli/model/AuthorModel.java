package love.flicli.model;

import android.graphics.Bitmap;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;

@ThreadSafe
public class AuthorModel {
    private final String id;
    private final String nsid;
    private final String ispro;
    private final String can_buy_pro;
    private final String iconserver;
    private final String iconfarm;
    private final String path_alias;
    private final String has_stats;

    private final String username;
    private final String realname;
    private final String location;
    private final String description;
    private final String photosurl;
    private final String profileurl;
    private final String mobileurl;
    private final String photos_firstdatetaken;
    private final String photos_firstdate;
    private final String photos_count;

    private final String buddyIcon;
    private final Bitmap buddyIconBitmap;


    @GuardedBy("Itself")
    private final ArrayList<FlickModel> flickers = new ArrayList<>();

    public AuthorModel(String id, String nsid, String ispro, String can_buy_pro, String iconserver, String iconfarm, String path_alias, String has_stats, String username, String realname, String location, String description, String photosurl, String profileurl, String mobileurl, String photos_firstdatetaken, String photos_firstdate, String photos_count, String buddyIcon, Bitmap image) {
        this.id = id;
        this.nsid = nsid;
        this.ispro = ispro;
        this.can_buy_pro = can_buy_pro;
        this.iconserver = iconserver;
        this.iconfarm = iconfarm;
        this.path_alias = path_alias;
        this.has_stats = has_stats;
        this.username = username;
        this.realname = realname;
        this.location = location;
        this.description = description;
        this.photosurl = photosurl;
        this.profileurl = profileurl;
        this.mobileurl = mobileurl;
        this.photos_firstdatetaken = photos_firstdatetaken;
        this.photos_firstdate = photos_firstdate;
        this.photos_count = photos_count;
        this.buddyIcon = buddyIcon;
        this.buddyIconBitmap = image;
    }

    public String getId() {
        return id;
    }

    public String getNsid() {
        return nsid;
    }

    public String getIspro() {
        return ispro;
    }

    public String getCan_buy_pro() {
        return can_buy_pro;
    }

    public String getIconserver() {
        return iconserver;
    }

    public String getIconfarm() {
        return iconfarm;
    }

    public String getPath_alias() {
        return path_alias;
    }

    public String getHas_stats() {
        return has_stats;
    }

    public String getUsername() {
        return username;
    }

    public String getRealname() {
        return realname;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotosurl() {
        return photosurl;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public String getMobileurl() {
        return mobileurl;
    }

    public String getPhotos_firstdatetaken() {
        return photos_firstdatetaken;
    }

    public String getPhotos_firstdate() {
        return photos_firstdate;
    }

    public String getPhotos_count() {
        return photos_count;
    }

    public String getBuddyIcon () {
        return buddyIcon;
    }

    public String getName() {
        if (getRealname() == "")
            return getUsername();

        return getRealname();
    }

    public synchronized ArrayList<FlickModel> getFlickers() {
        return this.flickers;
    }

    public synchronized void setFlickers(FlickModel flickers) {
        this.flickers.add(flickers);
    }

    public synchronized Bitmap getBuddyIconBitmap() {
        return this.buddyIconBitmap;
    }

}
