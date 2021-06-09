package fr.epita.assistants.ping.feature.git;

import fr.epita.assistants.ping.aspect.AnyAspect;
import fr.epita.assistants.ping.aspect.GitAspect;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.project.AnyProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException, GitAPIException {

        FolderNode root = new FolderNode(Path.of("../test-git-feature"));
        Set aspects = new HashSet();
        aspects.add(new GitAspect());
        AnyProject p = new AnyProject(root, aspects);

        Add addFeature = new Add();
        addFeature.execute(p, "test.txt");

        Commit commitFeature = new Commit();
        commitFeature.execute(p, "Message de commit");

        /*Push pushFeature = new Push();
        pushFeature.execute(p,null);

        Git git = Git.open(new File(String.valueOf(Path.of("../test-git-feature"))));
        git.add().addFilepattern(".").call();
        git.commit().setMessage( "Message de Commit" ).call();

        try {
            git.push().setRemote("origin").add("master").setCredentialsProvider(new UsernamePasswordCredentialsProvider("jeanne.morin@epita.fr", "")).call();
        } catch (GitAPIException e) {
            // Add your own logic here, for example:
            System.out.println("Username or password incorrect : " + e.getMessage());
        }*/
    }
}
