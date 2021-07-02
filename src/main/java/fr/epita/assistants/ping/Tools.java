package fr.epita.assistants.ping;

import fr.epita.assistants.myide.domain.entity.Node;

import java.nio.file.Path;
import java.util.Comparator;

public class Tools {

    public static class NodeComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Node n1 = (Node) o1;
            Node n2 = (Node) o2;
            if (n1.isFile()) {
                if (n2.isFolder())
                    return 1;
            } else {
                if (n2.isFile())
                    return -1;
            }
            return n1.getPath().compareTo(n2.getPath());
        }
    }

}
