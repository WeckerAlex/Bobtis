package lu.btsin.bobtis;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;
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

    /**
     * Constructor
     */
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
        //disable input on creation
        eTTitle.setEnabled(false);
        eTContent.setEnabled(false);
        //if editing allowed add the buttons
        if (!editing_allowed){
            removeButton.setVisibility(View.INVISIBLE);
            updateButton.setVisibility(View.INVISIBLE);
        }
        removeButton.setOnClickListener(v -> API.removeTest(test.getId_test(),this));
        updateButton.setOnClickListener(v -> {
            switch (currentState){
                case TEST_EDIT:
                case NO_TEST_EDIT:
                    //save the test
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
        //Get the test
        API.getTest(id_lesson,this);
    }

    private void setState(State state){
        currentState = state;
        switch (state){
            case TEST_DISPLAY:
                //disable editing
                eTTitle.setEnabled(false);
                eTContent.setEnabled(false);
                //set the texts to the tests data
                eTTitle.setText(test.getTitle());
                eTContent.setText(test.getContent());
                //switch button icon
                updateButton.setBackgroundResource(R.drawable.ic_baseline_edit_24);
                if (editing_allowed){
                    removeButton.setVisibility(View.VISIBLE);
                }
                break;
            case TEST_EDIT:
                //enable editing
                eTTitle.setEnabled(true);
                eTContent.setEnabled(true);
                //switch button icon
                updateButton.setBackgroundResource(R.drawable.ic_baseline_save_24);
                //show the delete button
                removeButton.setVisibility(View.VISIBLE);
                break;
            case NO_TEST_DISPLAY:
                //enable editing
                eTTitle.setEnabled(false);
                eTContent.setEnabled(false);
                //set the texts to no data placeholder
                eTTitle.setText(getResources().getString(R.string.noTest));
                eTContent.setText(getResources().getString(R.string.noTest));
                //switch button icon
                updateButton.setBackgroundResource(R.drawable.ic_baseline_add_circle_outline_24);
                //hide the delete button
                removeButton.setVisibility(View.INVISIBLE);
                break;
            case NO_TEST_EDIT:
                //enable editing
                eTTitle.setEnabled(true);
                eTContent.setEnabled(true);
                //wipe displayed text
                eTTitle.setText("");
                eTContent.setText("");
                //hide the delete button
                removeButton.setVisibility(View.INVISIBLE);
                //switch button icon
                updateButton.setBackgroundResource(R.drawable.ic_baseline_save_24);
                //set the focus on the title input
                eTTitle.requestFocus();
                //show the keyboard
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                manager.showSoftInput(eTTitle, InputMethodManager.SHOW_IMPLICIT);
                break;
        }
    }

    /**
     * saves or updates the test depending if the test exists
     */
    private void setTest() {
        if (test != null) {
            //update the test
            API.updateTest(test.getId_test(),eTContent.getText().toString(),eTTitle.getText().toString(),this);
        }else{
            //create the test
            API.addTest(id_lesson,eTContent.getText().toString(),eTTitle.getText().toString(),this);
        }
    }

    /**
     * Sets the data
     * @param id_lesson the lessons id
     * @param editing_allowed is editing allowed
     */
    public void setData(int id_lesson,boolean editing_allowed) {
        this.id_lesson = id_lesson;
        this.editing_allowed = editing_allowed;
    }

    @Override
    public void processFinish(ServerResponse response) {
        switch (response.endpoint){
            case TEST:
                switch (response.status){
                    case 200:{
                        //there was a test
                        try {
                            JSONObject json = new JSONObject(response.response);
                            test = Test.getTest(json);
                            //there is a test
                            //display the test
                            setState(State.TEST_DISPLAY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 404:{
                        //there is no test
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
                        //the test is deleted
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
                            //the test got updated
                            //display the test
                            setState(State.TEST_DISPLAY);
                        } catch (JSONException e) {
                            //set the state to no test display
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
                switch (response.status) {
                    case 200: {
                        //there was a test
                        try {
                            JSONObject json = new JSONObject(response.response);
                            test = Test.getTest(json);
                            //the test has been added
                            //display the test
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
                        test = null;

                        setState(State.NO_TEST_DISPLAY);
                        Toast.makeText(getActivity(), response.response, Toast.LENGTH_LONG).show();
                        break;
                }
        }
    }
}