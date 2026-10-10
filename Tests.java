package org.example;
public class Tests {
    static int fails = 0;
    static void check(boolean c, String msg) { if (!c) { fails++; System.out.println("FAIL: " + msg); } }

    public static void main(String[] args) {
        BST b = new BST();
        HashTable h = new HashTable();
        int[] keys = {50, 30, 70, 20, 40, 60, 80, 35, 45};
        for (int k : keys) { check(b.insert(k), "bst insert " + k); check(h.insert(k), "ht insert " + k); }
        check(!b.insert(50) && !h.insert(50), "duplicates rejected");
        for (int k : keys) { check(b.search(k), "bst search " + k); check(h.search(k), "ht search " + k); }
        check(!b.search(99) && !h.search(99), "missing key not found");
        for (int k : new int[]{20, 30, 50, 80}) { check(b.delete(k), "bst delete " + k); check(h.delete(k), "ht delete " + k); }
        check(!b.delete(20) && !h.delete(20), "double delete fails");
        for (int k : new int[]{40, 35, 45, 60, 70}) { check(b.search(k), "bst still has " + k); check(h.search(k), "ht still has " + k); }
        check(b.size() == 5 && h.size() == 5, "sizes after deletes");
        System.out.println(fails == 0 ? "All tests passed" : fails + " test(s) failed");
    }
}
