import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    public enum Type { INCOME, EXPENSE }

    private Type type;
    private String category;
    private double amount;
    private LocalDate date;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Transaction(Type type, String category, double amount, LocalDate date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return type + "," + category + "," + amount + "," + date.format(formatter);
    }

    public static Transaction fromString(String line) {
        String[] parts = line.split(",");
        Type type = Type.valueOf(parts[0].toUpperCase()); // Fix: make it uppercase
        String category = parts[1];
        double amount = Double.parseDouble(parts[2]);
        LocalDate date = LocalDate.parse(parts[3]);
        return new Transaction(type, category, amount, date);
    }

}
