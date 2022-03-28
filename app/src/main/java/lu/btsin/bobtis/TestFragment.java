package lu.btsin.bobtis;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getDrawable;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment implements AsyncResponse {

    private boolean is_allowed_create_absences;
    private int id_lesson;
    private EditText eTTitle;
    private EditText eTContent;
    private ImageButton removeButton;
    private ImageButton updateButton;
    private ImageButton editButton;
    private Test test;
    private boolean editingMode = false;

    public TestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestFragment newInstance(String param1, String param2) {
        TestFragment fragment = new TestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eTTitle = getView().findViewById(R.id.ETTestTitle);
        eTContent = getView().findViewById(R.id.ETTestContent);
        removeButton = getView().findViewById(R.id.removeTestButton);
        updateButton = getView().findViewById(R.id.addUpdateTestButton);
        eTTitle.setEnabled(false);
        eTContent.setEnabled(false);
        removeButton.setOnClickListener(v -> API.removeTest(test.getId_test(),this));
        updateButton.setOnClickListener(v -> {
            if (editingMode) {
                setTest();
            } else {
                enableEditing(!editingMode);
            }
        });
        API.getTest(id_lesson,this);
    }

    private void enableEditing(boolean editable){
        editingMode = editable;
        eTTitle.setEnabled(editable);
        eTContent.setEnabled(editable);
        if (test == null && editable){
            eTTitle.setText("");
            eTContent.setText("");
            updateButton.setBackgroundResource(R.drawable.ic_baseline_save_24);
            eTTitle.requestFocus();
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            manager.showSoftInput(eTTitle, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void setTest() {
        if (test != null) {
            //update the test
            API.updateTest(test.getId_test(),eTContent.getText().toString(),eTTitle.getText().toString(),this);
        }else{
            //create the test
            API.addTest(id_lesson,eTContent.getText().toString(),eTTitle.getText().toString(),this);
        }
        enableEditing(false);
    }


    public void setData(int id_lesson) {
        this.id_lesson = id_lesson;
    }

    private void initUI(Test test){
        if (test != null) {
            eTTitle.setText(test.getTitle());
            eTContent.setText(test.getContent());
            removeButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
            updateButton.setBackgroundResource(R.drawable.ic_baseline_save_24);
        }else{
            eTTitle.setText("no test");
            eTContent.setText("no test");
            removeButton.setVisibility(View.INVISIBLE);
            updateButton.setVisibility(View.VISIBLE);

            updateButton.setBackgroundResource(R.drawable.ic_baseline_add_circle_outline_24);
        }
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case TEST:
                Log.i("TEST_fragment","HOMEWORKS");
                switch (response.status){
                    case 200:{
                        //there was a test
                        try {
                            JSONObject json = new JSONObject(response.response);
                            test = Test.getTest(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 404:{
                        break;
                    }
                    default:{
                        test = null;
                        Toast.makeText(getActivity(),response.response,Toast.LENGTH_LONG).show();
                    }
                }
                initUI(test);
                break;
            case TEST_REMOVE:
                switch (response.status){
                    case 200:
                        test = null;
                        initUI(null);
                        enableEditing(false);
                        Toast.makeText(getContext(), "Test removed", Toast.LENGTH_SHORT).show();
                        updateButton.setBackgroundResource(R.drawable.ic_baseline_add_circle_outline_24);
                        break;
                    default:
                        Toast.makeText(getContext(), response.response, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case TEST_UPDATE:
                switch (response.status){
                    case 200:
                        try {
                            JSONObject json = new JSONObject(response.response);
                            test = Test.getTest(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        initUI(test);
                        updateButton.setBackgroundResource(R.drawable.ic_baseline_edit_24);
                        Toast.makeText(getContext(), "Test updated", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getContext(), response.response, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case TEST_ADD:
                break;
        }
    }
}