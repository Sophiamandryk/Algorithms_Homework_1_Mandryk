package ua.edu.ucu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Task 2 — Варіант із трьома реалізаціями:
 * 1️⃣ TreeSet + HashMap (оптимальний)
 * 2️⃣ ArrayList (простий)
 * 3️⃣ TreeMap (альтернатива)
 */
public class Task2 {

    /* ----------------------------------------------------
     *  VARIANT 1 — TreeSet + HashMap
     * ---------------------------------------------------- */
    public static class Variant1 {
        private TreeSet<Student> sorted;
        private HashMap<String, Student> byEmail;
        private HashMap<String, GroupStats> groupStats;

        private static class GroupStats {
            double total = 0;
            int count = 0;
            void add(float r) { total += r; count++; }
            void update(float oldR, float newR) { total += newR - oldR; }
            double avg() { return count == 0 ? 0 : total / count; }
        }

        public Variant1(List<Student> students) {
            sorted = new TreeSet<>((a, b) -> {
                int cmp = Float.compare(b.getRating(), a.getRating());
                return (cmp != 0) ? cmp : a.getEmail().compareTo(b.getEmail());
            });
            byEmail = new HashMap<>();
            groupStats = new HashMap<>();
            for (Student s : students) {
                sorted.add(s);
                byEmail.put(s.getEmail(), s);
                groupStats.computeIfAbsent(s.getGroup(), k -> new GroupStats()).add(s.getRating());
            }
        }

        public List<Student> getTop100() {
            List<Student> res = new ArrayList<>();
            int i = 0;
            for (Student s : sorted) {
                if (i++ >= 100) break;
                res.add(s);
            }
            return res;
        }

        public void setRating(String email, float newR) {
            Student s = byEmail.get(email);
            if (s == null) return;
            sorted.remove(s);
            float old = s.getRating();
            s.setRating(newR);
            sorted.add(s);
            groupStats.get(s.getGroup()).update(old, newR);
        }

        public String bestGroup() {
            String best = null;
            double max = -1;
            for (var e : groupStats.entrySet()) {
                double avg = e.getValue().avg();
                if (avg > max) {
                    max = avg;
                    best = e.getKey();
                }
            }
            return best;
        }
    }

    /* ----------------------------------------------------
     *  VARIANT 2 — ArrayList (простий)
     * ---------------------------------------------------- */
    public static class Variant2 {
        private ArrayList<Student> list;

        public Variant2(List<Student> students) {
            list = new ArrayList<>(students);
        }

        public List<Student> getTop100() {
            list.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
            return list.subList(0, Math.min(100, list.size()));
        }

        public void setRating(String email, float newR) {
            for (Student s : list)
                if (s.getEmail().equals(email)) {
                    s.setRating(newR);
                    break;
                }
        }

        public String bestGroup() {
            HashMap<String, float[]> map = new HashMap<>();
            for (Student s : list) {
                map.putIfAbsent(s.getGroup(), new float[]{0, 0});
                float[] arr = map.get(s.getGroup());
                arr[0] += s.getRating();
                arr[1]++;
            }
            double max = -1;
            String best = null;
            for (var e : map.entrySet()) {
                double avg = e.getValue()[0] / e.getValue()[1];
                if (avg > max) {
                    max = avg;
                    best = e.getKey();
                }
            }
            return best;
        }
    }

    /* ----------------------------------------------------
     *  VARIANT 3 — TreeMap
     * ---------------------------------------------------- */
    public static class Variant3 {
        private TreeMap<Float, List<Student>> ratingMap;
        private HashMap<String, Student> byEmail;
        private HashMap<String, float[]> groupStats;

        public Variant3(List<Student> students) {
            ratingMap = new TreeMap<>(Comparator.reverseOrder());
            byEmail = new HashMap<>();
            groupStats = new HashMap<>();
            for (Student s : students) {
                ratingMap.computeIfAbsent(s.getRating(), k -> new ArrayList<>()).add(s);
                byEmail.put(s.getEmail(), s);
                groupStats.computeIfAbsent(s.getGroup(), k -> new float[]{0, 0});
                float[] g = groupStats.get(s.getGroup());
                g[0] += s.getRating();
                g[1]++;
            }
        }

        public List<Student> getTop100() {
            List<Student> res = new ArrayList<>();
            for (var entry : ratingMap.entrySet()) {
                for (Student s : entry.getValue()) {
                    if (res.size() >= 100) return res;
                    res.add(s);
                }
            }
            return res;
        }

        public void setRating(String email, float newR) {
            Student s = byEmail.get(email);
            if (s == null) return;
            List<Student> oldList = ratingMap.get(s.getRating());
            if (oldList != null) oldList.remove(s);
            s.setRating(newR);
            ratingMap.computeIfAbsent(newR, k -> new ArrayList<>()).add(s);
        }

        public String bestGroup() {
            String best = null;
            double max = -1;
            for (var e : groupStats.entrySet()) {
                double avg = e.getValue()[0] / e.getValue()[1];
                if (avg > max) {
                    max = avg;
                    best = e.getKey();
                }
            }
            return best;
        }
    }

    /* ----------------------------------------------------
     *  DEMO — для перевірки роботи
     * ---------------------------------------------------- */
    public void runDemo() {
        System.out.println("\n=== Task 2 Demo ===");

        List<Student> demoStudents = new ArrayList<>(List.of(
            new Student("Anna", "Melnyk", "anna@student.org", 2000, 5, 14, "A1", 85.5f, "(099)123-45-67"),
            new Student("Bohdan", "Koval", "bohdan@student.org", 2001, 7, 3, "A1", 91.3f, "(067)234-56-78"),
            new Student("Olha", "Lys", "olha@student.org", 2002, 1, 20, "B2", 75.2f, "(050)345-67-89")
        ));

        Variant1 v1 = new Variant1(demoStudents);
        System.out.println("Top student (TreeSet+HashMap): " + v1.getTop100().get(0).getName());
        System.out.println("Best group: " + v1.bestGroup());
    }

    /* ----------------------------------------------------
     *  BENCHMARK
     * ---------------------------------------------------- */
    public static void runBenchmark(List<Student> students, int A, int B, int C) {
        Random rnd = new Random();
        int[] sizes = {100, 1000, 10000, 100000};

        System.out.println("\n=== BENCHMARK (A:B:C = " + A + ":" + B + ":" + C + ") ===");

        for (int n : sizes) {
            List<Student> subset = students.subList(0, Math.min(n, students.size()));
            long[] ops = new long[3];

            Variant1 v1 = new Variant1(subset);
            ops[0] = benchmarkVariant(() -> randomOpV1(v1, subset, rnd, A, B, C));

            Variant2 v2 = new Variant2(subset);
            ops[1] = benchmarkVariant(() -> randomOpV2(v2, subset, rnd, A, B, C));

            Variant3 v3 = new Variant3(subset);
            ops[2] = benchmarkVariant(() -> randomOpV3(v3, subset, rnd, A, B, C));

            System.out.printf("Розмір %6d | TreeSet+HashMap: %-8d | ArrayList: %-8d | TreeMap: %-8d%n",
                    n, ops[0], ops[1], ops[2]);
        }
    }

    private static long benchmarkVariant(Runnable action) {
        long start = System.currentTimeMillis();
        long end = start + 10_000;
        long ops = 0;
        while (System.currentTimeMillis() < end) {
            action.run();
            ops++;
        }
        return ops;
    }

    private static void randomOpV1(Variant1 v, List<Student> s, Random rnd, int A, int B, int C) {
        int total = A + B + C;
        int r = rnd.nextInt(total);
        if (r < A) v.getTop100();
        else if (r < A + B) v.setRating(s.get(rnd.nextInt(s.size())).getEmail(), rnd.nextFloat() * 100);
        else v.bestGroup();
    }

    private static void randomOpV2(Variant2 v, List<Student> s, Random rnd, int A, int B, int C) {
        int total = A + B + C;
        int r = rnd.nextInt(total);
        if (r < A) v.getTop100();
        else if (r < A + B) v.setRating(s.get(rnd.nextInt(s.size())).getEmail(), rnd.nextFloat() * 100);
        else v.bestGroup();
    }

    private static void randomOpV3(Variant3 v, List<Student> s, Random rnd, int A, int B, int C) {
        int total = A + B + C;
        int r = rnd.nextInt(total);
        if (r < A) v.getTop100();
        else if (r < A + B) v.setRating(s.get(rnd.nextInt(s.size())).getEmail(), rnd.nextFloat() * 100);
        else v.bestGroup();
    }
    public static long benchmarkVariant1(List<Student> students, int A, int B, int C) {
        Variant1 v1 = new Variant1(students);
        return benchmarkVariant(() -> randomOpV1(v1, students, new Random(), A, B, C));
    }

    public static long benchmarkVariant2(List<Student> students, int A, int B, int C) {
        Variant2 v2 = new Variant2(students);
        return benchmarkVariant(() -> randomOpV2(v2, students, new Random(), A, B, C));
    }

    public static long benchmarkVariant3(List<Student> students, int A, int B, int C) {
        Variant3 v3 = new Variant3(students);
        return benchmarkVariant(() -> randomOpV3(v3, students, new Random(), A, B, C));
    }

}
