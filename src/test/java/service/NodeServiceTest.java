package service;

import fr.epita.assistants.ping.service.ProjectManager;
import org.junit.jupiter.api.BeforeEach;

public class NodeServiceTest {

    private ProjectManager projectManager;

    @BeforeEach
    public void setUp(){
        projectManager = new ProjectManager();
    }

}
