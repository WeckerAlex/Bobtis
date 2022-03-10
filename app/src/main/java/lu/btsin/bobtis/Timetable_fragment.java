package lu.btsin.bobtis;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Timetable_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Timetable_fragment extends Fragment implements AsyncResponse {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public enum Actions {
        CLASS,
        ROOM,
        TEACHER
    }

    private LocalDate currentDate = LocalDate.now();
    private static Calendar calendar;
    private int hourHeight = 200;
    private int startHour = 7;
    private int endHour = 20;
    private Actions currentaction;
    private String requestedData;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Timetable_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Timetable_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Timetable_fragment newInstance(String param1, String param2) {
        Timetable_fragment fragment = new Timetable_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendar = Calendar.getInstance(Locale.FRANCE);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        Log.i("Calendar","created");
        createButtonListener();
        drawTimetable();
        updateDate();
    }

    private void createButtonListener() {
        ((ImageView) getView().findViewById(R.id.buttonNext)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate = currentDate.plusWeeks(1);
                updateDate();
            }
        });
        ((ImageView) getView().findViewById(R.id.buttonPrevious)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate = currentDate.minusWeeks(1);
                updateDate();
            }
        });
    }

    private void updateDate(){
        Log.i("Calendar","updateDate");
        removeEvents();
        ((TextView)getView().findViewById(R.id.DayTV)).setText(getWeekDescription());
        Log.i("currentaction", String.valueOf(currentaction));
        if (currentaction != null){
            switch (currentaction){
                case CLASS:
                    getClass(getSchoolyear(),getWeekNumber(),requestedData);
                    break;
                case ROOM:
                    getRoom(getSchoolyear(),getWeekNumber(),requestedData);
                    break;
                case TEACHER:
                    getTeacher(getSchoolyear(),getWeekNumber(),requestedData);
                    break;
            }
        }
    }

    private String getSchoolyear(){
        int currentyear = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).getYear();
        int currentSchoolyear = currentyear;
        if (currentDate.getMonth().getValue()<8){
            currentSchoolyear--;
        }
        return currentSchoolyear+"-"+ (currentSchoolyear+1);
    }

    private int getWeekNumber(){
        //get the date the user wants to be displayed
        calendar.setTime(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Log.i("Api week", String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private String getWeekDescription(){
        Log.i("API",currentDate.toString());
        String start = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString();
        String end = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toString();
        Log.i("API",start+"-"+end);
        return start+"-"+end;
    }

    protected void getClass(String schoolyear, int week, String requestedclass){
        API task =  new API();
        task.delegate = this;
        task.execute("class",schoolyear,week,requestedclass);
    }

    protected void getRoom(String schoolyear, int week, String room){
        API task =  new API();
        task.delegate = this;
        task.execute("room",schoolyear,week,room);
    }

    protected void getTeacher(String schoolyear, int week, String teacher){
        API task =  new API();
        task.delegate = this;
        task.execute("teacher",schoolyear,week,teacher);
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case LOGIN:
                //auto login
                prossessLogin(response);
                break;
            case TEACHER:
            case ROOM:
            case CLASS:
                prossessTimetable(response);
                break;
        }
    }

    protected void drawTimetable(){
        LinearLayout legend = getView().findViewById(R.id.legend);
        for (int i = startHour;i<endHour;i++){
            TextView tw = new TextView(getContext());
            tw.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hourHeight);
            params.weight = 1f;
            params.setMargins(0,1,0,1);
            tw.setLines(3);
            tw.setLayoutParams(params);
            tw.setText(i+":00"+"\n-\n"+(i+1)+":00");
            tw.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //tw.setBackgroundResource(R.drawable.legend);
            legend.addView(tw);
        }
    }

    protected void insertDayEvents(JSONArray daylessons,int day){
//        System.out.println("insertDayEvents");
        try {
            if (daylessons.length()!=0){
                //events recieved
                JSONArray firstlesson = daylessons.getJSONArray(0);
                if (firstlesson.length()!=0){
                    if(firstlesson.getJSONObject(0).getBoolean("is_holiday")){
                        //holiday
                        insertHoliday(firstlesson.getJSONObject(0),day);
                    }else{
                        //school
                        insertSchooldayEvents(daylessons,day);
                    }
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertHoliday(JSONObject info,int day) throws JSONException {
        Log.i("TAG", "insertHoliday: "+info);
        drawHolidayEvent(day,info.getString("color"),info.getString("name"));
    }

    private void insertSchooldayEvents(JSONArray daylessons,int day){
        Log.i("TAG", "insertSchooldayEvents: ");
        HashMap<String,String> extensions = new HashMap<>();
        try {
            //loop through every timeslot starting at the last course
            for (int hour=daylessons.length()-1; hour>=0; hour--){
                JSONArray concurrentarray = daylessons.getJSONArray(hour);//get the array containing all concurrent hours
                //loop through the concurrent lessons
                for (int lessoncount=concurrentarray.length()-1; lessoncount>=0; lessoncount--){
                    JSONObject schoolclass = new JSONObject(concurrentarray.getString(lessoncount));
                    //update endtime if nessesary
                    if (extensions.get(schoolclass.getString("id_lesson")) != null) {
                        //this lesson does not end at the saved end
                        schoolclass.put("end",extensions.get(schoolclass.getString("id_lesson")));
                        //remove the extension
                        extensions.remove(schoolclass.getString("id_lesson"));
                    }
                    //update hashmap
                    if (schoolclass.getString("fi_parent_lesson") != "null"){
                        //lesson belongs to other lesson
                        //save it into the extensions
                        extensions.put(schoolclass.getString("fi_parent_lesson"),schoolclass.getString("end"));
                        //remove the extension so it does not get displayed twice
                        concurrentarray.remove(lessoncount);
                    }else {
                        //lesson has no parent
                        //draw it

                        String classe = getStringfromJsonArray(schoolclass.getJSONArray("classe"));
                        String teacher = getStringfromJsonArray(schoolclass.getJSONArray("teacher"));
                        String subject = getStringfromJsonArray(schoolclass.getJSONArray("subject"));
                        String room = getStringfromJsonArray(schoolclass.getJSONArray("room"));
                        drawEvent1x4(day,schoolclass.getString("begin"),schoolclass.getString("end"), schoolclass.getString("color"), classe, teacher, subject, room);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void removeEvents(){
        LinearLayout parent = (LinearLayout) getView().findViewById(R.id.timetableLayout);
        for (int dayIndex = 0; dayIndex<5; dayIndex++){
            FrameLayout dayLayout = (FrameLayout) parent.getChildAt(dayIndex+1);
            dayLayout.removeAllViews();
        }

    }

    private String getStringfromJsonArray(JSONArray arr) throws JSONException {
        return arr.join("/").replace("\"","");
    }

    protected void drawEvent2x2(int dayIndex, String startTime, String endTime, String color, String classText, String teacherAbb, String branchName, String roomAbb){
        LinearLayout parent = (LinearLayout) getView().findViewById(R.id.timetableLayout);
        FrameLayout dayLayout = (FrameLayout) parent.getChildAt(dayIndex+1);
        int[] startime = Arrays.stream(startTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int[] endtime = Arrays.stream(endTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int startheight = Math.round((startime[0]-startHour+startime[1]/60f)*hourHeight);
        int duration = Math.round((Math.min(endtime[0],endHour)+endtime[1]/60f)*hourHeight)-Math.round((startime[0]+startime[1]/60f)*hourHeight);
        //the layout on which you are working
//        FrameLayout day1Layout = (FrameLayout) findViewById(view);//The whole day


        //create new entry
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setGravity(Gravity.CLIP_HORIZONTAL);
        mainLayout.setBackgroundResource(R.drawable.coursebackground);
        ((GradientDrawable) mainLayout.getBackground()).setColor(Color.parseColor(color));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, duration);
        layoutParams.topMargin = startheight;
        mainLayout.setLayoutParams(layoutParams);

        LinearLayout ln1 = new LinearLayout(getContext());
        ln1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1f));
        ln1.setOrientation(LinearLayout.VERTICAL);
        ln1.addView(createTextView(classText));
        ln1.addView(createTextView(branchName));

        LinearLayout ln2 = new LinearLayout(getContext());
        ln2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1f));
        ln2.setOrientation(LinearLayout.VERTICAL);
        ln2.addView(createTextView(teacherAbb));
        ln2.addView(createTextView(roomAbb));

        mainLayout.addView(ln1);
        mainLayout.addView(ln2);
        mainLayout.setPadding(10,10,10,10);
        dayLayout.addView(mainLayout);
    }

    protected void drawEvent1x4(int dayIndex, String startTime, String endTime, String color, String classText, String teacherAbb, String branchName, String roomAbb){
        LinearLayout parent = (LinearLayout) getView().findViewById(R.id.timetableLayout);
        FrameLayout dayLayout = (FrameLayout) parent.getChildAt(dayIndex+1);
        int[] startime = Arrays.stream(startTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int[] endtime = Arrays.stream(endTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int startheight = Math.round((startime[0]-startHour+startime[1]/60f)*hourHeight);
        int duration = Math.round((Math.min(endtime[0],endHour)+endtime[1]/60f)*hourHeight)-Math.round((startime[0]+startime[1]/60f)*hourHeight);
        //the layout on which you are working
//        FrameLayout day1Layout = (FrameLayout) findViewById(view);//The whole day

        //create new entry
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setGravity(Gravity.CLIP_VERTICAL);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundResource(R.drawable.coursebackground);
        ((GradientDrawable) mainLayout.getBackground()).setColor(Color.parseColor(color));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, duration);
        layoutParams.topMargin = startheight;
        mainLayout.setLayoutParams(layoutParams);

        mainLayout.addView(createTextView(classText));
        mainLayout.addView(createTextView(branchName));

        mainLayout.addView(createTextView(teacherAbb));
        mainLayout.addView(createTextView(roomAbb));

        mainLayout.setPadding(10,10,10,10);
        dayLayout.addView(mainLayout);
    }

    protected void drawHolidayEvent(int dayIndex, String color, String name){
        LinearLayout parent = (LinearLayout) getView().findViewById(R.id.timetableLayout);
        FrameLayout dayLayout = (FrameLayout) parent.getChildAt(dayIndex+1);
        int duration = (endHour-startHour)*hourHeight;

        //create new entry
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setBackgroundResource(R.drawable.coursebackground);
        ((GradientDrawable) mainLayout.getBackground()).setColor(Color.parseColor(color));

        TextView tw = new TextView(getContext());
        tw.setLines(1);
        tw.setMaxLines(1);
        tw.setText(name);
        tw.setTextSize(20f);
        tw.setTypeface(null, Typeface.BOLD);
        tw.setGravity(Gravity.CENTER);
        tw.setWidth((endHour-startHour)*hourHeight);
        tw.setRotation(270);

        RelativeLayout container = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(-duration/3, 0, -duration/3, 0);
        container.setGravity(Gravity.CENTER);
        container.setLayoutParams(lp);
        container.addView(tw);
        mainLayout.addView(container);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, duration);
        mainLayout.setLayoutParams(layoutParams);
        mainLayout.setPadding(10,10,10,10);
        dayLayout.addView(mainLayout);
    }

    protected TextView createTextView(String text){
        return createTextView(text,1);
    }

    protected TextView createTextView(String text,int lines){
        TextView tw = new TextView(getContext());
        tw.setText(text);
        tw.setTextSize(10f);
        tw.setTypeface(null, Typeface.BOLD);
        tw.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        tw.setLines(lines);
        tw.setLayoutParams(params);
        return tw;
    }

    private void prossessLogin(ServerResponse response){
        //auto login
        try {
            String message;
            JSONObject json = new JSONObject(response.response);
            switch (response.status){
                case 200:{
                    message = "You are logged in as "+json.getString("type");
                    getClass("2021-2022",10,"B2IN");
                    break;
                }
                case 500:
                case 400:
                case 404:
                case 412:{
                    message = json.getString("error");
                    break;
                }
                default:{
                    message = "Something went wrong";
                }
            }
            Toast.makeText(getContext(), response.status+" "+message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the API response. If there is no session it logs in.
     * @param response The API response
     */
    private void prossessTimetable(ServerResponse response){
        try {
            String message;
            Log.i("Login", String.valueOf(response.status));
            switch (response.status){
                case 200:{
                    JSONArray dayarray = new JSONArray(response.response);//get the array containing the single days
                    for (int day=0;day< dayarray.length();day++){
                        //insert a single day
                        Log.i("Day",String.valueOf(day));
                        Log.i("Day",dayarray.getJSONArray(day).toString());
                        insertDayEvents(dayarray.getJSONArray(day),day);
                    }
                    break;
                }
                case 400:{
                    //no valid session
                    //login with saved credentials
                    API.autologin(getContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE),this);
                    break;
                }
                case 404:
                case 500:{
                    Toast.makeText(getContext(),"Error: "+new JSONObject(response.response).getString("error"), Toast.LENGTH_LONG).show();
                    break;
                }
                default:{
                    Toast.makeText(getContext(),"Not connected to the internet", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData(Actions action,String data){
        currentaction = action;
        requestedData = data;
        switch (action){
            case CLASS:
                getClass(getSchoolyear(),getWeekNumber(),data);
                break;
            case ROOM:
                getRoom(getSchoolyear(),getWeekNumber(),data);
                break;
            case TEACHER:
                getTeacher(getSchoolyear(),getWeekNumber(),data);
                break;
        }
    }

//    private void autologin() {
//        System.out.println("Auto Logging in");
//        SharedPreferences prefs = getContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
//        if (prefs.contains("username") && prefs.contains("password")){
//            API task = new API();
//            task.delegate = this;
//            task.execute("login", prefs.getString("username", ""), prefs.getString("password", ""), prefs);
//        }else{
//            Toast.makeText(getContext(), "Log in first", Toast.LENGTH_LONG).show();
//        }
//
//    }
}