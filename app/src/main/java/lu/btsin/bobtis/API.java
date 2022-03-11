package lu.btsin.bobtis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

class API extends AsyncTask {
    public AsyncResponse delegate = null;
    private static CookieManager cookieManager = null;

//    public static void saveloginData(String username, String password, SharedPreferences prefs) {
//        SharedPreferences.Editor edit = prefs.edit();
//        edit.putString("username", username);
//        edit.putString("password", password);
//        edit.apply();
//    }

//    public static void saveloginDataAll(SharedPreferences prefs, String jsonString){
//        Log.i("spclasse","classe");
//        try {
//            JSONObject json = new JSONObject(jsonString);
//            SharedPreferences.Editor edit = prefs.edit();
//            edit.putString("type", json.getString("type"));
//            edit.putString("name", json.getString("name"));
//            edit.putString("firstname", json.getString("firstname"));
//            edit.putString("username", json.getString("username"));
//            edit.putString("email", json.getString("email"));
//            if (json.has("id_student")){
//                edit.putString("id_student", json.getString("id_student"));
//            }
//            if (json.has("id_teacher")){
//                edit.putString("id_teacher", json.getString("id_teacher"));
//            }
//            if (json.has("id_staff")){
//                edit.putString("id_staff", json.getString("id_staff"));
//            }
//            edit.putString("classe", json.getString("classe"));
//            JSONArray jsonclasse = json.getJSONArray("rights");
//            ArrayList<String> rightsdata = new ArrayList<>();
//            for (int i = 0; i < jsonclasse.length(); i++) {
//                rightsdata.add(jsonclasse.getString(i));
//            }
//            edit.putString("rights", String.join(",",rightsdata));
//            edit.apply();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public enum APIEndpoint {
        LOGIN,
        SCHOOLYEARS,
        CLASSES,
        CLASS,
        ROOMS,
        TEACHERS,
        TEACHER,
        ROOM,
        STUDENTS
    }

    public API() {
        super();
        // If the cookiemanager has not been initialized, initialize it
        if (cookieManager == null) {
            cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        }
    }


    /**
     * Sends a POST request to the API endpoint
     *
     * @param endpoint the api endpoint
     * @param data     the additional data
     * @return The response from the server
     */
    private ServerResponse sendApiCall(APIEndpoint endpoint, String data) {
        try {
            URL url = new URL("https://ssl.ltam.lu/bobtis/api/" + endpoint.toString().toLowerCase() + ".php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(data);
            out.flush();
            out.close();
            int status = con.getResponseCode();
            InputStreamReader isr = getInputStreamReader(status, con);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return new ServerResponse(endpoint, status, content.toString());
        } catch (Exception e) {
            return new ServerResponse(endpoint,0,null);
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        APIEndpoint endpoint = APIEndpoint.valueOf(objects[0].toString().trim().toUpperCase());
        String data = "";
        try {
            switch (endpoint) {
                case LOGIN: {
                    data = "username=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "password=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                }
                case SCHOOLYEARS: {
                    data = "";
                    break;
                }
                case CLASSES: {
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
                    break;
                }
                case CLASS: {
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "week=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "class=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                }
                case ROOMS:
                case TEACHERS:
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
                    break;
                case TEACHER:
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "week=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "teacher=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                case ROOM:
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "week=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "room=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                case STUDENTS:
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sendApiCall(endpoint, data);
    }

    @Override
    protected void onPostExecute(Object sr) {
        super.onPostExecute(sr);
        if (delegate != null){
            Log.i("onPostExecute", String.valueOf(sr == null));
            delegate.processFinish((ServerResponse) sr);
        }
    }

    private InputStreamReader getInputStreamReader(int status, HttpURLConnection con) throws IOException {
        InputStreamReader isr;
        if (status >= 200 && status < 300) {
            isr = new InputStreamReader(con.getInputStream());
        } else {
            //400 || 404 || 412
            isr = new InputStreamReader(con.getErrorStream());
        }
        return isr;
    }

    public static void autologin(User user,AsyncResponse ar) {
        System.out.println("Auto Logging in");
        if (user != null){
            API task = new API();
            task.delegate = ar;
            task.execute("login", user.getUsername(), user.getPassword());
        }
    }

}