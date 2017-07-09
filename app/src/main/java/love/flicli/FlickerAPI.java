package love.flicli;

import android.util.Log;

import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Map;

import love.flicli.controller.ApiController;

import static android.content.ContentValues.TAG;

/**
 * Created by jeko on 02/06/17.
 */

// todo singleton
@ThreadSafe
public class FlickerAPI {
    public static final String API_KEY = "7d915cac1d6d251a1014bc8e00a9bf2e";
    // I really need that info?
    public static final String SECRET_KEY = "10fde0314d4d1aef";

    public static final String ENDPOINT = "https://api.flickr.com/services/rest/?";
    public static final String FORMAT = "json";

    private final static String TAG = ApiController.class.getName();

    private static String makeUrl(Map<String, String> params) {
        String endpoint = ENDPOINT + "api_key=" + API_KEY + "&format=" + FORMAT + "&nojsoncallback=1";

        for (Map.Entry<String, String> entry : params.entrySet()) {
            endpoint += "&" + entry.getKey() + "=" + entry.getValue();
        }

        Log.d(TAG, "ENDPOINT --------" + endpoint + "--------------------------------");

        return endpoint;
    }

    //https://api.flickr.com/services/rest/?
    // method=flickr.photos.search
    // &api_key=efaa708098eef9c038ad4c123041733c
    // &text=" + search +"
    // &extras=url_z%2Cdescription%2Ctags%2Cowner_name
    // &per_page=50
    // &format=json
    // &nojsoncallback=1
    public String photos_search(String search) {
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
    public String photos_getRecent() {
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
    public String photos_getPopular() {
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
    public String photos_getComments(String photo_id) {
        String method = "flickr.photos.comments.getList";

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("photo_id", photo_id);

        return makeUrl(params);
    }

    public String photo_getFav(String photo_id) {
        String method = "flickr.photos.getFavorites";

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("photo_id", photo_id);

        return makeUrl(params);
    }

    public String photo_getAuthor(String author) {
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
}
