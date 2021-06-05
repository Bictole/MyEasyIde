import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.node.File;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

class FileTest {

    private Path path = Path.of("test_path");
    private File file = new File(path);

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