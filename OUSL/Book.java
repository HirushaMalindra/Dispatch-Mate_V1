package OUSL;

public class Book {


    private String bookId;
    private String title;
    private String faculty;
    private String department;
    private int level;
    private int totalCopies;
    private int availableCopies;


    public Book(String bookId, String title, String faculty ,String department, int level, int totalCopies) {

        this.bookId          = bookId;
        this.title           = title;
        this.faculty         = faculty;
        this.department      = department;
        this.level           = level;
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;
    }


    public String getStatus() {
        if (availableCopies > 0) {
            return "Available";
        } else {
            return "Out of Stock";
        }
    }


    public String getBookId()          { return bookId; }
    public String getTitle()           { return title; }
    public String getFaculty()         { return faculty; }
    public String getDepartment()      { return department; }
    public int getLevel()              { return level; }
    public int getTotalCopies()        { return totalCopies; }
    public int getAvailableCopies()    { return availableCopies; }


    public void setTotalCopies(int totalCopies)         { this.totalCopies     = totalCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }


    @Override
    public String toString() {
        return String.format("[%s] %-42s | %-14s | Copies: %d / %d",
                bookId, title, getStatus(), availableCopies, totalCopies);
    }
}
