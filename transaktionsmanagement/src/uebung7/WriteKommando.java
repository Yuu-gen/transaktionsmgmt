/**
 * 
 */
package uebung7;

/**
 * @author ck
 *
 */
public class WriteKommando extends DatenelementKommando {
	
	public WriteKommando(final String x) {
		super(x);
	}

	@Override
	public Typ getTyp() {
		return Typ.WRITE;
	}

}
