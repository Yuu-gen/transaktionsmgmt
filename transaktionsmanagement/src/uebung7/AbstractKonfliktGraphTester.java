package uebung7;

public abstract class AbstractKonfliktGraphTester {

	
	
	public abstract void process(final Kommando k, final int i);

	protected void commandIgnore(final Kommando k, final int i) {
		// System.out.printf("Kommando NICHT verarbeiten: (%s, %d)%n", k, i);
		System.out.printf("t%d: Kommando NICHT verarbeiten: %s%n", i, k);
	}

	protected void commandExecute(final Kommando k, final int i) {
		// System.out.printf("Kommando verarbeiten: (%s, %d)%n", k, i);
		System.out.printf("t%d: Kommando verarbeiten: %s%n", i, k);
	}

	protected void transactionAbort(final int i) {
		System.out.printf("t%d: Abbruch Transaktion%n", i);
	}
	

}