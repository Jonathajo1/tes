import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankAccount {
    private String accountNumber;
    private int bankId;

    public BankAccount(String accountNumber, int bankId) {
        this.accountNumber = accountNumber;
        this.bankId = bankId;
    }

    // Metode untuk mendapatkan saldo akun
    public double getBalance() throws SQLException {
        Connection connection = DatabaseHelper.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT balance FROM accounts WHERE account_number = ?");
        statement.setString(1, accountNumber);
        ResultSet resultSet = statement.executeQuery();

        double balance = 0.0;
        if (resultSet.next()) {
            balance = resultSet.getDouble("balance");
        }

        resultSet.close();
        statement.close();
        connection.close();
        return balance;
    }

    // Metode untuk menambah saldo
    public void deposit(double amount) throws SQLException {
        Connection connection = DatabaseHelper.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
        statement.setDouble(1, amount);
        statement.setString(2, accountNumber);
        statement.executeUpdate();

        statement.close();
        connection.close();
    }

    // Metode untuk menarik saldo
    public void withdraw(double amount) throws SQLException {
        Connection connection = DatabaseHelper.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_number = ?");
        statement.setDouble(1, amount);
        statement.setString(2, accountNumber);
        statement.executeUpdate();

        statement.close();
        connection.close();
    }

    // Metode untuk transfer antar bank dengan biaya tambahan jika bank berbeda
    public void transfer(BankAccount targetAccount, double amount) throws SQLException {
        Connection connection = DatabaseHelper.getConnection();
        connection.setAutoCommit(false); // Mulai transaksi

        try {
            // Dapatkan biaya transaksi jika bank berbeda
            double transactionFee = 0.0;
            if (this.bankId != targetAccount.bankId) {
                transactionFee = Bank.getTransactionFee(this.bankId);
            }

            double totalAmount = amount + transactionFee;

            // Periksa saldo pengirim
            double senderBalance = getBalance();
            if (senderBalance < totalAmount) {
                throw new SQLException("Saldo tidak cukup.");
            }

            // Kurangi saldo dari rekening pengirim
            PreparedStatement withdrawStmt = connection.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_number = ?");
            withdrawStmt.setDouble(1, totalAmount);
            withdrawStmt.setString(2, this.accountNumber);
            withdrawStmt.executeUpdate();

            // Tambahkan saldo ke rekening penerima
            PreparedStatement depositStmt = connection.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
            depositStmt.setDouble(1, amount);
            depositStmt.setString(2, targetAccount.accountNumber);
            depositStmt.executeUpdate();

            // Tambahkan catatan transaksi
            PreparedStatement transactionStmt = connection.prepareStatement(
                    "INSERT INTO transactions (source_account, target_account, amount, transaction_fee, transaction_date) VALUES (?, ?, ?, ?, ?)"
            );
            transactionStmt.setString(1, this.accountNumber);
            transactionStmt.setString(2, targetAccount.accountNumber);
            transactionStmt.setDouble(3, amount);
            transactionStmt.setDouble(4, transactionFee);
            transactionStmt.setString(5, getCurrentDate());
            transactionStmt.executeUpdate();

            // Komit transaksi
            connection.commit();

            withdrawStmt.close();
            depositStmt.close();
            transactionStmt.close();
            connection.close();
        } catch (SQLException e) {
            connection.rollback(); // Batalkan transaksi jika terjadi kesalahan
            throw e;
        }
    }

    // Metode untuk mendapatkan tanggal dan waktu saat ini
    private String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
