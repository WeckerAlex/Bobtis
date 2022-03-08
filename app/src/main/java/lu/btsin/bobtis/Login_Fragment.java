package lu.btsin.bobtis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login_Fragment extends Fragment implements AsyncResponse {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Login_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Login_Fragment newInstance(String param1, String param2) {
        Login_Fragment fragment = new Login_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        Button loginbutton = (Button) getView().findViewById(R.id.loginButton);
        EditText etUsername = (EditText) getView().findViewById(R.id.usernameInput);
        EditText etpassword = (EditText) getView().findViewById(R.id.passwordInput);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login(etUsername.getText().toString(),etpassword.getText().toString());
            }
        });
    }

    protected void login(String username, String password){
        API task =  new API();
        task.delegate = this;
        task.execute("login",username,password);
        API.saveSession(username,password,getContext().getSharedPreferences("UserPreferences",Context.MODE_PRIVATE));
    }

    @Override
    public void processFinish(ServerResponse response) {
        System.out.println(response.status);
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
        try {
            String message;
            JSONObject json = new JSONObject(response.response);
            switch (response.status){
                case 200:{
                    message = "You are logged in as "+json.getString("type");
                    break;
                }
                case 500:
                case 400:
                case 404:
                case 412:{
                    message = "ErrorLogin "+response.status+": "+json.getString("error");
                    break;
                }
                default:{
                    message = "Something went wrong";
                }
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}