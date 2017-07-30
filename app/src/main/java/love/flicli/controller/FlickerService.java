package love.flicli.controller;
import android.app.IntentService;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import love.flicli.ExecutorIntentService;
import love.flicli.FlickerAPI;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.Util;
import love.flicli.model.AuthorModel;
import love.flicli.model.CommentModel;
import love.flicli.model.FlickModel;

import static android.content.Intent.ACTION_SEND;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.os.Build.VERSION_CODES.M;
import static android.support.v4.content.FileProvider.getUriForFile;
import static love.flicli.FlickerAPI.makeRequest;
import static love.flicli.FlickerAPI.people_getInfo;
import static love.flicli.R.id.icon;
import static love.flicli.R.id.url;
import static love.flicli.R.id.views;

/**
 * Created by tommaso on 09/05/17.
 */

public class FlickerService extends ExecutorIntentService {
    private final static String TAG = FlickerService.class.getName();
    private final static String ACTION_FLICKER = "searchFlick";
    private final static String ACTION_RECENT = "getRecentFlick";
    private final static String ACTION_POPULAR = "getPopularFlick";
    private final static String ACTION_DETAIL = "getDetailFlick";
    private final static String ACTION_AUTHOR = "getFlickByAuthor";

    private final static String PARAM_ID = "paramId";
    private static String search = "";

    public FlickerService() {
        super("FlickerService");
    }

    @Override
    protected ExecutorService mkExecutorService() {
        return Executors.newFixedThreadPool(10);
        //return Executors.newSingleThreadExecutor();
    }

    @UiThread
    static void searchFlick(Context context, String param) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_FLICKER);
        intent.putExtra(PARAM_ID, param);

        context.startService(intent);
    }

    @UiThread
    static void getRecentFlick(Context context) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_RECENT);
        context.startService(intent);
    }

    static void getPopularFlick(Context context) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_POPULAR);
        context.startService(intent);
    }


    @UiThread
    static void getDetailFlick(Context context, int pos) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_DETAIL);
        intent.putExtra(PARAM_ID, pos);

        context.startService(intent);
    }

    static void getImageDetailFLick(Context context, int pos) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(PARAM_ID, pos);
        context.startService(intent);

    }

    @UiThread
    static void getFlickByAuthor(Context context, String author) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_AUTHOR);
        intent.putExtra(PARAM_ID, author);

        context.startService(intent);
    }

    @WorkerThread
    protected void onHandleIntent(Intent intent) {
        String param = "";

        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        JSONArray jPhoto = null;

        try {
            switch (intent.getAction()) {
                case ACTION_FLICKER:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    param = (String) intent.getSerializableExtra(PARAM_ID);

                    jPhoto = makeRequest(FlickerAPI.photos_search(param)).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);

                    break;

                case ACTION_RECENT:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    jPhoto = makeRequest(FlickerAPI.photos_getRecent()).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);
                    break;

                case ACTION_POPULAR:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    jPhoto = makeRequest(FlickerAPI.photos_getPopular()).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);

                    break;

                case ACTION_DETAIL:

                    int pos = (int) intent.getSerializableExtra(PARAM_ID);
                    FlickModel flick = mvc.model.getFlickers().get(pos);

                    JSONObject jComment = makeRequest(FlickerAPI.photos_getComments(flick.getId())).getJSONObject("comments");
                    JSONArray jFavourities = makeRequest(FlickerAPI.photos_getFav(flick.getId())).getJSONObject("photo").getJSONArray("person");

                    _generateFlickDetail(flick, jComment, jFavourities);

                    break;

                case ACTION_AUTHOR:
                    String author = (String) intent.getSerializableExtra(PARAM_ID);

                    jPhoto = makeRequest(FlickerAPI.photo_getAuthor(author)).getJSONObject("photos").getJSONArray("photo");
                    JSONObject jAuthor = makeRequest(FlickerAPI.people_getInfo(author)).getJSONObject("person");

                    _generateAuthor(jAuthor, jPhoto);

                    break;

                case ACTION_SEND:
                    pos = (int) intent.getSerializableExtra(PARAM_ID);
                    flick = mvc.model.getFlickers().get(pos);

                    File tempFile = Util.getImageUri(getApplicationContext(), flick.getBitmap_url_hd());
                    Uri r = getUriForFile(getApplicationContext(), "love.flicli.fileprovider", tempFile);

                    Intent shareAction = new Intent();
                    shareAction.setAction(Intent.ACTION_SEND);
                    shareAction.putExtra(Intent.EXTRA_STREAM, r);
                    shareAction.setType("image/jpeg");
                    shareAction.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(shareAction.createChooser(shareAction, "..."));

            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void _generateFlickers(JSONArray elements) throws JSONException, IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        for (int i = 0; i < elements.length(); i++) {
            JSONObject photo = elements.getJSONObject(i);
            Iterator<String> keys = photo.keys();
            FlickModel flick;
            try {

                flick = new FlickModel(
                        _getLabelString(photo, "id"),
                        _getLabelString(photo, "owner"),
                        _getLabelString(photo, "secret"),
                        _getLabelString(photo, "server"),
                        _getLabelString(photo, "title"),
                        _getLabelString(photo, "description"),
                        _getLabelString(photo, "license"),
                        _getLabelString(photo, "date_upload"),
                        _getLabelString(photo, "date_taken"),
                        _getLabelString(photo, "owner_name"),
                        _getLabelString(photo, "icon_server"),
                        _getLabelString(photo, "original_format"),
                        _getLabelString(photo, "last_update"),
                        _getLabelString(photo, "geo"),
                        _getLabelString(photo, "tags"),
                        _getLabelString(photo, "machine_tags"),
                        _getLabelString(photo, "o_dims"),
                        _getLabelString(photo, "views"),
                        _getLabelString(photo, "media"),
                        _getLabelString(photo, "path_alias"),
                        _getLabelString(photo, "url_sq"),
                        _getLabelString(photo, "url_t"),
                        _getLabelString(photo, "url_s"),
                        _getLabelString(photo, "url_q"),
                        _getLabelString(photo, "url_m"),
                        _getLabelString(photo, "url_n"),
                        _getLabelString(photo, "url_z"),
                        _getLabelString(photo, "url_c"),
                        _getLabelString(photo, "url_l"),
                        _getLabelString(photo, "url_o"),
                        _getLabelInt(photo, "farm"),
                        _getLabelInt(photo, "ispublic"),
                        _getLabelInt(photo, "isfriend"),
                        _getLabelInt(photo, "isfamily")
                );

                flick.setBitmap_url_s(BitmapFactory.decodeStream(new URL(flick.getUrl_sq()).openStream()));
                mvc.model.storeFlick(flick);

            } catch (Exception e) {}
        }
    }

    private void _generateFlickDetail(FlickModel flick, JSONObject jComment, JSONArray jFavourities) throws IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        // Comments
        ArrayList<CommentModel> comments = new ArrayList<CommentModel>();

        try {
            JSONArray commentArray = jComment.getJSONArray("comment");

            for (int i = 0; i < commentArray.length(); i++) {
                JSONObject jsonComments = commentArray.getJSONObject(i);

                CommentModel comment = new CommentModel(
                        _getLabelString(jsonComments, "id"),
                        _getLabelString(jsonComments, "author"),
                        _getLabelString(jsonComments, "author_is_deleted"),
                        _getLabelString(jsonComments, "authorname"),
                        _getLabelString(jsonComments, "iconserver"),
                        _getLabelString(jsonComments, "iconfarm"),
                        _getLabelString(jsonComments, "datecreate"),
                        _getLabelString(jsonComments, "permalink"),
                        _getLabelString(jsonComments, "pathalias"),
                        _getLabelString(jsonComments, "realname"),
                        _getLabelString(jsonComments, "_content")
                );

                comments.add(comment);
            }
        } catch (Exception e) {
        }

        // Photos
        String image = flick.getUrl_z().replace("_z", "_h");
        Bitmap bitmap_z;

        //non riesco a capire perchè l'immagine in hd non c'è
        try {
            bitmap_z = BitmapFactory.decodeStream((new URL(image)).openStream());
        } catch (FileNotFoundException e) {
            e.getMessage();
            bitmap_z = BitmapFactory.decodeStream((new URL(flick.getUrl_z())).openStream());
        }

        mvc.model.storeDetail(flick.getId(), jFavourities.length(), comments, bitmap_z);
    }

    private void _generateAuthor(JSONObject jAuthor, JSONArray elements) {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();
        //ArrayList<FlickModel> flickers = new ArrayList<FlickModel>();

        try {
            JSONObject jAuthor_photos = jAuthor.getJSONObject("photos");

            AuthorModel author = new AuthorModel(
                    _getLabelString(jAuthor, "id"),
                    _getLabelString(jAuthor, "nsid"),
                    _getLabelString(jAuthor, "ispro"),
                    _getLabelString(jAuthor, "can_buy_pro"),
                    _getLabelString(jAuthor, "iconserver"),
                    _getLabelString(jAuthor, "iconfarm"),
                    _getLabelString(jAuthor, "path_alias"),
                    _getLabelString(jAuthor, "has_stats"),
                    _getLabelString(_getJSONObject(jAuthor, "username"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor, "realname"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor, "location"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor, "description"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor, "photosurl"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor, "profileurl"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor, "mobileurl"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor_photos, "firstdatetaken"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor_photos, "firstdate"), "_content"),
                    _getLabelString(_getJSONObject(jAuthor_photos, "count"), "_content"),
                    FlickerAPI.buddyIcon(
                            _getLabelString(jAuthor, "iconfarm"),
                            _getLabelString(jAuthor, "iconserver"),
                            _getLabelString(jAuthor, "nsid")
                    )
            );

            for (int i = 0; i < elements.length(); i++) {
                JSONObject photo = elements.getJSONObject(i);
                Iterator<String> keys = photo.keys();

                try {
                    FlickModel flick;
                    // Generate new object based on first param, that is id of flickr
                    flick = new FlickModel(
                            _getLabelString(photo, "id"),
                            _getLabelString(photo, "owner"),
                            _getLabelString(photo, "secret"),
                            _getLabelString(photo, "server"),
                            _getLabelString(photo, "title"),
                            _getLabelString(photo, "description"),
                            _getLabelString(photo, "license"),
                            _getLabelString(photo, "date_upload"),
                            _getLabelString(photo, "date_taken"),
                            _getLabelString(photo, "owner_name"),
                            _getLabelString(photo, "icon_server"),
                            _getLabelString(photo, "original_format"),
                            _getLabelString(photo, "last_update"),
                            _getLabelString(photo, "geo"),
                            _getLabelString(photo, "tags"),
                            _getLabelString(photo, "machine_tags"),
                            _getLabelString(photo, "o_dims"),
                            _getLabelString(photo, "views"),
                            _getLabelString(photo, "media"),
                            _getLabelString(photo, "path_alias"),
                            _getLabelString(photo, "url_sq"),
                            _getLabelString(photo, "url_t"),
                            _getLabelString(photo, "url_s"),
                            _getLabelString(photo, "url_q"),
                            _getLabelString(photo, "url_m"),
                            _getLabelString(photo, "url_n"),
                            _getLabelString(photo, "url_z"),
                            _getLabelString(photo, "url_c"),
                            _getLabelString(photo, "url_l"),
                            _getLabelString(photo, "url_o"),
                            _getLabelInt(photo, "farm"),
                            _getLabelInt(photo, "ispublic"),
                            _getLabelInt(photo, "isfriend"),
                            _getLabelInt(photo, "isfamily")
                    );


                    flick.setBitmap_url_s(BitmapFactory.decodeStream(new URL(flick.getUrl_sq()).openStream()));
                    //flickers.add(flick);

                    author.setFlickers(flick);
                } catch (JSONException e) {}

                mvc.model.setAuthorModel(author);
            }
        } catch(JSONException e){
                e.printStackTrace();
        } catch(IOException e){
        }
    }

    private String _getLabelString(JSONObject json, String label) throws JSONException{
        if (json == null || label == "") {
            return "";
        }

        return (json.isNull(label)) ? "" : json.getString(label);
    }

    private int _getLabelInt(JSONObject json, String label)  throws JSONException{
        if (json == null || label == "") {
            return 0;
        }

        return (json.isNull(label)) ? 0 : json.getInt(label);

    }

    private JSONObject _getJSONObject(JSONObject json, String label)  throws JSONException{
        return json.isNull(label) ? null : json.getJSONObject(label);
    }
}
