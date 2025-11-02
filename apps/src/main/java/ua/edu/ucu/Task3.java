package ua.edu.ucu;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Task 3 — Variant S2
 * Посортувати всіх студентів за рейтингом (m_rating) і зберегти в CSV файл.
 *
 * Реалізовано два методи:
 *   a) стандартне сортування Java (Collections.sort)
 *   b) власна реалізація сортування (Radix Sort для float)
 *
 * Порівнюється час виконання (без урахування запису у файл).
 */
public class Task3 {

    private final List<Student> students;

    public Task3(List<Student> students) {
        this.students = new ArrayList<>(students); // make a copy
    }

    // ========== a) Standard Java Sort ==========
    public List<Student> sortByRatingStandard() {
        List<Student> sorted = new ArrayList<>(students);
        long start = System.nanoTime();
        sorted.sort(Comparator.comparingDouble(Student::getRating));
        long end = System.nanoTime();
        System.out.printf("⏱ Standard sort time: %.3f ms%n", (end - start) / 1_000_000.0);
        return sorted;
    }

    // ========== b) Custom Radix Sort for float ==========
    public List<Student> sortByRatingRadix() {
        List<Student> sorted = new ArrayList<>(students);
        long start = System.nanoTime();
        radixSortByRating(sorted);
        long end = System.nanoTime();
        System.out.printf("⚙️  Radix sort time: %.3f ms%n", (end - start) / 1_000_000.0);
        return sorted;
    }

    // Radix sort adapted for float values (0..100)
    private void radixSortByRating(List<Student> list) {
        int n = list.size();
        // Multiply rating by 100 to convert to int (preserving 2 decimals)
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) {
            keys[i] = (int) (list.get(i).getRating() * 100);
        }

        // Perform counting sort for each digit (base 10)
        int max = Arrays.stream(keys).max().orElse(0);
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSort(list, keys, exp);
        }
    }
    private void countingSort(List<Student> list, int[] keys, int exp) {
    int n = list.size();
    Student[] output = new Student[n];
    int[] outputKeys = new int[n];  
    int[] count = new int[10];
    Arrays.fill(count, 0);

    // Count occurrences
    for (int i = 0; i < n; i++) {
        int digit = (keys[i] / exp) % 10;
        count[digit]++;
    }

    // Cumulative count
    for (int i = 1; i < 10; i++) {
        count[i] += count[i - 1];
    }

    // Build output (stable order)
    for (int i = n - 1; i >= 0; i--) {
        int digit = (keys[i] / exp) % 10;
        output[count[digit] - 1] = list.get(i);
        outputKeys[count[digit] - 1] = keys[i]; 
        count[digit]--;
    }

    // Copy back
    for (int i = 0; i < n; i++) {
        list.set(i, output[i]);
        keys[i] = outputKeys[i]; 
    }
}

    
    // ========== Saved to CSV ==========
    public void saveToCSV(List<Student> sorted, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("m_name,m_surname,m_email,m_birth_year,m_birth_month,m_birth_day,m_group,m_rating,m_phone_number\n");
            for (Student s : sorted) {
                bw.write(String.format("%s,%s,%s,%d,%d,%d,%s,%.2f,%s%n",
                        s.getName(), s.getSurname(), s.getEmail(),
                        s.getBirthYear(), s.getBirthMonth(), s.getBirthDay(),
                        s.getGroup(), s.getRating(), s.getPhoneNumber()));
            }
        }
        System.out.println("💾 Sorted students saved to: " + filePath);
    }

    // ==========  demo ==========
    public void runDemo() throws IOException {
        System.out.println("\n=== Task 3 — Sorting by Rating ===");

        List<Student> sortedStandard = sortByRatingStandard();
        List<Student> sortedRadix = sortByRatingRadix();

        saveToCSV(sortedStandard, "students_sorted_standard.csv");
        saveToCSV(sortedRadix, "students_sorted_radix.csv");

        System.out.println("✅ Task 3 complete!");
    }
}
