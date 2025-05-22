import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExpenseTracker {
    private static final List<Transaction> transactions = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String MASTER_FILE = "transactions.txt";


    public static void run() {
        System.out.println("=== EXPENSE TRACKER ===");

        System.out.print("Do you want to import transactions from a file? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            System.out.print("Enter full file path (e.g., C:\\\\Users\\\\YourName\\\\input.txt): ");
            String filePath = scanner.nextLine();
            loadFromFile(filePath);
        }

        boolean running = true;
        while (running) {
            System.out.println("\n1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. Export to File");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1": addTransaction(Transaction.Type.INCOME); break;
                case "2": addTransaction(Transaction.Type.EXPENSE); break;
                case "3": viewMonthlySummary(); break;
                case "4":
                    System.out.print("Enter full file path to export (e.g., C:\\\\Users\\\\YourName\\\\output.txt): ");
                    String exportPath = scanner.nextLine();
                    saveToFile(exportPath);
                    break;
                case "5": running = false; break;
                default: System.out.println("Invalid option. Try again.");
            }
        }

        System.out.println("Thank you for using Expense Tracker!");
    }

    private static void addTransaction(Transaction.Type type) {
        System.out.println("Enter sub-category (" +
                (type == Transaction.Type.INCOME ? "Salary, Business" : "Food, Rent, Travel") + "):");
        String category = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter date in format yyyy-MM-dd (e.g., 2025-05-21): ");
        LocalDate date = LocalDate.parse(scanner.nextLine(), formatter);

        Transaction transaction = new Transaction(type, category, amount, date);
        transactions.add(transaction);
        System.out.println("Transaction added successfully.");

        // Append this transaction to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt", true))) {
            writer.write(transaction.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving transaction to file: " + e.getMessage());
        }
    }


    private static void viewMonthlySummary() {
        // Format: Map<Month, Map<Type, Map<Category, Amount>>>
        Map<String, Map<Transaction.Type, Map<String, Double>>> summary = new TreeMap<>();

        for (Transaction t : transactions) {
            String month = t.getDate().getYear() + "-" + String.format("%02d", t.getDate().getMonthValue());
            summary.putIfAbsent(month, new HashMap<>());

            Map<Transaction.Type, Map<String, Double>> typeMap = summary.get(month);
            typeMap.putIfAbsent(t.getType(), new HashMap<>());

            Map<String, Double> categoryMap = typeMap.get(t.getType());
            categoryMap.put(t.getCategory(), categoryMap.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
        }

        for (String month : summary.keySet()) {
            System.out.println("Month: " + month);
            System.out.println("-----------------------------------------------");
            System.out.printf("| %-8s | %-10s | %-20s |\n", "Type", "Category", "Amount");
            System.out.println("-----------------------------------------------");

            Map<Transaction.Type, Map<String, Double>> typeMap = summary.get(month);

            // Income Section
            double totalIncome = 0.0;
            if (typeMap.containsKey(Transaction.Type.INCOME)) {
                for (Map.Entry<String, Double> entry : typeMap.get(Transaction.Type.INCOME).entrySet()) {
                    System.out.printf("| %-8s | %-10s | %-20.2f |\n", "Income", entry.getKey(), entry.getValue());
                    totalIncome += entry.getValue();
                }
                System.out.printf("| %-8s | %-10s | %-20.2f |\n", "", "Total", totalIncome);
                System.out.println("-----------------------------------------------");
            }

            // Expense Section
            double totalExpense = 0.0;
            if (typeMap.containsKey(Transaction.Type.EXPENSE)) {
                for (Map.Entry<String, Double> entry : typeMap.get(Transaction.Type.EXPENSE).entrySet()) {
                    System.out.printf("| %-8s | %-10s | %-20.2f |\n", "Expense", entry.getKey(), entry.getValue());
                    totalExpense += entry.getValue();
                }
                System.out.printf("| %-8s | %-10s | %-20.2f |\n", "", "Total", totalExpense);
                System.out.println("-----------------------------------------------");
            }

            // Net Balance
            double net = totalIncome - totalExpense;
            System.out.printf("| %-22s | %-20.2f |\n", "Net Balance", net);
            System.out.println("-----------------------------------------------\n");
        }
    }


    public static void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Transaction t : transactions) {
                writer.write(t.toString());
                writer.newLine();
            }
            System.out.println("Transactions exported to: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public static void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<Transaction> importedTransactions = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction transaction = Transaction.fromString(line);
                transactions.add(transaction); // Store in memory
                importedTransactions.add(transaction); // For saving later
            }


            appendToMasterFile(importedTransactions);

            System.out.println("Transactions imported successfully and saved to master file.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }


    private static void appendToMasterFile(List<Transaction> newTransactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MASTER_FILE, true))) {
            for (Transaction t : newTransactions) {
                writer.write(t.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to master file: " + e.getMessage());
        }
    }

}
