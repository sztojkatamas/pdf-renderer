package org.example;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

@Slf4j
public class App {

    public void generatePdfFromHtml(String htmlTemplate, Map<String, String> data, String outputFilePath) throws Exception {

        String processedHtml = replacePlaceholders(htmlTemplate, data);
        String correctedHtml = processedHtml.replace("&nbsp;", " ");

        // Pre-process the HTML string before Jsoup
        String preProcessedHtml = correctedHtml;

        try (OutputStream os = new FileOutputStream(outputFilePath)) {
            Document jsoupDoc = Jsoup.parse(preProcessedHtml);
            correctImgTags(jsoupDoc);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(jsoupDoc.outerHtml()); // Use Jsoup output directly
            renderer.layout();
            renderer.createPDF(os);
        }
    }

    private void correctImgTags(Document jsoupDoc) {
        for (Element img : jsoupDoc.select("img")) {
            if (!img.hasText()) {
                img.appendText("");
            }
        }
    }


    private String replacePlaceholders(String html, Map<String, String> data) {
        String processedHtml = html;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            processedHtml = processedHtml.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return processedHtml;
    }

    public static void main(String[] args) throws Exception {
        File templatFile = new File(args[0]);
        String htmlTemplate = Files.readString(templatFile.toPath());

        Map<String, String> data = Map.of("name", "Alice", "image-1", "image-007.jpg");
        new App().generatePdfFromHtml(htmlTemplate, data, "output.pdf");
    }
}
