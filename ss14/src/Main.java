import java.sql.*;
import java.math.BigDecimal;

public class Main {

	private static final String URL = "jdbc:mysql://localhost:3306/ten_database_cua_ban?useSSL=false&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASS = "mat_khau_cua_ban";

	public static void main(String[] args) {
		processTransfer("ACC01", "ACC02", new BigDecimal("1500.00"));
	}

	public static void processTransfer(String senderId, String receiverId, BigDecimal amount) {
		Connection conn = null;

		try {
			conn.setAutoCommit(false);
			String sqlCheck = "SELECT Balance FROM Accounts WHERE AccountId = ?";
			try (PreparedStatement Check = conn.prepareStatement(sqlCheck)) {
				Check.setString(1, senderId);
				try (ResultSet result = Check.executeQuery()) {
					if (result.next()) {
						BigDecimal currentBalance = result.getBigDecimal("Balance");
						if (currentBalance.compareTo(amount) < 0) {
							throw new SQLException("Insufficient balance");
						}
					} else {
						throw new SQLException("Sender account not found");
					}
				}
			}

			try (CallableStatement Transfer = conn.prepareCall("{call sp_UpdateBalance(?, ?)}")) {
				Transfer.setString(1, senderId);
				Transfer.setBigDecimal(2, amount.negate());
				Transfer.executeUpdate();

				Transfer.setString(1, receiverId);
				Transfer.setBigDecimal(2, amount);
				Transfer.executeUpdate();
			}

			conn.commit();
			displayStatus(conn, senderId, receiverId);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void displayStatus(Connection conn, String id1, String id2) throws SQLException {
		String sqlSelect = "SELECT * FROM Accounts WHERE AccountId IN (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
			ps.setString(1, id1);
			ps.setString(2, id2);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					System.out.printf("ID: %s | Name: %s | Balance: %.2f\n",
							rs.getString("AccountId"),
							rs.getString("FullName"),
							rs.getBigDecimal("Balance"));
				}
			}
		}
	}
}