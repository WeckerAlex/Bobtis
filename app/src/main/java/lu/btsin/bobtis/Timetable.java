package lu.btsin.bobtis;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;

public class Timetable extends Activity {

    //private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable);
//
//        navigationView = (NavigationView) findViewById(R.id.navView2);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                System.out.println(item.getTitle());
//                return false;
//            }
//        });
    }
}
