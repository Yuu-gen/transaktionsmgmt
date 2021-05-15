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
	
	//Sehr wichtig bei der Arbeit mit Hash-Collections... 
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
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
