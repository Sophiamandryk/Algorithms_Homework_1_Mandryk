package ua.edu.ucu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

/**
 * Task 2 — Variant V5
 * Операції:
 * 1) Повернути 100 студентів з найвищим рейтингом
 * 2) Встановити рейтинг студенту за його електронною поштою (m_email)
 * 3) Знайти групу, в якій найбільший середній рейтинг студентів
 * 
 * Структури даних:
 * - TreeSet<Student> для підтримки сортування за рейтингом (спадання)
 * - HashMap<String, Student> для O(1) пошуку за email
 * - HashMap<String, GroupStats> для відстеження статистики груп
 */
public class Task2 {
    
    // TreeSet з компаратором: спадання за рейтингом, потім за email для унікальності
    private TreeSet<Student> studentsByRating;
    
    // Швидкий пошук за email
    private HashMap<String, Student> studentsByEmail;
    
    // Статистика груп для операції 3
    private HashMap<String, GroupStats> groupStats;
    
    // Допоміжний клас для відстеження статистики груп
    private static class GroupStats {
        double totalRating = 0.0;
        int count = 0;
        
        void addStudent(float rating) {
            totalRating += rating;
            count++;
        }
        
        void updateRating(float oldRating, float newRating) {
            totalRating = totalRating - oldRating + newRating;
        }
        
        double getAverageRating() {
            return count > 0 ? totalRating / count : 0.0;
        }
    }
    
    public Task2(List<Student> students) {
        // Ініціалізація TreeSet з компаратором (спадання за рейтингом)
        studentsByRating = new TreeSet<>((s1, s2) -> {
            int cmp = Float.compare(s2.getRating(), s1.getRating()); // спадання
            if (cmp != 0) return cmp;
            return s1.getEmail().compareTo(s2.getEmail()); // для унікальності
        });
        
        studentsByEmail = new HashMap<>();
        groupStats = new HashMap<>();
        
        // Заповнення структур даних
        for (Student s : students) {
            studentsByRating.add(s);
            studentsByEmail.put(s.getEmail(), s);
            
            groupStats.computeIfAbsent(s.getGroup(), k -> new GroupStats())
                      .addStudent(s.getRating());
        }
        
        System.out.println("✅ Task 2 ініціалізовано: " + students.size() + " студентів проіндексовано");
    }
    
    /**
     * Операція 1: Повернути 100 студентів з найвищим рейтингом
     * Складність: O(100) = O(1)
     */
    public List<Student> getTop100Students() {
        List<Student> result = new ArrayList<>();
        int count = 0;
        
        for (Student s : studentsByRating) {
            if (count >= 100) break;
            result.add(s);
            count++;
        }
        
        return result;
    }
    
    /**
     * Операція 2: Встановити рейтинг студенту за його електронною поштою
     * Складність: O(log n) через повторну вставку в TreeSet
     */
    public boolean setRatingByEmail(String email, float newRating) {
        Student student = studentsByEmail.get(email);
        if (student == null) {
            return false;
        }
        
        float oldRating = student.getRating();
        String group = student.getGroup();
        
        // Видалити з TreeSet (потрібен старий стан)
        studentsByRating.remove(student);
        
        // Оновити рейтинг
        student.setRating(newRating);
        
        // Повторно вставити в TreeSet з новим рейтингом
        studentsByRating.add(student);
        
        // Оновити статистику групи
        GroupStats stats = groupStats.get(group);
        if (stats != null) {
            stats.updateRating(oldRating, newRating);
        }
        
        return true;
    }
    
    /**
     * Операція 3: Знайти групу з найбільшим середнім рейтингом
     * Складність: O(g) g - кількість груп 
     */
    public String getGroupWithHighestAvgRating() {
        String bestGroup = null;
        double maxAvg = Double.NEGATIVE_INFINITY;
        
        for (Map.Entry<String, GroupStats> entry : groupStats.entrySet()) {
            double avg = entry.getValue().getAverageRating();
            if (avg > maxAvg) {
                maxAvg = avg;
                bestGroup = entry.getKey();
            }
        }
        
        return bestGroup;
    }
    
    /**
     * Отримати середній рейтинг для конкретної групи
     */
    public double getGroupAvgRating(String group) {
        GroupStats stats = groupStats.get(group);
        return stats != null ? stats.getAverageRating() : 0.0;
    }
    
    /**
     * Демонстрація роботи всіх операцій
     */
    public void runDemo() {
        System.out.println("\n=== Демонстрація Task 2 (Варіант V5) ===\n");
        
        // Операція 1: Топ 100 студентів
        System.out.println("📊 Операція 1: Топ 10 студентів за рейтингом:");
        List<Student> top = getTop100Students();
        for (int i = 0; i < Math.min(10, top.size()); i++) {
            Student s = top.get(i);
            System.out.printf("  %2d. %.2f — %s %s (%s)\n", 
                i + 1, s.getRating(), s.getName(), s.getSurname(), s.getGroup());
        }
        
        // Операція 3: Група з найвищим середнім
        System.out.println("\n🏆 Операція 3: Група з найвищим середнім рейтингом:");
        String bestGroup = getGroupWithHighestAvgRating();
        double bestAvg = getGroupAvgRating(bestGroup);
        System.out.printf("  %s з середнім рейтингом: %.2f\n", bestGroup, bestAvg);
        
        // Операція 2: Оновлення рейтингу
        System.out.println("\n✏️ Операція 2: Оновлення рейтингу студента...");
        if (!top.isEmpty()) {
            Student firstStudent = top.get(0);
            String email = firstStudent.getEmail();
            float oldRating = firstStudent.getRating();
            float newRating = 50.0f;
            
            System.out.printf("  До: %s має рейтинг %.2f\n", email, oldRating);
            setRatingByEmail(email, newRating);
            System.out.printf("  Після: %s має рейтинг %.2f\n", email, newRating);
            
            // Перевірка змін у топі
            List<Student> newTop = getTop100Students();
            System.out.println("\n  Новий топ 3 студенти:");
            for (int i = 0; i < Math.min(3, newTop.size()); i++) {
                Student s = newTop.get(i);
                System.out.printf("    %d. %.2f — %s %s\n", 
                    i + 1, s.getRating(), s.getName(), s.getSurname());
            }
        }
    }
    
    
    // Ппродуктивність
    
    public long runBenchmark(int durationSeconds, int ratioA, int ratioB, int ratioC) {
        Random rand = new Random();
        long operations = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (durationSeconds * 1000L);
        
        int totalRatio = ratioA + ratioB + ratioC;
        List<String> emails = new ArrayList<>(studentsByEmail.keySet());
        
        while (System.currentTimeMillis() < endTime) {
            int choice = rand.nextInt(totalRatio);
            
            if (choice < ratioA) {
                // Операція 1: Отримати топ 100
                getTop100Students();
            } else if (choice < ratioA + ratioB) {
                // Операція 2: Встановити рейтинг
                String email = emails.get(rand.nextInt(emails.size()));
                float newRating = rand.nextFloat() * 100;
                setRatingByEmail(email, newRating);
            } else {
                // Операція 3: Знайти найкращу групу
                getGroupWithHighestAvgRating();
            }
            
            operations++;
        }
        
        return operations;
    }
    
    
    // Оцінка використання пам'яті
    
    public long estimateMemoryUsage() {
        int studentCount = studentsByEmail.size();
        int groupCount = groupStats.size();
        
        
        long studentObjects = studentCount * 200L;
        long treeSetOverhead = studentCount * 40L;
        long hashMapOverhead = studentCount * 40L;
        long groupStatsMemory = groupCount * 32L;
        
        return studentObjects + treeSetOverhead + hashMapOverhead + groupStatsMemory;
    }
    
    /**
     * Отримати всіх студентів (для додаткових операцій)
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(studentsByRating);
    }
}