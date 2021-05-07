package uebung3;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;





public class BankTransferTransaktionThread extends Thread {

	private static final String selectBetrag = "SELECT betrag FROM konten WHERE kunde = ?;";
	private static final String updateBetrag = "UPDATE konten SET betrag = ? WHERE kunde = ?;";
	
	private final String connectionString;
	private final String sourceAccount;
	private final String targetAccount;
	private final BigDecimal amount;
	private Integer transfercount;
	private Integer commitcount;
	private Integer killcount;
	private Integer rollbackcount;
	private Connection conn;
	
	public BankTransferTransaktionThread(Connection conn, String connString, String source, String target, BigDecimal amount){
		this.conn = conn;
		this.connectionString = connString;
		this.sourceAccount = source;
		this.targetAccount = target;
		this.amount = amount;
		this.transfercount = 0;
		this.commitcount = 0;
		this.killcount = 0;
		this.rollbackcount = 0;
	}
	
	@Override
	public void run() {
		//try(Connection conn = this.conn){
		try(Connection conn = DriverManager.getConnection(this.connectionString)){
			for (int i = 0; i < 1000; i++) {
			bankTransfer(conn, this.sourceAccount, 
					this.targetAccount, this.amount);
			}
			System.out.printf("%s hat %d Überweisungen ausgeführt.%n", this.getName(),this.transfercount/2);
			System.out.printf("%s hat %d Commits ausgeführt.%n", this.getName(),this.commitcount);
			System.out.printf("%s hat %d Rollbacks ausgeführt.%n", this.getName(),this.rollbackcount);
		}catch (SQLException e) {
			// TODO: handle exception
		}
	}
	
	
	
	protected void bankTransfer(Connection conn, String von, String nach, BigDecimal betrag) {
		final BigDecimal ueberweisungsbetrag = BigDecimal.valueOf(100, 0);
		
		try {
			conn.setAutoCommit(false);
			this.killcount++;
			
			updateKontoBetrag(conn, von, ueberweisungsbetrag.negate());
			updateKontoBetrag(conn, nach, ueberweisungsbetrag);
			if(this.killcount % 2 == 0) {
			conn.commit();
			this.commitcount++;
			}else
			{
				throw new SQLException();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//System.err.println(e.getErrorCode());
			//e.printStackTrace();
			try {
				
				conn.rollback();
				this.rollbackcount++;
				//System.out.println("ROLLBACK");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				//System.err.println(e1.getErrorCode());
				//System.out.println("Couldn't rollback. You are on your own now.");
			}
		}
		
		
		
	}


	/**
	 * Aktualisieren des Kontostandes für einen Kunden mit bestimmtem Namen
	 * @param conn Eine aktive JDBC-Datenbankverbindung
	 * @param konto Name des Kunden // FIXME: better name!
	 * @param aenderung wird auf den aktuellen Kontostand addiert; 
	 *        positive Werte erhöhen den Kontostand, negative verringern den Kontostand
	 * @throws SQLException 
	 */
	private void updateKontoBetrag(Connection conn, String konto, final BigDecimal aenderung) throws SQLException {
		try (final PreparedStatement stmt = conn.prepareStatement(selectBetrag)) {
			stmt.setString(1, konto);
			final ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				BigDecimal kontostandVon = rs.getBigDecimal("betrag");
				//System.out.printf("Der Kontostand von %s beträgt %s.%n", konto, kontostandVon);
				kontostandVon = kontostandVon.add(aenderung);
				//System.out.printf("Der Kontostand von %s soll geändert werden auf %s.%n", konto, kontostandVon);
				
				try (final PreparedStatement us = conn.prepareStatement(updateBetrag)) {
					us.setBigDecimal(1, kontostandVon);
					us.setString(2, konto);
					int count = us.executeUpdate();
					if (count == 1) {
						//System.out.println("Kontostand geändert");
					}
				}	
				
			} else {
				//System.err.printf("Kunde %s nicht gefunden%n", konto);
			}
			final ResultSet afterRS = stmt.executeQuery();
			if (afterRS.next()) {
				BigDecimal kontostandVon = afterRS.getBigDecimal("betrag");
				//System.out.printf("Der Kontostand von %s beträgt nun %s.%n", konto, kontostandVon);
			}else {
				//System.err.printf("Kunde %s nicht gefunden%n", konto);
			}
			this.transfercount++;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw(e);
		}
	}	

	
}
