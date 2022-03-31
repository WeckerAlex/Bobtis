package lu.btsin.bobtis;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private final Fragment timetableFragment = new Timetable_fragment();
    private final Fragment loginFragment = new Login_Fragment();
    private final Fragment searchFragment = new Search_Fragment();
    private final Fragment detailsfragment = new DetailsFragment();
    private Fragment myHomeworkfragment;
    private Fragment myAbsencefragment;
    public User currentUser;
    private boolean enableSearch = false;
    private enum MenuGroup{
        CLASSES,ROOMS,TEACHERS
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //prefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        currentUser = User.loadUser(getSharedPreferences("UserPreferences", Context.MODE_PRIVATE));
        Log.i("creating timetableFragment","onCreate");
        if (currentUser == null){
            switchFragment(loginFragment);
            ((Timetable_fragment)timetableFragment).setExtentedView(false);
        }else{
            Log.i("User_loaded",currentUser.toString());
            //logging in at startup
            API.autologin(currentUser,this);
            //set the timetable fragment to display all information if event pressed
            ((Timetable_fragment)timetableFragment).setExtentedView(true);
            myHomeworkfragment = new MyHomeworkFragment();
            ((MyHomeworkFragment)myHomeworkfragment).setData(currentUser.getId());
            myAbsencefragment = new MyAbsencesFragment();
            ((MyAbsencesFragment)myAbsencefragment).setData(currentUser.getId());
        }
        initNavbar();
        setNavbarHeader();
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

        //make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navView);
        //side menu listener
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.personal_timetable: {
                    switch (currentUser.getRole()){
                        case TEACHER:{
                            displayTeacher(currentUser.getUsername().substring(0,5).toUpperCase(),true);
                            break;
                        }
                        case STUDENT:{
                            if (currentUser.getClasse()!=null){
                                displayStudent(currentUser.getId(),true);
                            }
                        }
                    }
                    break;
                }
                case R.id.nav_homework:{
                    switchFragment(myHomeworkfragment);
                    break;
                }
                case R.id.nav_ownAbsences:{
                    switchFragment(myAbsencefragment);
                    break;
                }
                case R.id.nav_login:{
                    Log.i("Navdraweriteminserttest", String.valueOf(item.getItemId()));
                    switchFragment(loginFragment);
                    break;
                }
                default:{
                    Log.i("Navdraweriteminserttest",item.getTitle().toString());
                    Log.i("Navdraweriteminserttest", String.valueOf(item.getIcon()));
                    switch (MenuGroup.values()[item.getGroupId()]){
                        case CLASSES:
                            displayClass(item.getTitle().toString());
                            break;
                        case ROOMS:
                            displayRoom(item.getTitle().toString());
                            break;
                        case TEACHERS:
                            displayTeacher(currentUser.getTeacherShortName(item.getTitle().toString()),false);
                            break;
                    }
                    break;
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
    }

    /**
     * set the menu header information and enable items
     */
    public void setNavbarHeader(){
        if (currentUser!= null && currentUser.getFirstname() != null && currentUser.getName()!=null && currentUser.getUsername()!=null){
            TextView nhn = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_name);
            nhn.setText(currentUser.getFirstname()+" "+currentUser.getName());
            TextView nhc = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_class);
            nhc.setText(currentUser.getClasse());
            TextView nhi = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_iam);
            nhi.setText(currentUser.getUsername());
            if (currentUser.has_Permission(User.Right.SCHEDULE_OWN)){
                navigationView.getMenu().getItem(0).setVisible(true);
            }
            if (currentUser.has_Permission(User.Right.HOMEWORK_OWN)){
                navigationView.getMenu().getItem(1).setVisible(true);
            }
            if (currentUser.has_Permission(User.Right.ABSENCES_OWN)){
                navigationView.getMenu().getItem(2).setVisible(true);
            }
        }
    }

    protected void switchFragment(Fragment fr){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fr, null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // name can be null
                .commit();
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //app bar icon is pressed
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else {
            //search buttonis pressed.
            FragmentContainerView fcv = findViewById(R.id.fragment_container_view);
            if (fcv.getFragment() instanceof Search_Fragment){
                //in case of search been displayed, go to timetable
                switchFragment(timetableFragment);
            }else{
                if (enableSearch){
                    switchFragment(searchFragment);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayTeacher(String teacher,boolean extendedView){
        Log.i("displayTeacher",teacher+" "+extendedView);
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.TEACHER, teacher);
        ((Timetable_fragment)timetableFragment).setExtentedView(extendedView);
        switchFragment(timetableFragment);
    }

    public void displayRoom(String room){
        Log.i("Segue","Room: "+room);
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.ROOM,room);
        ((Timetable_fragment)timetableFragment).setExtentedView(false);
        switchFragment(timetableFragment);
    }

    public void displayClass(String classe){
        Log.i("Segue","Class: "+classe);
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.CLASS,classe);
        ((Timetable_fragment)timetableFragment).setExtentedView(false);
        switchFragment(timetableFragment);
    }

    public void displayStudent(int studentid,boolean extendedView){
        Log.i("Segue","Student: "+studentid);
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.STUDENT, String.valueOf(studentid));
        ((Timetable_fragment)timetableFragment).setExtentedView(extendedView);
        switchFragment(timetableFragment);
    }

    public void displayDetails(int lessonId, String schoolyear, boolean extendedViewEnabled, String className, String branchName, String startTime, String endTime, String date){
        Log.i("Segue","Absences: "+lessonId);
        ((DetailsFragment)detailsfragment).setData(schoolyear,lessonId,extendedViewEnabled,className,branchName,startTime,endTime,date);
        switchFragment(detailsfragment);
    }

    @Override
    public void processFinish(ServerResponse response) {
        //handle login
        switch (response.status){
            case 200:
                currentUser.updateUser(getSharedPreferences("UserPreferences", Context.MODE_PRIVATE),response.response);
                loadPreferences();
                setEnableSearch();
                setLinks();
                setNavbarHeader();
                break;
            case 400:
            case 404:
            case 412:{
                Toast.makeText(getApplicationContext(),"Please log in again",Toast.LENGTH_LONG).show();
                break;
            }
            case 500:
                Toast.makeText(getApplicationContext(),"Error connecting to server",Toast.LENGTH_LONG).show();
                break;
        }
    }

    protected void loadPreferences(){
        currentUser.loadClasses(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE));
        currentUser.loadRooms(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE));
        currentUser.loadTeachers(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE));
    }

    protected void setLinks(){
        MenuItem classmenu = navigationView.getMenu().findItem(R.id.class_timetable);
        MenuItem roommenu = navigationView.getMenu().findItem(R.id.room_timetables);
        MenuItem teachermenu = navigationView.getMenu().findItem(R.id.teacher_timetable);
        classmenu.getSubMenu().clear();
        roommenu.getSubMenu().clear();
        teachermenu.getSubMenu().clear();
        ArrayList<String> data = currentUser.getClasses();
        for (String entry : data) {
            classmenu.getSubMenu().add(MenuGroup.CLASSES.ordinal(), Menu.NONE, Menu.NONE, entry).setIcon(getDrawable(R.drawable.ic_baseline_group_24));
        }
        data = currentUser.getRooms();
        for (String entry : data) {
            roommenu.getSubMenu().add(MenuGroup.ROOMS.ordinal(), Menu.NONE, Menu.NONE, entry).setIcon(getDrawable(R.drawable.ic_outline_meeting_room_24));
        }
        data = currentUser.getTeachers();
        for (String entry : data) {
            teachermenu.getSubMenu().add(MenuGroup.TEACHERS.ordinal(), Menu.NONE, Menu.NONE, entry).setIcon(getDrawable(R.drawable.ic_baseline_person_24));
        }
        myHomeworkfragment = new MyHomeworkFragment();
        ((MyHomeworkFragment)myHomeworkfragment).setData(currentUser.getId());
        myAbsencefragment = new MyAbsencesFragment();
        ((MyAbsencesFragment)myAbsencefragment).setData(currentUser.getId());
    }

    protected void setEnableSearch(){
        enableSearch = currentUser!=null && (currentUser.has_Permission(User.Right.SCHEDULE_CLASSES) || currentUser.has_Permission(User.Right.SCHEDULE_ROOMS) || currentUser.has_Permission(User.Right.SCHEDULE_TEACHERS));
    }

    public void addClass(String data) {
        //TODO:add data to navdrawer
        currentUser.addClass(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE),data);
        MenuItem classmenu = navigationView.getMenu().findItem(R.id.class_timetable);
        classmenu.getSubMenu().add(MenuGroup.CLASSES.ordinal(), Menu.NONE, Menu.NONE, data).setIcon(getDrawable(R.drawable.ic_baseline_group_24));
    }

    public void addRoom(String data) {
        //TODO:add data to navdrawer
        currentUser.addRoom(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE),data);
        MenuItem roommenu = navigationView.getMenu().findItem(R.id.room_timetables);
        roommenu.getSubMenu().add(MenuGroup.ROOMS.ordinal(), Menu.NONE, Menu.NONE, data).setIcon(getDrawable(R.drawable.ic_outline_meeting_room_24));
    }

    public void addTeacher(String[] data) {
        //TODO:add data to navdrawer
        currentUser.addTeacher(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE),data);
        MenuItem teachermenu = navigationView.getMenu().findItem(R.id.teacher_timetable);
        teachermenu.getSubMenu().add(MenuGroup.TEACHERS.ordinal(), Menu.NONE, Menu.NONE, data[1]).setIcon(getDrawable(R.drawable.ic_baseline_person_24));
    }

}