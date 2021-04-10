package uebung2;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BanktransferThread extends Thread {

	private static final String selectBetrag = "SELECT betrag FROM konten WHERE kunde = ?;";
	private static final String updateBetrag = "UPDATE konten SET betrag = ? WHERE kunde = ?;";
	
	private final String connectionString;
	private final String sourceAccount;
	private final String targetAccount;
	private final BigDecimal amount;
	private Integer transfercount;
	
	public BanktransferThread(String connString, String source, String target, BigDecimal amount){
		this.connectionString = connString;
		this.sourceAccount = source;
		this.targetAccount = target;
		this.amount = amount;
		this.transfercount = 0;
	}
	
	@Override
	public void run() {
		try(Connection conn = DriverManager.getConnection(this.connectionString)){
			for (int i = 0; i < 1000; i++) {
			bankTransfer(conn, this.sourceAccount, 
					this.targetAccount, this.amount);
			}
			System.out.printf("%s hat %d Updates (2 pro Überweisung) ausgeführt.%n", this.getName(),this.transfercount);
		}catch (SQLException e) {
			// TODO: handle exception
		}
	}
	
	
	
	protected void bankTransfer(Connection conn, String von, String nach, BigDecimal betrag) {
		final BigDecimal ueberweisungsbetrag = BigDecimal.valueOf(100, 0);
		
		updateKontoBetrag(conn, von, ueberweisungsbetrag.negate());
		updateKontoBetrag(conn, nach, ueberweisungsbetrag);
	}


	/**
	 * Aktualisieren des Kontostandes für einen Kunden mit bestimmtem Namen
	 * @param conn Eine aktive JDBC-Datenbankverbindung
	 * @param konto Name des Kunden // FIXME: better name!
	 * @param aenderung wird auf den aktuellen Kontostand addiert; 
	 *        positive Werte erhöhen den Kontostand, negative verringern den Kontostand
	 */
	private void updateKontoBetrag(Connection conn, String konto, final BigDecimal aenderung) {
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
			
		}
	}	

	
	
	
}
