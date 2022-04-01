package lu.btsin.bobtis;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class User {

    /**
     * The users role
     */
    public enum Role{
        STUDENT,
        TEACHER,
        STAFF,
        SEPAS
    }

    /**
     * The users rights
     */
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

    /**
     * Constructor. Creates a user using data in a server response
     * @param prefs The SharedPreferences used to save the user
     * @param response a string in json format containing a user
     * @param password the users password
     */
    public User(SharedPreferences prefs,String response, String password) {
        try {
            //initialize all variables
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
            //save the user in the SharedPreferences
            saveUser(prefs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a user using the data stored in the SharedPreferences
     * @param prefs the SharedPreferences containing the user
     */
    private User(SharedPreferences prefs){
        //initialize all variables
        role = Role.valueOf(prefs.getString("type","").toUpperCase());
        name = prefs.getString("name","");
        firstname = prefs.getString("firstname","");
        username = prefs.getString("username","");
        password = prefs.getString("password","");
        email = prefs.getString("email","");
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
     * Creates a new user out of the stored data. If there is no user returns null
     * @param prefs the SharedPreferences used to store the user
     */
    public static User loadUser(SharedPreferences prefs) {
        if (prefs.getBoolean("loginState",false)){
            return new User(prefs);
        }else{
            return null;
        }
        
    }

    /**
     * Saves the user to the SharedPreferences
     * @param prefs the SharedPreferences used to store the user
     */
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

    /**
     * Updates the user stored in the SharedPreferences
     * @param prefs The SharedPreferences used to save the user
     * @param response a string in json format containing a user
     */
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

    /**
     * Gets the users password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the users role
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets the users username
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the users email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the users name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the users firstname
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Gets the users id
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the users class
     * @return the class
     */
    public String getClasse() {
        return classe;
    }

    /**
     * Gets the users rights
     * @return the rights
     */
    public ArrayList<Right> getRights() {
        return rights;
    }

    /**
     * Checks if a user has a permission
     * @param right the permission to check
     * @return
     */
    public boolean has_Permission(Right right){
        return (rights != null && rights.contains(right));
    }

    /**
     * Adds a class to the user's preferred classes
     * @param userprefs the specific user's SharedPreferences
     * @param data the class name to save
     */
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

    /**
     * Adds a room to the user's preferred classes
     * @param userprefs the specific user's SharedPreferences
     * @param data the room name to save
     */
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

    /**
     * Adds a teacher to the user's preferred classes
     * @param userprefs the specific user's SharedPreferences
     * @param data an array containing the teachers full name and the 5 letter in his username
     */
    public void addTeacher(SharedPreferences userprefs, String[] data) {
        teachers.put(data[1],data[0]);
        SharedPreferences.Editor edit = userprefs.edit();
        StringJoiner sj = new StringJoiner(",");
        for (Map.Entry<String, String> entry : teachers.entrySet()) {
            sj.add(entry.getKey()+";"+entry.getValue());
        }
        edit.putString("teachers",String.join(",", sj.toString()));
        edit.apply();
    }

    /**
     * Checks if a class is in the user's preferred classes
     * @param data the class to check
     * @return is the class in the user favorite classes
     */
    public boolean hasClass(String data) {
        return classes!=null && classes.contains(data);
    }

    /**
     * Checks if a room is in the user's preferred rooms
     * @param data the room to check
     * @return is the room in the user favorite rooms
     */
    public boolean hasRoom(String data) {
        return rooms!=null && rooms.contains(data);
    }

    /**
     * Checks if a teacher is in the user's preferred teacher
     * @param data the teacher to check
     * @return is the teacher in the user favorite teachers
     */
    public boolean hasTeacher(String data) {
        return teachers!=null && teachers.containsKey(data);
    }

    /**
     * loads the preferred classes from the SharedPreferences
     * @param userprefs the specific user's SharedPreferences
     */
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

    /**
     * loads the preferred rooms from the SharedPreferences
     * @param userprefs the specific user's SharedPreferences
     */
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

    /**
     * loads the preferred teachers from the SharedPreferences
     * @param userprefs the specific user's SharedPreferences
     */
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

    /**
     * Retrieves a teacher's 5 first letters of his username
     * @param teacherFullName the teachers full name
     * @return the 5 first letters of his username
     */
    public String getTeacherShortName(String teacherFullName) {
        return teachers.get(teacherFullName);
    }

    /**
     * Gets the users preferred classes
     * @return the users preferred classes
     */
    public ArrayList<String> getClasses() {
        return classes;
    }

    /**
     * Gets the users preferred rooms
     * @return the users preferred rooms
     */
    public ArrayList<String> getRooms() {
        return rooms;
    }

    /**
     * Gets a list of the user's preferred teacher's full names
     * @return a list of the user's preferred teacher's full names
     */
    public ArrayList<String> getTeachers() {
        //retrieve list of full names
        return new ArrayList<>(teachers.keySet()) ;
    }

}