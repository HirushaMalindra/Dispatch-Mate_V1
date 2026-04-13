package OUSL;

import java.util.ArrayList;

public class DataStore {

    public static ArrayList<String[]> students = new ArrayList<>();

    public static ArrayList<String[]> admins = new ArrayList<>();

    public static ArrayList<Book> books = new ArrayList<>();

    public static ArrayList<Reservation> reservations = new ArrayList<>();

    public static ArrayList<String[]> alertSubscriptions = new ArrayList<>();

    private static int reservationCounter = 1000;

    public static void initialize() {

        students.add(new String[]{"student1", "pass123",  "Hirusha Perera",   "S12345"});
        students.add(new String[]{"student2", "pass456",  "Kavindu Kumara",    "S67890"});
        students.add(new String[]{"student3", "pass78",   " Dinesh Perera",    "S526541"});


        admins.add(new String[]{"admin",     "admin123", "Head Admin Officer"});
        admins.add(new String[]{"staffmember", "sm456",   " Dispatch Unit Staff Member"});


        books.add(new Book("COU3301", "Database Management Systems",      "Natural Science",   "Computer Science",   3 ,  70 ));
        books.add(new Book("COU3306", "Data Structures and Algorithms",   "Natural Science",   "Computer Science",   3 ,  50));
        books.add(new Book("ITU5307", "Cybersecurity",                    "Natural Science",   "Computer Science",   5 ,  0));
        books.add(new Book("COU3304", "Fundamentals of Programming",      "Natural Science",   "Computer Science",   3 ,  55));
        books.add(new Book("COU3306", "Data Structures and Algorithms",   "Natural Science",   "Computer Science",   3 ,  65));
        books.add(new Book("COU4305", "Computer Networks",                "Natural Science",   "Computer Science",   4 ,  0));
        books.add(new Book("COU3303", "Software Engineering",             "Natural Science",   "Computer Science",   3 ,  0));
        books.add(new Book("ITU3201", "Web Development",                  "Natural Science",   "Computer Science",   3 ,  45));
    }


    public static String[] authenticateStudent(String username, String password) {
        for (String[] s : students) {
            if (s[0].equals(username) && s[1].equals(password)) {
                return s;
            }
        }
        return null;
    }


    public static String[] authenticateAdmin(String username, String password) {
        for (String[] a : admins) {
            if (a[0].equals(username) && a[1].equals(password)) {
                return a;
            }
        }
        return null;
    }


    public static Book findBookById(String bookId) {
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(bookId)) {
                return b;
            }
        }
        return null;
    }


    public static boolean hasActiveReservation(String studentUsername, String bookId) {
        for (Reservation r : reservations) {
            if (r.getStudentUsername().equals(studentUsername)
                    && r.getBookId().equals(bookId)
                    && !r.isCollected()) {
                return true;
            }
        }
        return false;
    }


    public static String generateReservationId() {
        reservationCounter++;
        return "RES-" + reservationCounter;
    }


    public static String generateToken(String studentId, String bookId) {
        String raw   = studentId + bookId + System.currentTimeMillis();
        int    hash  = Math.abs(raw.hashCode()) % 1_000_000;
        return "TKN-" + bookId + "-" + String.format("%06d", hash);
    }


    public static Reservation findReservationByToken(String token) {
        for (Reservation r : reservations) {
            if (r.getCollectionToken().equalsIgnoreCase(token.trim())) {
                return r;
            }
        }
        return null;
    }


    public static boolean isSubscribed(String studentUsername, String bookId) {
        for (String[] sub : alertSubscriptions) {
            if (sub[0].equals(studentUsername) && sub[1].equals(bookId)) {
                return true;
            }
        }
        return false;
    }
}

