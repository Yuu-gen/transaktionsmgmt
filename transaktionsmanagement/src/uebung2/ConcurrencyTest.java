package uebung2;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConcurrencyTest {
	
	private static final String deleteKontenSQL = "DELETE FROM konten;";
	private static final String insertUsersSQL = "INSERT INTO konten(kto, betrag, kunde) VALUES (1, 1000, 'A'), (2, 1000, 'B'), (3, 1000, 'C');";
	
	public static void main(String[] args) {
		// final String url = "jdbc:sqlite:helloFhdw.sqlite.db";
		final String mysqliteURL = "jdbc:sqlite:helloFhdw.sqlite.db";
		final String mariadbURL = "jdbc:mariadb://localhost:3306/test?user=transaktionsmanagement&password=Apfelkuchen?!";
		final String postgreSQLURL = "jdbc:postgresql://localhost/test?user=transaktionsmanagement&password=Apfelkuchen?!";
			
			/*
			 * CREATE TABLE "konten" (
				"kto"	INTEGER NOT NULL,
				"betrag"	INTEGER NOT NULL,
				"kunde"	TEXT NOT NULL,
				PRIMARY KEY("kto")
			);
			 */
			
			String[] connections = {mysqliteURL, mariadbURL, postgreSQLURL};
			for (String url : connections) {
				
				try (final Connection conn = DriverManager.getConnection(url)) {
					System.out.println("Verbunden mit Datenbank " + url);
					resetDatabase(conn);
					//Aufgabe 1
					BanktransferThread ab = new BanktransferThread(url, "A", "B", BigDecimal.valueOf(100));
					BanktransferThread bc = new BanktransferThread(url, "B", "C", BigDecimal.valueOf(100));
					BanktransferThread ca = new BanktransferThread(url, "C", "A", BigDecimal.valueOf(100));
					
					ab.start();
					bc.start();
					ca.start();
					
					ab.join();
					bc.join();
					ca.join();
					listKunden(conn);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("****Ende der Datenbankaktionen****\n");
			}
			
			
//			 listKunden(conn);
//			//createKunden(conn);
//			
//			final String von = "A";
//			final String nach = "B";
//			final BigDecimal betrag = BigDecimal.valueOf(100, 0);			
//			bankTransfer(conn, von, nach, betrag);
			
			
			
		
		
	}
	
	
	/**
	 * Setzt die Datenbank auf einen Basiszustand zurück.
	 * @param conn
	 */
	private static void resetDatabase(Connection conn) {
		try (final Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(deleteKontenSQL);
			System.out.println("Konten deleted.");
			stmt.executeUpdate(insertUsersSQL);
			System.out.println("Users inserted.");
		} catch (SQLException e) {
			System.out.println("Database "+ conn +" didn't like being reset.");;
			e.printStackTrace();
		}
	}
	
	
	

		

	/**
	 * Ausgabe aller Kunden und des jeweils aktuellen Kontostandes aus der Tabelle `konten`:
	 * <xmp>
	 * Alle Kunden
	 * Kunde A hat 1000 Geldeinheiten
	 * Kunde B hat 3000 Geldeinheiten
	 * ENDE Alle Kunden
	 * </xmp> 
	 * @param conn Eine aktive JDBC-Datenbankverbindung
	 */
	private static void listKunden(final Connection conn) {
		try (final Statement stmt = conn.createStatement()) {
			System.out.println("Alle Kunden");
			try (ResultSet rs = stmt.executeQuery("SELECT kunde, betrag FROM konten")) {
				while(rs.next()) {
					final String kunde = rs.getString("kunde");
					final BigDecimal kontostand = rs.getBigDecimal("betrag");
					System.out.printf("Kunde %s hat %s Geldeinheiten%n", kunde, kontostand);
				}
			}
			System.out.println("ENDE Alle Kunden");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


}
