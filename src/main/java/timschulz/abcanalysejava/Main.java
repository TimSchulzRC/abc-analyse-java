package timschulz.abcanalysejava;

import timschulz.abcanalysejava.database.Database;

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        Database.setFromToDateofFirstRechnung();
        Database.setToToDateofLastRechnung();
        MainApplication.main(args);
    }
}
