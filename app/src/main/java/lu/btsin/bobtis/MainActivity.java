package lu.btsin.bobtis;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private Fragment timetableFragment = new Timetable_fragment();
    private Fragment loginFragment = new Login_Fragment();
    private Fragment searchFragment = new Search_Fragment();
    //private SharedPreferences prefs;
    public User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //prefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        currentUser = User.loadUser(getSharedPreferences("UserPreferences", Context.MODE_PRIVATE));
        if (currentUser == null){
            switchFragment(loginFragment);
        }else{
            Log.i("User_loaded",currentUser.toString());
            //logging in at startup
            //Todo create ar
            API.autologin(currentUser,null);
        }
        initNavbar();
        setNavbarHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentUser!=null && (currentUser.has_Permission(User.Right.SCHEDULE_CLASSES) || currentUser.has_Permission(User.Right.SCHEDULE_ROOMS) || currentUser.has_Permission(User.Right.SCHEDULE_TEACHERS))){
            getMenuInflater().inflate(R.menu.search_menu,menu);
        }
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
                //TODO: check right to access
                switch (item.getItemId()){
                    case R.id.personal_timetable: {
                        if (currentUser.getClasse()!=null){
                            displayClass(currentUser.getClasse());
                        }
                        break;
                    }
                    case R.id.nav_homework:{
                        Log.i("onNavigationItemSelected","nav_homework");
                        break;
                    }
                    case R.id.nav_absence:{
                        //LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        //View layoutt = inflater.inflate(R.layout.fragment_popup,navigationView);

                        new PopupFragment().show(getSupportFragmentManager(),"");
                        break;
                    }
                    case R.id.nav_settings:{
                        Log.i("Settings","drg");
                        break;
                    }
                    case R.id.nav_login:{
                        switchFragment(loginFragment);
                        break;
                    }
                    default:{
                        break;
                    }
                }
                DrawerLayout dl = findViewById(R.id.my_drawer_layout);
                dl.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    public void setNavbarHeader(){
//        if (prefs.contains("firstname") && prefs.contains("name") && prefs.contains("classe") && prefs.contains("username")){
        if (currentUser!= null && currentUser.getFirstname() != null && currentUser.getName()!=null && currentUser.getUsername()!=null){
            TextView nhn = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_name);
            nhn.setText(currentUser.getFirstname()+" "+currentUser.getName());
            TextView nhc = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_class);
            nhc.setText(currentUser.getClasse());
            TextView nhi = navigationView.getHeaderView(0).findViewById(R.id.navbar_header_iam);
            nhi.setText(currentUser.getUsername());
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
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else {
            FragmentContainerView fcv = findViewById(R.id.fragment_container_view);
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
        Log.i("displayTeacher",teacher);
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.TEACHER, teacher);
        switchFragment(timetableFragment);
    }

    public void displayRoom(String room){
        Log.i("Segue","Room: "+room);
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.ROOM,room);
        switchFragment(timetableFragment);
    }

    public void displayClass(String classe){
        Log.i("Segue","Class: "+classe);
        ((Timetable_fragment)timetableFragment).setData(Timetable_fragment.Actions.CLASS,classe);
        switchFragment(timetableFragment);
    }
}