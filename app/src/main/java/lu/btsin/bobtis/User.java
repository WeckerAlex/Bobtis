package lu.btsin.bobtis;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

public class User {


    public enum Role{
        STUDENT,
        TEACHER,
        STAFF,
        SEPAS
    }
    public enum Right{
        LOGIN,
        SCHEDULE_OWN,
        SCHEDULE_STUDENTS,
        SCHEDULE_CLASS,
        SCHEDULE_CLASSES,
        SCHEDULE_TEACHERS,
        SCHEDULE_ROOMS,
        ABSENCES_TEACHES,
        ABSENCES_OWN,
        ABSENCES_CLASSES,
        MARK_TEACHES,
        MARK_CLASSES
    }

    private String password;
    private Role role;
    private String username;
    private String email;
    private String name;
    private String firstname;
    private int id;
    private String classe;
    private ArrayList<Right> rights;


    public User(SharedPreferences prefs,String response, String password) {
        try {
            JSONObject json = new JSONObject(response);
            role = Role.valueOf(json.getString("type").toUpperCase());
            name = json.getString("name");
            firstname = json.getString("firstname");
            username = json.getString("username");
            this.password = password;
            email = json.getString("email");
            id = Integer.parseInt(json.getString("id_" + role.toString().toLowerCase()));
            if (role == Role.STUDENT){
                classe = json.getString("classe");
            }
            JSONArray jsonclasse = json.getJSONArray("rights");
            rights = new ArrayList<Right>();
            for (int i = 0; i < jsonclasse.length(); i++) {
                rights.add(Right.valueOf(jsonclasse.getString(i).replace(".","_").toUpperCase()));
            }
            saveUser(prefs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    private User(SharedPreferences prefs){
        role = Role.valueOf(prefs.getString("type","").toUpperCase());
        name = prefs.getString("name","");
        firstname = prefs.getString("firstname","");
        username = prefs.getString("username","");
        password = prefs.getString("password","");
        email = prefs.getString("email","");
        id = prefs.getInt("id_" + role.toString().toLowerCase(),0);
        if (role == Role.STUDENT){
            classe = prefs.getString("classe","");
        }
        rights = new ArrayList<Right>();
        String[] tempRights = prefs.getString("rights","").split(",");
        for (int i = 0; i < tempRights.length; i++) {
            rights.add(Right.valueOf(tempRights[i].replace(".","_").toUpperCase()));
        }
    }

    /**
     * Create a new user out of the stored data
     * @param prefs the SharedPreferences used to store the user
     */
    public static User loadUser(SharedPreferences prefs) {
        if (prefs.getBoolean("loginState",false)){
            return new User(prefs);
        }else{
            return null;
        }
        
    }

    public void saveUser(SharedPreferences prefs){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("type", role.toString());
        edit.putString("name", name);
        edit.putString("firstname", firstname);
        edit.putString("username", username);
        edit.putString("password", password);
        edit.putString("email", email);
        edit.putInt("id", id);
        if (role == Role.STUDENT){
            edit.putString("classe", classe);
        }
        StringJoiner sj = new StringJoiner(",");
        for (int i = 0; i < rights.size(); i++) {
            sj.add(rights.get(i).toString());
        }
        edit.putString("rights", String.join(",", sj.toString()));
        edit.putBoolean("loginState", true);
        edit.apply();

    }

    @Override
    public String toString() {
        return "User{" +
                "password='" + password + '\'' +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", firstname='" + firstname + '\'' +
                ", id=" + id +
                ", classe='" + classe + '\'' +
                ", rights=" + rights +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getFirstname() {
        return firstname;
    }

    public int getId() {
        return id;
    }

    public String getClasse() {
        return classe;
    }

    public ArrayList<Right> getRights() {
        return rights;
    }
}