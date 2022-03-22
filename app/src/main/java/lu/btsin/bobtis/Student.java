package lu.btsin.bobtis;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;

public class Student {
    private int id;
    private String name;
    private String firstname;
    private String email;
    private String iam;
    private boolean present;

    public Student(int id, String name, String firstname, String email, String iam, boolean present) {
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.email = email;
        this.iam = iam;
        this.present = present;
    }

    public static Student getStudent(JSONObject json){
        try {
            //Student st = new Student();
            int id = Integer.parseInt(json.getString("id_student"));
            String name = json.getString("name");
            String firstname = json.getString("firstname");
            String email = json.getString("email");
            String iam = json.getString("iam");
            boolean present = Boolean.parseBoolean(json.getString("present"));
            return new Student(id,name,firstname,email,iam,present);
        } catch (JSONException e) {
            System.out.println(json);
            e.printStackTrace();
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }

    public String getIam() {
        return iam;
    }

    public boolean isPresent() {
        return present;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstname='" + firstname + '\'' +
                ", email='" + email + '\'' +
                ", iam='" + iam + '\'' +
                ", present=" + present +
                '}';
    }
}
