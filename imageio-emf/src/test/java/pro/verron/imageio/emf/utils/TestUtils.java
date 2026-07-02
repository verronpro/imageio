package pro.verron.imageio.emf.utils;

import org.opentest4j.AssertionFailedError;
import org.w3c.dom.Node;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TestUtils {
    public static Optional<Node> findChild(Node parent, String name) {
        return Stream.iterate(parent.getFirstChild(), Objects::nonNull, Node::getNextSibling)
                .filter(n -> Objects.equals(n.getNodeName(), name))
                .findFirst();
    }

    public static Supplier<AssertionError> error(String message) {
        return () -> new AssertionFailedError(message);
    }
}
