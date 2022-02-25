package lu.btsin.bobtis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private String user = "sdg";
    private String pass = "esfg"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                System.out.println(item.getTitle());
                return false;
            }
        });

        drawTimetable();
        initWidgets();
        new ApiCall().execute("login");
    }

    private void initWidgets() {
//        monthDayText = findViewById(R.id.monthDayText);
//        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
//        hourListView = findViewById(R.id.hourListView);
    }

    protected void drawTimetable(){
        //the layout on which you are working
        LinearLayout day1Layout = (LinearLayout) findViewById(R.id.TLDay1);

        //create new entry
        TableRow tr = new TableRow(this);
        tr.setGravity(Gravity.CLIP_HORIZONTAL);
        tr.setBackgroundResource(R.drawable.coursebackground);
        tr.getBackground().setTint(Color.parseColor("#D31212"));

        LinearLayout ln1 = new LinearLayout(this);
        ln1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
        TextView twclass = createTextView("twclass");
        TextView twbranch = createTextView("twbranch");
        ln1.setOrientation(LinearLayout.VERTICAL);
        ln1.addView(twclass);
        ln1.addView(twbranch);

        LinearLayout ln2 = new LinearLayout(this);
        ln2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
        TextView twteacher = createTextView("twteacher");
        TextView twbroom = createTextView("twroom");
        ln2.setOrientation(LinearLayout.VERTICAL);
        ln2.addView(twteacher);
        ln2.addView(twbroom);


        tr.addView(ln1);
        tr.addView(ln2);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        day1Layout.addView(tr);
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