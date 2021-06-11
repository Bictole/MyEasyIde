package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.service.ProjectManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Dist implements Feature {
    private class ExecutionReportDist implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportDist() {
            this.success = true;
        }

        public ExecutionReportDist(String errorMessage) {
            this.success = false;
            this.errorMessage = errorMessage;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    @Override
    public ExecutionReport execute(Project project, Object... param) {
        if (param.length > 0)
            return new Dist.ExecutionReportDist("Too much argument provided");

        ProjectManager projectManager = new ProjectManager();
        projectManager.execute(project, Mandatory.Features.Any.CLEANUP);

        return dist(project);
    }

    private Dist.ExecutionReportDist dist(Project project) {
        try {
            String sourceFile = String.valueOf(project.getRootNode().getPath());
            FileOutputStream fos = new FileOutputStream(project.getRootNode().getPath() + ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(sourceFile);

            distRec(fileToZip, fileToZip.getName(), zipOut);

            zipOut.close();
            fos.close();

            return new Dist.ExecutionReportDist();
        }
        catch (Exception e) {
            return new Dist.ExecutionReportDist("Error Dist Feature : " + e.getMessage());
        }
    }

    private static void distRec(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }

            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                distRec(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Any.DIST;
    }
}
