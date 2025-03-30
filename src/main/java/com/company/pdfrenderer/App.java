package com.company.pdfrenderer;

import com.company.pdfrenderer.flyingsaucer.FlyingSaucerRenderer;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.Files;
import java.util.Map;

@Slf4j
public class App {

    public static void main(String[] args) throws Exception{
        App application = new App();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("flying-saucer")) {
                application.renderWithFlyingSaucer(args[1], args[2], args[3]);
            } else {
                log.error("Unknown solution");
            }
        } else {
            log.error("Usage:\njava -jar build\\libs\\pdfmaker-all.ja template.html datafile.properties output-filename.pdf");
        }
    }

    private void renderWithFlyingSaucer(String templateFilename, String dataFilename, String outputFilename) throws Exception{

        String htmlTemplate = Files.readString(new File(templateFilename).toPath());

        Map<String, String> data = Map.of("name", "Alice", "image-1", "image-007.jpg");

        FlyingSaucerRenderer pdfRenderer = new FlyingSaucerRenderer();
        pdfRenderer.generatePdfFromHtml(htmlTemplate, data, outputFilename);

    }
}
