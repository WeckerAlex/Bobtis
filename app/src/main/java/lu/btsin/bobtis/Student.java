package lu.btsin.bobtis;

import org.json.JSONException;
import org.json.JSONObject;

public class Student {
    private int id;
    private String name;
    private String firstname;
    private String email;
    private String iam;
    private boolean present;
    private int absenceId = 0;
    private String absenceEndTime = "";
    private String comment = "";
    private int reasonId;
    private String absenceEndDate = "";

    public Student(int id, String name, String firstname, String email, String iam, boolean present) {
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.email = email;
        this.iam = iam;
        this.present = present;
    }

    /**
     * Get the Student in the JSON object
     * @param json JSON containing an student
     * @return a new Student
     */
    public static Student getStudent(JSONObject json){
        try {
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

    /**
     * Gets the id
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name
     * @return the students name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the first name
     * @return the students first name
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Gets the email
     * @return the students email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the iam
     * @return the students iam
     */
    public String getIam() {
        return iam;
    }

    /**
     * Gets the presence
     * @return the students presence
     */
    public boolean isPresent() {
        return present;
    }

    /**
     * Gets the students absence id related to a lesson
     * @return the students absence id
     */
    public int getAbsenceId() {
        return absenceId;
    }

    /**
     * Sets the students absence id related to a lesson
     * @param absenceId the absence id
     */
    public void setAbsenceId(int absenceId) {
        this.absenceId = absenceId;
    }

    /**
     * Gets the students absence end time related to a lesson
     * @return the students absence end time
     */
    public String getAbsenceEndTime() {
        return absenceEndTime;
    }

    /**
     * Sets the students absence end Time related to a lesson
     * @param arrival the student's absence end Time
     */
    public void setAbsenceEndTime(String arrival) {
        this.absenceEndTime = arrival;
    }

    /**
     * Gets the students absence comment related to a lesson
     * @return the students absence comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the students absence comment related to a lesson
     * @param comment the student's absence comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the students absence reason related to a lesson
     * @return the students absence reason id
     */
    public int getReasonId() {
        return reasonId;
    }

    /**
     * Sets the students absence reason related to a lesson
     * @param reasonId the student's absence reason id
     */
    public void setReasonId(int reasonId) {
        this.reasonId = reasonId;
    }

    /**
     * Gets the students absence end Date related to a lesson
     * @return the students absence end Date
     */
    public String getAbsenceEndDate() {
        return absenceEndDate;
    }

    /**
     * Sets the students absence end Date related to a lesson
     * @param absenceEndDate the student's absence end Date
     */
    public void setAbsenceEndDate(String absenceEndDate) {
        this.absenceEndDate = absenceEndDate;
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
