package lu.btsin.bobtis;

import static lu.btsin.bobtis.API.APIEndpoint.*;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Absenses_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Absenses_fragment extends Fragment implements AsyncResponse {

    // TODO: Rename and change types of parameters
    private int id_teacher;

    private View view;
    private ArrayList<String> data = new ArrayList<>();
    private static ListAdapter adapter;

    public Absenses_fragment() {
        // Required empty public constructor
        for (int i = 0; i < 50; i++) {
            data.add(String.valueOf(i));
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Absenses_fragment newInstance(String param1, String param2) {
        Absenses_fragment fragment = new Absenses_fragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        getDialog().setTitle(getString(R.string.app_name));
        //id_teacher = getArguments().getInt("id_teacher");
        Log.i("passdatatest", String.valueOf(id_teacher));
        View view = inflater.inflate(R.layout.fragment_absenses,container,false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_teacher = getArguments().getInt("id_teacher");
        }
        Log.i("passdatatest", String.valueOf(id_teacher));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("passdatatest", String.valueOf(id_teacher));
        init();
    }

    public void init(){
        Log.i("passdatatest", String.valueOf(id_teacher));
        Log.i("initinit","initinit");
        ListView list = getView().findViewById(R.id.studentList);
        if (adapter == null){
            adapter = new ListAdapter<String>() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Log.i("initinit","Drawing " + position);
                    TextView tw = new TextView(getContext());
                    LinearLayout.LayoutParams layoutParamsText = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    layoutParamsText.setMargins(1,1,1,1);
                    layoutParamsText.weight = 1;

                    tw.setPadding(1,0,1,0);
                    tw.setLayoutParams(layoutParamsText);
                    tw.setText(data.get(position));

                    LinearLayout ll = new LinearLayout(getContext());
                    ll.setOnClickListener(view -> Log.i("initinit","Pressed "+position));
                    ll.setGravity(Gravity.CLIP_HORIZONTAL);
                    ll.setBackgroundResource(R.drawable.coursebackground);
                    ((GradientDrawable) ll.getBackground()).setColor(Color.parseColor("#FFFF99"));
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    ll.setPadding(10,0,10,0);
                    layoutParams.setMargins(0,3,0,3);
                    ll.setLayoutParams(layoutParams);

                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.addView(tw);
                    Button buttonVtt = new Button(getContext());
                    buttonVtt.setText("VTT");
                    ll.addView(buttonVtt);

                    Button buttonAbsence = new Button(getContext());
                    buttonAbsence.setText("Absence");
                    ll.addView(buttonAbsence);
                    return ll;
                }

                @Override
                public boolean filterEntry(String entry, CharSequence constraint) {
                    Log.i("filterEntry",entry+" - "+constraint);
                    return ((entry).toUpperCase().contains(((String) constraint).toUpperCase()));
                }
            };
        }
        list.setAdapter(adapter);
        adapter.setData(data);
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case AREASONS:
                Log.i("Absenses_fragment","AREASONS");
                break;
            case TEACHER:
                Log.i("Absenses_fragment","TEACHER");
                break;
            case STUDENTS:
                Log.i("Absenses_fragment","STUDENTS");
                break;
            case ABSENCE_SPEED:
                Log.i("Absenses_fragment","ABSENCE_SPEED");
                break;
            case ABSENCE_UPDATE:
                Log.i("Absenses_fragment","ABSENCE_UPDATE");
                break;
            case ABSENCE_SHORTEN:
                Log.i("Absenses_fragment","ABSENCE_SHORTEN");
                break;
            case ABSENCE_REMOVE:
                Log.i("Absenses_fragment","ABSENCE_REMOVE");
                break;
        }
    }

    protected void getAbsenceReasons(){
        API task =  new API();
        task.delegate = this;
        task.execute(AREASONS);
    }

    protected void getTeacherTimetable(String schoolyear, int week, String id){
        API task =  new API();
        task.delegate = this;
        task.execute(TEACHER,schoolyear,week,id);
    }

    protected void getStudents(String schoolyear, String idLesson){
        API task =  new API();
        task.delegate = this;
        task.execute(STUDENTS,schoolyear,idLesson);
    }

    protected void setAbsenceSpeed(int id_lesson, int id_student){
        API task =  new API();
        task.delegate = this;
        task.execute(ABSENCE_SPEED,id_lesson,id_student);
    }

    protected void updateAbsence(int id_absence, String acomment, int fi_areason,String endTime){
        API task =  new API();
        task.delegate = this;
        task.execute(ABSENCE_UPDATE,id_absence,acomment,fi_areason,endTime);
    }

    protected void shortenAbsence(int id_absence){
        API task =  new API();
        task.delegate = this;
        task.execute(ABSENCE_SHORTEN,id_absence);
    }

    protected void removeAbsence(int id_absence){
        API task =  new API();
        task.delegate = this;
        task.execute(ABSENCE_REMOVE,id_absence);
    }

}