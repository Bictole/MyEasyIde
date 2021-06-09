package fr.epita.assistants.ping.feature.git;

import fr.epita.assistants.ping.aspect.AnyAspect;
import fr.epita.assistants.ping.aspect.GitAspect;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.project.AnyProject;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException, GitAPIException, URISyntaxException {

        FolderNode root = new FolderNode(Path.of("../test-git-feature"));
        Set aspects = new HashSet();
        aspects.add(new GitAspect());
        AnyProject p = new AnyProject(root, aspects);

        Add addFeature = new Add();
        addFeature.execute(p, "test.txt");

        Commit commitFeature = new Commit();
        commitFeature.execute(p, "Message de commit");

        Push pushFeature = new Push();
        pushFeature.execute(p,null);

        p.getgit().close();

    }
}
