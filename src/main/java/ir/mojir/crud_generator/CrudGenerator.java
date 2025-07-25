package ir.mojir.crud_generator;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class CrudGenerator {

    public static void main(String[] args) {
        try {
            createSources("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSources(String basePackage) throws URISyntaxException {
        URL url = CrudGenerator.class.getClassLoader().getResource("templatefiles");

        File templateFilesDir = Paths.get(url.toURI()).toFile();
        for(File file: templateFilesDir.listFiles()) {
            System.out.println(file.getName());
        }

    }


}
