package services;

import modal.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ExpenseService {
    private static final String DB_URL = "jdbc:sqlite:expenses.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createTableQuery = """
                    CREATE TABLE IF NOT EXISTS expenses (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        category TEXT NOT NULL,
                        description TEXT,
                        amount REAL NOT NULL,
                        date TEXT NOT NULL
                    );
                    """;
            Statement stmt = conn.createStatement();
            stmt.execute(createTableQuery);
        } catch (SQLException e) {
            System.err.println("Error initializing the database: " + e.getMessage());
        }
    }

    public static void addExpense(Scanner scanner) {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount;
        try {
            amount = scanner.nextDouble();
            scanner.nextLine(); // Clear buffer
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for amount. Please enter a numeric value.");
            scanner.nextLine(); // Clear invalid input
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String insertQuery = "INSERT INTO expenses (category, description, amount, date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, category);
            pstmt.setString(2, description);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.executeUpdate();
            System.out.println("Expense added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
        }
    }

    public static void editExpense(Scanner scanner) {
        System.out.print("Enter expense ID to edit: ");
        int id;
        try {
            id = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String selectQuery = "SELECT * FROM expenses WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.print("New category (current: " + rs.getString("category") + "): ");
                String newCategory = scanner.nextLine();
                System.out.print("New description (current: " + rs.getString("description") + "): ");
                String newDescription = scanner.nextLine();
                System.out.print("New amount (current: " + rs.getDouble("amount") + "): ");
                double newAmount;
                try {
                    newAmount = scanner.nextDouble();
                    scanner.nextLine(); // Clear buffer
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input for amount. No changes made to this field.");
                    scanner.nextLine(); // Clear invalid input
                    return;
                }

                String updateQuery = "UPDATE expenses SET category = ?, description = ?, amount = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, newCategory.isEmpty() ? rs.getString("category") : newCategory);
                updateStmt.setString(2, newDescription.isEmpty() ? rs.getString("description") : newDescription);
                updateStmt.setDouble(3, newAmount);
                updateStmt.setInt(4, id);
                updateStmt.executeUpdate();
                System.out.println("Expense updated successfully!");
            } else {
                System.out.println("Expense ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
        }
    }

    public static void deleteExpense(Scanner scanner) {
        System.out.print("Enter expense ID to delete: ");
        int id;
        try {
            id = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String deleteQuery = "DELETE FROM expenses WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expense deleted successfully!");
            } else {
                System.out.println("Expense ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
        }
    }

    public static void viewExpenses() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String selectQuery = "SELECT * FROM expenses";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectQuery);

            if (!rs.isBeforeFirst()) {
                System.out.println("No expenses recorded.");
                return;
            }

            System.out.println("Recorded Expenses:");
            while (rs.next()) {
                System.out.printf("ID: %d | Category: %s | Description: %s | Amount: %.2f | Date: %s%n",
                        rs.getInt("id"), rs.getString("category"), rs.getString("description"),
                        rs.getDouble("amount"), rs.getString("date"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expenses: " + e.getMessage());
        }
    }

    public static void generateReport() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String selectQuery = "SELECT * FROM expenses";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectQuery);

            if (!rs.isBeforeFirst()) {
                System.out.println("No expenses to generate a report.");
                return;
            }

            System.out.println("Expense Report:");
            double total = 0;
            while (rs.next()) {
                System.out.printf("ID: %d | Category: %s | Description: %s | Amount: %.2f | Date: %s%n",
                        rs.getInt("id"), rs.getString("category"), rs.getString("description"),
                        rs.getDouble("amount"), rs.getString("date"));
                total += rs.getDouble("amount");
            }
            System.out.println("Total Expenses: " + total);
        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
}
