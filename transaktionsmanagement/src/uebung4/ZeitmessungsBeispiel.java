package uebung4;

import java.util.List;
import java.util.function.Consumer;

public class ZeitmessungsBeispiel {

	public static void main(String[] args) {
		
		final ZeitmessungsBeispiel z = new ZeitmessungsBeispiel(); 
		
		
		
		z.messungMillis();
		
		z.messungNanos();
		
		
		
	}
	
	public  void messungMillis() {
		//Zeitpunkt vorher
		long vorher = System.currentTimeMillis();
		eineMethode();
		//Zeitpunkt nachher
		long nachher = System.currentTimeMillis();
		long dauer = nachher - vorher;
		System.out.println("Dauer: " + dauer + "ms");
	}
	
	public  void messungNanos() {
		//Zeitpunkt vorher
		long vorher = System.nanoTime();
		eineMethode();
		//Zeitpunkt nachher
		long nachher = System.nanoTime();
		long dauer = nachher - vorher;
		System.out.println("Dauer: " + dauer/ 1_000_000 + "ms");
	}
	
	
	public void eineMethode() {
		//irgendwas
		long sum = 0;
		for(int i=0; i<1_000_000_000; i++) {
			sum += i;
		}
		System.out.println("Summe: " + sum);
	}

}
