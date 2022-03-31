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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    /**
     * Sets the navigation button listener, passes data to the fragments and hides navigation items if necessary
     * @param view
     * @param savedInstanceState
     */
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
        //editing is allowed if it is the own timetable and the user has the permission to edit or the user has the permission to edit all absences
        boolean editing_allowed_absences = (user.has_Permission(User.Right.MARK_TEACHES) && extendedViewEnabled)|| user.has_Permission(User.Right.MARK_CLASSES);
        //editing is allowed if it is the own timetable and the user has the permission to edit
        boolean editing_allowed = (user.has_Permission(User.Right.MARK_TEACHES) && extendedViewEnabled);
        infofragment.setData(classname,branchname,starttime,endtime,day);
        absencesfragment.setData(schoolyear,id_lesson, editing_allowed_absences);
        homeworkfragment.setData(schoolyear,id_lesson,editing_allowed);
        testfragment.setData(id_lesson,editing_allowed);

        if (!extendedViewEnabled){
            //the lesson is not in the own timetable
            //disable the navigation to everything the user is not allowed to see
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

    /**
     * Initializes the data
     * @param schoolyear The displayed schoolyear
     * @param id_lesson The lesson's id
     * @param extendedViewEnabled does the timetable belong to the user
     * @param classname the class name
     * @param branchname the branchname
     * @param starttime the time the lesson starts
     * @param endtime the time the lesson ends
     * @param date the lessons date
     */
    public void setData(String schoolyear,int id_lesson,boolean extendedViewEnabled,String classname,String branchname,String starttime,String endtime,String date) {
        this.schoolyear = schoolyear;
        this.id_lesson = id_lesson;
        this.extendedViewEnabled = extendedViewEnabled;
        this.classname = classname;
        this.branchname = branchname;
        this.starttime = starttime;
        this.endtime = endtime;
        this.day = date;
    }
}