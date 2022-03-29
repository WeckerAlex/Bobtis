package lu.btsin.bobtis;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
        MARK_CLASSES,
        TEST_OWN,
        TEST_CLASSES,
        HOMEWORK_OWN,
        HOMEWORK_CLASSES
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

    private ArrayList<String> classes;
    private ArrayList<String> rooms;
    private HashMap<String,String> teachers;


    public User(SharedPreferences prefs,String response, String password) {
        try {
            JSONObject json = new JSONObject(response);
            role = Role.valueOf(json.getString("type").toUpperCase());
            name = json.getString("name");
            firstname = json.getString("firstname");
            username = json.getString("username");
            this.password = password;
            email = json.getString("email");
            id = Integer.parseInt(json.getString("id_"+role.toString().toLowerCase()));
            if (role == Role.STUDENT){
                classe = json.getString("classe");
            }
            JSONArray jsonclasse = json.getJSONArray("rights");
            rights = new ArrayList<Right>();
            for (int i = 0; i < jsonclasse.length(); i++) {
                rights.add(Right.valueOf(jsonclasse.getString(i).replace(".","_").toUpperCase()));
            }
            saveUser(prefs);
            Log.i("CurrentUser",this.toString());
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
        Log.i("User", String.valueOf(prefs.getInt("id",0)));
        id = prefs.getInt("id",0);
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
        Log.i("saveUser", String.valueOf(id));
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

    public void updateUser(SharedPreferences prefs,String response){
        try {
            JSONObject json = new JSONObject(response);
            role = Role.valueOf(json.getString("type").toUpperCase());
            name = json.getString("name");
            firstname = json.getString("firstname");
            username = json.getString("username");
            email = json.getString("email");
            id = Integer.parseInt(json.getString("id_"+role.toString().toLowerCase()));
            if (role == Role.STUDENT){
                classe = json.getString("classe");
            }
            JSONArray jsonclasse = json.getJSONArray("rights");
            rights.clear();
            rights = new ArrayList<Right>();
            for (int i = 0; i < jsonclasse.length(); i++) {
                rights.add(Right.valueOf(jsonclasse.getString(i).replace(".","_").toUpperCase()));
            }
            saveUser(prefs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "  role=" + role +
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

    public boolean has_Permission(Right right){
        //testing override
        return true;
        //TODO remove override
//        return (rights != null && rights.contains(right));
    }

    public void addClass(SharedPreferences userprefs, String data) {
        classes.add(data);
        SharedPreferences.Editor edit = userprefs.edit();
        StringJoiner sj = new StringJoiner(",");
        for (int i = 0; i < classes.size(); i++) {
            sj.add(classes.get(i));
        }
        edit.putString("classes",String.join(",", sj.toString()));
        edit.apply();
    }

    public void addRoom(SharedPreferences userprefs, String data) {
        rooms.add(data);
        SharedPreferences.Editor edit = userprefs.edit();
        StringJoiner sj = new StringJoiner(",");
        for (int i = 0; i < rooms.size(); i++) {
            sj.add(rooms.get(i));
        }
        edit.putString("rooms",String.join(",", sj.toString()));
        edit.apply();
    }

    public void addTeacher(SharedPreferences userprefs, String[] data) {
        Log.i("getTeacherShortName_addTeacher",data[0]+" "+data[1]);
        teachers.put(data[1],data[0]);
        for (String key:teachers.keySet()) {
            Log.i("getTeacherShortName_add_enu",key);
        }
        SharedPreferences.Editor edit = userprefs.edit();
        StringJoiner sj = new StringJoiner(",");
        for (Map.Entry<String, String> entry : teachers.entrySet()) {
            sj.add(entry.getKey()+";"+entry.getValue());
        }
        edit.putString("teachers",String.join(",", sj.toString()));
        edit.apply();
    }

    public boolean hasClass(String data) {
        return classes!=null && classes.contains(data);
    }

    public boolean hasRoom(String data) {
        return rooms!=null && rooms.contains(data);
    }

    public boolean hasTeacher(String data) {
        return teachers!=null && teachers.containsKey(data);
    }

    protected void loadClasses(SharedPreferences userprefs){
        String[] temp;
        classes = new ArrayList<>();
        temp = userprefs.getString("classes","").split(",");
        for (int i = 0; i < temp.length; i++) {
            if (!temp[0].equalsIgnoreCase("")){
                classes.add(temp[i]);
            }
        }
    }

    protected void loadRooms(SharedPreferences userprefs){
        String[] temp;
        rooms = new ArrayList<>();
        temp = userprefs.getString("rooms","").split(",");
        for (int i = 0; i < temp.length; i++) {
            if (!temp[0].equalsIgnoreCase("")){
                rooms.add(temp[i]);
            }
        }
    }

    protected void loadTeachers(SharedPreferences userprefs){
        String[] temp;
        teachers = new HashMap<String,String>();
        temp = userprefs.getString("teachers","").split(",");
        for (int i = 0; i < temp.length; i++) {
            if (!temp[0].equalsIgnoreCase("")){
                String[] hashdata = temp[i].split(";");
                teachers.put(hashdata[0],hashdata[1]);
            }
        }
    }

    public String getTeacherShortName(String teacherFullName) {
        Log.i("getTeacherShortName_input",teacherFullName);
        Log.i("getTeacherShortName_res",teachers.get(teacherFullName)+ " returned");
        for (String key:teachers.keySet()) {
            Log.i("getTeacherShortName_enu",key);
        }
        return teachers.get(teacherFullName);
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public ArrayList<String> getRooms() {
        return rooms;
    }

    public ArrayList<String> getTeachers() {
        //retrieve list of full names
        return new ArrayList<>(teachers.keySet()) ;
    }

}