import java.sql.*;

public class Transaction {
    private int id;
    private String accountNumber;
    private double amount;
    private String transactionType;
    private String date;

    public Transaction(int id, String accountNumber, double amount, String transactionType, String date) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getDate() {
        return date;
    }

    public void recordTransaction() throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO transactions (account_number, amount, transaction_type, date) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, this.accountNumber);
            stmt.setDouble(2, this.amount);
            stmt.setString(3, this.transactionType);
            stmt.setString(4, this.date);
            stmt.executeUpdate();
        }
    }
}

