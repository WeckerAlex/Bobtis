package lu.btsin.bobtis;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timetable_fragment extends Fragment implements AsyncResponse {
    public enum Actions {
        CLASS,
        ROOM,
        TEACHER,
        STUDENT
    }

    private static LocalDate currentDate = LocalDate.now();
    private static Calendar calendar;
    private final int hourHeight = 150;
    private final int startHour = 7;
    private final int endHour = 17;
    private static boolean extendedViewEnabled = false;
    private Actions currentaction;
    private String requestedData;
    private TextView tv1header;
    private TextView tv2header;
    private TextView tv3header;
    private TextView tv4header;
    private TextView tv5header;

    private static boolean weekMode = true;
    private static JSONArray timetabledata = null;

    /**
     * Constructor
     */
    public Timetable_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            currentDate = currentDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }
        Log.i("currentdate", currentDate.getDayOfWeek().toString()+" onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("currentdate", currentDate.getDayOfWeek().toString()+" onCreateView");
        Log.i("currentdate", String.valueOf(savedInstanceState == null));
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("currentdate", String.valueOf(savedInstanceState == null));
        Log.i("currentdate", currentDate.getDayOfWeek().toString()+" onViewCreated");
        tv1header = getView().findViewById(R.id.TLDay1header);
        tv2header = getView().findViewById(R.id.TLDay2header);
        tv3header = getView().findViewById(R.id.TLDay3header);
        tv4header = getView().findViewById(R.id.TLDay4header);
        tv5header = getView().findViewById(R.id.TLDay5header);

        calendar = Calendar.getInstance(Locale.FRANCE);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        createButtonListener();
        drawTimetable();
        Log.i("setExtentedViewO", String.valueOf(extendedViewEnabled));
        updateDate();
    }

    /**
     * Set the timetable header button listener
     */
    private void createButtonListener() {
        int[] ids = {R.id.TLDay1header,R.id.TLDay2header,R.id.TLDay3header,R.id.TLDay4header,R.id.TLDay5header};
        getView().findViewById(R.id.buttonNext).setOnClickListener(view -> {
            if (weekMode){
                currentDate = currentDate.plusWeeks(1);
                updateDate();
            }else{
                if (currentDate.getDayOfWeek() == DayOfWeek.FRIDAY){
                    currentDate = currentDate.plusDays(3);
                    updateDate();
                }else{
                    currentDate = currentDate.plusDays(1);
                }
                switchDisplayedDay(ids[currentDate.getDayOfWeek().getValue()-1]);
            }
        });
        getView().findViewById(R.id.buttonPrevious).setOnClickListener(view -> {
            if (weekMode){
                currentDate = currentDate.minusWeeks(1);
                updateDate();
            }else {
                if (currentDate.getDayOfWeek() == DayOfWeek.MONDAY){
                    currentDate = currentDate.minusDays(3);
                    updateDate();
                }else{
                    currentDate = currentDate.minusDays(1);
                }
                switchDisplayedDay(ids[currentDate.getDayOfWeek().getValue()-1]);
            }
        });
        tv1header.setOnClickListener(view -> toggleDayWeek(view.getId()));
        tv2header.setOnClickListener(view -> toggleDayWeek(view.getId()));
        tv3header.setOnClickListener(view -> toggleDayWeek(view.getId()));
        tv4header.setOnClickListener(view -> toggleDayWeek(view.getId()));
        tv5header.setOnClickListener(view -> toggleDayWeek(view.getId()));
    }

    /**
     * toggle between day and week view
     * @param tvId
     */
    private void toggleDayWeek(int tvId){
        weekMode = !weekMode;
        //remove all events
        removeEvents();
        try {
            //update ui to week mode
            for (int day=0;day< timetabledata.length();day++){
                //insert a single day
                insertDayEvents(timetabledata.getJSONArray(day),day);
            }
            if (!weekMode){
                //switched to day view
                //set the date to the selected date
                currentDate = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                switch (tvId){
                    case R.id.TLDay2header:{
                        currentDate = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
                        break;
                    }
                    case R.id.TLDay3header:{
                        currentDate = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
                        break;
                    }
                    case R.id.TLDay4header:{
                        currentDate = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
                        break;
                    }
                    case R.id.TLDay5header:{
                        currentDate = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
                        break;
                    }
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
            Log.e("toggleDayWeek",e.getLocalizedMessage());
        }
        switchDisplayedDay(tvId);
    }

    /**
     * show only one day and hide the other days
     * @param tvId the day to display
     */
    private void switchDisplayedDay(int tvId){
        FrameLayout DL1 = getView().findViewById(R.id.TLDay1);
        FrameLayout DL2 = getView().findViewById(R.id.TLDay2);
        FrameLayout DL3 = getView().findViewById(R.id.TLDay3);
        FrameLayout DL4 = getView().findViewById(R.id.TLDay4);
        FrameLayout DL5 = getView().findViewById(R.id.TLDay5);
        //switch between week and day view
        tv1header.setVisibility((tvId == R.id.TLDay1header || weekMode) ? View.VISIBLE : View.GONE);
        DL1.setVisibility((tvId == R.id.TLDay1header || weekMode) ? View.VISIBLE : View.GONE);
        tv2header.setVisibility((tvId == R.id.TLDay2header || weekMode) ? View.VISIBLE : View.GONE);
        DL2.setVisibility((tvId == R.id.TLDay2header || weekMode) ? View.VISIBLE : View.GONE);
        tv3header.setVisibility((tvId == R.id.TLDay3header || weekMode) ? View.VISIBLE : View.GONE);
        DL3.setVisibility((tvId == R.id.TLDay3header || weekMode) ? View.VISIBLE : View.GONE);
        tv4header.setVisibility((tvId == R.id.TLDay4header || weekMode) ? View.VISIBLE : View.GONE);
        DL4.setVisibility((tvId == R.id.TLDay4header || weekMode) ? View.VISIBLE : View.GONE);
        tv5header.setVisibility((tvId == R.id.TLDay5header || weekMode) ? View.VISIBLE : View.GONE);
        DL5.setVisibility((tvId == R.id.TLDay5header || weekMode) ? View.VISIBLE : View.GONE);
    }

    /**
     * Requests the new data and updates the headers
     */
    private void updateDate(){
        //remove previously displayed events
        removeEvents();
        //update the week description
        ((TextView)getView().findViewById(R.id.DayTV)).setText(getWeekDescription());
        //request the new data
        if (currentaction != null){
            //The action is defined
            switch (currentaction){
                case CLASS:
                    API.getClass(getSchoolyear(),getWeekNumber(),requestedData,this);
                    break;
                case ROOM:
                    API.getRoom(getSchoolyear(),getWeekNumber(),requestedData,this);
                    break;
                case TEACHER:
                    API.getTeacher(getSchoolyear(),getWeekNumber(),requestedData,this);
                    break;
                case STUDENT:
                    API.getStudent(getSchoolyear(),getWeekNumber(),requestedData,this);
                    break;
            }
        }else{
            //no action is defined
            Log.i("setExtentedViewU", String.valueOf(extendedViewEnabled));
            User user = ((MainActivity)getActivity()).currentUser;
            if (user != null){
                switch (user.getRole()){
                    case STUDENT:
                        ((MainActivity)getActivity()).displayStudent(user.getId(), extendedViewEnabled);
                        break;
                    case TEACHER:
                        ((MainActivity)getActivity()).displayTeacher(user.getUsername().substring(0,5).toUpperCase(), extendedViewEnabled);
                        break;
                    case STAFF:
                    case SEPAS:
                        break;
                }
            }
        }
        LocalDate tempdate = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        tv1header.setText(tempdate.getDayOfMonth()+".\n"+ getResources().getString(R.string.Monday));
        tempdate = tempdate.plusDays(1);
        tv2header.setText(tempdate.getDayOfMonth()+".\n"+ getResources().getString(R.string.Tuesday));
        tempdate = tempdate.plusDays(1);
        tv3header.setText(tempdate.getDayOfMonth()+".\n"+ getResources().getString(R.string.Wednesday));
        tempdate = tempdate.plusDays(1);
        tv4header.setText(tempdate.getDayOfMonth()+".\n"+ getResources().getString(R.string.Thursday));
        tempdate = tempdate.plusDays(1);
        tv5header.setText(tempdate.getDayOfMonth()+".\n"+ getResources().getString(R.string.Friday));

    }

    /**
     * Gets the current displayed schoolyear
     * @return the schoolyear
     */
    private String getSchoolyear(){
        int currentyear = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).getYear();
        if (currentDate.getMonth().getValue()<8){
            currentyear--;
        }
        return currentyear + "-" + (currentyear+1);
    }

    /**
     * Gets the current week of the year
     * @return
     */
    private int getWeekNumber(){
        //get the date the user wants to be displayed
        calendar.setTime(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Log.i("Api week", String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Gets a textual description of the displayed week
     * @return text describing the selected week
     */
    private String getWeekDescription(){
        LocalDate start = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return start.getDayOfMonth()+"."+start.getMonthValue()+"."+start.getYear()+"-"+end.getDayOfMonth()+"."+end.getMonthValue()+"."+end.getYear();
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case TEACHER:
            case ROOM:
            case CLASS:{
                prossessTimetable(response);
                break;
            }
            case STUDENT:{
                prossessTimetable(response);
            }
        }
    }

    /**
     * draws the hour labels
     */
    protected void drawTimetable(){
        //draw the time labels
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

    /**
     * inserts all event during a single day
     * @param daylessons a jsonarray containing all lessons
     * @param day the selected day
     */
    protected void insertDayEvents(JSONArray daylessons,int day){
        try {
            if (daylessons.length()!=0){
                //events recieved
                JSONArray firstlesson = daylessons.getJSONArray(0);
                Log.i("insertDayEvents",day + " " + (firstlesson.length()));
                //check if the first lesson is empty(if holidays it would not) and if it is not empty check if it is holiday
                if (firstlesson.length()==0 || !firstlesson.getJSONObject(0).getBoolean("is_holiday")){
                    //school
                    insertSchooldayEvents(daylessons,day);
                }else{
                    //holiday
                    insertHoliday(firstlesson.getJSONObject(0),day);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts holidays to a day
     * @param info a JsonObject containing holiday information
     * @param day
     * @throws JSONException
     */
    private void insertHoliday(JSONObject info,int day) throws JSONException {
        drawHolidayEvent(day,info.getString("color"),info.getString("name"));
    }

    /**
     * inserts all school events during a single day
     * @param daylessons a jsonarray containing all lessons
     * @param day the selected day
     */
    private void insertSchooldayEvents(JSONArray daylessons,int day){
        Log.i("TAG", "insertSchooldayEvents: ");
        HashMap<String,String> extensions = new HashMap<>();
        try {
            //loop through every timeslot starting at the last course
            for (int hour = daylessons.length()-1; hour>=0; hour--){
                JSONArray concurrentarray = daylessons.getJSONArray(hour);//get the array containing all concurrent hours
                //loop through the concurrent lessons
                for (int lessoncount=concurrentarray.length()-1; lessoncount>=0; lessoncount--){
                    //update endtime if nessesary
                    if (extensions.get(((JSONObject)concurrentarray.get(lessoncount)).getString("id_lesson")) != null) {
                        //this lesson does not end at the saved end
                        ((JSONObject)concurrentarray.get(lessoncount)).put("end",extensions.get(((JSONObject)concurrentarray.get(lessoncount)).getString("id_lesson")));
                        //remove the extension
                        extensions.remove(((JSONObject)concurrentarray.get(lessoncount)).getString("id_lesson"));
                    }
                    //update hashmap
                    if (!((JSONObject) concurrentarray.get(lessoncount)).getString("fi_parent_lesson").equals("null")){
                        //lesson belongs to other lesson
                        //save it into the extensions
                        extensions.put(((JSONObject)concurrentarray.get(lessoncount)).getString("fi_parent_lesson"),((JSONObject)concurrentarray.get(lessoncount)).getString("end"));
                        //remove the extension so it does not get displayed twice
                        ((JSONObject)concurrentarray.get(lessoncount)).put("hidden",true);
//                        concurrentarray.remove(lessoncount);
                    }
                }
                //Draw the timeslot
                Log.i("timeslotcon", String.valueOf(daylessons));
                drawTimeslot(concurrentarray,day);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Draws a timeslot
     * @param concurrentarray an array contaning one or multiple lessons
     * @param dayIndex the day
     * @throws JSONException
     */
    private void drawTimeslot(JSONArray concurrentarray, int dayIndex) throws JSONException {
        LinearLayout parent = getView().findViewById(R.id.timetableLayout);
        FrameLayout dayLayout = (FrameLayout) parent.getChildAt(dayIndex+1);
        LinearLayout timeslotLayout = new LinearLayout(getContext());
        timeslotLayout.setGravity(Gravity.CLIP_HORIZONTAL);
        timeslotLayout.setOrientation(LinearLayout.HORIZONTAL);
        int minstartheight = Integer.MAX_VALUE;
        int maxEndTime = Integer.MIN_VALUE;
        int startheight = 0;
        for (int lessoncount = 0; lessoncount < concurrentarray.length(); lessoncount++){
            //get single classes
            JSONObject schoolclass = new JSONObject(concurrentarray.getString(lessoncount));
            //get the dimensions
            int[] startime = Arrays.stream(schoolclass.getString("begin").split(":")).mapToInt(Integer::parseInt).toArray();
            int[] endtime = Arrays.stream(schoolclass.getString("end").split(":")).mapToInt(Integer::parseInt).toArray();
            startheight = Math.round((startime[0]-startHour+startime[1]/60f)*hourHeight);
            int endTime = Math.round((endtime[0]-startHour+endtime[1]/60f)*hourHeight);
            minstartheight = Math.min(minstartheight,startheight);
            maxEndTime = Math.max(maxEndTime,endTime);
        }
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, maxEndTime-minstartheight);
        layoutParams.topMargin = startheight;
        timeslotLayout.setLayoutParams(layoutParams);
        for (int lessoncount = 0; lessoncount < concurrentarray.length(); lessoncount++){
            JSONObject schoolclass = new JSONObject(concurrentarray.getString(lessoncount));
            if (!schoolclass.has("hidden")){
                String classe = getStringfromJsonArray(schoolclass.getJSONArray("classe"));
                String teacher = getStringfromJsonArray(schoolclass.getJSONArray("teacher"));
                String subject = getStringfromJsonArray(schoolclass.getJSONArray("subject"));
                String room = getStringfromJsonArray(schoolclass.getJSONArray("room"));
                int[] endtime = Arrays.stream(schoolclass.getString("end").split(":")).mapToInt(Integer::parseInt).toArray();
                int[] startime = Arrays.stream(schoolclass.getString("begin").split(":")).mapToInt(Integer::parseInt).toArray();
                int ownStartHeight = Math.round((startime[0]-startHour+startime[1]/60f)*hourHeight);
                int duration = Math.round((Math.min(endtime[0],endHour)+endtime[1]/60f)*hourHeight)-Math.round((startime[0]+startime[1]/60f)*hourHeight);
                LinearLayout mainLayout;
                if (weekMode){
                    mainLayout = drawEvent1x4(schoolclass.getString("begin"),schoolclass.getString("end"), schoolclass.getString("color"), classe, teacher, subject, room,ownStartHeight-minstartheight,schoolclass.getBoolean("is_online"));
                }else{
                    mainLayout = drawEvent2x2(schoolclass.getString("begin"),schoolclass.getString("end"), schoolclass.getString("color"), classe, teacher, subject, room,ownStartHeight-minstartheight,schoolclass.getBoolean("is_online"));
                }
                TableRow.LayoutParams layoutParamsEvent = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, duration);
                layoutParamsEvent.weight = 1f;
                mainLayout.setLayoutParams(layoutParamsEvent);
                mainLayout.setOnClickListener(view -> {
                    try {
                        ((MainActivity)getActivity()).displayDetails(schoolclass.getInt("id_lesson"),getSchoolyear(), extendedViewEnabled,classe,subject,schoolclass.getString("begin"),schoolclass.getString("end"),schoolclass.getString("date"));
                        Log.i("enumerstudent", String.valueOf(schoolclass.getInt("id_lesson")));
                    } catch (JSONException e) {
                        Log.e("Event_setListener",schoolclass.toString());
                    }
                });
                timeslotLayout.addView(mainLayout);
            }

        }
        //add layout to day
        dayLayout.addView(timeslotLayout);
    }

    /**
     * Deletes all events displayed
     */
    protected void removeEvents(){
        LinearLayout parent = getView().findViewById(R.id.timetableLayout);
        for (int dayIndex = 0; dayIndex<5; dayIndex++){
            FrameLayout dayLayout = (FrameLayout) parent.getChildAt(dayIndex+1);
            dayLayout.removeAllViews();
        }

    }

    /**
     * turns a json array into a string
     * @param arr a json array
     * @return the array items concatenated as String
     * @throws JSONException
     */
    private String getStringfromJsonArray(JSONArray arr) throws JSONException {
        return arr.join("/").replace("\"","");
    }

    /**
     * Creates a visual representation of a lesson in 2*2 layout
     * @param startTime the start time
     * @param endTime the end time
     * @param color the color
     * @param classText the class(es)
     * @param teacherAbb the teacher(s) abbreviated username(s)
     * @param branchName the branch(es)
     * @param roomAbb the room(s)
     * @param parentOffset the vertical offset
     * @param is_online is the lesson online
     * @return the representation of the lesson
     */
    protected LinearLayout drawEvent2x2(String startTime, String endTime, String color, String classText, String teacherAbb, String branchName, String roomAbb,int parentOffset,boolean is_online){

        int[] startime = Arrays.stream(startTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int[] endtime = Arrays.stream(endTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int startheight = Math.round((startime[0]-startHour+startime[1]/60f)*hourHeight);
        int duration = Math.round((Math.min(endtime[0],endHour)+endtime[1]/60f)*hourHeight)-Math.round((startime[0]+startime[1]/60f)*hourHeight);

        //create new entry
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setGravity(Gravity.CLIP_HORIZONTAL);
        mainLayout.setBackgroundResource(R.drawable.coursebackground);
        ((GradientDrawable) mainLayout.getBackground()).setColor(Color.parseColor(color));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, duration);
        //set the vertical offset
        //substract the offset the parent has to the top of the day
        layoutParams.topMargin = startheight-parentOffset;
        mainLayout.setLayoutParams(layoutParams);

        LinearLayout ln1 = new LinearLayout(getContext());
        ln1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1f));
        ln1.setOrientation(LinearLayout.VERTICAL);

        ln1.addView(createTextView(classText, (int) Math.ceil(classText.split("/", -1).length/2f),is_online));
        ln1.addView(createTextView(branchName, (int) Math.ceil(branchName.split("/", -1).length/2f),false));

        LinearLayout ln2 = new LinearLayout(getContext());
        ln2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1f));
        ln2.setOrientation(LinearLayout.VERTICAL);

        ln2.addView(createTextView(teacherAbb, (int) Math.ceil(teacherAbb.split("/", -1).length/2f),false));
        ln2.addView(createTextView(roomAbb, (int) Math.ceil(roomAbb.split("/", -1).length/2f),false));

        mainLayout.addView(ln1);
        mainLayout.addView(ln2);
        mainLayout.setPadding(10,10,10,10);
        return mainLayout;
    }

    /**
     * Creates a visual representation of a lesson in 1*4 layout
     * @param startTime the start time
     * @param endTime the end time
     * @param color the color
     * @param classText the class(es)
     * @param teacherAbb the teacher(s) abbreviated username(s)
     * @param branchName the branch(es)
     * @param roomAbb the room(s)
     * @param parentOffset the vertical offset
     * @param is_online is the lesson online
     * @return the representation of the lesson
     */
    protected LinearLayout drawEvent1x4(String startTime, String endTime, String color, String classText, String teacherAbb, String branchName, String roomAbb,int parentOffset,boolean is_online){

        Log.i("Timeslot", "drawEvent1x4");
        int[] startime = Arrays.stream(startTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int[] endtime = Arrays.stream(endTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int startheight = Math.round((startime[0]-startHour+startime[1]/60f)*hourHeight);
        int duration = Math.round((Math.min(endtime[0],endHour)+endtime[1]/60f)*hourHeight)-Math.round((startime[0]+startime[1]/60f)*hourHeight);
        //the layout on which you are working

        //create new entry
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setGravity(Gravity.CLIP_VERTICAL);
        mainLayout.setWeightSum(4f);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundResource(R.drawable.coursebackground);
        ((GradientDrawable) mainLayout.getBackground()).setColor(Color.parseColor(color));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, duration);
        //set the vertical offset
        //substract the offset the parent has to the top of the day
        layoutParams.topMargin = startheight-parentOffset;
        mainLayout.setLayoutParams(layoutParams);

        mainLayout.addView(createTextView(classText, (int) Math.ceil(classText.split("/", -1).length/2f),is_online));
        mainLayout.addView(createTextView(branchName, (int) Math.ceil(branchName.split("/", -1).length/2f),false));
        mainLayout.addView(createTextView(teacherAbb, (int) Math.ceil(teacherAbb.split("/", -1).length/2f),false));
        mainLayout.addView(createTextView(roomAbb, (int) Math.ceil(roomAbb.split("/", -1).length/2f),false));

        mainLayout.setPadding(10,10,10,10);
//        dayLayout.addView(mainLayout);
        return mainLayout;
    }

    /**
     * Draws an holiday event
     * @param dayIndex the day of the eva??nt
     * @param color the color
     * @param name the name
     */
    protected void drawHolidayEvent(int dayIndex, String color, String name){
        LinearLayout parent = getView().findViewById(R.id.timetableLayout);
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

    /**
     * Creates a TextView with the given text
     * @param text the text to display
     * @param lines the lines to use
     * @param add_camera add a camera symbol
     * @return
     */
    protected TextView createTextView(String text,int lines,boolean add_camera){
        TextView tw = new TextView(getContext());
        tw.setTextSize(10f);
        tw.setAutoSizeTextTypeUniformWithConfiguration(1,13,1, TypedValue.COMPLEX_UNIT_DIP);
        tw.setTypeface(null, Typeface.BOLD);
        tw.setGravity(Gravity.CENTER);
        if (add_camera){
            tw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_videocam_24, 0, 0, 0);
        }
        tw.setText(text, TextView.BufferType.SPANNABLE);
//        Spannable spannable = (Spannable) tw.getText();
        Pattern pattern = Pattern.compile("\\*(.*)\\*");
        SpannableStringBuilder ssb = new SpannableStringBuilder( tw.getText() );
        Matcher matcher = pattern.matcher( tw.getText() );
        int matchesSoFar = 0;
        while( matcher.find() )
        {
            int start = matcher.start() - (matchesSoFar * 2);
            int end = matcher.end() - (matchesSoFar * 2);
            CharacterStyle span0 = new ForegroundColorSpan(getResources().getColor(R.color.strikethrough));
            StrikethroughSpan span = new StrikethroughSpan();
            ssb.setSpan( span, start + 1, end - 1, 0 );
            ssb.setSpan( span0, start + 1, end - 1, 0 );
            ssb.delete(start, start + 1);
            ssb.delete(end - 2, end -1);
            matchesSoFar++;
        }

        tw.setText(ssb);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1f;
        tw.setLines(lines);
        tw.setLayoutParams(params);
        return tw;
    }

    /**
     * Processes the API response. If there is no session it logs in.
     * @param response The API response
     */
    private void prossessTimetable(ServerResponse response){
        try {
            Log.i("Login", String.valueOf(response.status));
            switch (response.status){
                case 200:{
                    int[] ids = {R.id.TLDay1header,R.id.TLDay2header,R.id.TLDay3header,R.id.TLDay4header,R.id.TLDay5header};
                    switchDisplayedDay(ids[currentDate.getDayOfWeek().getValue()-1]);
                    timetabledata = new JSONArray(response.response);//get the array containing the single days
                    for (int day=0;day< timetabledata.length();day++){
                        //insert a single day
                        Log.i("Day",String.valueOf(day));
                        Log.i("Day",timetabledata.getJSONArray(day).toString());
                        insertDayEvents(timetabledata.getJSONArray(day),day);
                    }
                    break;
                }
                case 400:{
                    //no valid session
                    //login with saved credentials
                    API.autologin(((MainActivity)getActivity()).currentUser,this);
                    break;
                }
                case 404:
                case 500:{
                    Toast.makeText(getActivity(),"Error: "+new JSONObject(response.response).getString("error"), Toast.LENGTH_LONG).show();
                    break;
                }
                default:{
                    Toast.makeText(getActivity(),"Not connected to the internet", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the data
     * @param action the requested action
     * @param data the requested data
     */
    public void setData(Actions action,String data){
        currentaction = action;
        requestedData = data;
        Log.i("setExtentedViewD", String.valueOf(extendedViewEnabled));
        extendedViewEnabled = false;
        try {
            //try to remove all the events from the timetable
            removeEvents();
        }catch (NullPointerException npe){
            Log.e("setData",npe.toString());
        }

        Log.i("setData",action + " " + data);
        switch (action){
            case CLASS:
                API.getClass(getSchoolyear(),getWeekNumber(),data,this);
                break;
            case ROOM:
                API.getRoom(getSchoolyear(),getWeekNumber(),data,this);
                break;
            case TEACHER:
                API.getTeacher(getSchoolyear(),getWeekNumber(),data,this);
                break;
            case STUDENT:
                API.getStudent(getSchoolyear(),getWeekNumber(),data,this);
                break;
        }
    }

    /**
     * Sets if the timetable belongs to the user
     * @param is_own_Timetable does it belong to the user
     */
    public void setExtentedView(boolean is_own_Timetable) {
        Log.i("setExtentedViewS", String.valueOf(is_own_Timetable));
        this.extendedViewEnabled = is_own_Timetable;
    }

}