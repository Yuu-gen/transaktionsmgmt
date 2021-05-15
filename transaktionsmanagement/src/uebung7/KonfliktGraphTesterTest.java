package uebung7;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KonfliktGraphTesterTest {
	Set<IntegerTuple> oldGraph;
    Set<IntegerTuple> newIntegerTuples;
	
	@BeforeEach
	void setUp() throws Exception {
		this.oldGraph = new HashSet<>();
        this.newIntegerTuples = new HashSet<>();
		
	}

	@Test
	void testAddNewAndTransitiveRelations() {

        this.oldGraph.add(new IntegerTuple(1, 2));
        this.oldGraph.add(new IntegerTuple(1, 3));
        this.oldGraph.add(new IntegerTuple(2, 3));
        
        this.newIntegerTuples.add(new IntegerTuple(3, 4));
        
        Set<IntegerTuple> supposedRelations = new HashSet<>(this.oldGraph);
        supposedRelations.add(new IntegerTuple(1, 4));
        supposedRelations.add(new IntegerTuple(2, 4));
        supposedRelations.add(new IntegerTuple(3, 4));
        
        Set<IntegerTuple> newTRGraph = new KonfliktGraphTester().addNewAndTransitiveElementsToGraph(this.oldGraph, this.newIntegerTuples);
//        for (IntegerTuple integerTuple : newTRGraph) {
//            System.out.println("(" + integerTuple.getFirst() + " " + integerTuple.getSecond() + ")");
//        }
        assertFalse(supposedRelations.addAll(newTRGraph));
	}
	
	@Test
	void testAddNewAndTransitiveRelationsEmptySet() {
		Set<IntegerTuple> oldGraph = new HashSet<>();
        Set<IntegerTuple> newIntegerTuples = new HashSet<>();
        Set<IntegerTuple> supposedRelations = new HashSet<>();
        
        Set<IntegerTuple> newTRGraph = new KonfliktGraphTester().addNewAndTransitiveElementsToGraph(oldGraph, newIntegerTuples);

        assertFalse(supposedRelations.addAll(newTRGraph));
	}
	
	@Test
	void testGraphCycles() {
		this.oldGraph.add(new IntegerTuple(1, 2));
        this.oldGraph.add(new IntegerTuple(1, 3));
        this.oldGraph.add(new IntegerTuple(2, 3));
        this.newIntegerTuples.add(new IntegerTuple(3, 4));
        this.newIntegerTuples.add(new IntegerTuple(2, 1));
        Set<IntegerTuple> extendedGraph = new KonfliktGraphTester().addNewAndTransitiveElementsToGraph(this.oldGraph, this.newIntegerTuples);
//        System.out.println("Zyklentest");
//        for (IntegerTuple integerTuple : extendedGraph) {
//            System.out.println("(" + integerTuple.getFirst() + " " + integerTuple.getSecond() + ")");
//        }
        assertTrue( new KonfliktGraphTester().checkIfGraphHasCycles(extendedGraph));
	}
	
	@Test
	void testGraphNoCycles() {
		this.oldGraph.add(new IntegerTuple(1, 2));
        this.oldGraph.add(new IntegerTuple(1, 3));
        this.oldGraph.add(new IntegerTuple(2, 3));
        this.newIntegerTuples.add(new IntegerTuple(3, 4));
        Set<IntegerTuple> extendedGraph = new KonfliktGraphTester().addNewAndTransitiveElementsToGraph(this.oldGraph, this.newIntegerTuples);
//        System.out.println("Zyklentest");
//        for (IntegerTuple integerTuple : extendedGraph) {
//            System.out.println("(" + integerTuple.getFirst() + " " + integerTuple.getSecond() + ")");
//        }
        assertFalse( new KonfliktGraphTester().checkIfGraphHasCycles(extendedGraph));
	}
	
	
}
