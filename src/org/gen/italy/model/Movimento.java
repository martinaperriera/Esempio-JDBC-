package org.gen.italy.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Movimento {
	public int id;
	public LocalDate data;
	public String codProdotto, codMovimento, riferimento;
	public int quantità;

	@Override
	public String toString() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return "\n[data=" + data.format(df) + "\ncodProdotto=" + codProdotto + "\ncodMovimento=" + codMovimento
				+ "\nquantità=" + quantità + "]\n";

	}
}
