package lu.btsin.bobtis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class DetailsFragment extends Fragment {

    private boolean extendedViewEnabled;
    private String classname;
    private String branchname;
    private String starttime;
    private String endtime;
    private String day;
    private String schoolyear;
    private int id_lesson;
    private final InfoFragment infofragment = new InfoFragment();
    private final Absenses_fragment absencesfragment = new Absenses_fragment();
    private final HomeworkFragment homeworkfragment = new HomeworkFragment();
    private final TestFragment testfragment = new TestFragment();

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switchFragment(infofragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //details are opened
        //set navigation listeners
        Button infobutton = getView().findViewById(R.id.infobutton);
        Button absencebutton = getView().findViewById(R.id.absencesbutton);
        Button homeworkbutton = getView().findViewById(R.id.homeworkbutton);
        Button testbutton = getView().findViewById(R.id.testsbutton);

        infobutton.setOnClickListener(v -> switchFragment(infofragment));
        absencebutton.setOnClickListener(v -> switchFragment(absencesfragment));
        homeworkbutton.setOnClickListener(v -> switchFragment(homeworkfragment));
        testbutton.setOnClickListener(v -> switchFragment(testfragment));

        User user = ((MainActivity) getActivity()).currentUser;
        boolean editing_allowed = (user.has_Permission(User.Right.MARK_TEACHES) && extendedViewEnabled)|| user.has_Permission(User.Right.MARK_CLASSES);
        infofragment.setData(classname,branchname,starttime,endtime,day);
        absencesfragment.setData(schoolyear,id_lesson, editing_allowed);
        homeworkfragment.setData(schoolyear,id_lesson,editing_allowed);
        testfragment.setData(id_lesson,editing_allowed);

        if (!extendedViewEnabled){
            //the lesson is not in the own timetable
            if (!user.has_Permission(User.Right.ABSENCES_CLASSES)) {
                absencebutton.setVisibility(View.GONE);
            }
            if (!user.has_Permission(User.Right.HOMEWORK_CLASSES)) {
                homeworkbutton.setVisibility(View.GONE);
            }
            if (!user.has_Permission(User.Right.TEST_CLASSES)){
                testbutton.setVisibility(View.GONE);
            }
        }

        //on creation display the course information
        switchFragment(infofragment);
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
        this.extendedViewEnabled = is_allowed_create_absences;
        this.classname = classname;
        this.branchname = branchname;
        this.starttime = starttime;
        this.endtime = endtime;
        this.day = date;
    }
}