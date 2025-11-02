package ua.edu.ucu;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // === Task 1 ===
        Task1 task1 = new Task1();
        task1.runInitialization();

        // === Task 2 ===
        Task2 task2 = new Task2(task1.getStudents());
        task2.runDemo();

        // === Benchmark for Task 2 ===
        long ops = task2.runBenchmark(10, 50, 10, 5);
        System.out.println("\n🏁 Виконано " + ops + " операцій за 10 секунд (A:B:C = 50:10:5)");

        // === Task 3 ===
        Task3 task3 = new Task3(task1.getStudents());
        task3.runDemo();
    }
}
