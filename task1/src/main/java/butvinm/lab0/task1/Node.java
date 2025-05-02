package butvinm.lab0.task1;

import java.util.*;
import java.util.function.Consumer;

public record Node<T>(String label, T value, List<Node<T>> neighbors) {
    public static <T> Node<T> of(String label, T value) {
        return new Node<>(label, value, new ArrayList<>());
    }

    public void addNeighbor(Node<T> neighbor) {
        neighbors.add(neighbor);
    }

    @Override
    public String toString() {
        return "Node(label=%s, value=%s)".formatted(label, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(label, node.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}
