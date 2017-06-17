package love.flicli.controller;
import android.app.IntentService;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import love.flicli.FlickerAPI;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.model.Comment;
import love.flicli.model.FlickModel;

import static love.flicli.R.id.comments;

/**
 * Created by tommaso on 09/05/17.
 */

public class ApiController extends IntentService {
    private final static String TAG = ApiController.class.getName();
    private final static String ACTION_FLICKER = "searchFlick";
    private final static String ACTION_RECENT = "getRecentFlick";
    private final static String ACTION_POPULAR = "getPopularFlick";
    private final static String ACTION_COMMENT = "getCommentFlick";
    private final static String ACTION_AUTHOR = "getFlickByAuthor";
    private final static String ACTION_FAVOURITE = "getFavourities";

    private final static String PARAM_SEARCHABLE = "param";
    private final static String PARAM_PHOTOID = "photo_id";
    private static String search = "";

    public ApiController() {
        super("ApiController");
    }

    @WorkerThread
    private JSONObject makeRequest(String endpoint) {
        String response = "";
        String line = "";
        BufferedReader in = null;
        JSONObject json = null;

        try {
            URL url = new URL(endpoint);
            URLConnection conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = in.readLine()) != null) {
                Log.d(TAG, "STARTING SEARCH OF" + line);
                response += line + "\n";
            }

            in.close();

            json = new JSONObject(response);

        } catch (IOException e) {
            Log.d(TAG, "I/O error", e);
            return null;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        return json;
    }

    @UiThread
    static void searchFlick(Context context, String param) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_FLICKER);
        intent.putExtra(PARAM_SEARCHABLE, param);
        context.startService(intent);
    }

    @UiThread
    static void getRecentFlick(Context context) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_RECENT);
        context.startService(intent);
    }

   @UiThread
    static void getPopularFlick(Context context) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_POPULAR);
        context.startService(intent);
    }

   @UiThread
    static void getCommentFlick(Context context, String photo_id) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_COMMENT);
        intent.putExtra(PARAM_PHOTOID, photo_id);
        context.startService(intent);
    }

    @UiThread
    static void getFavourities(Context context, String photo_id) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_FAVOURITE);
        intent.putExtra(PARAM_PHOTOID, photo_id);
        context.startService(intent);
    }

   /*   @UiThread
    static void getFlickByAuthor(Context context, String author) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_AUTHOR);
        search = author;
        intent.putExtra(PARAM_SEARCHABLE, search);
        context.startService(intent);
    }*/

    @WorkerThread
    protected void onHandleIntent(Intent intent) {

        FlickerAPI flickerAPI = ((FlicliApplication) getApplication()).getFlickerAPI();

        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        JSONArray jPhoto = null;
        JSONArray jComment = null;

        try {
            switch (intent.getAction()) {
                case ACTION_FLICKER:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    String param = (String) intent.getSerializableExtra(PARAM_SEARCHABLE);

                    jPhoto = makeRequest(flickerAPI.photos_search(param)).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);

                    break;

                case ACTION_RECENT:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    jPhoto = makeRequest(flickerAPI.photos_getRecent()).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);

                    break;

                case ACTION_POPULAR:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    jPhoto = makeRequest(flickerAPI.photos_getPopular()).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);

                    break;

                case ACTION_COMMENT:
                    String photo_id = (String) intent.getSerializableExtra(PARAM_PHOTOID);

                    jComment = makeRequest(flickerAPI.photos_getComments(photo_id)).getJSONObject("comments").getJSONArray("comment");
                    _generateComments(jComment, photo_id);

                    break;

                case ACTION_FAVOURITE:
                    photo_id = (String) intent.getSerializableExtra(PARAM_PHOTOID);

                    jComment = makeRequest(flickerAPI.photo_getFav(photo_id)).getJSONObject("photo").getJSONArray("person");
                    _setFavourities(jComment, photo_id);

                    break;

/*                case ACTION_AUTHOR:
                    String author = (String) intent.getSerializableExtra(PARAM_SEARCHABLE);
                    result = author(author);
                    mvc.model.storeFactorization(result);
*/
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private  void _generateFlickers(JSONArray elements) throws JSONException, IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        for (int i = 0; i < elements.length(); i++) {
            JSONObject photo = elements.getJSONObject(i);

            FlickModel flick = new FlickModel(photo.getString("id"));
            flick.setDescription(photo.getString("description"));
            flick.setUrl_z(photo.getString("url_z"));
            flick.setTitle(photo.getString("title"));
            flick.setOwner(photo.getString("owner"));
            flick.setOwner_name(photo.getString("ownername"));
            flick.setUrl_sq(photo.getString("url_sq"));
            flick.setUrl_m(photo.getString("url_s"));
            flick.setViews(photo.getString("views"));

            try {
                flick.setBitmap_url_s(BitmapFactory.decodeStream(new URL(flick.getUrl_sq()).openStream()));
            } catch (IOException e) {
                e.printStackTrace();
                flick.setBitmap_url_s(null);
            }

            mvc.model.storeFactorization(flick);
        }
    }

    private void _generateComments(JSONArray elements, String photo_id) throws JSONException, IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        ArrayList<Comment> comments = new ArrayList<Comment>();

        for (int i = 0; i < elements.length(); i++) {
            JSONObject photo = elements.getJSONObject(i);

            Comment comment = new Comment(photo.getString("id"));
            comment.setAuthor(photo.getString("author"));
            comment.setAuthor_is_deleted(photo.getString("author_is_deleted"));
            comment.setAuthorname(photo.getString("authorname"));
            comment.setIconserver(photo.getString("iconserver"));
            comment.setIconfarm(photo.getString("iconfarm"));
            comment.setDatecreate(photo.getString("datecreate"));
            comment.setPermalink(photo.getString("permalink"));
            comment.setPath_alias(photo.getString("path_alias"));
            comment.setRealname(photo.getString("realname"));
            comment.set_content(photo.getString("_content"));

            comments.add(comment);
        }

        mvc.model.storeComments(comments, photo_id);
    }

    private void _setFavourities(JSONArray elements, String photo_id) throws JSONException, IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        mvc.model.setFavourities(photo_id, elements.length());

    }
}