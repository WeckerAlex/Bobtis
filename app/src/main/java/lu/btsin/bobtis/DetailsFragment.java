package lu.btsin.bobtis;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DetailsFragment extends Fragment {

    private boolean is_allowed_create_absences;
    private String classname;
    private String branchname;
    private String starttime;
    private String endtime;
    private String day;
    private String schoolyear;
    private int id_lesson;
    private InfoFragment infofragment = new InfoFragment();
    private Absenses_fragment absencesfragment = new Absenses_fragment();
    private HomeworkFragment homeworkfragment = new HomeworkFragment();
    private TestFragment testfragment = new TestFragment();

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infofragment.setData(schoolyear,id_lesson,is_allowed_create_absences,classname,branchname,starttime,endtime,day);
        absencesfragment.setData(schoolyear,id_lesson,is_allowed_create_absences,classname,branchname,starttime,endtime,day);
        switchFragment(infofragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button infobutton = (Button) getView().findViewById(R.id.infobutton);
        Button absencebutton = (Button) getView().findViewById(R.id.absencesbutton);
        Button homeworkbutton = (Button) getView().findViewById(R.id.homeworkbutton);
        Button testbutton = (Button) getView().findViewById(R.id.testsbutton);

        infobutton.setOnClickListener(v -> switchFragment(infofragment));
        absencebutton.setOnClickListener(v -> switchFragment(absencesfragment));

        homeworkbutton.setOnClickListener(v -> switchFragment(homeworkfragment));
        testbutton.setOnClickListener(v -> switchFragment(testfragment));
        super.onViewCreated(view, savedInstanceState);
    }

    protected void switchFragment(Fragment fr){
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.details_container_view, fr, null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // name can be null
                .commit();
    }

    public void setData(String schoolyear,int id_lesson,boolean is_allowed_create_absences,String classname,String branchname,String starttime,String endtime,String date) {
        this.schoolyear = schoolyear;
        this.id_lesson = id_lesson;
        this.is_allowed_create_absences = is_allowed_create_absences;
        this.classname = classname;
        this.branchname = branchname;
        this.starttime = starttime;
        this.endtime = endtime;
        this.day = date;
    }
}