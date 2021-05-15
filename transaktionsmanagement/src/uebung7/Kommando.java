/**
 * 
 */
package uebung7;

/**
 * @author ck
 *
 */
public interface Kommando {
	
	public enum Typ {
		READ,
		WRITE,
		COMMIT;
		
		@Override
		public String toString() {
			return super.toString().substring(0, 1);
		}
	}
	
	public static final Kommando COMMIT = new Kommando() {
		@Override
		public Typ getTyp() {
			return Typ.COMMIT;
		}
		@Override
		public String toString() {
			return "COMMIT";
		}
		
	};
	
	public Typ getTyp();	

}
