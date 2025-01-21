import modal.Expense;
import services.ExpenseService;

import java.util.*;
import java.time.LocalDate;

public class ExpenseManagerCLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nExpense Management System");
            System.out.println("1. Add Expense");
            System.out.println("2. Edit Expense");
            System.out.println("3. Delete Expense");
            System.out.println("4. View Expenses");
            System.out.println("5. Generate Report");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (choice) {
                case 1 -> ExpenseService.addExpense(scanner);
                case 2 -> ExpenseService.editExpense(scanner);
                case 3 -> ExpenseService.deleteExpense(scanner);
                case 4 -> ExpenseService.viewExpenses();
                case 5 -> ExpenseService.generateReport();
                case 6 -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}
