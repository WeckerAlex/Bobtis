package lu.btsin.bobtis;

import static lu.btsin.bobtis.API.APIEndpoint.*;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Absenses_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Absenses_fragment extends Fragment implements AsyncResponse {

    public static final int BLACK = Color.BLACK;
    private ArrayList<Student> data;
    private static ListAdapter adapter;

    public Absenses_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lessonId Parameter 1.
     * @param schoolyear
     * @return A new instance of fragment PopupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Absenses_fragment newInstance(int lessonId, String schoolyear) {
        Bundle bundle = new Bundle();
        bundle.putInt("id_lesson",lessonId);
        bundle.putString("schoolyear",schoolyear);
        Absenses_fragment fragment = new Absenses_fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_absenses,container,false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("enumerstudent", "onCreate");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init(){
        ListView list = getView().findViewById(R.id.studentList);
        Log.i("enumerstudent_init_view", "list is null: " +(list==null));
        Log.i("enumerstudent_init_view", "context is null: " +(getContext()==null));
        if (adapter == null){
            data = new ArrayList<>();
            adapter = new ListAdapter<String>() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Student student = data.get(position);
                    Log.i("getView",data.size() +", "+student.getName()+ " " +student.getFirstname());
                    TextView tw = new TextView(parent.getContext());
                    LinearLayout.LayoutParams layoutParamsText = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    layoutParamsText.setMargins(1,1,1,1);
                    layoutParamsText.weight = 1;

                    tw.setPadding(1,0,1,0);
                    tw.setLayoutParams(layoutParamsText);
                    tw.setText(student.getFirstname()+" "+student.getName());

                    LinearLayout ll = new LinearLayout(parent.getContext());
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

                    Button buttonVtt = new Button(parent.getContext());
                    LinearLayout.LayoutParams layoutParamsShorten = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    layoutParamsShorten.setMargins(0,3,0,3);
                    buttonVtt.setLayoutParams(layoutParamsShorten);
                    buttonVtt.setVisibility(View.INVISIBLE);
                    buttonVtt.setText("VTT");
                    ll.addView(buttonVtt);

                    ImageButton buttonAbsence = new ImageButton(parent.getContext());
                    buttonAbsence.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                    LinearLayout.LayoutParams layoutParamsInsert = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                    layoutParamsInsert.setMargins(0,3,0,3);
                    buttonAbsence.setLayoutParams(layoutParamsInsert);
                    buttonAbsence.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_post_add_24));
                    ll.addView(buttonAbsence);

                    if (((MainActivity)getActivity()).currentUser.has_Permission(User.Right.SCHEDULE_STUDENTS)){
                        ll.setOnClickListener(view -> ((MainActivity)getActivity()).displayStudent(student.getId()));
                    }
                    return ll;
                }

                @Override
                public boolean filterEntry(String entry, CharSequence constraint) {
                    return ((entry).toUpperCase().contains(((String) constraint).toUpperCase()));
                }
            };
        }
        adapter.setData(data);
        list.setAdapter(adapter);
    }

    public void setData(String schoolyear,int id_lesson) {
        getStudents(schoolyear, id_lesson);
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
                switch (response.status){
                    case 200:{
                        try {
                            data.clear();
                            JSONArray json = new JSONArray(response.response);
                            for (int i = 0; i < json.length(); i++) {
                                Student student = Student.getStudent(json.getJSONObject(i));
                                Log.i("enumerstudent", student.getFirstname());
                                data.add(student);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.i("enumerstudent", data.toString());
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:{
                        Toast.makeText(getActivity(),response.response,Toast.LENGTH_LONG).show();
                    }
                }
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

    protected void getStudents(String schoolyear, int idLesson){
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