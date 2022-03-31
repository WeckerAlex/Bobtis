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

    /**
     * The different API endpoints in capital letters and the dot been replaced by underscore
     */
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
        TEST,
        ABSENCE_SPEED,
        ABSENCE_UPDATE,
        ABSENCE_SHORTEN,
        ABSENCE_REMOVE,
        HOMEWORK_ADD,
        HOMEWORK_REMOVE,
        HOMEWORK_UPDATE,
        TEST_ADD,
        TEST_REMOVE,
        TEST_UPDATE,
        TESTS;

        /**
         * Replaces the underscore with a dot and converts it to lowercase
         * @return The api endpoint string
         */
        @Override
        public String toString() {
            return super.toString().replace("_",".").toLowerCase();
        }
    }

    /**
     * Creates an API call and creates the cookie Manager to establish the session
     */
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
     * @param data     the additional data, already encoded
     * @return The data from the API as ServerResponse
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
            //get the html code
            int status = con.getResponseCode();
            InputStreamReader isr = getInputStreamReader(status, con);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            //create a ServerResponse object and return it
            return new ServerResponse(endpoint, status, content.toString());
        } catch (Exception e) {
            //There has been a problem such as no response
            return new ServerResponse(endpoint,0,null);
        }
    }

    /**
     * Performs a request asynchronously. Called by API().execute()
     * @param objects An array of objects. The first array element is the API endpoint,the successing elements are data to be passed to the API
     * @return The data from the API as ServerResponse
     */
    @Override
    protected Object doInBackground(Object[] objects) {
        APIEndpoint endpoint = APIEndpoint.valueOf(objects[0].toString().replace(".","_").toUpperCase());
        String data = "";
        //encode the data to be passed to the API
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
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "id_lesson=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                case ABSENCES:
                case HOMEWORKS:
                    switch (objects.length){
                        case 2:
                            //objects contains the endpoint and id_student
                            data = "id_student=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
                            break;
                        case 3:
                            //objects contains the endpoint, schoolyear and id_lesson
                            data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "id_lesson=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                            break;
                    }
                    break;
                case INFO:
                    data = "id_teacher=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "id_student=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                case TIMEGRID:
                    data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "class=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                    break;
                case TEST:
                    data = "id_lesson=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
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
                    data = "id_lesson=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "content=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "date_due=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                case HOMEWORK_REMOVE:
                    data = "id_homework=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
                    break;
                case HOMEWORK_UPDATE:
                    data = "id_homework=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "content=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "date_due=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                case TEST_ADD:
                    data = "id_lesson=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "content=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "title=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                case TEST_REMOVE:
                    data = "id_test=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
                    break;
                case TEST_UPDATE:
                    data = "id_test=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "content=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "title=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                    break;
                case TESTS:
                    switch (objects.length){
                        case 2:{
                            //objects contains the endpoint and id_student
                            data = "id_student=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
                            break;
                        }
                        case 3:{
                            //objects contains the endpoint, schoolyear and class
                            data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "class=" + URLEncoder.encode(objects[2].toString(), "UTF-8");
                            break;
                        }
                        case 4:{
                            //objects contains the endpoint, schoolyear, class and subject
                            data = "schoolyear=" + URLEncoder.encode(objects[1].toString(), "UTF-8") + "&" + "class=" + URLEncoder.encode(objects[2].toString(), "UTF-8") + "&" + "subject=" + URLEncoder.encode(objects[3].toString(), "UTF-8");
                            break;
                        }
                    }
                    data = "id_lesson=" + URLEncoder.encode(objects[1].toString(), "UTF-8");
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sendApiCall(endpoint, data);
    }

    /**
     * Sends the data to the delegate
     * @param sr A ServerResponse Object returned from doInBackground
     */
    @Override
    protected void onPostExecute(Object sr) {
        super.onPostExecute(sr);
        if (delegate != null){
            Log.i("onPostExecute", String.valueOf(sr == null));
            delegate.processFinish((ServerResponse) sr);
        }
    }

    /**
     *
     * @param status The html status code
     * @param con The HttpURLConnection
     * @return An InputStreamReader
     * @throws IOException
     */
    private InputStreamReader getInputStreamReader(int status, HttpURLConnection con) throws IOException {
        InputStreamReader isr;
        if (status >= 200 && status < 300) {
            isr = new InputStreamReader(con.getInputStream());
        } else {
            //400 || 404 || 412 ...
            isr = new InputStreamReader(con.getErrorStream());
        }
        return isr;
    }

    /**
     * Logs the user in and sends the response to the listener
     * @param user The user to be logged in
     * @param listener The object to send the response to
     */
    public static void autologin(User user,AsyncResponse listener) {
        System.out.println("Auto Logging in");
        if (user != null){
            API task = new API();
            task.delegate = listener;
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
        task.execute(APIEndpoint.CLASSES,schoolyear);
    }
//    ROOMS,
    public static void getRooms(String schoolyear,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.ROOMS,schoolyear);
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
        task.execute(APIEndpoint.CLASS,schoolyear,week,requestedclass);
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
        task.execute(APIEndpoint.ROOM,schoolyear,week,room);
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
        task.execute(APIEndpoint.STUDENTS,schoolyear,idLesson);
    }
//    ABSENCES,
    public static void getAbsences(String schoolyear, int idLesson,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.ABSENCES,schoolyear,idLesson);
    }
    public static void getAbsencesByStudent(int id_student,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.ABSENCES,id_student);
    }
//    HOMEWORKS,
    public static void getHomeworks(String schoolyear, int idLesson,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.HOMEWORKS,schoolyear,idLesson);
    }
    public static void getHomeworksByStudent(int id_student,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.HOMEWORKS,id_student);
    }
//    TEST,
    public static void getTest(int idLesson,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TEST,idLesson);
    }
//    ABSENCE_SPEED,
    public static void setAbsenceSpeed(int id_lesson, int id_student,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.ABSENCE_SPEED,id_lesson,id_student);
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
        task.execute(APIEndpoint.ABSENCE_SHORTEN,id_absence);
    }
//    ABSENCE_REMOVE,
    public static void deleteAbsence(int absenceId,AsyncResponse listener) {
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.ABSENCE_REMOVE,absenceId);
    }
//    HOMEWORK_ADD,
    public static void addHomework(int id_lesson, String content,String date_due,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.HOMEWORK_ADD,id_lesson,content,date_due);
    }
//    HOMEWORK_REMOVE,
    public static void removeHomework(int id_homework,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.HOMEWORK_REMOVE,id_homework);
    }
//    HOMEWORK_UPDATE,
    public static void updateHomework(int id_homework,String content,String date_due,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.HOMEWORK_UPDATE,id_homework,content,date_due);
    }
//    TEST_ADD,
    public static void addTest(int id_lesson,String content,String title,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TEST_ADD,id_lesson,content,title);
    }
//    TEST_REMOVE,
    public static void removeTest(int id_test,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TEST_REMOVE,id_test);
    }
//    TEST_UPDATE,
    public static void updateTest(int id_test,String content,String title,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TEST_UPDATE,id_test,content,title);
    }
//    TESTS;
    public static void getTests(int id_student,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TESTS,id_student);
    }
    public static void getTests(String schoolyear,String classe,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TESTS,schoolyear,classe);
    }
    public static void getTests(String schoolyear,String classe,String subject,AsyncResponse listener){
        API task =  new API();
        task.delegate = listener;
        task.execute(APIEndpoint.TESTS,schoolyear,classe,subject);
    }
}