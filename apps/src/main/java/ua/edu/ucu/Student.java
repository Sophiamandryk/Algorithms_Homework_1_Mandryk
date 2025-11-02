package ua.edu.ucu;
// модель
import java.util.Objects;

public class Student {
    private String name;
    private String surname;
    private String email;
    private int birthYear;
    private int birthMonth;
    private int birthDay;
    private String group;
    private float rating;
    private String phoneNumber;

    public Student(String name, String surname, String email,
                   int birthYear, int birthMonth, int birthDay,
                   String group, float rating, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.group = group;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
    }

    // --- getters ---
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public int getBirthYear() { return birthYear; }
    public int getBirthMonth() { return birthMonth; }
    public int getBirthDay() { return birthDay; }
    public String getGroup() { return group; }
    public float getRating() { return rating; }      // <- required by comparator
    public String getPhoneNumber() { return phoneNumber; }

    // --- setters (only for fields you'll update) ---
    public void setGroup(String group) { this.group = group; }
    public void setRating(float rating) { this.rating = rating; }

    @Override
    public String toString() {
        return name + " " + surname + " (" + group + ") — " + rating;
    }

    // Use email as unique identity for equals/hashCode (adjust if needed)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return Objects.equals(email, s.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
