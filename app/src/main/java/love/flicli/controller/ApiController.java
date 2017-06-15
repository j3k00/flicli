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

    /*public String getThumb(String id) {
        String SIZE = "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=5cf3287df65ea5208bf0b2aade1f929d&photo_id=" + id + "&format=json&nojsoncallback=1";

        try {
            URL url = new URL(SIZE);
            URLConnection conn = url.openConnection();
            String answer = "";

            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    Log.d(TAG, "starting search of" + line);
                    answer += line + "\n";
                }
            }
            finally {
                if (in != null)
                    in.close();
            }

            JSONObject jsonObj = new JSONObject(answer);
            JSONObject sizes = jsonObj.getJSONObject("sizes");
            JSONArray jobject = sizes.getJSONArray("size");

            for (int i = 0; i < jobject.length(); i++) {
                JSONObject j = jobject.getJSONObject(i);
                if (j.getString("label").compareTo("Square") == 0) {
                    return j.getString("source");
                }
            }

        } catch (IOException e) {
            Log.d(TAG, "I/O Error");
        } catch (JSONException e) {
            Log.d(TAG, "JSNOError");
        }
        return "";
    }


    public Flick[] author(String author) {
        String SERVER = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ea7e30b03f88bdde3c16dd1f33f91049&user_id=" + author + "&text=-&extras=url_z%2Cdescription%2Ctags%2Cowner_name&per_page=50&format=json&nojsoncallback=1";
        LinkedList<Flick> result = new LinkedList<Flick>();

        try {
            URL url = new URL(SERVER);
            URLConnection conn = url.openConnection();
            String answer = "";

            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    Log.d(TAG, "starting search of" + line);
                    answer += line + "\n";
                }
            }
            finally {
                if (in != null)
                    in.close();
            }

            //Creazione array delle photo
            JSONObject jsonObj = new JSONObject(answer);
            JSONObject photos = jsonObj.getJSONObject("photos");
            JSONArray jPhoto = photos.getJSONArray("photo");


            for (int i = 0; i < jPhoto.length(); i++) {
                Log.d(TAG, String.valueOf(i));
                JSONObject photo = jPhoto.getJSONObject(i);
                String id = (photo.isNull("id")) ? "" : photo.getString("id");
                String description = (photo.isNull("description")) ? "" :photo.getString("description");
                String image = (photo.isNull("url_z")) ? "" : photo.getString("url_z");
                String title = (photo.isNull("title")) ? "" : photo.getString("title");
                String autho = (photo.isNull("ownername")) ? "" : photo.getString("ownername");
                String image_square = image.replace("_z", "_s");//getThumb(id);
                String user_id = (photo.isNull("owner")) ? "" : photo.getString("owner");

                Flick f = new Flick(image, description, id, autho, title, image_square, user_id);
                result.add(f);
            }
        }
        catch (IOException e) {
            Log.d(TAG, "I/O error", e);
            return null;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        return result.toArray(new Flick[result.size()]);
    }*/
}