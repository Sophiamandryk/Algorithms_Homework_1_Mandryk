package ua.edu.ucu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // === Task 1 ===
        Task1 task1 = new Task1();
        task1.runInitialization();
        List<Student> students = task1.getStudents();

        // === Task 2 ===
        System.out.println("\n=== Task 2 — Multiple Variants Benchmark ===");
        int A = 50, B = 10, C = 5;

        // Save benchmark results for Python
        // File csvFile = new File("apps/src/main/java/ua/edu/ucu/benchmark_results.csv");
        // File csvFile = new File("src/main/java/ua/edu/ucu/benchmark_results.csv");
        File csvFile = new File("benchmark_results.csv");


        try (FileWriter fw = new FileWriter(csvFile)) {
            fw.write("size,TreeSet+HashMap,ArrayList,TreeMap\n");
            int[] sizes = {100, 1000, 10000, 100000};

            for (int n : sizes) {
                System.out.println("\n🔹 Testing dataset size = " + n);
                List<Student> subset = students.subList(0, Math.min(n, students.size()));

                long[] ops = new long[3];
                ops[0] = Task2.benchmarkVariant1(subset, A, B, C);
                ops[1] = Task2.benchmarkVariant2(subset, A, B, C);
                ops[2] = Task2.benchmarkVariant3(subset, A, B, C);

                System.out.printf("Розмір %6d | TreeSet+HashMap: %-8d | ArrayList: %-8d | TreeMap: %-8d%n",
                        n, ops[0], ops[1], ops[2]);
                fw.write(String.format("%d,%d,%d,%d%n", n, ops[0], ops[1], ops[2]));
            }
            System.out.println("📁 Benchmark results saved to " + csvFile.getAbsolutePath());
        }

        // === Task 3 ===
        System.out.println("\n=== Task 3 — Sorting ===");
        Task3 task3 = new Task3(students);
        task3.runDemo();

        // === Run Python visualization ===
        System.out.println("\n📊 Running Python visualization...");
        try {
            Process process = new ProcessBuilder("python3", "benchmark_plot.py")
                    .directory(new File("apps/src/main/java/ua/edu/ucu"))
                    .inheritIO()
                    .start();
            process.waitFor();
            System.out.println("✅ Visualization finished successfully!");
        } catch (Exception e) {
            System.err.println("⚠️ Could not run Python visualization automatically: " + e.getMessage());
            System.out.println("Please run manually:");
            System.out.println("   cd apps/src/main/java/ua/edu/ucu");
            System.out.println("   python3 benchmark_plot.py");
        }
    }
}



// Висновок:
// Зі збільшенням кількості студентів, реалізація TreeSet + HashMap показує найкращий баланс продуктивності.
// ArrayList є прийнятним для невеликих обсягів даних, але не масштабується.
// TreeMap працює стабільніше, ніж ArrayList, однак поступається TreeSet+HashMap за швидкістю при більшості сценаріїв.