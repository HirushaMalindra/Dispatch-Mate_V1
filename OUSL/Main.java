package OUSL;

import java.util.Scanner;



public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {


        DataStore.initialize();

        printWelcomeBanner();

        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = readInt();

            switch (choice) {
                case 1:
                    handleStudentLogin();
                    break;
                case 2:
                    handleAdminLogin();
                    break;
                case 0:
                    System.out.println("\n  ════════════════════════════════════════");
                    System.out.println("   Thank you for using OUSL Dispatch Mate.");
                    System.out.println("   Goodbye!");
                    System.out.println("  ════════════════════════════════════════\n");
                    running = false;
                    break;
                default:
                    System.out.println("  [!] Invalid choice. Please enter 1, 2, or 0.");
            }
        }

        scanner.close();
    }

    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    static void printWelcomeBanner() {
        System.out.println();
        System.out.println(CYAN + "╔════════════════════════════════════════════════════╗");
        System.out.println("║" + YELLOW + "              OUSL DISPATCH MATE                   " + CYAN + "║");
        System.out.println("║" + RESET + "         Course Material Collection System          " + CYAN + "║");
        System.out.println("║" + RESET + "            Open University of Sri Lanka            " + CYAN + "║");
        System.out.println("╚════════════════════════════════════════════════════╝" + RESET);
    }

    static void showMainMenu() {
        System.out.println();
        System.out.println(CYAN + "┌────────────────────────────────────────┐");
        System.out.println("│" + YELLOW + "               MAIN MENU                " + CYAN + "│");
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│ " + RESET + "1. Student Login                       " + CYAN + "│");
        System.out.println("│ " + RESET + "2. Admin Login                         " + CYAN + "│");
        System.out.println("│ " + RESET + "0. Exit                                " + CYAN + "│");
        System.out.println("└────────────────────────────────────────┘" + RESET);
        System.out.print(GREEN + " Enter your choice: " + RESET);
    }

    static void handleStudentLogin() {
        try {
            System.out.println("\n  ── STUDENT LOGIN ────────────────────────────");
            System.out.print("  Username: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty.");
            }

            System.out.print("  Password: ");
            String password = scanner.nextLine().trim();

            if (password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty.");
            }

            String[] student = DataStore.authenticateStudent(username, password);

            if (student == null) {
                throw new SecurityException("Incorrect username or password. Please try again.");
            }

            System.out.println("  ✓ Login successful!");

            StudentPortal portal = new StudentPortal(scanner, student);
            portal.start();

        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Input Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("  [!] Login Failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [!] Unexpected Error: " + e.getMessage());
        }
    }

    static void handleAdminLogin() {
        try {
            System.out.println("\n  ── ADMIN LOGIN ──────────────────────────────");
            System.out.print("  Username: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty.");
            }

            System.out.print("  Password: ");
            String password = scanner.nextLine().trim();

            if (password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty.");
            }

            String[] admin = DataStore.authenticateAdmin(username, password);

            if (admin == null) {
                throw new SecurityException("Incorrect username or password. Please try again.");
            }

            System.out.println("  ✓ Login successful!");

            AdminPortal portal = new AdminPortal(scanner, admin);
            portal.start();

        } catch (IllegalArgumentException e) {
            System.out.println("  [!] Input Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("  [!] Login Failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [!] Unexpected Error: " + e.getMessage());
        }
    }

    static int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid number.");
            return -1;
        }
    }
}

