package love.flicli.controller;
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
import static android.support.v4.content.FileProvider.getUriForFile;
import static love.flicli.FlickerAPI.makeRequest;


public class FlickerService extends ExecutorIntentService {
    private final static String TAG = FlickerService.class.getName();

    private final static String ACTION_FLICKER = "searchFlick";
    private final static String ACTION_RECENT = "getRecentFlick";
    private final static String ACTION_POPULAR = "getPopularFlick";
    private final static String ACTION_DETAIL = "getDetailFlick";
    private final static String ACTION_AUTHOR = "getFlickByAuthor";

    private final static String PARAM = "param";

    public FlickerService() {
        super("FlickerService");
    }

    @Override
    protected ExecutorService mkExecutorService() {
        return Executors.newFixedThreadPool(10);
    }

    @UiThread
    static void searchFlick(Context context, String param) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_FLICKER);
        intent.putExtra(PARAM, param);

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
        intent.putExtra(PARAM, pos);

        context.startService(intent);
    }

    static void getImageDetailFLick(Context context, int pos) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(PARAM, pos);
        context.startService(intent);

    }

    @UiThread
    static void getFlickByAuthor(Context context, String author) {
        Intent intent = new Intent(context, FlickerService.class);
        intent.setAction(ACTION_AUTHOR);
        intent.putExtra(PARAM, author);

        context.startService(intent);
    }

    @WorkerThread
    protected void onHandleIntent(Intent intent) {
        String param;

        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        JSONArray jPhoto;

        try {
            switch (intent.getAction()) {
                case ACTION_FLICKER:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    param = (String) intent.getSerializableExtra(PARAM);

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

                    int pos = (int) intent.getSerializableExtra(PARAM);
                    FlickModel flick = mvc.model.getFlickers().get(pos);

                    JSONObject jComment = makeRequest(FlickerAPI.photos_getComments(flick.getId())).getJSONObject("comments");
                    JSONArray jFavourities = makeRequest(FlickerAPI.photos_getFav(flick.getId())).getJSONObject("photo").getJSONArray("person");

                    _generateFlickDetail(flick, jComment, jFavourities);

                    break;

                case ACTION_AUTHOR:
                    String author = (String) intent.getSerializableExtra(PARAM);

                    jPhoto = makeRequest(FlickerAPI.photo_getAuthor(author)).getJSONObject("photos").getJSONArray("photo");
                    JSONObject jAuthor = makeRequest(FlickerAPI.people_getInfo(author)).getJSONObject("person");

                    _generateAuthor(jAuthor, jPhoto);

                    break;

                case ACTION_SEND:
                    pos = (int) intent.getSerializableExtra(PARAM);
                    flick = mvc.model.getFlickers().get(pos);

                    _shareImage(flick);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    private void _shareImage(FlickModel flickModel) throws IOException {

        File tempFile;
        Bitmap z;
        //altrimenti non riesco a svuotare la memoria delle immagini che scarico
        if (flickModel.getBitmap_url_hd() != null)
            tempFile = Util.getImageUri(getApplicationContext(), flickModel.getBitmap_url_hd());
        else {
            z = BitmapFactory.decodeStream(new URL(flickModel.getUrl_h()).openStream());
            tempFile = Util.getImageUri(getApplicationContext(), z);
        }

        Uri r = getUriForFile(getApplicationContext(), "love.flicli.fileprovider", tempFile);

        Intent shareAction = new Intent();
        shareAction.setAction(Intent.ACTION_SEND);
        shareAction.putExtra(Intent.EXTRA_STREAM, r);
        shareAction.setType("image/jpeg");
        shareAction.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(shareAction.createChooser(shareAction, "..."));

    }

    private void _generateFlickers(JSONArray elements) throws JSONException, IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        for (int i = 0; i < elements.length(); i++) {
            JSONObject photo = elements.getJSONObject(i);

            try {
                FlickModel flick = new FlickModel(
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

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void _generateFlickDetail(FlickModel flick, JSONObject jComment, JSONArray jFavourities) throws IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

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

        Bitmap bitmap_z = null;

        try {
            bitmap_z = BitmapFactory.decodeStream((new URL(flick.getUrl_h())).openStream());
        } catch (FileNotFoundException e) {
            //bitmap_z = BitmapFactory.decodeStream((new URL(flick.getUrl_z())).openStream());

            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        mvc.model.storeDetail(flick.getId(), jFavourities.length(), comments, bitmap_z);
    }

    private void _generateAuthor(JSONObject jAuthor, JSONArray elements) {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

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
                    ),
                    BitmapFactory.decodeStream(new URL(FlickerAPI.buddyIcon(
                            _getLabelString(jAuthor, "iconfarm"),
                            _getLabelString(jAuthor, "iconserver"),
                            _getLabelString(jAuthor, "nsid")
                    )).openStream())
            );

            for (int i = 0; i < elements.length(); i++) {
                JSONObject photo = elements.getJSONObject(i);

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

                    author.setFlickers(flick);
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }

                mvc.model.setAuthorModel(author);
            }

        } catch(JSONException e){
            Log.d(TAG, e.getMessage());
            e.printStackTrace();

        } catch(IOException e){
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private String _getLabelString(JSONObject json, String label) throws JSONException {
        if (json == null || label == "") {
            return "";
        }

        return (json.isNull(label)) ? "" : json.getString(label);
    }

    private int _getLabelInt(JSONObject json, String label)  throws JSONException {
        if (json == null || label == "") {
            return 0;
        }

        return (json.isNull(label)) ? 0 : json.getInt(label);

    }

    private JSONObject _getJSONObject(JSONObject json, String label)  throws JSONException {
        return json.isNull(label) ? null : json.getJSONObject(label);
    }
}
