package test.node;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.ping.node.FileNode;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

class FileTest {

    private Path path = Path.of("test_path");
    private FileNode file = new FileNode(path);

    @Test
    void getPathValid() {
        assert (file.getPath() == path);
    }

    @Test
    void getPathInvalid() {
        assert file.getPath() != Path.of("not_the_same");
    }

    @Test
    void getType() {
        assert file.getType() == Node.Types.FILE;
    }

    @Test
    void getChildren() {
        assert file.getChildren() == (List<Node>) Collections.EMPTY_LIST;
    }
}