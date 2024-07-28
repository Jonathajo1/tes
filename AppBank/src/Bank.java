import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Bank {
    public static double getTransactionFee(int bankId) throws SQLException {
        Connection connection = DatabaseHelper.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT transaction_fee FROM banks WHERE bank_id = ?");
        statement.setInt(1, bankId);
        ResultSet resultSet = statement.executeQuery();

        double fee = 0.0;
        if (resultSet.next()) {
            fee = resultSet.getDouble("transaction_fee");
        }

        resultSet.close();
        statement.close();
        connection.close();
        return fee;
    }
}
