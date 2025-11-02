package ua.edu.ucu;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Task1 {

    private List<Student> students = new ArrayList<>();

    /**
     * Task I — Ініціалізація:
     * Завантаження бази студентів з непосортованого CSV файлу у памʼять.
     */
    public void runInitialization() throws IOException {
        // String filePath = "/Users/sofiyamandryk/Desktop/homework_1/src/main/resources/students.csv";
        String filePath = "/Users/sofiyamandryk/Desktop/homework_1/apps/src/main/resources/students.csv";
        // List<String> lines = Files.readAllLines(Paths.get(filePath));
        // List<String> lines = Files.readAllLines(Paths.get("../resources/students.csv"));
        List<String> lines = Files.readAllLines(Paths.get(filePath));



        // Skip header row (starts from index 1)
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",", -1); // -1 → keeps empty fields
            if (parts.length < 9) {
                System.err.println("Skipping invalid line " + i + ": " + line);
                continue;
            }

            String name = parts[0].trim();
            String surname = parts[1].trim();
            String email = parts[2].trim();
            int birthYear = Integer.parseInt(parts[3].trim());
            int birthMonth = Integer.parseInt(parts[4].trim());
            int birthDay = Integer.parseInt(parts[5].trim());
            String group = parts[6].trim();
            float rating = Float.parseFloat(parts[7].trim());
            String phone = parts[8].trim();

            Student s = new Student(
                    name, surname, email,
                    birthYear, birthMonth, birthDay,
                    group, rating, phone
            );
            students.add(s);
        }

        System.out.println("✅ Task 1 complete: loaded " + students.size() + " students into memory.");
    }

    public List<Student> getStudents() {
        return students;
    }
}
