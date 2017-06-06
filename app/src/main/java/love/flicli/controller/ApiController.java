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
import java.util.LinkedList;

import love.flicli.FlickerAPI;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
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
/*
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
    static void getCommentFlick(Context context, String image) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_COMMENT);
        search = image;
        intent.putExtra(PARAM_SEARCHABLE, search);
        context.startService(intent);
    }

    @UiThread
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

        try {
            switch (intent.getAction()) {
                case ACTION_FLICKER:
                    String param = (String) intent.getSerializableExtra(PARAM_SEARCHABLE);

                    jPhoto = makeRequest(flickerAPI.photos_search(param)).getJSONObject("photos").getJSONArray("photo");
                    mvc.model.storeFactorization(_generateFlickers(jPhoto));

                    break;

                case ACTION_RECENT:
                    jPhoto = makeRequest(flickerAPI.photos_getRecent()).getJSONObject("photos").getJSONArray("photo");
                    mvc.model.storeFactorization(_generateFlickers(jPhoto));

                    break;
    /*
                case ACTION_POPULAR:
                    result = Popular();
                    mvc.model.storeFactorization(result);
                    break;

            case ACTION_COMMENT:
                String image = (String) intent.getSerializableExtra(PARAM_SEARCHABLE);
                Comments[] comments = Comment(image);
                mvc.model.storeComments(comments);
                break;

            case ACTION_AUTHOR:
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


    private  LinkedList<FlickModel> _generateFlickers(JSONArray elements) throws JSONException, IOException {
        LinkedList<FlickModel> result = new LinkedList<FlickModel>();

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

            result.add(flick);
        }

        return result;
    }
/*
    @WorkerThread
    private FlickModel[] Recent() {
        String SERVER = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=efaa708098eef9c038ad4c123041733c&extras=url_z%2Cdescription%2Ctags%2Cowner_name&per_page=50&format=json&nojsoncallback=1";
        LinkedList<FlickModel> result = new LinkedList<Flick>();

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
                String author = (photo.isNull("ownername")) ? "" : photo.getString("ownername");
                String image_square = image.replace("_z", "_s");//getThumb(id);
                String user_id = (photo.isNull("owner")) ? "" : photo.getString("owner");

                Flick f = new Flick(image, description, id, author, title, image_square, user_id);
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
    }
    @WorkerThread
    private Flick[] Popular() {
        LinkedList<Flick> result = new LinkedList<Flick>();

        String SERVER = "https://api.flickr.com/services/rest/?method=flickr.photos.getPopular&api_key=efaa708098eef9c038ad4c123041733c&format=json&nojsoncallback=1";

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
                String author = (photo.isNull("ownername")) ? "" : photo.getString("ownername");
                String image_square = getThumb(id);
                String user_id = (photo.isNull("owner")) ? "" : photo.getString("owner");

                Flick f = new Flick(image, description, id, author, title, image_square, user_id);
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
    }

    private Comments[] Comment(String image) {
        LinkedList<Comments> result = new LinkedList<>();

        String SERVER = "https://api.flickr.com/services/rest/?method=flickr.photos.comments.getList&api_key=efaa708098eef9c038ad4c123041733c&photo_id=" + image + "&format=json&nojsoncallback=1";

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

            JSONObject jsonObj = new JSONObject(answer);
            JSONObject comment = jsonObj.getJSONObject("comments");
            JSONArray jComment = null;
            JSONObject job = null;
            try {
                jComment = comment.getJSONArray("comment");
            } catch (JSONException e) {
            }

            if (jComment == null)
                job = jsonObj.getJSONObject("comment");

            String author = "";
            String commit = "";

            if (jComment != null) {
                if (jComment != null)
                    for (int i = 0; i < jComment.length(); i++) {
                        JSONObject j = jComment.getJSONObject(i);

                        author = (j.isNull("authorname")) ? j.getString("author") : j.getString("authorname");
                        commit = (j.isNull("_content")) ? "" : j.getString("_content");

                        Comments c = new Comments(author, commit);
                        result.add(c);
                    }
            } else {
                author = (job.isNull("authorname")) ? job.getString("author") : job.getString("authorname");
                commit = (job.isNull("_content")) ? "" : job.getString("_content");

                Comments c = new Comments(author, commit);
                result.add(c);
            }

        }catch (IOException e) {
            Log.d(TAG, "I/O exception");
        } catch (JSONException e) {
            Log.d(TAG, "JSONException");
        }

        if (result.size() != 0)
            return  result.toArray(new Comments[result.size()]);
        else {
            Comments c = new Comments("Nessun Commento", "Nessun Comment");
            result.add(c);
            return result.toArray(new Comments[result.size()]);
        }
    }

    public String getThumb(String id) {
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