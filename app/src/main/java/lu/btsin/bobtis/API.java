package lu.btsin.bobtis;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

class API extends AsyncTask {
    public AsyncResponse delegate = null;
    private static CookieManager cookieManager = null;

    public enum APIEndpoint{
        LOGIN,
        SCHOOLYEARS,
        CLASSES,
        CLASS
    }

    public API() {
        super();
        if (cookieManager == null){
            cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        }
    }

    /**
     *Sends a POST request to the API endpoint
     * @param endpoint the api endpoint
     * @param data the additional data
     * @return The response from the server
     */
    private ServerResponse sendApiCall(APIEndpoint endpoint, String data){
        try {
            URL url = new URL("https://ssl.ltam.lu/bobtis/api/"+endpoint.toString().toLowerCase()+".php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(data);
            out.flush();
            out.close();
            int status = con.getResponseCode();
            InputStreamReader isr = getInputStreamReader(status,con);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JSONObject jsondata = new JSONObject(content.toString());
            System.out.println(jsondata);
            return new ServerResponse(endpoint,status, jsondata);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        APIEndpoint endpoint = APIEndpoint.valueOf(objects[0].toString().trim().toUpperCase());
        String data="";
        try {
            switch (endpoint){
                case LOGIN:{
                    data = "username="+ URLEncoder.encode(objects[1].toString(), "UTF-8")+"&"+"password="+URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                }
                case SCHOOLYEARS:{
                    data="";
                    break;
                }
                case CLASSES:{
                    data = "schoolyear="+ URLEncoder.encode(objects[1].toString(), "UTF-8");
                    break;
                }
                case CLASS:{
                    data = "schoolyear="+ URLEncoder.encode(objects[1].toString(), "UTF-8")+"&"+"week="+ URLEncoder.encode(objects[2].toString(), "UTF-8")+"&"+"class="+URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sendApiCall(endpoint,data);
    }

    @Override
    protected void onPostExecute(Object sr) {
        super.onPostExecute(sr);
        delegate.processFinish((ServerResponse) sr);
    }

    private InputStreamReader getInputStreamReader(int status, HttpURLConnection con) throws IOException {
        InputStreamReader isr;
        if (status >= 200 && status<300){
            isr = new InputStreamReader(con.getInputStream());
        }else{
            //400 || 404 || 412
            isr = new InputStreamReader(con.getErrorStream());
        }
        return isr;
    }

}