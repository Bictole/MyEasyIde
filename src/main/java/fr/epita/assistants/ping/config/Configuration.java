package fr.epita.assistants.ping.config;

public class Configuration {

    public boolean windows = false;
    public String mavenCmd = "mvn";

    public String theme = "dark.xml";

    public Configuration()
    {
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            this.windows = true;
            mavenCmd = "mvn.cmd";
        }
    }
}