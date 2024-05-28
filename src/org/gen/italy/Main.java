package org.gen.italy;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Date;

import org.gen.italy.model.Movimento;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String url = "jdbc:mysql://localhost:3306/magazzino"; // stringa di connessione
		// significato parti della stringa
		// jdbc: java data base connectivity (api da utilizzare per connettersi al
		// database)
		// mysql: indica la tipologia di DBMS
		// localhost: macchina sulla quale è in esecuzione il dbms
		// 3306: porta sulla quale è in ascolto il dmbs (la porta individua
		// l'applicazione che utilizza la rete a parità di indirizzo ip - questa è
		// quella di default di mysql
		// magazzino: nome db da utilizzare

		ArrayList<Movimento> elencoMovimenti = new ArrayList<Movimento>();
		Movimento m = new Movimento();

		System.out.println("Tentativo di connessione al db...");
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			// la connessione è andata a buon fine
			System.out.println("La connessione è andata a buon fine");

			System.out.println("*** INSERIMENTO MOVIMENTO ***");
			// leggo i dati del movimento

			m = new Movimento();
			System.out.print("Id: ");
			m.id = sc.nextInt();
			sc.nextLine();

			boolean dataValida = false;

			do {
				System.out.print("Data: ");
				try {
					m.data = LocalDate.parse(sc.nextLine(), df);
					dataValida = true;

				} catch (Exception e) {
					// se la data non è valida gestisco l'eccezione
					System.out.println("La data non è valida, riprova");
				}
			} while (!dataValida); // torno indietro se la data non è valida

			System.out.print("Cod prodotto: ");
			String codProdotto = sc.nextLine();

			System.out.print("Cod movimento: ");
			String codMovimento = sc.nextLine();

			System.out.print("Quantità: ");
			int quantità = sc.nextInt();
			sc.nextLine();

			String sql = "INSERT INTO prodotti (id, data, codProdotto, codMovimento, quantità)"
					+ "VALUES (?, ?, ?, ?, ?)";

			System.out.println("Tentativo di esecuzione INSERT");
			try (PreparedStatement ps = conn.prepareStatement(sql)) { // provo a creare l'istruzione sql

				// imposto i valori dei parametri
				ps.setInt(1, m.id);
				ps.setObject(2, Date.valueOf(m.data));
				ps.setString(3, m.codProdotto);
				ps.setString(4, m.codMovimento);
				ps.setInt(5, m.quantità);

				int righeInteressate = ps.executeUpdate(); // eseguo l'istruzione
				System.out.println("Righe inserite: " + righeInteressate);
			}

			System.out.println("\n\n\n\n");
			System.out.println("*** ELENCO MOVIMENTI ***");

			sql = "SELECT * FROM movimenti"; //// oppure, in caso di parametri: "SELECT * FROM movimenti WHERE id=?";
			try (PreparedStatement ps = conn.prepareStatement(sql)) { // provo a creare l'istruzione sql
				try (ResultSet rs = ps.executeQuery()) { // il ResultSet mi consente di scorrere il risultato della
															// SELECT una riga alla volta
					// scorro le righe
					while (rs.next()) { // rs.next() restituisce true se c'è ancora qualche riga da leggere, falso
										// altrimenti
						m = new Movimento();
						m.id = rs.getInt("id");
						m.data = rs.getDate("Data").toLocalDate();
						m.codProdotto = rs.getString("codProdotto");
						m.codMovimento = rs.getString("codMovimento");
						m.quantità = rs.getInt("quantità");
						elencoMovimenti.add(m);

					}

				}
			}
			// stampo i movimenti letti dal db
			for (Movimento mov : elencoMovimenti)
				System.out.println(mov.toString());

		} catch (SQLTimeoutException e) {
			// si è verificato un timeout
			System.err.println("Ricordati di far partire il DBMS!");
		} catch (SQLException e) {
			// si è verificato un problema sql
			System.err.println("Errore SQL: " + e.getMessage());

		} catch (Exception e) {
			// si è verificato un problema. l'oggetto è (di tipo exception) continene
			// informazioni sull'errore verificato
			System.err.println("Si è verificato un errore: " + e.getMessage());

		}
		sc.close();
	}

}
