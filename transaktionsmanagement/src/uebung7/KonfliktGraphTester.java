package uebung7;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uebung7.Kommando.Typ;

public class KonfliktGraphTester extends AbstractKonfliktGraphTester {

	private Set<IntegerTuple> graph;
	private Map<String, Set<Integer>> writeSet;
	private Map<String, Set<Integer>> readSet;
	private Set<Integer> finished;
	
	
	
	public KonfliktGraphTester() {
		this.graph = new HashSet<IntegerTuple>();
		this.writeSet = new HashMap<String, Set<Integer>>();
		this.readSet = new HashMap<String, Set<Integer>>();
		this.finished = new HashSet<Integer>();
	}


	@Override
	public void process(Kommando k, int i) {
		Set<IntegerTuple> newElements = new HashSet<IntegerTuple>();
		
		switch (k.getTyp()) {
		case COMMIT:
			//Wenn K = commit, Verarbeiten und k in finished
			this.commandExecute(k, i);
			this.finished.add(i);
			return;
		case READ:
			//Wenn K = read, 
			DatenelementKommando r = (DatenelementKommando) k;
			checkForNewDataElements(r);
			readSet.get(r.getX()).add(i);
			for (Integer oldWrite : this.writeSet.get(r.getX())) {
				newElements.add(new IntegerTuple(oldWrite, i));
			}	
			break;
		case WRITE:	
			DatenelementKommando w = (DatenelementKommando) k;
			checkForNewDataElements(w);
			writeSet.get(w.getX()).add(i);
			//(w,i), für alle w aus write(x)
			for (Integer oldWrite : this.writeSet.get(w.getX())) {
				newElements.add(new IntegerTuple(oldWrite, i));
			}
			//(r,i), wenn r!=i und r aus read(x)
			for (Integer oldRead : this.writeSet.get(w.getX())) {
				if(oldRead!=i) {
					newElements.add(new IntegerTuple(oldRead, i));
				}
			}
			break;
		default:
			break;
		}
		//Erstellen des erweiterten Graph2
		Set<IntegerTuple> newGraph = this.addNewAndTransitiveElementsToGraph(this.graph, newElements);
		//Prüfen des Graphen und newElements 
		//inklusive transitiver Tupel auf Zyklen
		if(checkIfGraphHasCycles(newGraph)) {
			//Wenn Zyklen da sind
			this.commandIgnore(k, i);
			this.removeAbortedTransitions(i);	
		}else {
			//Keine Zyklen
			this.commandExecute(k, i);	
		}
		
	}
	/**
	 * Gibt eine neue Menge aus IntegerTupeln zurück, 
	 * in dem die Tupel aus newElements UND transitive Relationen hinzugefügt wurden.
	 * @param oldGraph
	 * @param newElements
	 * @return
	 */
	protected Set<IntegerTuple> addNewAndTransitiveElementsToGraph(Set<IntegerTuple> oldGraph, Set<IntegerTuple> newElements){
		Set<IntegerTuple> newGraph = new HashSet<IntegerTuple>(oldGraph);
		for (IntegerTuple newTuple : newElements) {
			newGraph.add(newTuple);
			newGraph.addAll(findTransitiveRelations(oldGraph, newTuple.getFirst(), newTuple.getSecond()));
		}
		return newGraph;
	}
	
	/**
	 * Rekursiv, sucht für jedes Tupel einer gegebenen Menge nach transitiven Relationen zu einer Integer transitiveEnd
	 * Die currentNode verweist dabei jeweils auf das aktuelle Bindungsglied zum transitiveEnd
	 * Terminiert, wenn oldGraph leer ist.
	 * @param oldGraph
	 * @param currentNode
	 * @param transitiveEnd
	 * @return
	 */
	protected Set<IntegerTuple> findTransitiveRelations(Set<IntegerTuple> oldGraph, Integer currentNode, Integer transitiveEnd){
		Set<IntegerTuple> transitiveElements = new HashSet<IntegerTuple>();
		for (IntegerTuple oldTuple : oldGraph) {
			if(currentNode==oldTuple.getSecond()) {
				transitiveElements.add(new IntegerTuple(oldTuple.getSecond(), transitiveEnd));
				Set<IntegerTuple> smallerGraph = new HashSet<IntegerTuple>(oldGraph);
				smallerGraph.remove(oldTuple);
				transitiveElements.addAll(findTransitiveRelations(smallerGraph, oldTuple.getFirst(), transitiveEnd));
			}
			
		}
		return transitiveElements;
	}
	
	
	protected Boolean checkIfGraphHasCycles(Set<IntegerTuple> extendedGraph) {
		for (IntegerTuple tuple : extendedGraph) {
			if(extendedGraph.contains(new IntegerTuple(tuple.getSecond(), tuple.getFirst()))) {
				return true;
			}
		}
		return false;
	}

	private void removeAbortedTransitions(Integer abortedTransition) {
		Set<Integer> aborted = new HashSet<Integer>();
		Set<IntegerTuple> removeElements = new HashSet<IntegerTuple>();
		aborted.add(abortedTransition);
		for (IntegerTuple tuple : this.graph) {
			if(tuple.getFirst()==abortedTransition) {
				aborted.add(tuple.getSecond());
			}
		}
		//Graph+ = Graph+ - (i,j) € Graph+ | i aus aborted oder j aus aborted
		for (IntegerTuple tuple : this.graph) {
			if(aborted.contains(tuple.getFirst())||aborted.contains(tuple.getSecond())){
				removeElements.add(tuple);
			}		
		}
		this.graph.removeAll(removeElements);
		//Für alle Datenelemente entferne aus der jeweiligen read-Menge, die Transaktionen aus aborted
		for (Set<Integer> read : this.readSet.values()) {
			read.removeAll(aborted);
		}
		//Für alle Datenelemente entferne aus der jeweiligen write-Menge, die Transaktionen aus aborted
		for (Set<Integer> write : this.writeSet.values()) {
			write.removeAll(aborted);
		}
		//Finished = finished - aborted
		this.finished.removeAll(aborted);
	}
	
	
	private void checkForNewDataElements(DatenelementKommando k) {
		if(!writeSet.containsKey(k.getX())) {
			writeSet.put(k.getX(), new HashSet<Integer>());
		}
		if(!readSet.containsKey(k.getX())) {
			readSet.put(k.getX(), new HashSet<Integer>());
		}
	}
	
	
}
