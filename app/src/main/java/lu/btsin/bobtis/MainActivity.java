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

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements AsyncResponse{

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private String user = "sdg";
    private String pass = "esfg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawTimetable();
        setTitlebar();
        initNavbar();
        login("","");
        getSchoolyears();
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

    private void setTitlebar() {
//        monthDayText = findViewById(R.id.monthDayText);
//        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
//        hourListView = findViewById(R.id.hourListView);
    }

    protected void drawTimetable(){
        LinearLayout legend = findViewById(R.id.legend);
        for (int i = 0;i<24;i++){
            TextView tw = new TextView(this);
            tw.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
            params.weight = 1f;
            tw.setLines(3);
            tw.setLayoutParams(params);
            tw.setText(i+":00"+"\n-\n"+(i+1)+":00");
            tw.setBackgroundResource(R.drawable.legend);
            legend.addView(tw);
        }
//        drawEvent(R.id.TLDay1,1000,"#D31282","B2IN","BOUCH","PROJE","SC-02",1000);
//        drawEvent(R.id.TLDay2,500,"#F31212","B2IN","BOUCH","PROJE","SC-03",600);
//        drawEvent(R.id.TLDay2,500,"#131212","B2IN","BOUCH","PROJE","SC-03",0);
//        drawEvent(R.id.TLDay3,700,"#D15212","B2IN","BOUCH","TEMPL","SC-03",500);
//        drawEvent(R.id.TLDay4,200,"#581212","B1IN","FISRO","PROJE","SC-03",900);
//        drawEvent(R.id.TLDay5,200,"#F31412","B2IN","BOUCH","PROJE","SC-01",300);
        drawEvent(R.id.TLDay1,600,"#D31282","B2IN","BOUCH","PROJE","SC-02",0);
        drawEvent(R.id.TLDay2,1200,"#F31212","B2IN","BOUCH","PROJE","SC-03",0);
        drawEvent(R.id.TLDay3,1800,"#131212","B2IN","BOUCH","PROJE","SC-03",0);
        drawEvent(R.id.TLDay4,2000,"#D15212","B2IN","BOUCH","TEMPL","SC-03",0);
        drawEvent(R.id.TLDay5,2050,"#581212","B1IN","FISRO","PROJE","SC-03",0);
        drawEvent(R.id.TLDay1,2000,"#F31412","B2IN","BOUCH","PROJE","SC-01",0);
    }

    protected void drawEvent(int view,int height,String color,String classText,String teacherAbb,String branchName,String roomAbb,int offset){
        //the layout on which you are working
        FrameLayout day1Layout = (FrameLayout) findViewById(view);

        //create new entry
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setGravity(Gravity.CLIP_HORIZONTAL);
        mainLayout.setBackgroundResource(R.drawable.coursebackground);
        mainLayout.getBackground().setTint(Color.parseColor(color));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, height);
        layoutParams.topMargin = offset;
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
        day1Layout.addView(mainLayout);
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
            switch (response.status){
                case 200:{
                    message = "You are logged in as "+response.response.getString("type");
                    break;
                }
                case 500:
                case 400:
                case 404:
                case 412:{
                    message = "Error: "+response.response.getString("error");
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
                    message = "Retrieved the schoolyears";
//                    response.response.toJSONArray();
                    break;
                }
                case 400:
                case 500:{
                    message = "Error: "+response.response.getString("error");
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

    }

    private void prossessClass(ServerResponse response){

    }

    //Delete this. Used to test API
    private void getAllNames(JSONObject obj){
        for (Iterator<String> it = obj.keys(); it.hasNext(); ) {
            String name = it.next();
            System.out.println(name);
        }
    }
}