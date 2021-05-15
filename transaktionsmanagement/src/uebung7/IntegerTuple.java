package uebung7;

/**
 * Holds two Integers.
 * @author Y
 *
 */
public class IntegerTuple {
	
	private final Integer first;
	private final Integer second;
	
	public IntegerTuple(Integer first, Integer second) {
		this.first = first;
		this.second = second;
	}

	public Integer getFirst() {
		return first;
	}

	public Integer getSecond() {
		return second;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		try {
			IntegerTuple t = (IntegerTuple) obj;
			return (t.getFirst()==this.getFirst()&&t.getSecond()==this.getSecond());
		} catch (Exception e) {
			return false;
		}
	}

	
	
}
