package de.uebung;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Aufbau und einfache Nutzung einer JDBC-Datenbankverbindung wie in der Übung 1 gezeigt.
 * @author ck
 */
public class MinimalbeispielSQLKoepp {
	
	public static void main(String[] args) {
		// final String url = "jdbc:sqlite:helloFhdw.sqlite.db";
		final String mysqliteURL = "jdbc:sqlite:helloFhdw.sqlite.db";
		final String mariadbURL = "jdbc:mariadb://localhost:3306/test?user=transaktionsmanagemenet&password=Apfelkuchen?!";
		final String postgreSQLURL = "jdbc:postgresql://localhost/test?user=transaktionsmanagement&password=Apfelkuchen?!";
		
		final String url = mysqliteURL;
		try (final Connection conn = DriverManager.getConnection(url)) {
			System.out.println("Verbunden mit Datenbank " + url);
			
			/*
			 * CREATE TABLE "konten" (
				"kto"	INTEGER NOT NULL,
				"betrag"	INTEGER NOT NULL,
				"kunde"	TEXT NOT NULL,
				PRIMARY KEY("kto")
			);
			 */
			
			 listKunden(conn);
			//createKunden(conn);
			
			final String von = "A";
			final String nach = "B";
			final BigDecimal betrag = BigDecimal.valueOf(100, 0);			
			bankTransfer(conn, von, nach, betrag);
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * Einfügen von zwei Beispielkunden in die Tabelle konten
	 * <p>Anmerkung: Bei mehrfacher Ausführung kann es zu Kollisionen von eindeutigen Feldwerten wie der Kto kommen</p>
	 * @param conn Eine aktive JDBC-Datenbankverbindung
	 */
	private static void createKunden(Connection conn) {
		try (final Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("INSERT INTO konten(kto, betrag, kunde) VALUES (3, 1000, 'A');");
			stmt.executeUpdate("INSERT INTO konten(kto, betrag, kunde) VALUES (4, 2000, 'B');");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static final String selectBetrag = "SELECT betrag FROM konten WHERE kunde = ?;";
	private static final String updateBetrag = "UPDATE konten SET betrag = ? WHERE kunde = ?;";
	
	private static void bankTransfer(Connection conn, String von, String nach, BigDecimal betrag) {
		final BigDecimal ueberweisungsbetrag = BigDecimal.valueOf(100, 0);
		
		updateKontoBetrag(conn, von, ueberweisungsbetrag.negate());
		updateKontoBetrag(conn, nach, ueberweisungsbetrag);
		
		
		
		
		
		/*
		try (final Statement stmt = conn.createStatement()) {
			// 1. Einzug von "von"
			stmt.executeQuery("SELECT betrag FROM konten WHERE kunde = " + von);
			
			// 2. Gutschrift bei "nach"
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}


	/**
	 * Aktualisieren des Kontostandes für einen Kunden mit bestimmtem Namen
	 * @param conn Eine aktive JDBC-Datenbankverbindung
	 * @param konto Name des Kunden // FIXME: better name!
	 * @param aenderung wird auf den aktuellen Kontostand addiert; 
	 *        positive Werte erhöhen den Kontostand, negative verringern den Kontostand
	 */
	private static void updateKontoBetrag(Connection conn, String konto, final BigDecimal aenderung) {
		try (final PreparedStatement stmt = conn.prepareStatement(selectBetrag)) {
			stmt.setString(1, konto);
			final ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				BigDecimal kontostandVon = rs.getBigDecimal("betrag");
				System.out.printf("der Kontostand von %s beträgt %s%n", konto, kontostandVon);
				kontostandVon = kontostandVon.add(aenderung);
				System.out.printf("der Kontostand von %s soll geändert werden auf %s%n", konto, kontostandVon);
				
				try (final PreparedStatement us = conn.prepareStatement(updateBetrag)) {
					us.setBigDecimal(1, kontostandVon);
					us.setString(2, konto);
					int count = us.executeUpdate();
					if (count == 1) {
						System.out.println("Kontostand geändert");
					}
				}
			} else {
				System.err.printf("Kunde %s nicht gefunden%n", konto);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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



	/**
	 * Example for Code for Error-Handling without try-with-resource
	 * @param url a JDBC connection-string
	 */
	private static void tryExample(final String url) {
		Connection cn = null;
		try {
			cn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (cn != null) {
				try {
					cn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}

