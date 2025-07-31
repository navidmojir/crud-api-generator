package ir.mojir.crud_generator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CrudGenerator {

    private static Config config = new Config();

    
    public static void main(String[] args) {
        try {
            createSources();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSources() throws URISyntaxException, IOException {
        URL url = CrudGenerator.class.getClassLoader().getResource("templatefiles");

        File templateFilesDir = Paths.get(url.toURI()).toFile();

        replaceAndCopy(templateFilesDir, "");

    }


	private static void replaceAndCopy(File file, String parentDir) throws IOException {

        if(file.isDirectory()) {
        	System.out.println("DIR:"+file.getName());
            String dirName = file.getName();
            dirName = dirName.replaceAll("xxx", config.getEntityNameCamel());
            String path = parentDir + "/" + dirName;
            for(File subDir: file.listFiles()) {
                Files.createDirectories(Paths.get(config.getOutputDir() + path));
                replaceAndCopy(subDir, path);
            }
        }
        else {
        	System.out.println("FILE:"+file.getName());
            String fileContent = Files.readString(file.toPath());
            fileContent = fileContent.replaceAll("Xxx", config.getEntityName());
            fileContent =  fileContent.replaceAll("xxx", config.getEntityNameCamel());
            fileContent =  fileContent.replaceAll("xxxFa", config.getEntityPersianName());
            fileContent = fileContent.replaceAll("com.example.demo", config.getPackageName());
            String fileName = file.getName();
            fileName = fileName.replaceAll("Xxx", config.getEntityName());
            fileName = fileName.replaceAll("xxx", config.getEntityNameCamel());
            Files.writeString(Paths.get(config.getOutputDir() + parentDir + "/" + fileName), fileContent);
        }
    }


}
