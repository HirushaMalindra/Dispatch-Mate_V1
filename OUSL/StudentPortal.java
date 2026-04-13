package OUSL;

import java.util.Scanner;

public class StudentPortal {

    private Scanner  scanner;
    private String[] currentStudent;

    public StudentPortal(Scanner scanner, String[] currentStudent) {
        this.scanner        = scanner;
        this.currentStudent = currentStudent;
    }

    public void start() {
        printHeader("STUDENT PORTAL",
                "Welcome, " + currentStudent[2] + "!  [ ID: " + currentStudent[3] + " ]");

        boolean inPortal = true;
        while (inPortal) {
            showMenu();
            int choice = readInt();

            switch (choice) {
                case 1: checkBookAvailability();   break;
                case 2: reserveBook();             break;
                case 3: viewMyTokens();            break;
                case 4: subscribeToRestockAlert(); break;
                case 5: viewMyReservations();      break;
                case 0:
                    System.out.println("\n  Logging out... See you next time, "
                            + currentStudent[2] + "!\n");
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
        System.out.println("│" + YELLOW + "          STUDENT PORTAL MENU           " + CYAN + "│");
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│ " + RESET + "1. Check Book Availability           " + CYAN + "│");
        System.out.println("│ " + RESET + "2. Reserve a Book                    " + CYAN + "│");
        System.out.println("│ " + RESET + "3. View My Collection Tokens         " + CYAN + "│");
        System.out.println("│ " + RESET + "4. Subscribe to Restock Alert        " + CYAN + "│");
        System.out.println("│ " + RESET + "5. View My Reservations              " + CYAN + "│");
        System.out.println("│ " + RESET + "0. Logout                            " + CYAN + "│");
        System.out.println("└────────────────────────────────────────┘" + RESET);
        System.out.print(GREEN + " Enter your choice: " + RESET);
    }

    private void checkBookAvailability() {
        try {
            System.out.println("\n  ── CHECK BOOK AVAILABILITY ──────────────────");

            if (DataStore.books.isEmpty()) {
                throw new Exception("The book catalogue is currently empty.");
            }

            System.out.println();
            System.out.printf("  %-6s  %-42s  %-14s  %s%n",
                    "ID", "TITLE", "STATUS", "COPIES");
            System.out.println("  " + "─".repeat(75));

            for (Book book : DataStore.books) {
                String statusMark = book.getAvailableCopies() > 0 ? "[OK]" : "[--]";
                System.out.printf("  %-6s  %-42s  %s %-10s  %d/%d%n",
                        book.getBookId(),
                        book.getTitle(),
                        statusMark,
                        book.getStatus(),
                        book.getAvailableCopies(),
                        book.getTotalCopies());
            }

            System.out.println();
            System.out.print("  Enter a Book ID for full details (or press Enter to go back): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) return;

            Book book = DataStore.findBookById(input);
            if (book == null) {
                throw new IllegalArgumentException("No book found with ID: '" + input + "'");
            }

            System.out.println();
            System.out.println("  ┌─ BOOK DETAILS ──────────────────────────────");
            System.out.println("  │  ID          : " + book.getBookId());
            System.out.println("  │  Title       : " + book.getTitle());
            System.out.println("  │  Faculty     : " + book.getFaculty());
            System.out.println("  │  Department  : " + book.getDepartment());
            System.out.println("  |  Level       : " + book.getLevel() );
            System.out.println("  │  Status      : " + book.getStatus());
            System.out.println("  │  Copies      : " + book.getAvailableCopies()
                    + " available out of " + book.getTotalCopies() + " total");
            System.out.println("  └─────────────────────────────────────────────");

        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [!] System Info: " + e.getMessage());
        }
    }


    private void reserveBook() {
        try {
            System.out.println("\n  ── RESERVE A BOOK ───────────────────────────");

            System.out.println("  Books currently in stock:");
            boolean anyAvailable = false;
            for (Book b : DataStore.books) {
                if (b.getAvailableCopies() > 0) {
                    System.out.printf("    %-6s  %s%n", b.getBookId(), b.getTitle());
                    anyAvailable = true;
                }
            }
            if (!anyAvailable) {
                throw new IllegalStateException(
                        "All books are currently out of stock. Try subscribing for restock alerts (Option 4).");
            }

            System.out.println();
            System.out.print("  Enter Book ID to reserve: ");
            String bookId = scanner.nextLine().trim();

            if (bookId.isEmpty()) {
                throw new IllegalArgumentException("Book ID cannot be empty.");
            }

            Book book = DataStore.findBookById(bookId);
            if (book == null) {
                throw new IllegalArgumentException("No book found with ID: '" + bookId + "'");
            }

            if (book.getAvailableCopies() <= 0) {
                throw new IllegalStateException(
                        "'" + book.getTitle() + "' is Out of Stock.\n"
                                + "  Tip: Go to Option 4 to subscribe for a restock alert.");
            }

            if (DataStore.hasActiveReservation(currentStudent[0], bookId)) {
                throw new IllegalStateException(
                        "You already have an active reservation for this book.");
            }

            String reservationId   = DataStore.generateReservationId();
            String collectionToken = DataStore.generateToken(currentStudent[3], bookId);

            Reservation reservation = new Reservation(
                    reservationId,
                    currentStudent[0],
                    currentStudent[2],
                    bookId,
                    book.getTitle(),
                    book.getLevel(),
                    collectionToken
            );

            DataStore.reservations.add(reservation);
            book.setAvailableCopies(book.getAvailableCopies() - 1);

            System.out.println();
            System.out.println("  ✓ RESERVATION SUCCESSFUL!");
            System.out.println("    Book    : " + book.getTitle());
            System.out.println("    Reserved: " + reservation.getReservedDate());
            printCollectionToken(collectionToken, reservationId);

        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Input Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("  [!] Reservation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [!] Unexpected Error: " + e.getMessage());
        }
    }

    private void viewMyTokens() {
        try {
            System.out.println("\n  ── MY COLLECTION TOKENS ─────────────────────");

            boolean found = false;
            for (Reservation r : DataStore.reservations) {

                if (r.getStudentUsername().equals(currentStudent[0]) && !r.isCollected()) {
                    found = true;
                    System.out.println("  Book : " + r.getBookTitle());
                    printCollectionToken(r.getCollectionToken(), r.getReservationId());
                    System.out.println();
                }
            }

            if (!found) {

                throw new Exception("You have no active collection tokens right now.");
            }

        } catch (Exception e) {
            System.out.println("  [i] " + e.getMessage());
        }
    }

    private void subscribeToRestockAlert() {
        try {
            System.out.println("\n  ── SUBSCRIBE TO RESTOCK ALERT ───────────────");
            System.out.println("  Books currently out of stock:");
            boolean hasOutOfStock = false;
            for (Book b : DataStore.books) {
                if (b.getAvailableCopies() == 0) {
                    hasOutOfStock = true;
                    System.out.printf("    %-6s  %s%n", b.getBookId(), b.getTitle());
                }
            }

            if (!hasOutOfStock) {
                throw new Exception("Great news – all books are currently in stock! No alerts needed.");
            }

            System.out.println();
            System.out.print("  Enter Book ID to subscribe for a restock alert: ");
            String bookId = scanner.nextLine().trim();

            if (bookId.isEmpty()) {
                throw new IllegalArgumentException("Book ID cannot be empty.");
            }

            Book book = DataStore.findBookById(bookId);
            if (book == null) {
                throw new IllegalArgumentException("No book found with ID: '" + bookId + "'");
            }

            if (book.getAvailableCopies() > 0) {
                System.out.println("  [i] This book is already in stock! Go ahead and reserve it (Option 2).");
                return;
            }

            if (DataStore.isSubscribed(currentStudent[0], bookId)) {
                throw new IllegalStateException(
                        "You are already subscribed for restock alerts on this book.");
            }

            DataStore.alertSubscriptions.add(
                    new String[]{currentStudent[0], bookId, currentStudent[2]}
            );

            System.out.println("  ✓ Subscribed! You will receive an alert when '");
            System.out.println("    " + book.getTitle() + "' is restocked.");

        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Input Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("  [!] Subscription Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [i] " + e.getMessage());
        }
    }

    private void viewMyReservations() {
        try {
            System.out.println("\n  ── MY RESERVATIONS ──────────────────────────");

            boolean found = false;
            int     count = 1;
            System.out.printf("  %-4s %-11s %-28s %-20s %-10s%n",
                    "#", "RES ID", "BOOK", "TOKEN", "STATUS");
            System.out.println("  " + "─".repeat(80));

            for (Reservation r : DataStore.reservations) {
                if (r.getStudentUsername().equals(currentStudent[0])) {
                    found = true;
                    String bookShort = r.getBookTitle().length() > 26
                            ? r.getBookTitle().substring(0, 23) + "..."
                            : r.getBookTitle();
                    System.out.printf("  %-4d %-11s %-28s %-20s %-10s%n",
                            count++,
                            r.getReservationId(),
                            bookShort,
                            r.getCollectionToken(),
                            r.isCollected() ? "Collected" : "Pending");
                }
            }

            if (!found) {
                throw new Exception("You have no reservations yet. Use Option 2 to reserve a book.");
            }

        } catch (Exception e) {
            System.out.println("  [i] " + e.getMessage());
        }
    }


    private void printCollectionToken(String token, String reservationId) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║       OUSL COLLECTION TOKEN          ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf( "  ║  Reservation : %-22s║%n", reservationId);
        System.out.println("  ║                                      ║");
        System.out.printf( "  ║  Token: %-29s║%n", token);
        System.out.println("  ║                                      ║");
        printAsciiQR(token);
        System.out.println("  ║                                      ║");
        System.out.println("  ║  Show this token at the Dispatch     ║");
        System.out.println("  ║  Counter to collect your book.       ║");
        System.out.println("  ╚══════════════════════════════════════╝");
    }


    private void printAsciiQR(String token) {

        int  hash = Math.abs(token.hashCode());
        String bits = String.format("%32s", Integer.toBinaryString(hash)).replace(' ', '0');

        for (int row = 0; row < 4; row++) {
            System.out.print("  ║  ");
            for (int col = 0; col < 8; col++) {
                char bit = bits.charAt(row * 8 + col);
                System.out.print(bit == '1' ? "██" : "  ");
            }
            System.out.println("  ║");
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
