package de.uebung;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MinimalbeispielSqLite {

	public static void main(String[] args) {
		final String url = "jdbc:sqlite:helloFHDW.sqlite.db";
		//"jdbc:postgresql://localhost/test?user=transaktionsmanagement&password=Apfelkuchen?!"
		try (final Connection conn = DriverManager.getConnection(url)) {
			System.out.println("Verbunden mit der DB " + url);
			
			/*
			 * CREATE TABLE "konten" (
			   "kto" INTEGER NOT NULL,
			   "betrag" INTEGER NOT NULL,
			   "kunde" TEXT NOT NULL,
			   PRIMARY KEY("kto")
			   );
			 */
			try(final Statement stmt = conn.createStatement()){
				System.out.println("Alle Kunden: ");
				try (ResultSet rs = stmt.executeQuery("SELECT kunde, betrag FROM konten")){
					while(rs.next()) {
						final String kunde = rs.getString("kunde");
						final BigDecimal kontostand = rs.getBigDecimal("betrag");
						System.out.printf("Kunde %s hat %s Geldeinheiten%n", kunde, kontostand);
					}
				}
				System.out.println("ENDE Alle Kunden");
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
