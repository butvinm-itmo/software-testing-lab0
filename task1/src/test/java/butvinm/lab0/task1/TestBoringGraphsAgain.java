package butvinm.lab0.task1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import butvinm.lab0.task1.BoringGraphsAgain;
import butvinm.lab0.task1.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestBoringGraphsAgain {

    private BoringGraphsAgain graphSearch;

    @BeforeEach
    void setUp() {
        graphSearch = new BoringGraphsAgain();
    }

    @Test
    void testDFS_EmptyGraph() {
        Node<Integer> startNode = Node.of("A", 1);
        List<Node<Integer>> result = graphSearch.depthFirstSearch(startNode);
        List<String> expectedCheckpoints = Arrays.asList("VISIT_NODE:A", "CHECK_NEIGHBORS:A", "FINISHED_NODE:A");
        assertEquals(expectedCheckpoints, graphSearch.getCheckpoints());

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).label());
    }

    @Test
    void testDFS_GraphWithNeighbors() {
        Node<Integer> node1 = Node.of("A", 1);
        Node<Integer> node2 = Node.of("B", 2);
        Node<Integer> node3 = Node.of("C", 3);
        node1.addNeighbor(node2);
        node1.addNeighbor(node3);
        graphSearch.resetCheckpoints();
        List<Node<Integer>> result = graphSearch.depthFirstSearch(node1);
        List<String> expectedCheckpoints = Arrays.asList(
            "VISIT_NODE:A",
            "CHECK_NEIGHBORS:A",
            "VISIT_NEIGHBOR:B",
            "VISIT_NODE:B",
            "CHECK_NEIGHBORS:B",
            "FINISHED_NODE:B",
            "VISIT_NEIGHBOR:C",
            "VISIT_NODE:C",
            "CHECK_NEIGHBORS:C",
            "FINISHED_NODE:C",
            "FINISHED_NODE:A"
        );
        assertEquals(expectedCheckpoints, graphSearch.getCheckpoints());

        assertEquals(3, result.size());
        assertEquals("A", result.get(0).label());
        assertEquals("B", result.get(1).label());
        assertEquals("C", result.get(2).label());
    }

    @Test
    void testDFS_Cycle() {
        Node<String> a = Node.of("A", "A");
        Node<String> b = Node.of("B", "B");
        Node<String> c = Node.of("C", "C");
        a.addNeighbor(b);
        b.addNeighbor(c);
        c.addNeighbor(a);
        graphSearch.resetCheckpoints();
        List<Node<String>> result = graphSearch.depthFirstSearch(a);
        List<String> expectedCheckpoints = Arrays.asList(
            "VISIT_NODE:A",
            "CHECK_NEIGHBORS:A",
            "VISIT_NEIGHBOR:B",
            "VISIT_NODE:B",
            "CHECK_NEIGHBORS:B",
            "VISIT_NEIGHBOR:C",
            "VISIT_NODE:C",
            "CHECK_NEIGHBORS:C",
            "SKIP_VISITED_NEIGHBOR:A",
            "FINISHED_NODE:C",
            "FINISHED_NODE:B",
            "FINISHED_NODE:A"
        );
        assertEquals(expectedCheckpoints, graphSearch.getCheckpoints());

        assertEquals(3, result.size());
        assertEquals("A", result.get(0).label());
        assertEquals("B", result.get(1).label());
        assertEquals("C", result.get(2).label());
    }
}
