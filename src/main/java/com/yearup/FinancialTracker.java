package com.yearup;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;




public class FinancialTracker {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    //  private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT + " " + TIME_FORMAT);

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);


    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";
    //  private static final String DATE_FORMAT = "yyyy-MM-dd";
    // private static final String TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    // private static DateTimeFormatter DATE_TIME_FORMATTER;
    private static Object TransactionType;

    public static void main(String[] args) {


        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    addPayment(scanner);
                    break;
                case "L":
                    ledgerMenu(scanner);
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }

        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                //LocalDateTime dateTime = LocalDateTime.parse(fields[0] + " " + fields[1], DATE_TIME_FORMATTER);
                LocalDate date = LocalDate.parse(fields[0], DATE_FORMATTER);
                LocalTime time = LocalTime.parse(fields[1], TIME_FORMATTER);
                String description = fields[2];
                String vendor = fields[3];
                double amount = Double.parseDouble(fields[4]);
                transactions.add(new Transaction(date,time,description,vendor,amount));

            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }


    private static void addDeposit(Scanner scanner) {
        System.out.print("Enter deposit date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        System.out.print("Enter deposit vendor: ");
        String vendor = scanner.nextLine();
        System.out.print("Enter deposit amount: ");
        String amountStr = scanner.nextLine();

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                System.out.println("Invalid amount, please enter a positive number.");
                return;
            }

            LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Deposit deposit = new Deposit(date, vendor, amount);
            transactions.add(deposit);

            System.out.println("Deposit saved successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount, please enter a number.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format, please enter a date in the format yyyy-MM-dd.");
        }
    }


    private static void addPayment() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the date and time of the payment (yyyy-MM-dd HH:mm:ss):");
        LocalDateTime dateTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        while (dateTime == null) {
            String input = scanner.nextLine();
            try {
                dateTime = LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date and time format. Please enter in the format yyyy-MM-dd HH:mm:ss");
            }
        }

        System.out.println("Enter the vendor of the payment:");
        String vendor = scanner.nextLine();

        System.out.println("Enter the amount of the payment:");
        double amount = -1;
        while (amount < 0) {
            try {
                amount = Double.parseDouble(scanner.nextLine());
                if (amount < 0) {
                    System.out.println("Amount must be a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount format. Please enter a valid number.");
            }
        }

        Transaction payment = new Transaction(dateTime, vendor, amount);
        transactions.add(payment);
        System.out.println("Payment added to the transaction list.");
    }


    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A":
                    displayLedger();
                    break;
                case "D":
                    displayDeposits();
                    break;
                case "P":
                    displayPayments();
                    break;
                case "R":
                    reportsMenu(scanner);
                    break;
                case "H":
                    running = false;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void displayPayments() {

    }


    private static void displayLedger() {


        // Print the header row
        System.out.printf("%-12s %-10s %-30s %-20s %-15s\n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("-------------------------------------------------------------------------------------");
        // Print the transactions
        double balance = 0.0;
        for (Transaction transaction : transactions) {
            System.out.printf("%-12s %-10s %-30s %-20s $%-15.2f\n",
                    transaction.getDate(), transaction.getTime(),
                    transaction.getDescription(),
                    transaction.getVendor(),
                    transaction.getAmount());
            balance += transaction.getAmount();
        }

        // Print the footer row
        System.out.printf("%-12s %-10s %-20s %-10s %10.2f\n",
                "", "", "", "Total", balance);
    }


    private static void addPayment(Scanner scanner) {
        System.out.print("Enter the date and time (yyyy-MM-dd HH:mm:ss): ");
        String dateTimeString = scanner.nextLine();

        System.out.print("Enter the vendor name: ");
        String vendor = scanner.nextLine();

        double amount = -1;
        while (amount < 0) {
            System.out.print("Enter the payment amount: ");
            try {
                amount = Double.parseDouble(scanner.nextLine());
                if (amount < 0) {
                    System.out.println("Error: Payment amount must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid payment amount format.");
            }
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Deposit.Payment payment = new Deposit.Payment(dateTime, vendor, amount);
            transactions.add(payment);
            System.out.println("Payment added successfully.");
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date/time format.");
        }
    }

    private static void displayDeposits() {
        System.out.println("Deposits:");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-15s %-15s %-20s %-15s\n", "Date", "Time", "Vendor", "Amount");
        System.out.println("--------------------------------------------------------------------------");

        double totalDeposits = 0.0;
        for (Transaction t : transactions) {
            if (t instanceof Deposit) {
                System.out.printf("%-15s %-15s %-20s $%-15.2f\n", t.getDate(), t.getTime(), t.getVendor(), t.getAmount());
                totalDeposits += t.getAmount();
            }
        }

        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-51s $%-15.2f\n", "Total Deposits:", totalDeposits);
    }


    private static void reportsMenu(Scanner scanner) {

        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    MonthToDateReport();
                    // Generate a report for all transactions within the current month,
                    // including the date, vendor, and amount for each transaction.
                    // The report should include a total of all transaction amounts for the month.
                case "2":
                    MonthlyReport();
                    // Generate a report for all transactions within the previous month,
                    // including the date, vendor, and amount for each transaction.
                    // The report should include a total of all transaction amounts for the month.
                case "3":
                    YearlyReport();
                    // Generate a report for all transactions within the current year,
                    // including the date, vendor, and amount for each transaction.
                    // The report should include a total of all transaction amounts for the year.

                case "4":
                    PreviousYearReport();
                    // Generate a report for all transactions within the previous year,
                    // including the date, vendor, and amount for each transaction.
                    // The report should include a total of all transaction amounts for the year.
                case "5":
                    VendorReport();
                    // Prompt the user to enter a vendor name, then generate a report for all transactions
                    // with that vendor, including the date, vendor, and amount for each transaction.
                    // The report should include a total of all transaction amounts for the vendor.
                case "0":
                    running = false;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void MonthToDateReport() {
        // Get the current month and year
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        // Create a new ArrayList to hold all transactions within the current month
        ArrayList<Deposit.Payment> monthlyTransactions = new ArrayList<>();

        // Loop through all transactions and add those that occurred in the current month to the new ArrayList
        for (Transaction payment : transactions) {
            LocalDate paymentDate = payment.getPaymentDateTime().toLocalDate();
            if (paymentDate.getMonthValue() == month && paymentDate.getYear() == year) {
                monthlyTransactions.add((Deposit.Payment) payment);
            }
        }

        // Print the report header
        System.out.println("Monthly Report - " + currentDate.getMonth().toString() + " " + year);
        System.out.println("--------------------------------------------------");

        // Loop through all transactions in the current month and print the date, vendor, and amount for each transaction
        for (Deposit.Payment payment : monthlyTransactions) {
            System.out.println(payment.getPaymentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - " + payment.getVendor() + " - " + payment.getAmount());
        }

      /*  LocalDate currentDate = LocalDate.now();
        List<Transaction> transactionsForMonth = new ArrayList<>();
        double totalDeposits = 0.0;
        double totalPayments = 0.0;

        for (Transaction transaction : transactions) {
            LocalDateTime transactionDateTime = transaction.getDateTime();
            LocalDate transactionDate = transactionDateTime.toLocalDate();
            if (transactionDate.getMonth() == currentDate.getMonth()) {
                transactionsForMonth.add(transaction);
                if (transaction.getType().equals("DEPOSIT")) {
                    totalDeposits += transaction.getAmount();
                } else if (transaction.getType().equals("PAYMENT")) {
                    totalPayments += Math.abs(transaction.getAmount());
                }

            }
        }

        System.out.println("Month To Date Report");
        System.out.println("====================");
        System.out.println(String.format("Transactions for %s:\n", currentDate.getMonth().toString()));
        System.out.println("Date       | Vendor           | Amount   ");
        System.out.println("------------------------------------------");
        for (Transaction transaction : transactionsForMonth) {
            String formattedDate = transaction.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String vendor = transaction.getVendor();
            String amount = String.format("%.2f", transaction.getAmount());
            System.out.println(String.format("%s | %-15s | $%8s", formattedDate, vendor, amount));
        }
        System.out.println();
        System.out.println(String.format("Total Deposits: $%.2f", totalDeposits));
        System.out.println(String.format("Total Payments: $%.2f", totalPayments))

       */
    }







    private static void MonthlyReport() {
        LocalDate now = LocalDate.now();
        LocalDate previousMonth = now.minusMonths(1);

        double totalAmount = 0.0;

        System.out.printf("%-12s %-20s %-15s%n", "Date", "Vendor", "Amount");
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = (LocalDate) transaction.getDate();
            if (transactionDate.isAfter(ChronoLocalDate.from(previousMonth.atStartOfDay())) || transactionDate.isEqual(ChronoLocalDate.from(previousMonth.atStartOfDay()))) {
                System.out.printf("%-12s %-20s $%-15.2f%n", transactionDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        transaction.getVendor(), transaction.getAmount());
                totalAmount += transaction.getAmount();
            }
        }

        System.out.printf("%nTotal for %s: $%.2f%n", previousMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), totalAmount);
    }


    private static void YearlyReport() {

        LocalDate currentDate = LocalDate.now();
        double totalAmount = 0.0;

        System.out.printf("%-12s %-20s %-15s%n", "Date", "Vendor", "Amount");

        for (Transaction transaction : transactions) {
            LocalDate transactionDate = (LocalDate) transaction.getDate();
            if (transactionDate.getYear() == currentDate.getYear()) {
                System.out.printf("%-12s %-20s $%-15.2f%n", transactionDate, transaction.getVendor(), transaction.getAmount());
                totalAmount += transaction.getAmount();
            }
        }

        System.out.printf("Total for the year: $%.2f%n", totalAmount);
    }




    private static void PreviousYearReport() {
        LocalDate currentDate = LocalDate.now();
        LocalDate previousYear = currentDate.minusYears(1);

        double totalAmount = 0.0;

        System.out.printf("%-12s %-20s %-15s%n", "Date", "Vendor", "Amount");

        for (Transaction transaction : transactions) {
            LocalDate transactionDate = (LocalDate) transaction.getDate();
            if (transactionDate.isBefore(currentDate) && transactionDate.isAfter(previousYear)) {
                System.out.printf("%-12s %-20s $%-15.2f%n", transaction.getDate(),
                        transaction.getVendor(), transaction.getAmount());
                totalAmount += transaction.getAmount();
            }
        }

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-33s $%-15.2f%n", "Total transactions for previous year:", totalAmount);
    }


    private static void VendorReport() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter vendor name: ");
        String vendorName = scanner.nextLine();

        double totalAmount = 0.0;
        System.out.printf("%-12s %-20s %-15s%n", "Date", "Vendor", "Amount");
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendorName)) {
                System.out.printf("%-12s %-20s $%-15.2f%n", transaction.getDate(),
                        transaction.getVendor(), transaction.getAmount());
                totalAmount += transaction.getAmount();
            }
        }
        System.out.printf("Total for vendor %s: $%.2f%n", vendorName, totalAmount);
    }
    }



    class Deposit extends Transaction {
        public Deposit(LocalDateTime dateTime, String vendor, double amount) {
            super(dateTime, vendor, amount);
        }

        public static class Payment extends Transaction {
            public Payment(LocalDateTime dateTime, String vendor, double amount) {
                super(dateTime, vendor, amount);
            }


        }
    }



/* private static void displayLedger()
        Collections.sort(transactions, Collections.reverseOrder());

        // Print the header row
        System.out.printf("%-12s %-10s %-20s %-10s %-10s\n",
                "Date", "Time", "Vendor", "Type", "Amount");

        // Print the transactions
        double balance = 0.0;
        for (Transaction transaction : transactions) {
            System.out.printf("%-12s %-10s %-20s %-10s %10.2f\n",
                    transaction.getDate(), transaction.getTime(),
                    transaction.getVendor(), transaction.getType(),
                    transaction.getAmount());
            balance += transaction.getAmount();
        }

        // Print the footer row
        System.out.printf("%-12s %-10s %-20s %-10s %10.2f\n",
                "", "", "", "Total", balance);
    }


        private static void displayLedger() {
        System.out.printf("%-12s %-12s %-30s %-20s %-15s%n", "Date", "Time", "Description", "Vendor", "Amount");
        for(Transaction transaction : transactions){
            LocalDateTime dateTime = transaction.getDateTime();
            String formattedDate = DATE_FORMATTER.format(dateTime);
            String formattedTime = TIME_FORMATTER.format(dateTime);
            System.out.printf("%-12s %-12s %-30s %-20s $%-15.2f%n", formattedDate,
                    formattedTime, transaction.getDescription(), transaction.getVendor(), transaction.getAmount());

        }
    }

        private static void filterTransactionsByDate(LocalDate startDate, LocalDate endDate) {
        // This method filters the transactions by date and prints a report to the console.
        // It takes two parameters: startDate and endDate, which represent the range of dates to filter by.
        // The method loops through the transactions list and checks each transaction's date against the date range.
        // Transactions that fall within the date range are printed to the console.
        // If no transactions fall within the date range, the method prints a message indicating that there are no results.
    }

    private static void filterTransactionsByVendor(String vendor) {
        // This method filters the transactions by vendor and prints a report to the console.
        // It takes one parameter: vendor, which represents the name of the vendor to filter by.
        // The method loops through the transactions list and checks each transaction's vendor name against the specified vendor name.
        // Transactions with a matching vendor name are printed to the console.
        // If no transactions match the specified vendor name, the method prints a message indicating that there are no results.
    }
*/