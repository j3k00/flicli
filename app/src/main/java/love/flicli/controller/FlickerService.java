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
                    (photo.isNull("id")) ? "" : photo.getString("id"),
                    (photo.isNull("owner")) ? "" : photo.getString("owner"),
                    (photo.isNull("secret")) ? "" : photo.getString("secret"),
                    (photo.isNull("server")) ? "" : photo.getString("server"),
                    (photo.isNull("title")) ? "" : photo.getString("title"),
                    (photo.isNull("description")) ? "" : photo.getString("description"),
                    (photo.isNull("license")) ? "" : photo.getString("license"),
                    (photo.isNull("date_upload")) ? "" : photo.getString("date_upload"),
                    (photo.isNull("date_taken")) ? "" : photo.getString("date_taken"),
                    (photo.isNull("owner_name")) ? "" : photo.getString("owener_name"),
                    (photo.isNull("icon_server")) ? "" : photo.getString("icon_server"),
                    (photo.isNull("original_format")) ? "" : photo.getString("original_format"),
                    (photo.isNull("last_update")) ? "" : photo.getString("last_update"),
                    (photo.isNull("geo")) ? "" : photo.getString("geo"),
                    (photo.isNull("tags")) ? "" : photo.getString("tags"),
                    (photo.isNull("machine_tags")) ? "" : photo.getString("machine_tags"),
                    (photo.isNull("o_dims")) ? "" : photo.getString("o_dims"),
                    (photo.isNull("views")) ? "" : photo.getString("views"),
                    (photo.isNull("media")) ? "" : photo.getString("media"),
                    (photo.isNull("path_alias")) ? "" : photo.getString("path_alias"),
                    (photo.isNull("url_sq")) ? "" : photo.getString("url_sq"),
                    (photo.isNull("url_t")) ? "" : photo.getString("url_t"),
                    (photo.isNull("url_s")) ? "" : photo.getString("url_s"),
                    (photo.isNull("url_q")) ? "" : photo.getString("url_q"),
                    (photo.isNull("url_m")) ? "" : photo.getString("url_m"),
                    (photo.isNull("url_n")) ? "" : photo.getString("url_n"),
                    (photo.isNull("url_z")) ? "" : photo.getString("url_z"),
                    (photo.isNull("url_c")) ? "" : photo.getString("url_c"),
                    (photo.isNull("url_l")) ? "" : photo.getString("url_l"),
                    (photo.isNull("url_o")) ? "" : photo.getString("url_o"),
                    (photo.isNull("fam")) ? 0 : photo.getInt("farm"),
                    (photo.isNull("ispublic")) ? 0 : photo.getInt("ispublic"),
                    (photo.isNull("isfriend")) ? 0 : photo.getInt("isfriend"),
                    (photo.isNull("isfamily")) ? 0 : photo.getInt("isfamily"));


                flick.setBitmap_url_s(BitmapFactory.decodeStream(new URL(flick.getUrl_sq()).openStream()));
                mvc.model.storeFlick(flick);

            } catch (Exception e) {


            }
            // Generate new object based on first param, that is id of flickr
            /*
            FlickModel flick = new FlickModel(photo.getString(keys.next()));

            while (keys.hasNext()) {
                String keyValue = keys.next();

                try {
                    flick.reflectJson(keyValue, photo.getString(keyValue));
                } catch (Exception e) {}
            }

            flick.setBitmap_url_s(BitmapFactory.decodeStream(new URL(flick.getUrl_sq()).openStream()));
            */
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
                        (jsonComments.isNull("id")) ? "" : jsonComments.getString("id"),
                        (jsonComments.isNull("author")) ? "" : jsonComments.getString("author"),
                        (jsonComments.isNull("author_is_deleted")) ? "" : jsonComments.getString("author_is_deleted"),
                        (jsonComments.isNull("authorname")) ? "" : jsonComments.getString("authorname"),
                        (jsonComments.isNull("iconserver")) ? "" : jsonComments.getString("iconserver"),
                        (jsonComments.isNull("iconfarm")) ? "" : jsonComments.getString("iconfarm"),
                        (jsonComments.isNull("datecreate")) ? "" : jsonComments.getString("datecreate"),
                        (jsonComments.isNull("permalink")) ? "" : jsonComments.getString("permalink"),
                        (jsonComments.isNull("pathalias")) ? "" : jsonComments.getString("pathalias"),
                        (jsonComments.isNull("realname")) ? "" : jsonComments.getString("realname"),
                        (jsonComments.isNull("_content")) ? "" : jsonComments.getString("_content")
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
            JSONObject jName = jAuthor.getJSONObject("username");
            JSONObject jAuthor_photos = jAuthor.getJSONObject("photos");


            //TODO devi leggere separatamente i sotto JSONObject perchè molti autori non hanno valorizzati tutti i campi, se fallisce questo fallisce
            //tutto
            AuthorModel author = new AuthorModel(
                    (jAuthor.isNull("id")) ? "" : jAuthor.getString("id"),
                    (jAuthor.isNull("nsid")) ? "" : jAuthor.getString("nsid"),
                    (jAuthor.isNull("ispro")) ? "" : jAuthor.getString("ispro"),
                    (jAuthor.isNull("can_buy_pro")) ? "" : jAuthor.getString("can_buy_pro"),
                    (jAuthor.isNull("iconserver")) ? "" : jAuthor.getString("iconserver"),
                    (jAuthor.isNull("iconfarm")) ? "" : jAuthor.getString("iconfarm"),
                    (jAuthor.isNull("path_alias")) ? "" : jAuthor.getString("path_alias"),
                    (jAuthor.isNull("has_stats")) ? "" : jAuthor.getString("has_stats"),
                    jAuthor.getJSONObject("username").isNull("_content") ? "" : jAuthor.getJSONObject("username").getString("_content"),
                    jAuthor.getJSONObject("realname").isNull("_content") ? "" : jAuthor.getJSONObject("realname").getString("_content"),
                    jAuthor.getJSONObject("location").isNull("_content") ? "" : jAuthor.getJSONObject("location").getString("_content"),
                    jAuthor.getJSONObject("description").isNull("_content") ? "" : jAuthor.getJSONObject("description").getString("_content"),
                    jAuthor.getJSONObject("photosurl").isNull("_content") ? "" : jAuthor.getJSONObject("photosurl").getString("_content"),
                    jAuthor.getJSONObject("profileurl").isNull("_content") ? "" : jAuthor.getJSONObject("profileurl").getString("_content"),
                    jAuthor.getJSONObject("mobileurl").isNull("_content") ? "" : jAuthor.getJSONObject("mobileurl").getString("_content"),
                    jAuthor_photos.getJSONObject("firstdatetaken").isNull("_content") ? "" : jAuthor_photos.getJSONObject("firstdatetaken").getString("_content"),
                    jAuthor_photos.getJSONObject("firstdate").isNull("_content") ? "" : jAuthor_photos.getJSONObject("firstdate").getString("_content"),
                    jAuthor_photos.getJSONObject("count").isNull("_content") ? "" : jAuthor_photos.getJSONObject("count").getString("_content"),
                    FlickerAPI.buddyIcon(
                            (jAuthor.isNull("iconfarm")) ? "" : jAuthor.getString("iconfarm"),
                            (jAuthor.isNull("iconserver")) ? "" : jAuthor.getString("iconserver"),
                            (jAuthor.isNull("nsid")) ? "" : jAuthor.getString("nsid")
                    )
            );


            for (int i = 0; i < elements.length(); i++) {
                JSONObject photo = elements.getJSONObject(i);
                Iterator<String> keys = photo.keys();

                try {
                    FlickModel flick;
                    // Generate new object based on first param, that is id of flickr
                    flick = new FlickModel(
                            (photo.isNull("id")) ? "" : photo.getString("id"),
                            (photo.isNull("owner")) ? "" : photo.getString("owner"),
                            (photo.isNull("secret")) ? "" : photo.getString("secret"),
                            (photo.isNull("server")) ? "" : photo.getString("server"),
                            (photo.isNull("title")) ? "" : photo.getString("title"),
                            (photo.isNull("description")) ? "" : photo.getString("description"),
                            (photo.isNull("license")) ? "" : photo.getString("license"),
                            (photo.isNull("date_upload")) ? "" : photo.getString("date_upload"),
                            (photo.isNull("date_taken")) ? "" : photo.getString("date_taken"),
                            (photo.isNull("owner_name")) ? "" : photo.getString("owener_name"),
                            (photo.isNull("icon_server")) ? "" : photo.getString("icon_server"),
                            (photo.isNull("original_format")) ? "" : photo.getString("original_format"),
                            (photo.isNull("last_update")) ? "" : photo.getString("last_update"),
                            (photo.isNull("geo")) ? "" : photo.getString("geo"),
                            (photo.isNull("tags")) ? "" : photo.getString("tags"),
                            (photo.isNull("machine_tags")) ? "" : photo.getString("machine_tags"),
                            (photo.isNull("o_dims")) ? "" : photo.getString("o_dims"),
                            (photo.isNull("views")) ? "" : photo.getString("views"),
                            (photo.isNull("media")) ? "" : photo.getString("media"),
                            (photo.isNull("path_alias")) ? "" : photo.getString("path_alias"),
                            (photo.isNull("url_sq")) ? "" : photo.getString("url_sq"),
                            (photo.isNull("url_t")) ? "" : photo.getString("url_t"),
                            (photo.isNull("url_s")) ? "" : photo.getString("url_s"),
                            (photo.isNull("url_q")) ? "" : photo.getString("url_q"),
                            (photo.isNull("url_m")) ? "" : photo.getString("url_m"),
                            (photo.isNull("url_n")) ? "" : photo.getString("url_n"),
                            (photo.isNull("url_z")) ? "" : photo.getString("url_z"),
                            (photo.isNull("url_c")) ? "" : photo.getString("url_c"),
                            (photo.isNull("url_l")) ? "" : photo.getString("url_l"),
                            (photo.isNull("url_o")) ? "" : photo.getString("url_o"),
                            (photo.isNull("fam")) ? 0 : photo.getInt("farm"),
                            (photo.isNull("ispublic")) ? 0 : photo.getInt("ispublic"),
                            (photo.isNull("isfriend")) ? 0 : photo.getInt("isfriend"),
                            (photo.isNull("isfamily")) ? 0 : photo.getInt("isfamily"));


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
}
