package lu.btsin.bobtis;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InfoFragment extends Fragment {

    private String classname;
    private String branchname;
    private String starttime;
    private String endtime;
    private String day;
    private String schoolyear;
    private int id_lesson;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
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
        TextView twclasse = getView().findViewById(R.id.tv_class);
        TextView twbranch = getView().findViewById(R.id.tv_branch);
        TextView twlesson = getView().findViewById(R.id.tv_lessonDay);
        TextView twperiod = getView().findViewById(R.id.tv_period);
        twclasse.setText(classname);
        twbranch.setText(branchname);
        twlesson.setText(day);
        twperiod.setText(starttime + " - " + endtime);
    }

    public void setData(String schoolyear,int id_lesson,boolean is_allowed_create_absences,String classname,String branchname,String starttime,String endtime,String date) {
        this.schoolyear = schoolyear;
        this.id_lesson = id_lesson;
        this.classname = classname;
        this.branchname = branchname;
        this.starttime = starttime;
        this.endtime = endtime;
        this.day = date;
    }
}