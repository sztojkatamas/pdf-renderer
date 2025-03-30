package com.company.pdfrenderer.flyingsaucer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

public class FlyingSaucerRenderer {

    public void generatePdfFromHtml(String htmlTemplate, Map<String, String> data, String outputFilePath) throws Exception {

        String processedHtml = replacePlaceholders(htmlTemplate, data);
        //String correctedHtml = processedHtml.replace("&nbsp;", " ");
        //String preProcessedHtml = correctedHtml;

        try (OutputStream os = new FileOutputStream(outputFilePath)) {
            Document jsoupDoc = Jsoup.parse(processedHtml);
            jsoupDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            //correctImgTags(jsoupDoc);

            ITextRenderer renderer = new ITextRenderer();
            //renderer.setDocumentFromString(jsoupDoc.outerHtml()); // Use Jsoup output directly
            renderer.setDocumentFromString(jsoupDoc.html());
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

}
