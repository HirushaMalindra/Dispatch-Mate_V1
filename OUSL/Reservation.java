package OUSL;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {

    private String  reservationId;
    private String  studentUsername;
    private String  studentName;
    private String  bookId;
    private String  bookTitle;
    private int level;
    private String  collectionToken;
    private boolean isCollected;
    private String  reservedDate;

    public Reservation(String reservationId, String studentUsername,
                       String studentName, String bookId,
                       String bookTitle, int level , String collectionToken) {

        this.reservationId   = reservationId;
        this.studentUsername = studentUsername;
        this.studentName     = studentName;
        this.bookId          = bookId;
        this.bookTitle       = bookTitle;
        this.level           = level;
        this.collectionToken = collectionToken;
        this.isCollected     = false;


        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.reservedDate = LocalDateTime.now().format(fmt);
    }


    public String  getReservationId()   { return reservationId; }
    public String  getStudentUsername() { return studentUsername; }
    public String  getStudentName()     { return studentName; }
    public String  getBookId()          { return bookId; }
    public String  getBookTitle()       { return bookTitle; }
    public int getLevel()           { return level; }
    public String  getCollectionToken() { return collectionToken; }
    public boolean isCollected()        { return isCollected; }
    public String  getReservedDate()    { return reservedDate; }


    public void setCollected(boolean collected) { this.isCollected = collected; }


    @Override
    public String toString() {
        String status = isCollected ? "Collected" : "Pending";
        return String.format("%-11s | %-28s | %-20s | %-10s | %s",
                reservationId,
                bookTitle.length() > 28 ? bookTitle.substring(0, 25) + "..." : bookTitle,
                collectionToken,
                status,
                reservedDate);
    }
}
