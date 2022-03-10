package lu.btsin.bobtis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
//    private String user = "";
//    private String pass = "";
//    private int hourHeight = 200;
//    private int startHour = 7;
//    private int endHour = 20;
//    private int week = 10;
    private ArrayList<String> availableSchoolYears;
    private ArrayList<String> availableClasses;
    private ArrayList<String> availableRooms;
    private ArrayList<String> availableTeachers;
    private String displayedTeacher="";
    private String displayedRoom="";
    private String displayedClass="";

    private Fragment timetableFragment = new Timetable_fragment();
    private Fragment loginFragment = new Login_Fragment();
    private Fragment searchFragment = new Search_Fragment();
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        if (!(prefs.contains("username") && prefs.contains("password"))){
            switchFragment(loginFragment);
        }else{
            //logging in at startup
            API.autologin(prefs, null);
        }
        initNavbar();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_baseline_search_24);
        if (prefs.contains("firstname") && prefs.contains("name") && prefs.contains("classe") && prefs.contains("username")){
            TextView nhn = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_name);
            nhn.setText(prefs.getString("firstname","")+" "+prefs.getString("name",""));
            TextView nhc = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_class);
            nhc.setText(prefs.getString("classe",""));
            TextView nhi = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_iam);
            nhi.setText(prefs.getString("username",""));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
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
                //TODO: adapt to new items
                switch (item.getItemId()){
                    case R.id.personal_timetable: {
                        Log.i("Menuit","sdg");
                        if (prefs.contains("classe")){
                            displayClass(prefs.getString("classe",""));
                        }
                        break;
                    }
                    case R.id.nav_homework:{
                        System.out.println("Homework");
                        break;
                    }
                    case R.id.nav_settings:{
                        System.out.println("Settings");
                        break;
                    }
                    case R.id.nav_login:{
                        switchFragment(loginFragment);
                        break;
                    }
                    default:{
                        Log.i("Nav", String.valueOf(item.getTitle()));
                        setTitle("requestedclass");
                    }
                }
                return false;
            }
        });
    }

    protected void switchFragment(Fragment fr){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fr, null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // name can be null
                .commit();
        DrawerLayout dl = findViewById(R.id.my_drawer_layout);
        dl.closeDrawer(GravityCompat.START);
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
        }else {
            FragmentContainerView fcv = findViewById(R.id.fragment_container_view);
            Log.i("Fragment", String.valueOf(((Fragment)fcv.getFragment()).getTag()));
            if (fcv.getFragment() instanceof Timetable_fragment){
                switchFragment(searchFragment);
            }
            if (fcv.getFragment() instanceof Search_Fragment){
                switchFragment(timetableFragment);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void displayTeacher(String teacher){
        displayedTeacher=teacher;
        displayedRoom="";
        displayedClass="";
        String lastname = teacher.split(" ")[0].substring(0,3).toUpperCase();
        String firstname = teacher.split(" ")[1].substring(0,2).toUpperCase();
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.TEACHER,lastname + firstname);
        switchFragment(timetableFragment);
    }

    public void displayRoom(String room){
        Log.i("Segue","Room: "+room);
        displayedTeacher="";
        displayedRoom=room;
        displayedClass="";
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.ROOM,room);
        switchFragment(timetableFragment);
    }

    public void displayClass(String classe){
        Log.i("Segue","Class: "+classe);
        displayedTeacher="";
        displayedRoom="";
        displayedClass=classe;
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.CLASS,classe);
        switchFragment(timetableFragment);
    }

//    protected void login(String username, String password){
//        API task =  new API();
//        task.delegate = this;
//        task.execute("login",username,password);
//    }

//    private void getSchoolyears(){
//        API task =  new API();
//        task.delegate = this;
//        task.execute("schoolyears");
//
//    }

//    private void getRooms(String schoolyear){
//        API task =  new API();
//        task.delegate = this;
//        task.execute("rooms",schoolyear);
//    }

//    private void getTeachers(String schoolyear){
//        API task =  new API();
//        task.delegate = this;
//        task.execute("teachers",schoolyear);
//    }

//    private void getClasses(String schoolyear){
//        API task =  new API();
//        task.delegate = this;
//        task.execute("classes",schoolyear);
//    }

//    @Override
//    public void processFinish(ServerResponse response) {
//        System.out.println(response.status);
//        if (response.status != 412){
//            switch (response.endpoint){
//                case LOGIN:
//                    //initial login response
//                    prossessLogin(response);
//                    break;
//                case SCHOOLYEARS:
//                case CLASSES:
//                case ROOMS:
//                case TEACHERS:
//                    prossessResponseArray(response);
//                    break;
//            }
//        }else {
//            System.out.println("Auto logging in");
//            SharedPreferences prefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
//            API task =  new API();
//            task.delegate = this;
//            task.execute("login",prefs.getString("username",""),prefs.getString("password",""),prefs);
//        }
//    }

//    private void prossessLogin(ServerResponse response){
//        try {
//            String message;
//            JSONObject json = new JSONObject(response.response);
//            switch (response.status){
//                case 200:{
//                    fillSideNavigation();
//                    message = "You are logged in as "+json.getString("type");
//                    break;
//                }
//                case 500:
//                case 400:
//                case 404:
//                case 412:{
//                    message = "Error: "+json.getString("error");
//                    break;
//                }
//                default:{
//                    message = "Something went wrong";
//                }
//            }
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    private void prossessResponseArray(ServerResponse response){
//        Log.i("prossessResponseArray", String.valueOf(response.status));
//        try {
//            String message;
//            switch (response.status){
//                case 200:{
//                    JSONArray json = new JSONArray(response.response);
//                    Menu menu = null;
//                    Log.i("Log",response.endpoint.toString());
//                    switch (response.endpoint){
//                        case SCHOOLYEARS:
//                            menu = findViewById(R.id.class_timetable);
//                            availableSchoolYears = jsontoArrayList(json);
//                            Log.i("Log", String.valueOf(availableSchoolYears.size()));
//                            break;
//                        case CLASSES:
//                            menu = navigationView.getMenu().getItem(2).getSubMenu();
//                            availableClasses = jsontoArrayList(json);
//                            Log.i("Log", String.valueOf(availableClasses.size()));
//                            for (int i = 0; i <availableClasses.size(); i++) {
//                                Log.i("Log", String.valueOf(menu != null));
//                                if (menu != null){
//                                    Log.i("Log","Inserting " + availableClasses.get(i));
//
//                                    menu.add(availableClasses.get(i));
//                                }
//                            }
//                            break;
//                        case ROOMS:
//                            availableRooms = jsontoArrayList(json);
//                            Log.i("Log", String.valueOf(availableRooms.size()));
//                            break;
//                        case TEACHERS:
//                            menu = findViewById(R.id.teacher_timetable);
//                            availableTeachers = jsontoArrayList(json);
//                            Log.i("Log", String.valueOf(availableTeachers.size()));
//                            break;
//                    }
//
//
//                    message = "Retrieved the "+response.endpoint;
//                    break;
//                }
//                case 400:
//                case 500:{
//                    JSONObject json = new JSONObject(response.response);
//                    message = "Error: "+json.getString("error");
//                    break;
//                }
//                default:{
//                    message = "Something went wrong";
//                }
//            }
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    private ArrayList<String> jsontoArrayList(JSONArray json){
//        ArrayList<String> arr = new ArrayList<>();
//        try {
//            for (int i = 0; i < json.length(); i++) {
//                arr.add(json.getString(i));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return arr;
//    }
}