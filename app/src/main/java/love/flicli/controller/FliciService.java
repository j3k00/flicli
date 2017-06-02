package love.flicli.controller;
import android.app.IntentService;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;
import net.jcip.annotations.Immutable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

import love.flicli.FlicliApplication;
import love.flicli.MVC;

import static android.provider.Telephony.Carriers.SERVER;

/**
 * Created by tommaso on 09/05/17.
 */

public class FlicliService extends IntentService {
    private final static String TAG = FlicliService.class.getName();
    private final static String ACTION_FLICKER = "Flicker";
    private final static String ACTION_RECENT = "Recent";
    private final static String ACTION_POPULAR = "Popular";
    private final static String ACTION_COMMENT = "Comment";
    private final static String ACTION_IMAGE = "Image";
    private final static String ACTION_AUTHOR = "Author";

    private final static String PARAM_N = "n";
    private static String search = "";

    public FlicliService() {
        super("Flicker Service");
    }

    @Immutable
    public class Flick {
        private String imgUrl = "";
        private String descriptiion = "";
        private String id = "";
        private String title = "";
        private String author = "";
        private String image_square = "";
        private String user_id = "";

        public Flick(String image, String descriptiion, String id, String author, String title, String image_square, String user_id) {
            this.imgUrl = image;
            this.descriptiion = descriptiion;
            this.id = id;
            this.author = author;
            this.title = title;
            this.image_square = image_square;
            this.user_id = user_id;
        }

        public String getImgUrl() {
            return this.imgUrl;
        }

        public String getId(){ return  this.id; }

        public String getThumbNail() {
            return this.image_square;
        }

        public String getDescriptiion() {
            return this.descriptiion;
        }

        public String getTitle() {
            return this.title;
        }

        public String getAuthor() {
            return this.author;
        }

        public String getUser_id() { return this.user_id; }
    }

    @Immutable
    public class Comments {
        private String author = "";
        private String comment = "";

        public Comments(String author, String comment) {
            this.author = author;
            this.comment = comment;
        }

        public String getAuthor() { return this.author; }
        public String getComments() { return this.comment; }

    }

    //devo passargli la stringa da concatenare con la ricerca
    @UiThread
    static void flicker(Context context, String n) {
        Intent intent = new Intent(context, FlicliService.class);
        intent.setAction(ACTION_FLICKER);
        search = n;
        intent.putExtra(PARAM_N, n);
        context.startService(intent);
    }

    @UiThread
    static void recent(Context context) {
        Intent intent = new Intent(context, FlicliService.class);
        intent.setAction(ACTION_RECENT);
        context.startService(intent);
    }

    @UiThread
    static void popular(Context context) {
        Intent intent = new Intent(context, FlicliService.class);
        intent.setAction(ACTION_POPULAR);
        context.startService(intent);
    }

    @UiThread
    static void comment(Context context, String image) {
        Intent intent = new Intent(context, FlicliService.class);
        intent.setAction(ACTION_COMMENT);
        search = image;
        intent.putExtra(PARAM_N, search);
        context.startService(intent);
    }

    @UiThread
    static void lastAuthorImage(Context context, String author) {
        Intent intent = new Intent(context, FlicliService.class);
        intent.setAction(ACTION_AUTHOR);
        search = author;
        intent.putExtra(PARAM_N, search);
        context.startService(intent);
    }

    @UiThread
    static void image(Context context, String image) {
        Intent intent = new Intent(context, FlicliService.class);
        intent.setAction(ACTION_IMAGE);
        search = image;
        intent.putExtra(PARAM_N, search);
        context.startService(intent);
    }

    @WorkerThread
    protected void onHandleIntent(Intent intent) {
        Flick[] result;
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();
        switch (intent.getAction()) {
            case ACTION_FLICKER:
                String n = (String) intent.getSerializableExtra(PARAM_N);
                result = Flickers(n, mvc);
                mvc.model.storeFactorization(result);
                break;
            case ACTION_RECENT:
                result = Recent();
                mvc.model.storeFactorization(result);
                break;
            case ACTION_POPULAR:
                result = Popular();
                mvc.model.storeFactorization(result);
                break;
            case ACTION_COMMENT:
                String image = (String) intent.getSerializableExtra(PARAM_N);
                Comments[] comments = Comment(image);
                mvc.model.storeComments(comments);
                break;
            case ACTION_IMAGE:
                break;
            case ACTION_AUTHOR:
                String author = (String) intent.getSerializableExtra(PARAM_N);
                result = author(author);
                mvc.model.storeFactorization(result);

        }
    }

    // change this to the actual server that you want to query

    @WorkerThread
    private Flick[] Flickers(String search, MVC mvc) {
//        Log.d(TAG, "starting search of" + search);

        String endpoint = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=efaa708098eef9c038ad4c123041733c&text=" + search +"&extras=url_z%2Cdescription%2Ctags%2Cowner_name&per_page=50&format=json&nojsoncallback=1";

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

                JSONObject photo = jPhoto.getJSONObject(i);
                String id = (photo.isNull("id")) ? "" : photo.getString("id");
                String description = (photo.isNull("description")) ? "" :photo.getString("description");
                String image = (photo.isNull("url_z")) ? "" : photo.getString("url_z");
                String title = (photo.isNull("title")) ? "" : photo.getString("title");
                String image_square = image.replace("_z", "_s");//getThumb(id);
                String user_id = (photo.isNull("owner")) ? "" : photo.getString("owner");

                // il filtro author dimezza il numero di immagini per richiesta
                String author = "";//photo.getString("ownername");

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
    private Flick[] Recent() {
        String SERVER = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=efaa708098eef9c038ad4c123041733c&extras=url_z%2Cdescription%2Ctags%2Cowner_name&per_page=50&format=json&nojsoncallback=1";
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
    }
}