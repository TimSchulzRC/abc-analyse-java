package timschulz.abcxyzanalysejava;

import timschulz.abcxyzanalysejava.database.Database;

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        MainApplication.main(args);
    }
}
