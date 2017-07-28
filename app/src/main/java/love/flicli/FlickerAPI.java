package love.flicli;

import android.util.Log;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import love.flicli.controller.FlickerService;

/**
 * Created by jeko on 02/06/17.
 */

@ThreadSafe
public class FlickerAPI {
    public static final String API_KEY = "7d915cac1d6d251a1014bc8e00a9bf2e";
    // I really need that info?
    public static final String SECRET_KEY = "10fde0314d4d1aef";

    public static final String ENDPOINT = "https://api.flickr.com/services/rest/?";
    public static final String FORMAT = "json";

    private final static String TAG = FlickerService.class.getName();

    private static String makeUrl(Map<String, String> params) {
        String endpoint = ENDPOINT;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            endpoint += entry.getKey() + "=" + entry.getValue() + "&";
        }

        Log.d(TAG, "ENDPOINT --------" + endpoint + "api_key=" + API_KEY + "&format=" + FORMAT + "&nojsoncallback=1 --------------------------------");

        return endpoint + "api_key=" + API_KEY + "&format=" + FORMAT + "&nojsoncallback=1";
    }

    //https://api.flickr.com/services/rest/?
    // method=flickr.photos.search
    // &api_key=efaa708098eef9c038ad4c123041733c
    // &text=" + search +"
    // &extras=url_z%2Cdescription%2Ctags%2Cowner_name
    // &per_page=50
    // &format=json
    // &nojsoncallback=1
    public static String photos_search(String search) {
        String method = "flickr.photos.search";
        String extras = "url_z%2Cdescription%2Ctags%2Cowner_name%2Curl_s%2Curl_sq%2Cviews%2Curl_o";
        String per_page = "50";

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("text", search);
        params.put("extras", extras);
        params.put("per_page", per_page);
        // add more params here

        return makeUrl(params);
    }

    //https://api.flickr.com/services/rest/?
    // method=flickr.photos.getRecent
    // &api_key=efaa708098eef9c038ad4c123041733c
    // &extras=url_z%2Cdescription%2Ctags%2Cowner_name
    // &per_page=50
    // &format=json
    // &nojsoncallback=1"
    public static String photos_getRecent() {
        String method = "flickr.photos.getRecent";
        String extras = "url_z%2Cdescription%2Ctags%2Cowner_name%2Curl_s%2Curl_sq%2Cviews%2Curl_o";
        String per_page = "50";

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("extras", extras);
        params.put("per_page", per_page);

        return makeUrl(params);
    }

    //"https://api.flickr.com/services/rest/?
    // method=flickr.photos.getPopular&
    // api_key=efaa708098eef9c038ad4c123041733c&
    // format=json&
    // nojsoncallback=1";

    // https://www.flickr.com/services/api/flickr.interestingness.getList.html
    // https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=2a877a945a0b58aecf11c2e9003bf1a6&format=rest&api_sig=1b6fd7f0f9925a5cf17a35e1f4d9df13
    public static String photos_getPopular() {
        String method = "flickr.interestingness.getList";
        String extras = "url_z%2Cdescription%2Ctags%2Cowner_name%2Curl_s%2Curl_sq%2Cviews%2Curl_o";
        String per_page = "50";

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("extras", extras);
        params.put("per_page", per_page);

        return makeUrl(params);
    }


    //https://api.flickr.com/services/rest/?
    // method=flickr.photos.comments.getList&
    // api_key=efaa708098eef9c038ad4c123041733c&
    // photo_id=" + image + "
    // &format=json&nojsoncallback=1
    public static String photos_getComments(String photo_id) {
        String method = "flickr.photos.comments.getList";

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("photo_id", photo_id);

        return makeUrl(params);
    }

    public static String photos_getFav(String photo_id) {
        String method = "flickr.photos.getFavorites";

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("photo_id", photo_id);

        return makeUrl(params);
    }

    public static String photo_getAuthor(String author) {
        String method = "flickr.people.getPublicPhotos";
        String extras = "url_z%2Cdescription%2Ctags%2Cowner_name%2Curl_s%2Curl_sq%2Cviews%2Curl_o";
        String per_page = "50";

        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("per_page", per_page);
        params.put("user_id", author);
        params.put("extras", extras);

        return makeUrl(params);
    }

    public static String people_getInfo(String userId) {
        String method = "flickr.people.getInfo";

        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("user_id", userId);

        return makeUrl(params);
    }

    public static String buddyIcon(String iconFarm, String iconServer, String nsid) {
        if (Integer.parseInt(iconServer) > 0) {
            return "http://farm" + iconFarm + ".staticflickr.com/" + iconServer + "/buddyicons/" + nsid + ".jpg";
        }

        return "https://www.flickr.com/images/buddyicon.gif";
    }

    // {"stat":"fail","code":112,"message":"Method \"unknown\" not found"}
    public static JSONObject makeRequest(String endpoint) {
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
}
