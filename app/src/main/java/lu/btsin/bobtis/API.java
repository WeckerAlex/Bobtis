package lu.btsin.bobtis;

import static lu.btsin.bobtis.API.APIEndpoint.ABSENCE_SHORTEN;
import static lu.btsin.bobtis.API.APIEndpoint.ABSENCE_SPEED;
import static lu.btsin.bobtis.API.APIEndpoint.STUDENTS;

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

    public enum APIEndpoint {
        LOGIN,
        SCHOOLYEARS,
        CLASSES,
        ROOMS,
        TEACHERS,
        AREASONS,
        TIMEGRID,
        INFO,
        CLASS,
        TEACHER,
        ROOM,
        STUDENT,
        STUDENTS,
        ABSENCES,
        HOMEWORKS,
        ABSENCE_SPEED,
        ABSENCE_UPDATE,
        ABSENCE_SHORTEN,
        ABSENCE_REMOVE,
        HOMEWORK_ADD,
        HOMEWORK_REMOVE,
        HOMEWORK_UPDATE;

        @Override
        public String toString() {
            return super.toString().replace("_",".").toLowerCase();
        }
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
            URL url = new URL("https://ssl.ltam.lu/bobtis/api/" + endpoint.toString() + ".php");
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
        APIEndpoint endpoint = APIEndpoint.valueOf(objects[0].toString().replace(".","_").toUpperCase());
        String data = "";
        try {
            switch (endpoint) {
                case LOGIN: {
                    data = "username=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "password=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                }
                case AREASONS:
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
                case STUDENT:
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "week=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "id_student=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                case STUDENTS:
                case ABSENCES:
                case HOMEWORKS:
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "id_lesson=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                case INFO:
                    data = "id_teacher=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "id_student=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                case TIMEGRID:
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "class=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                case ABSENCE_SPEED:
                    data = "id_lesson=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "id_student=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                case ABSENCE_UPDATE:
                    data = "id_absence=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "acomment=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "fi_areason=" + URLEncoder.encode(objects[3].toString(), "UTF-8") + "&" + "endTime=" + URLEncoder.encode(objects[4].toString(), "UTF-8");
                    break;
                case ABSENCE_SHORTEN:
                case ABSENCE_REMOVE:
                    data = "id_absence=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
                    break;
                case HOMEWORK_ADD:
                    Log.i("API",endpoint.toString());
                    break;
                case HOMEWORK_REMOVE:
                    Log.i("API",endpoint.toString());
                    break;
                case HOMEWORK_UPDATE:
                    Log.i("API",endpoint.toString());
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
            task.execute(APIEndpoint.LOGIN, user.getUsername(), user.getPassword());
        }
    }



//    LOGIN,
    public static void login(String username, String password,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.LOGIN,username,password);
    }
//    SCHOOLYEARS,
//    CLASSES,
    public static void getClasses(String schoolyear,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute("classes",schoolyear);
    }
//    ROOMS,
    public static void getRooms(String schoolyear,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute("rooms",schoolyear);
    }
//    TEACHERS,
    public static void getTeachers(String schoolyear,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TEACHERS,schoolyear);
    }
//    AREASONS,
    public static void getReasons(AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.AREASONS);
    }
//    TIMEGRID,
//    INFO,
//    CLASS,
    public static void getClass(String schoolyear, int week, String requestedclass,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute("class",schoolyear,week,requestedclass);
    }
//    TEACHER,
    public static void  getTeacher(String schoolyear, int week, String id, AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TEACHER,schoolyear,week,id);
    }
//    ROOM,
    public static void getRoom(String schoolyear, int week, String room, AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute("room",schoolyear,week,room);
    }
//    STUDENT,
    public static void getStudent(String schoolyear, int week, String id,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.STUDENT,schoolyear,week,id);
    }
//    STUDENTS,
    public static void getStudents(String schoolyear, int idLesson, AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(STUDENTS,schoolyear,idLesson);
    }
//    ABSENCES,
    public static void getAbsences(String schoolyear, int idLesson,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.ABSENCES,schoolyear,idLesson);
    }
//    HOMEWORKS,
//    ABSENCE_SPEED,
    public static void setAbsenceSpeed(int id_lesson, int id_student,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(ABSENCE_SPEED,id_lesson,id_student);
    }
//    ABSENCE_UPDATE,
    public static void updateAbsence(int id_absence, String comment, int id_reason,String endTime,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.ABSENCE_UPDATE,id_absence,comment,id_reason,endTime);
    }
//    ABSENCE_SHORTEN,
    public static void shortenAbsence(int id_absence,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(ABSENCE_SHORTEN,id_absence);
    }
//    ABSENCE_REMOVE,
    public static void deleteAbsence(int absenceId,AsyncResponse listener) {
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.ABSENCE_REMOVE,absenceId);
    }
//    HOMEWORK_ADD,
//    HOMEWORK_REMOVE,
//    HOMEWORK_UPDATE
}