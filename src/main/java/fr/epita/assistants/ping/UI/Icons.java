package fr.epita.assistants.ping.UI;

public enum Icons {
    NEW_PROJECT("newProject.png"),
    OPEN_PROJECT("open.png"),
    NEW_FOLDER("newFolder.png"),
    NEW_FILE("newFile.png"),
    OPEN("open.png"),
    SAVE("save.png"),
    SAVE_AS("save_as.png"),
    COPY("copy.png"),
    CUT("cut.png"),
    PASTE("paste.png"),
    UNDO("undo.png"),
    REDO("redo.png"),
    EXIT("exit.png"),
    RUN("exec.png"),
    GIT_COMMIT("gitCommit.png"),
    GIT_ADD("gitAdd.png"),
    GIT_PULL("gitPull.png"),
    GIT_PUSH("gitPush.png");

    public String path;

    Icons(String s) {
        String mainPath = "src/main/resources/icons/";
        path = mainPath + s;
    }
}
