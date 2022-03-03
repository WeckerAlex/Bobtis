package lu.btsin.bobtis;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements AsyncResponse{

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private String user = "";
    private String pass = "";
    private int hourHeight = 200;
    private int startHour = 7;
    private int endHour = 20;
    private int week = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawTimetable();
        initNavbar();
        login(user,pass);
        //getSchoolyears();
        //getClasses("2021-2022");
        getClass("2021-2022",1,"B2IN");

    }

    protected void initNavbar(){
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.toString()){
                    case "Bobtis": {

                    }
                    case "Settings":{
                        goToActivity(Timetable.class);
                    }
                    default:{
                        System.out.println(item.toString());
                    }
                }
                return false;
            }
        });
    }

    public void goToActivity(Class targetClass){
        Intent switchActivityIntent = new Intent(this, targetClass);
        startActivity(switchActivityIntent);
    }

    protected void drawTimetable(){
        LinearLayout legend = findViewById(R.id.legend);
        for (int i = startHour;i<endHour;i++){
            TextView tw = new TextView(this);
            tw.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hourHeight);
            params.weight = 1f;
            tw.setLines(3);
            tw.setLayoutParams(params);
            tw.setText(i+":00"+"\n-\n"+(i+1)+":00");
            tw.setBackgroundResource(R.drawable.legend);
            legend.addView(tw);
        }
//        drawEvent(R.id.TLDay1,"7:00","15:00","#D31282","B2IN","BOUCH","PROJE","SC-02");
//        drawEvent(R.id.TLDay2,"11:00","13:00","#F31212","B2IN","BOUCH","PROJE","SC-03");
//        drawEvent(R.id.TLDay3,"9:00","12:00","#131212","B2IN","BOUCH","PROJE","SC-03");
//        drawEvent(R.id.TLDay4,"12:00","20:00","#D15212","B2IN","BOUCH","TEMPL","SC-03");
//        drawEvent(R.id.TLDay5,"8:00","16:00","#581212","B1IN","FISRO","PROJE","SC-03");
//        drawEvent(R.id.TLDay1,"16:00","25:00","#F31412","B2IN","BOUCH","PROJE","SC-01");
    }

    protected void insertDayEvents(JSONArray daylessons,int day){
        System.out.println("Day");
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
                        //TODO: draw the event

                        drawEvent(day,schoolclass.getString("begin"),schoolclass.getString("end"), schoolclass.getString("color"), schoolclass.getString("classe"), schoolclass.getString("teacher"), schoolclass.getString("subject"), schoolclass.getString("room"));

                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void drawEvent(int dayIndex,String startTime,String endTime,String color,String classText,String teacherAbb,String branchName,String roomAbb){
        LinearLayout parent = (LinearLayout) findViewById(R.id.timetableLayout);
        FrameLayout dayLayout = (FrameLayout) parent.getChildAt(dayIndex+1);
        System.out.println("DayLayout "+dayIndex+":"+dayLayout);
        int[] startime = Arrays.stream(startTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int[] endtime = Arrays.stream(endTime.split(":")).mapToInt(Integer::parseInt).toArray();
        int startheight = Math.round((startime[0]-startHour+startime[1]/60f)*hourHeight);
        int duration = Math.round((Math.min(endtime[0],endHour)+endtime[1]/60f)*hourHeight)-Math.round((startime[0]+startime[1]/60f)*hourHeight);
        System.out.println(startheight);
        System.out.println(duration);
        //the layout on which you are working
//        FrameLayout day1Layout = (FrameLayout) findViewById(view);//The whole day


        //create new entry
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setGravity(Gravity.CLIP_HORIZONTAL);
        mainLayout.setBackgroundResource(R.drawable.coursebackground);
        mainLayout.getBackground().setTint(Color.parseColor(color));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, duration);
        layoutParams.topMargin = startheight;
        mainLayout.setLayoutParams(layoutParams);

        LinearLayout ln1 = new LinearLayout(this);
        ln1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1f));
        ln1.setOrientation(LinearLayout.VERTICAL);
        ln1.addView(createTextView(classText));
        ln1.addView(createTextView(branchName));

        LinearLayout ln2 = new LinearLayout(this);
        ln2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1f));
        ln2.setOrientation(LinearLayout.VERTICAL);
        ln2.addView(createTextView(teacherAbb));
        ln2.addView(createTextView(roomAbb));

        mainLayout.addView(ln1);
        mainLayout.addView(ln2);
        mainLayout.setPadding(10,10,10,10);
        dayLayout.addView(mainLayout);
    }

    public static String dateTextfromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE d");
        return date.format(formatter);
    }

    protected TextView createTextView(String text){
        TextView tw = new TextView(this);
        tw.setText(text);
        tw.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        tw.setLines(1);
        tw.setLayoutParams(params);
        return tw;
    }

    private void setDayView(){

    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void login(String username, String password){
        API task =  new API();
        task.delegate = this;
        task.execute("login",username,password);
    }

    protected void getSchoolyears(){
        API task =  new API();
        task.delegate = this;
        task.execute("schoolyears");
    }

    protected void getClasses(String schoolyear){
        API task =  new API();
        task.delegate = this;
        task.execute("classes",schoolyear);
    }

    protected void getClass(String schoolyear,int week,String requestedclass){
        API task =  new API();
        task.delegate = this;
        task.execute("class",schoolyear,week,requestedclass);
        setTitle(requestedclass);
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case LOGIN:
                prossessLogin(response);
                break;
            case SCHOOLYEARS:
                prossessSchoolyear(response);
                break;
            case CLASSES:
                prossessClasses(response);
                break;
            case CLASS:
                prossessClass(response);
                break;
        }
    }

    private void prossessLogin(ServerResponse response){
        try {
            String message;
            JSONObject json = new JSONObject(response.response);
            switch (response.status){
                case 200:{
                    message = "You are logged in as "+json.getString("type");
                    break;
                }
                case 500:
                case 400:
                case 404:
                case 412:{
                    message = "Error: "+json.getString("error");
                    break;
                }
                default:{
                    message = "Something went wrong";
                }
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prossessSchoolyear(ServerResponse response){
        try {
            String message;
            switch (response.status){
                case 200:{
                    JSONArray json = new JSONArray(response.response);
                    message = "Retrieved the schoolyears";
                    for (int i=0;i< json.length();i++){
                        Toast.makeText(this, json.getString(i), Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case 400:
                case 500:{
                    JSONObject json = new JSONObject(response.response);
                    message = "Error: "+json.getString("error");
                    break;
                }
                default:{
                    message = "Something went wrong";
                }
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prossessClasses(ServerResponse response){
        try {
            String message;
            switch (response.status){
                case 200:{
                    JSONArray json = new JSONArray(response.response);
                    message = "Retrieved the schoolyears";
                    for (int i=0;i< json.length();i++){
                        Toast.makeText(this, json.getString(i), Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case 400:
                case 500:{
                    JSONObject json = new JSONObject(response.response);
                    message = "Error: "+json.getString("error");
                    break;
                }
                default:{
                    message = "Something went wrong";
                }
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prossessClass(ServerResponse response){
        try {
            String message;
            switch (response.status){
                case 200:{
                    JSONArray dayarray = new JSONArray(response.response);//get the array containing the single days
                    for (int day=0;day< dayarray.length();day++){
                        //insert a single day
                        insertDayEvents(dayarray.getJSONArray(day),day);
                    }
                    break;
                }
                case 400:
                case 500:{
                    JSONObject json = new JSONObject(response.response);
                    message = "Error: "+json.getString("error");
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    break;
                }
                default:{
                    message = "Something went wrong";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Delete this. Used to test API
    private void getAllNames(JSONObject obj){
        for (Iterator<String> it = obj.keys(); it.hasNext(); ) {
            String name = it.next();
            System.out.println(name);
        }
    }
}