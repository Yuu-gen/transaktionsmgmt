/**
 * 
 */
package uebung7;

/**
 * @author ck
 *
 */
public abstract class DatenelementKommando implements Kommando {
	
	private String x;

	public DatenelementKommando(final String x) {
		this.x = x;
	}
	
	public String getX() {
		return x;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)", getTyp(), getX());
	}

}
