package lu.btsin.bobtis;

import android.content.Context;
import android.os.Bundle;
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
        //load the user from SharedPreferences
        currentUser = User.loadUser(getSharedPreferences("UserPreferences", Context.MODE_PRIVATE));
        if (currentUser == null){
            //there was no user
            //switch to login
            switchFragment(loginFragment);
            ((Timetable_fragment)timetableFragment).setExtentedView(false);
        }else{
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
                    //display the users personal timetable
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
                    //display the own homework
                    switchFragment(myHomeworkfragment);
                    break;
                }
                case R.id.nav_ownAbsences:{
                    //display the own absences
                    switchFragment(myAbsencefragment);
                    break;
                }
                case R.id.nav_login:{
                    //switch to the login
                    switchFragment(loginFragment);
                    break;
                }
                default:{
                    //An item from the preferences has been pressed
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
            //update the header
            TextView nhn = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_name);
            nhn.setText(currentUser.getFirstname()+" "+currentUser.getName());
            TextView nhc = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_class);
            nhc.setText(currentUser.getClasse());
            TextView nhi = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_iam);
            nhi.setText(currentUser.getUsername());
            //update the links
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

    /**
     * switches to another fragment
     * @param fr the fragment to switch to
     */
    protected void switchFragment(Fragment fr){
        FragmentManager fragmentManager = getSupportFragmentManager();
        //switch fragment and enable back button functionality
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fr, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
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
            //search button is pressed.
            FragmentContainerView fcv = findViewById(R.id.fragment_container_view);
            if (fcv.getFragment() instanceof Search_Fragment){
                //in case of search been displayed, go to timetable
                switchFragment(timetableFragment);
            }else{
                //only switch if user is allowed to see schedules
                if (enableSearch){
                    switchFragment(searchFragment);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays a teacher timetable
     * @param teacher the teachers username's first 5 letters
     * @param extendedView is this the users timetable
     */
    public void displayTeacher(String teacher,boolean extendedView){
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.TEACHER, teacher);
        ((Timetable_fragment)timetableFragment).setExtentedView(extendedView);
        switchFragment(timetableFragment);
    }

    /**
     * Displays a teacher timetable
     * @param room The rooms name
     */
    public void displayRoom(String room){
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.ROOM,room);
        ((Timetable_fragment)timetableFragment).setExtentedView(false);
        switchFragment(timetableFragment);
    }

    /**
     * Displays a teacher timetable
     * @param classe The class's name
     */
    public void displayClass(String classe){
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.CLASS,classe);
        ((Timetable_fragment)timetableFragment).setExtentedView(false);
        switchFragment(timetableFragment);
    }

    /**
     * Displays a Students timetable
     * @param studentid the students id
     * @param extendedView is this the users timetable
     */
    public void displayStudent(int studentid,boolean extendedView){
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.STUDENT, String.valueOf(studentid));
        ((Timetable_fragment)timetableFragment).setExtentedView(extendedView);
        switchFragment(timetableFragment);
    }

    public void displayDetails(int lessonId, String schoolyear, boolean extendedViewEnabled, String className, String branchName, String startTime, String endTime, String date){
        ((DetailsFragment)detailsfragment).setData(schoolyear,lessonId,extendedViewEnabled,className,branchName,startTime,endTime,date);
        switchFragment(detailsfragment);
    }

    @Override
    public void processFinish(ServerResponse response) {
        //handle login
        switch (response.status){
            case 200:
                //update the user
                currentUser.updateUser(getSharedPreferences("UserPreferences", Context.MODE_PRIVATE),response.response);
                //load the new users preferences
                loadPreferences();
                //enable the search if the user is allowed
                setEnableSearch();
                //create the Links saved in the preferences and enable the own Absences and own Homework links
                setLinks();
                //refresh the Navigation header data
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

    /**
     * Loads the users preferred links
     */
    protected void loadPreferences(){
        currentUser.loadClasses(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE));
        currentUser.loadRooms(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE));
        currentUser.loadTeachers(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE));
    }

    /**
     * updates the links and sets the own homework and absences links
     */
    protected void setLinks(){
        MenuItem classmenu = navigationView.getMenu().findItem(R.id.class_timetable);
        MenuItem roommenu = navigationView.getMenu().findItem(R.id.room_timetables);
        MenuItem teachermenu = navigationView.getMenu().findItem(R.id.teacher_timetable);
        //remove existing links
        classmenu.getSubMenu().clear();
        roommenu.getSubMenu().clear();
        teachermenu.getSubMenu().clear();
        //fill in the new links
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
        //update the own homework and absences links
        myHomeworkfragment = new MyHomeworkFragment();
        ((MyHomeworkFragment)myHomeworkfragment).setData(currentUser.getId());
        myAbsencefragment = new MyAbsencesFragment();
        ((MyAbsencesFragment)myAbsencefragment).setData(currentUser.getId());
    }

    /**
     * Sets if the search button should work
     */
    protected void setEnableSearch(){
        enableSearch = currentUser!=null && (currentUser.has_Permission(User.Right.SCHEDULE_CLASSES) || currentUser.has_Permission(User.Right.SCHEDULE_ROOMS) || currentUser.has_Permission(User.Right.SCHEDULE_TEACHERS));
    }

    /**
     * adds a Class to the users preferences
     * @param data the class's name
     */
    public void addClass(String data) {
        currentUser.addClass(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE),data);
        MenuItem classmenu = navigationView.getMenu().findItem(R.id.class_timetable);
        classmenu.getSubMenu().add(MenuGroup.CLASSES.ordinal(), Menu.NONE, Menu.NONE, data).setIcon(getDrawable(R.drawable.ic_baseline_group_24));
    }

    /**
     * adds a room to the users preferences
     * @param data the room's name
     */
    public void addRoom(String data) {
        currentUser.addRoom(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE),data);
        MenuItem roommenu = navigationView.getMenu().findItem(R.id.room_timetables);
        roommenu.getSubMenu().add(MenuGroup.ROOMS.ordinal(), Menu.NONE, Menu.NONE, data).setIcon(getDrawable(R.drawable.ic_outline_meeting_room_24));
    }

    /**
     * adds a teacher to the users preferences
     * @param data an Array containing the teachers full name and his username's the first 5 letters
     */
    public void addTeacher(String[] data) {
        currentUser.addTeacher(getSharedPreferences(currentUser.getUsername(), Context.MODE_PRIVATE),data);
        MenuItem teachermenu = navigationView.getMenu().findItem(R.id.teacher_timetable);
        teachermenu.getSubMenu().add(MenuGroup.TEACHERS.ordinal(), Menu.NONE, Menu.NONE, data[1]).setIcon(getDrawable(R.drawable.ic_baseline_person_24));
    }

}