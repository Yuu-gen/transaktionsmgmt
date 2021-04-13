package uebung3;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;



import uebung2.BanktransferThread;

public class TransactionExample {
	private static final String updateBetrag = "UPDATE konten SET betrag = ? WHERE kunde = ?;";
	public static void main(String[] args) {
		final String mysqliteURL = "jdbc:sqlite:helloFhdw.sqlite.db";
		final String mariadbURL = "jdbc:mariadb://localhost:3306/test?user=transaktionsmanagement&password=Apfelkuchen?!";
		final String postgreSQLURL = "jdbc:postgresql://localhost/test?user=transaktionsmanagement&password=Apfelkuchen?!";
		
		String[] connections = {mysqliteURL, mariadbURL, postgreSQLURL};
		for (String url : connections) {
			
			transactionExample(url);
		}

	}
	
	private static void transactionExample(final String jdbcConnectionURL) {
		try (final Connection conn = DriverManager.getConnection(jdbcConnectionURL)) {
			System.out.println("Verbunden mit Datenbank " + jdbcConnectionURL);
			
			conn.setAutoCommit(false);
			
			try (final PreparedStatement us = conn.prepareStatement(updateBetrag)){
				us.setBigDecimal(1, new BigDecimal(-1_000_000));
				us.setString(2, "B");
				int count = us.executeUpdate();
				if(count == 1) {
					System.out.println("Kontostand geändert.");
				}
				
				conn.createStatement().execute("DROP TABLE kunde;");
				
				conn.rollback();
			} catch (SQLException e) {
				// TODO: handle exception
			}
			
			
			conn.commit();
			
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
