package OUSL;

import java.sql.SQLOutput;
import java.util.Scanner;

public class AdminPortal {

    private Scanner  scanner;
    private String[] currentAdmin; // {username, password, name}

    public AdminPortal(Scanner scanner, String[] currentAdmin) {
        this.scanner      = scanner;
        this.currentAdmin = currentAdmin;
    }


    public void start() {
        printHeader("ADMIN PORTAL", "Welcome, " + currentAdmin[2] + "!");

        boolean inPortal = true;
        while (inPortal) {
            showMenu();
            int choice = readInt();

            switch (choice) {
                case 1: updateBookStock();       break;
                case 2: checkAllReservations();  break;
                case 3: scanCollectionToken();   break;
                case 4: addNewBook();            break;
                case 5: viewAlertSubscribers();  break;
                case 0:
                    System.out.println("\n  Logging out of Admin Portal. Goodbye!\n");
                    inPortal = false;
                    break;
                default:
                    System.out.println("  [!] Invalid choice. Please pick 1-5 or 0.");
            }
        }
    }


    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";

    private void showMenu() {
        System.out.println("\n" + CYAN + "┌────────────────────────────────────────┐");
        System.out.println("│" + YELLOW + "           ADMIN PORTAL MENU            " + CYAN + "│");
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│ " + RESET + "1. Update Book Stock                 " + CYAN + "│");
        System.out.println("│ " + RESET + "2. Check All Reservations            " + CYAN + "│");
        System.out.println("│ " + RESET + "3. Scan / Verify Collection Token    " + CYAN + "│");
        System.out.println("│ " + RESET + "4. Add New Book to Catalogue         " + CYAN + "│");
        System.out.println("│ " + RESET + "5. View Restock Alert Subscribers    " + CYAN + "│");
        System.out.println("│ " + RESET + "0. Logout                            " + CYAN + "│");
        System.out.println("└────────────────────────────────────────┘" + RESET);
        System.out.print(GREEN + " Enter your choice: " + RESET);
    }

    private void updateBookStock() {
        try {
            System.out.println("\n  ── UPDATE BOOK STOCK ────────────────────────");

            if (DataStore.books.isEmpty()) {
                throw new Exception("No books in the catalogue yet.");
            }

            System.out.printf("  %-6s  %-40s  %9s  %7s%n",
                    "ID", "TITLE", "AVAILABLE", "TOTAL");
            System.out.println("  " + "─".repeat(68));
            for (Book b : DataStore.books) {
                System.out.printf("  %-6s  %-40s  %9d  %7d%n",
                        b.getBookId(), b.getTitle(),
                        b.getAvailableCopies(), b.getTotalCopies());
            }

            System.out.println();
            System.out.print("  Enter Book ID to update: ");
            String bookId = scanner.nextLine().trim();

            if (bookId.isEmpty()) {
                throw new IllegalArgumentException("Book ID cannot be empty.");
            }

            Book book = DataStore.findBookById(bookId);
            if (book == null) {
                throw new IllegalArgumentException("No book found with ID: '" + bookId + "'");
            }

            System.out.println("  Book           : " + book.getTitle());
            System.out.println("  Current Total  : " + book.getTotalCopies());
            System.out.println("  Now Available  : " + book.getAvailableCopies());
            System.out.print("  Enter NEW total number of copies: ");


            int newTotal = Integer.parseInt(scanner.nextLine().trim());

            if (newTotal < 0) {
                throw new IllegalArgumentException("Number of copies cannot be negative.");
            }


            int difference    = newTotal - book.getTotalCopies();
            int newAvailable  = Math.max(0, book.getAvailableCopies() + difference);

            book.setTotalCopies(newTotal);
            book.setAvailableCopies(newAvailable);

            System.out.println("\n  ✓ Stock updated successfully!");
            System.out.println("    " + book.getTitle());
            System.out.println("    New Total     : " + book.getTotalCopies());
            System.out.println("    New Available : " + book.getAvailableCopies());

            if (difference > 0 && newAvailable > 0) {
                notifySubscribers(bookId, book.getTitle());
            }

        } catch (NumberFormatException e) {
            System.out.println("  [!] Input Error: Please enter a valid whole number for copies.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [!] System Error: " + e.getMessage());
        }
    }


    private void checkAllReservations() {
        try {
            System.out.println("\n  ── ALL RESERVATIONS ─────────────────────────");

            if (DataStore.reservations.isEmpty()) {
                throw new Exception("No reservations have been made yet.");
            }

            System.out.println("  Filter options:");
            System.out.println("    1. All reservations");
            System.out.println("    2. Pending only");
            System.out.println("    3. Collected only");
            System.out.print("  Enter filter choice (default 1): ");

            String filterStr = scanner.nextLine().trim();
            int filter = filterStr.isEmpty() ? 1 : Integer.parseInt(filterStr);

            if (filter < 1 || filter > 3) {
                throw new IllegalArgumentException("Filter must be 1, 2 or 3.");
            }


            System.out.println();
            System.out.printf("  %-11s  %-20s  %-28s  %-22s  %-10s%n",
                    "RES ID", "STUDENT", "BOOK", "TOKEN", "STATUS");
            System.out.println("  " + "─".repeat(97));

            boolean found = false;
            for (Reservation r : DataStore.reservations) {
                boolean show = (filter == 1)
                        || (filter == 2 && !r.isCollected())
                        || (filter == 3 && r.isCollected());

                if (show) {
                    found = true;
                    String book = r.getBookTitle().length() > 26
                            ? r.getBookTitle().substring(0, 23) + "..."
                            : r.getBookTitle();
                    System.out.printf("  %-11s  %-20s  %-28s  %-22s  %-10s%n",
                            r.getReservationId(), r.getStudentName(), book,
                            r.getCollectionToken(),
                            r.isCollected() ? "Collected" : "Pending");
                }
            }

            if (!found) {
                System.out.println("  [i] No reservations match the selected filter.");
            }

        } catch (NumberFormatException e) {
            System.out.println("  [!] Input Error: Filter must be a number (1, 2 or 3).");
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [i] " + e.getMessage());
        }
    }

    private void scanCollectionToken() {
        try {
            System.out.println("\n  ── SCAN / VERIFY COLLECTION TOKEN ───────────");
            System.out.println("  (Type the token printed on the student's slip)");
            System.out.println();
            System.out.print("  Enter Collection Token: ");
            String token = scanner.nextLine().trim();

            if (token.isEmpty()) {
                throw new IllegalArgumentException("Token cannot be empty.");
            }

            Reservation res = DataStore.findReservationByToken(token);

            if (res == null) {
                throw new IllegalArgumentException(
                        "Token not recognised: '" + token + "'.\n"
                                + "  Please check the token and try again.");
            }

            System.out.println("\n  ✓ TOKEN VERIFIED");
            System.out.println("  ─────────────────────────────────────────────");
            System.out.println("  Reservation ID : " + res.getReservationId());
            System.out.println("  Student Name   : " + res.getStudentName());
            System.out.println("  Student User   : " + res.getStudentUsername());
            System.out.println("  Book           : " + res.getBookTitle());
            System.out.println("  Reserved On    : " + res.getReservedDate());
            System.out.println("  Current Status : " + (res.isCollected() ? "Already Collected" : "Pending Collection"));
            System.out.println("  ─────────────────────────────────────────────");

            if (res.isCollected()) {
                System.out.println("\n  ⚠  WARNING: This book has ALREADY been collected!");
                System.out.println("     Do NOT hand out another copy.");
            } else {
                System.out.print("\n  Mark this book as collected? (yes / no): ");
                String answer = scanner.nextLine().trim().toLowerCase();

                if (answer.equals("yes") || answer.equals("y")) {
                    res.setCollected(true);
                    System.out.println("  ✓ Marked as Collected.");
                    System.out.println("    '" + res.getStudentName()
                            + "' has collected: " + res.getBookTitle());
                } else {
                    System.out.println("  [i] Status unchanged – still Pending.");
                }
            }

        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [!] System Error: " + e.getMessage());
        }
    }

    private void addNewBook() {
        try {
            System.out.println("\n  ── ADD NEW BOOK TO CATALOGUE ────────────────");

            System.out.print("  Book ID   (e.g. COU3304) : ");
            String bookId = scanner.nextLine().trim();
            if (bookId.isEmpty()) throw new IllegalArgumentException("Book ID cannot be empty.");
            if (DataStore.findBookById(bookId) != null)
                throw new IllegalArgumentException("A book with ID '" + bookId + "' already exists.");

            System.out.print("  Title                  : ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) throw new IllegalArgumentException("Title cannot be empty.");

            System.out.print("  faculty                : ");
            String faculty = scanner.nextLine().trim();
            if (faculty.isEmpty()) throw new IllegalArgumentException("faculty cannot be empty.");

            System.out.print("  department             : ");
            String department = scanner.nextLine().trim();
            if (department.isEmpty()) department = "General";

            System.out.print("  Level                 : ");
            String levelInput = scanner.nextLine().trim();

            if (levelInput.isEmpty()) {
                throw new IllegalArgumentException("Level cannot be empty.");
            }

            int level = Integer.parseInt(levelInput);

            if (level < 3) {
                throw new IllegalArgumentException("Level must be 3 or higher.");
            }

            System.out.print("  Number of copies       : ");
            int copies = Integer.parseInt(scanner.nextLine().trim());
            if (copies < 0) throw new IllegalArgumentException("Copies cannot be negative.");

            Book newBook = new Book(bookId, title, faculty, department, level , copies);
            DataStore.books.add(newBook);

            System.out.println("\n  ✓ Book added successfully!");
            System.out.println("  " + newBook);

        } catch (NumberFormatException e) {
            System.out.println("  [!] Input Error: Copies must be a whole number.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [!] System Error: " + e.getMessage());
        }
    }

    private void viewAlertSubscribers() {
        try {
            System.out.println("\n  ── RESTOCK ALERT SUBSCRIBERS ────────────────");

            if (DataStore.alertSubscriptions.isEmpty()) {
                throw new Exception("No active alert subscriptions found.");
            }

            System.out.printf("  %-20s  %-8s  %-40s%n", "STUDENT", "BOOK ID", "BOOK TITLE");
            System.out.println("  " + "─".repeat(72));

            for (String[] sub : DataStore.alertSubscriptions) {
                String studentName = sub[2];
                String bookId      = sub[1];
                Book   book        = DataStore.findBookById(bookId);
                String bookTitle   = (book != null) ? book.getTitle() : "Unknown Book";
                System.out.printf("  %-20s  %-8s  %-40s%n", studentName, bookId, bookTitle);
            }

        } catch (Exception e) {
            System.out.println("  [i] " + e.getMessage());
        }
    }

    private void notifySubscribers(String bookId, String bookTitle) {
        boolean notifiedSomeone = false;
        System.out.println("\n  ── SENDING RESTOCK NOTIFICATIONS ────────────");
        for (String[] sub : DataStore.alertSubscriptions) {
            if (sub[1].equals(bookId)) {
                System.out.println("  ★ ALERT → " + sub[2]
                        + ": '" + bookTitle + "' is now back in stock!");
                notifiedSomeone = true;
            }
        }
        if (!notifiedSomeone) {
            System.out.println("  [i] No subscribers for this book – no alerts sent.");
        }
    }

    private void printHeader(String title, String subtitle) {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.printf( "  ║  %-37s║%n", title);
        System.out.printf( "  ║  %-37s║%n", subtitle);
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid number.");
            return -1;
        }
    }
}

