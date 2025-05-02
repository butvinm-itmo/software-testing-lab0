package butvinm.lab0.task1;

import java.util.*;

public class BoringGraphsAgain {

    private final List<String> checkpoints = new ArrayList<>();

    public void resetCheckpoints() {
        checkpoints.clear();
    }

    public List<String> getCheckpoints() {
        return new ArrayList<>(checkpoints);
    }

    private void checkpoint(String point) {
        checkpoints.add(point);
    }

    public <T> List<Node<T>> depthFirstSearch(Node<T> startNode) {
        List<Node<T>> visited = new ArrayList<>();
        dfsRecursive(startNode, visited);
        return visited;
    }

    private <T> void dfsRecursive(Node<T> current, List<Node<T>> visited) {
        checkpoint("VISIT_NODE:" + current.label());
        visited.add(current);

        checkpoint("CHECK_NEIGHBORS:" + current.label());
        for (Node<T> neighbor : current.neighbors()) {
            if (!visited.contains(neighbor)) {
                checkpoint("VISIT_NEIGHBOR:" + neighbor.label());
                dfsRecursive(neighbor, visited);
            } else {
                checkpoint("SKIP_VISITED_NEIGHBOR:" + neighbor.label());
            }
        }
        checkpoint("FINISHED_NODE:" + current.label());
    }
}
