package de.uebung1;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MinimalbeispielSqlLiteValentin {

	/**
	 * Opens a Databaseconnection and does Stuff with it
	 */
	public static void main(String[] args) {
		
		final String mysqliteURL = "jdbc:sqlite:helloFhdw.sqlite.db";
		final String mariadbURL = "jdbc:mariadb://localhost:3306/test?user=transaktionsmanagement&password=Apfelkuchen?!";
		final String postgreSQLURL = "jdbc:postgresql://localhost/test?user=transaktionsmanagement&password=Apfelkuchen?!";
		
		try ( final Connection conn = DriverManager.getConnection(postgreSQLURL)) {
			System.out.println("Verbunden mit Datenbank" + postgreSQLURL);
			/*
			 * CREATE TABLE "konten" (
			 * "kto"	INTEGER NOT NULL,
			 * "betrag"	INTEGER NOT NULL,
			 * "kunde"	TEXT NOT NULL,
			 * PRIMARY KEY("kto")
			 * );
			 */
			
			kundenListing(conn);
							
			final String von = "A";
			final String nach = "B";
			final BigDecimal betrag = BigDecimal.valueOf(100, 0);
			bankTransfer(conn, von, nach, betrag);
			
			kundenListing(conn);
				
			}
			catch (Exception e) {
				System.out.println("HÄH?");;
				e.printStackTrace();
			}
			
		
		}
	/**
	 * Lists every "kunde" and his "betrag"
	 */
	private static void kundenListing(Connection conn) {
		try(final Statement stmt = conn.createStatement()){
			try(ResultSet rs = stmt.executeQuery("Select kunde, betrag from konten")){
				while(rs.next()) {
					final String kunde = rs.getString("kunde");
					final BigDecimal kontostand =rs.getBigDecimal("betrag");
					System.out.printf("Kunde %s hat %s Geldeinheiten%n",kunde,kontostand);
				}
				System.out.println("Ende aller Kunden");
			}catch (SQLException e) {
				e.printStackTrace();
		}
		}catch (SQLException e) {
			e.printStackTrace();
	}
	}
	/**
	 * changes the "betrag" of a "kunde" to a desired new value
	 */
	private static void changeKontostand(Connection conn, String kunde, BigDecimal betrag) {
		final String selectBetrag = "Select betrag From konten Where kunde = ?";
		final String updateBetrag = "Update konten Set betrag = ? Where kunde = ?";
		try(final PreparedStatement stmt = conn.prepareStatement(selectBetrag)){
			stmt.setString(1, kunde);
			final ResultSet rs= stmt.executeQuery();
			if(rs.next()){
				BigDecimal kontostandVon = rs.getBigDecimal("betrag");
				System.out.printf("Der Kontostand von %s betraegt %s%n",kunde,kontostandVon);
				kontostandVon = betrag;
				System.out.printf("der Kontostand von %s soll geaendert werden, auf %s%n",kunde,kontostandVon);
				
				try(final PreparedStatement us = conn.prepareStatement(updateBetrag)){
					us.setBigDecimal(1, kontostandVon);
					us.setString(2, kunde);
					int count = us.executeUpdate();
					if(count == 1) {
						System.out.println("Kontostand erfolgreich geaendert");
					}
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void bankTransfer(Connection conn, String von, String nach, BigDecimal betrag) {
		final String selectBetrag = "Select betrag From konten Where kunde = ?";
		try(final PreparedStatement stmt = conn.prepareStatement(selectBetrag)){
			stmt.setString(1, von);
			final ResultSet rs= stmt.executeQuery();
			if(rs.next()){
				BigDecimal kontostandVon = rs.getBigDecimal("betrag");
				System.out.printf("Der Kontostand von %s betraegt %s%n",von,kontostandVon);
				kontostandVon = kontostandVon.subtract(betrag);
				
				changeKontostand(conn, von, kontostandVon);
			}
				stmt.setString(1, nach);
				final ResultSet rs1= stmt.executeQuery();
				if(rs1.next()){
					BigDecimal kontostandNach = rs1.getBigDecimal("betrag");
					System.out.printf("Der Kontostand von %s betrÃ¤gt %s%n",nach,kontostandNach);
					kontostandNach = kontostandNach.add(betrag);
					
					changeKontostand(conn, nach, kontostandNach);
				
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		/*
		try(final Statement stmt = conn.createStatement()){
		// 1. Einzug von "von"
		stmt.executeQuery("select betrag from konten where kunde ="+von);
		// 2. Gutschrift zu "nach"
		}catch (SQLException e) {
			// TODO: handle exception
		}
	*/
	}


