package love.flicli.model;

import android.util.Log;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeko on 02/06/17.
 */

@ThreadSafe
public class ApiModel {
    public static final String API_KEY = "7d915cac1d6d251a1014bc8e00a9bf2e";
    public static final String ENDPOINT = "https://api.flickr.com/services/rest/?";
    public static final String FORMAT = "json";

    private static JSONObject makeRequest(Map<String, String> params) {
        String response = "";
        String line = "";
        BufferedReader in = null;
        JSONObject json = null;

        // Generate url

        String endpoint = ENDPOINT + "api_key=" + API_KEY + "&format=" + FORMAT + "&nojsoncallback=1";

        for (Map.Entry<String, String> entry : params.entrySet()) {
            endpoint += "&" + entry.getKey() + "=" + entry.getValue();
        }

        // Generate request

        try {
            URL url = new URL(endpoint);
            URLConnection conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = in.readLine()) != null) {
               // Log.d(TAG, "starting search of" + line);
                response += line + "\n";
            }

            in.close();

            json = new JSONObject(response);

        }catch (IOException e) {
            Log.d(TAG, "I/O error", e);
            return null;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        return json;
    }

    //https://api.flickr.com/services/rest/?
    // method=flickr.photos.search
    // &api_key=efaa708098eef9c038ad4c123041733c
    // &text=" + search +"
    // &extras=url_z%2Cdescription%2Ctags%2Cowner_name
    // &per_page=50
    // &nojsoncallback=1
    public JSONObject photos_search(String search) {
        String method = "flickr.photos.search";
        String extras = "url_z%2Cdescription%2Ctags%2Cowner_name";
        String per_page = "50";

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", method);
        params.put("text", search);
        params.put("extras", extras);
        params.put("per_page", per_page);

        return makeRequest(params);
    }
}
