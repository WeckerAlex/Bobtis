package lu.btsin.bobtis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;
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
        //new ApiCall().execute("login");
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
            tw.setText(i+":00"+"\n-\n"+(i+1)+":00");
            tw.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 100);
            params.weight = 1f;
            tw.setLines(3);
            tw.setLayoutParams(params);
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

    class ApiCall extends AsyncTask {

        private Object logincall(Object[] objects){

            return null;
        }

        private Object sendApiCall(String address, String data){
            try {
                URL url = new URL("https://ssl.ltam.lu/bobtis/api/"+address+".php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(data);
                out.flush();
                out.close();
                int status = con.getResponseCode();
                InputStreamReader isr;
                if (status >= 200 && status<300){
                    isr = new InputStreamReader(con.getInputStream());
                }else{
                    //400 || 404 || 412
                    isr = new InputStreamReader(con.getErrorStream());
                }
                switch (status){
                    case 400:{
                        sendApiCall("login","");
                    }
                }
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                System.out.println(content.toString());
            }catch (Exception e){
                System.out.println(e);
                System.out.println("--------------------------------------");
            }
            return null;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String data="";
            try {
                switch (objects[0].toString()){
                    case "login":{
                        data = "username="+URLEncoder.encode(objects[1].toString(), "UTF-8")+"&"+"password="+URLEncoder.encode(objects[2].toString(), "UTF-8");
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return sendApiCall(objects[0].toString(),data);
        }


    }

}