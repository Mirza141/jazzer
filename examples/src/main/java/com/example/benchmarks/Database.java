package com.example.benchmarks;
import java.util.HashMap;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

class Database {
    final static int N = 1000;
    private final HashMap<Integer, Entry> entries = new HashMap<Integer, Entry>();
    private int counter = 0;

    public static void main(String[] args) {
        test(0, 1);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        test(data.consumeInt(), data.consumeInt(0,N));
    }

    public static void test(final int a, final int b) {
        final Database db = new Database();
        for (int id = 0; id < N; id++) {
            db.add(id);
        }
        Thread t1 = new Thread() {
            @Override
            public void run() {
                if(db.contains(a)) { db.replace(a, 2*N);  db.getId(2*N); }
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                if(db.contains(b)) { db.replace(b, 3*N);  db.getId(3*N); }
            }
        };
        t1.start();
        t2.start();
    }

    private boolean contains(int a) {
        if (entries.size() == 0) {
            throw new IllegalArgumentException("database is empty");
        }
        for (Entry entry : entries.values()) {
            if (entry.value == a) {
                return true;
            }
        }
        throw new IllegalArgumentException("value not found");
    }

    public void add(int value) {
        int id = counter++;
        entries.put(id, new Entry(id, value));
    }

    private void replace(int oldValue, int newValue) {
        for (Entry entry : entries.values()) {
            if (entry.value == oldValue) {
                int id = entry.id;
                entries.put(id, new Entry(id, newValue));
                return;
            }
        }
    }
    public int getId(int value) {
        if (entries.size() == 0) {
            throw new IllegalArgumentException("database is empty");
        }
        for (Entry entry : entries.values()) {
            if (entry.value == value) {
                return entry.id;
            }
        }
        throw new IllegalArgumentException("value not found");
    }

    private static class Entry {
        final int id;
        int value;

        public Entry(int id, int value) {
            this.id = id;
            this.value = value;
        }
    }
}