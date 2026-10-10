package org.example;
import java.io.*;
import java.util.*;

public class Benchmark {
    static final int[] SIZES = {100, 1_000, 10_000};
    static final int RUNS = 20;
    static final int WARMUP = 5;

    interface Structure {
        boolean insert(int k);
        boolean search(int k);
        boolean delete(int k);
    }

    static Structure bst() {
        BST t = new BST();
        return new Structure() {
            public boolean insert(int k) { return t.insert(k); }
            public boolean search(int k) { return t.search(k); }
            public boolean delete(int k) { return t.delete(k); }
        };
    }

    static Structure hash() {
        HashTable h = new HashTable();
        return new Structure() {
            public boolean insert(int k) { return h.insert(k); }
            public boolean search(int k) { return h.search(k); }
            public boolean delete(int k) { return h.delete(k); }
        };
    }

    static double[] measure(Supplier s, int[] data) {
        Structure st = s.get();
        long t0 = System.nanoTime();
        for (int k : data) st.insert(k);
        long t1 = System.nanoTime();
        for (int k : data) st.search(k);
        long t2 = System.nanoTime();
        for (int k : data) st.delete(k);
        long t3 = System.nanoTime();
        return new double[]{(t1 - t0) / 1e6, (t2 - t1) / 1e6, (t3 - t2) / 1e6};
    }

    interface Supplier { Structure get(); }

    static void run(String label, Supplier s, int[] data) {
        for (int i = 0; i < WARMUP; i++) measure(s, data);
        double[] sum = new double[3];
        for (int i = 0; i < RUNS; i++) {
            double[] r = measure(s, data);
            for (int j = 0; j < 3; j++) sum[j] += r[j];
        }
        System.out.printf("%-12s n=%-6d insert=%8.4f ms  search=%8.4f ms  delete=%8.4f ms%n",
                label, data.length, sum[0] / RUNS, sum[1] / RUNS, sum[2] / RUNS);
    }

    public static void main(String[] args) throws IOException {
        PrintStream file = new PrintStream(new FileOutputStream("results.txt"), true);
        PrintStream console = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                console.write(b);
                file.write(b);
            }
        }, true));
        Random rnd = new Random(42);
        System.out.println("=== Random keys (average case) ===");
        for (int n : SIZES) {
            int[] data = randomUnique(n, rnd);
            run("BST", Benchmark::bst, data);
            run("HashTable", Benchmark::hash, data);
        }
        System.out.println("\n=== Sorted keys (BST worst case) ===");
        for (int n : SIZES) {
            int[] data = new int[n];
            for (int i = 0; i < n; i++) data[i] = i;
            run("BST", Benchmark::bst, data);
            run("HashTable", Benchmark::hash, data);
        }
        System.out.println("\n=== BST height ===");
        for (int n : SIZES) {
            BST r = new BST();
            for (int k : randomUnique(n, new Random(1))) r.insert(k);
            BST s = new BST();
            for (int i = 0; i < n; i++) s.insert(i);
            System.out.printf("n=%-6d random height=%-4d sorted height=%d%n", n, r.height(), s.height());
        }
    }

    static int[] randomUnique(int n, Random rnd) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(i);
        Collections.shuffle(list, rnd);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = list.get(i);
        return a;
    }
}
