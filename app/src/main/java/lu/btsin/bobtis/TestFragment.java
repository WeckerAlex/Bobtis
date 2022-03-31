package lu.btsin.bobtis;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

public class TestFragment extends Fragment implements AsyncResponse {

    private int id_lesson;
    private EditText eTTitle;
    private EditText eTContent;
    private ImageButton removeButton;
    private ImageButton updateButton;
    private Test test;
    private State currentState;
    private static boolean editing_allowed;

    private enum State{
        TEST_DISPLAY,
        TEST_EDIT,
        NO_TEST_DISPLAY,
        NO_TEST_EDIT
    }

    public TestFragment() {
        // Required empty public constructor
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
        if (!editing_allowed){
            removeButton.setVisibility(View.INVISIBLE);
            updateButton.setVisibility(View.INVISIBLE);
        }
        removeButton.setOnClickListener(v -> API.removeTest(test.getId_test(),this));
        updateButton.setOnClickListener(v -> {
            switch (currentState){
                case TEST_EDIT:
                case NO_TEST_EDIT:
                    setTest();
                    break;
                case TEST_DISPLAY:
                    setState(State.TEST_EDIT);
                    break;
                case NO_TEST_DISPLAY:
                    setState(State.NO_TEST_EDIT);
                    break;
            }
        });
        API.getTest(id_lesson,this);
    }

    private void setState(State state){
        currentState = state;
        switch (state){
            case TEST_DISPLAY:
                Log.i("testaddresponse_setState", test.getTitle());
                Log.i("testaddresponse_setState",test.getContent());
                eTTitle.setEnabled(false);
                eTContent.setEnabled(false);
                eTTitle.setText(test.getTitle());
                eTContent.setText(test.getContent());
                updateButton.setBackgroundResource(R.drawable.ic_baseline_edit_24);
                if (editing_allowed){
                    removeButton.setVisibility(View.VISIBLE);
                }
                break;
            case TEST_EDIT:
                eTTitle.setEnabled(true);
                eTContent.setEnabled(true);
                updateButton.setBackgroundResource(R.drawable.ic_baseline_save_24);
                removeButton.setVisibility(View.VISIBLE);
                break;
            case NO_TEST_DISPLAY:
                eTTitle.setText(getResources().getString(R.string.noTest));
                eTContent.setText(getResources().getString(R.string.noTest));
                eTTitle.setEnabled(false);
                eTContent.setEnabled(false);
                updateButton.setBackgroundResource(R.drawable.ic_baseline_add_circle_outline_24);
                removeButton.setVisibility(View.INVISIBLE);
                break;
            case NO_TEST_EDIT:
                eTTitle.setText("");
                eTContent.setText("");
                eTTitle.setEnabled(true);
                eTContent.setEnabled(true);
                removeButton.setVisibility(View.INVISIBLE);
                updateButton.setBackgroundResource(R.drawable.ic_baseline_save_24);
                eTTitle.requestFocus();
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                manager.showSoftInput(eTTitle, InputMethodManager.SHOW_IMPLICIT);
                break;
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
    }


    public void setData(int id_lesson,boolean editing_allowed) {
        this.id_lesson = id_lesson;
        this.editing_allowed = editing_allowed;
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
                            setState(State.TEST_DISPLAY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 404:{
                        setState(State.NO_TEST_DISPLAY);
                        break;
                    }
                    default:{
                        test = null;
                        setState(State.NO_TEST_DISPLAY);
                        Toast.makeText(getActivity(),response.response,Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case TEST_REMOVE:
                switch (response.status){
                    case 200:
                        test = null;
                        setState(State.NO_TEST_DISPLAY);
                        Toast.makeText(getContext(), "Test removed", Toast.LENGTH_SHORT).show();
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
                            setState(State.TEST_DISPLAY);
                        } catch (JSONException e) {
                            setState(State.NO_TEST_DISPLAY);
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), "Test updated", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getContext(), response.response, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case TEST_ADD:
                Log.i("testaddresponse", String.valueOf(response.status));
                Log.i("testaddresponse",response.response);
                switch (response.status) {
                    case 200: {
                        //there was a test
                        try {
                            JSONObject json = new JSONObject(response.response);
                            test = Test.getTest(json);
                            setState(State.TEST_DISPLAY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 400:
                    case 401:
                    case 404:
                        Toast.makeText(getActivity(), response.response, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Log.i("testaddresponse_default", String.valueOf(response.status));
                        Log.i("testaddresponse_default", response.response);
                        test = null;
                        setState(State.NO_TEST_DISPLAY);
                        Toast.makeText(getActivity(), response.response, Toast.LENGTH_LONG).show();
                        break;
                }
        }
    }
}