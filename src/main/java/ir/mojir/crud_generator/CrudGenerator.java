package ir.mojir.crud_generator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CrudGenerator {

    private static String entityName = "Ticket";
    private static String entityNameCamel = "ticket";

    private static String packageName = "ir.mojir.simple_ticketing_system";

    private final static String outputDir = "./output/";
    public static void main(String[] args) {
        try {
            createSources("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSources(String basePackage) throws URISyntaxException, IOException {
        URL url = CrudGenerator.class.getClassLoader().getResource("templatefiles");

        File templateFilesDir = Paths.get(url.toURI()).toFile();

        replaceAndCopy(templateFilesDir, "");

//        for(File file: templateFilesDir.listFiles()) {
//            System.out.println(file.getName());
//        }

    }

    private static void replaceAndCopy(File file, String parentDir) throws IOException {

        if(file.isDirectory()) {
            String dirName = file.getName();
            dirName = dirName.replaceAll("xxx", entityNameCamel);
            String path = parentDir + "/" + dirName;
            for(File subDir: file.listFiles()) {
//                System.out.println(path);
                Files.createDirectories(Paths.get(outputDir + path));
                replaceAndCopy(subDir, path);
            }
//

        }
        else {
//            System.out.println(parentDir + "/" + file.getName());
            String fileContent = Files.readString(file.toPath());
            fileContent = fileContent.replaceAll("Xxx", entityName);
            fileContent =  fileContent.replaceAll("xxx", entityNameCamel);
            fileContent = fileContent.replaceAll("com.example.demo", packageName);
            String fileName = file.getName();
            fileName = fileName.replaceAll("Xxx", entityName);
            Files.writeString(Paths.get(outputDir + parentDir + "/" + fileName), fileContent);
        }
    }


}
