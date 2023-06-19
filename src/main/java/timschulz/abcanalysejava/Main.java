package timschulz.abcanalysejava;

import timschulz.abcanalysejava.database.Database;

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        MainApplication.main(args);
    }
}
