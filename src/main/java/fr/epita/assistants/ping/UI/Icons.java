package fr.epita.assistants.ping.UI;

public enum Icons {
    NEW_PROJECT("folder.png"),
    OPEN_PROJECT("folder.png"),
    NEW_FOLDER("folder.png"),
    NEW_FILE("file.png"),
    OPEN("folder.png"),
    SAVE("save.png"),
    SAVE_AS("save.png"),
    COPY("copy.png"),
    CUT("cut.png"),
    PASTE("paste.png"),
    UNDO("undo.png"),
    REDO("redo.png"),
    EXIT("folder.png"),
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
