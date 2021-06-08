package node;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.ping.node.FileNode;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FileTest {

    private Path path = Path.of("ProjectTests/EmptyFolder");
    private FileNode file = new FileNode(path);

    @Test
    void getPathValid() {
        assertEquals(path, file.getPath());
    }

    @Test
    void getPathInvalid() {
        assertNotEquals(Path.of("ProjectTests/SimpleProject"), file.getPath());
    }

    @Test
    void getType() {
        assertEquals(Node.Types.FILE, file.getType());
    }

    @Test
    void getChildren() {
        assertEquals(Collections.emptyList(), file.getChildren());
    }
}