/**
 * 
 */
package uebung7;

/**
 * @author ck
 *
 */
public class ReadKommando extends DatenelementKommando {
	
	public ReadKommando(final String x) {
		super(x);
	}

	@Override
	public Typ getTyp() {
		return Typ.READ;
	}

}
