package lu.btsin.bobtis;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Login_Fragment extends Fragment implements AsyncResponse {

    private EditText etUsername;
    private EditText etPassword;

    /**
     * Constructor
     */
    public Login_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button loginbutton = getView().findViewById(R.id.loginButton);
        etUsername = getView().findViewById(R.id.usernameInput);
        etPassword = getView().findViewById(R.id.passwordInput);
        loginbutton.setOnClickListener(v -> login(etUsername.getText().toString(), etPassword.getText().toString()));
    }

    /**
     * Logs in and saves the credentials
     * @param username the users username
     * @param password the password belonging to the username
     */
    protected void login(String username, String password){
        //disable the input fields
        etUsername.setEnabled(false);
        etPassword.setEnabled(false);
        API.login(username,password,this);
    }

    @Override
    public void processFinish(ServerResponse response) {
        Log.i("prossessLogin_Fra", String.valueOf(response.status));
        switch (response.endpoint){
            case LOGIN:
                prossessLogin(response);
                break;
            default:{
                break;
            }

        }
    }

    private void prossessLogin(ServerResponse response){
        switch (response.status){
            case 0:{
                Toast.makeText(getContext(),"You are not connected to the internet" , Toast.LENGTH_LONG).show();
                break;
            }
            case 200:{
                User user = new User(getActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE),response.response, etPassword.getText().toString());
                //update the user
                ((MainActivity)getActivity()).currentUser = user;
                //load the new users preferences
                ((MainActivity)getActivity()).loadPreferences();
                //enable the search if the user is allowed
                ((MainActivity)getActivity()).setEnableSearch();
                //create the Links saved in the preferences and enable the own Absences and own Homework links
                ((MainActivity)getActivity()).setLinks();
                //refresh the Navigation header data
                ((MainActivity)getActivity()).setNavbarHeader();
                //if the user is a teacher or student display its timetable
                switch (user.getRole()){
                    case STUDENT:{
                        ((MainActivity)getActivity()).displayStudent(user.getId(),true);
                        break;
                    }
                    case TEACHER:{
                        ((MainActivity)getActivity()).displayTeacher(user.getUsername().substring(0,5).toUpperCase(),true);
                        break;
                    }
                }
                break;
            }
            case 500:
            case 400:
            case 404:
            case 412:{
                //in case something goes wrong re-enable the fields and wipe the data
                etUsername.setEnabled(true);
                etPassword.setEnabled(true);
                etUsername.setText("");
                etPassword.setText("");
                break;
            }
            default:{
                Toast.makeText(getContext(),"Something went wrong" , Toast.LENGTH_LONG).show();
            }
        }
        etUsername.setEnabled(true);
        etPassword.setEnabled(true);
    }
}