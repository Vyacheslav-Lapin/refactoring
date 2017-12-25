package ru.vlapin.patterns.adapter;

public class Singleton {

    private static Singleton instance;

    private Singleton() {
    }

    public static String getString() {
        return "Нечто важное!";
    }

    public static Singleton getInstance() {
        if (instance == null)
            synchronized (Singleton.class) {
                if (instance == null)
                    instance = new Singleton();
            }

        return instance;
    }
}
