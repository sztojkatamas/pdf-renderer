package com.company.pdfrenderer.flyingsaucer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlyingSaucerRenderer {

    public void generatePdfFromHtml(String htmlTemplate, Map<String, String> propertiesData, String outputFilePath) throws Exception {

            String processedHtmlWithCount = replacePlaceholders(htmlTemplate, propertiesData);
            String finalProcessedHtml = processRepetitionsAndPlaceholders(processedHtmlWithCount, propertiesData);

            try (OutputStream os = new FileOutputStream(outputFilePath)) {
                Document jsoupDoc = Jsoup.parse(finalProcessedHtml);
                jsoupDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(jsoupDoc.html());
                renderer.layout();
                renderer.createPDF(os);
            }
        }

    private String processRepetitionsAndPlaceholders(String htmlTemplate, Map<String, String> data) {
        Document doc = Jsoup.parse(htmlTemplate);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        Element repeatedElement = doc.select("[data-repeat]").first();
        if (repeatedElement != null) {
            String repeatCountStr = repeatedElement.attr("data-repeat");
            try {
                int repeat = Integer.parseInt(repeatCountStr);
                repeatedElement.removeAttr("data-repeat");
                Element parent = repeatedElement.parent();
                int index = parent.children().indexOf(repeatedElement);

                for (int i = 0; i < repeat; i++) {
                    Element clonedElement = repeatedElement.clone();
                    String processedClone = generateNumberedPlaceholders(clonedElement.outerHtml(), i + 1);
                    parent.insertChildren(index + i + 1, Jsoup.parseBodyFragment(processedClone).body().children());
                }
                repeatedElement.remove(); // Remove the original template element

                // Now, perform the final placeholder replacement on the modified DOM
                return replacePlaceholders(doc.body().html(), data);

            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid repeat count for element: " + repeatedElement.tagName() + ". Expected a number, but got: " + repeatCountStr);
            }
        }

        return doc.body().html();
    }

        private String generateNumberedPlaceholders(String html, int index) {
            Pattern placeholderPattern = Pattern.compile("\\{\\{([a-zA-Z0-9-]+)\\}\\}");
            Matcher matcher = placeholderPattern.matcher(html);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String placeholderKey = matcher.group(1);
                matcher.appendReplacement(sb, "{{"+ placeholderKey + index + "}}");
            }
            matcher.appendTail(sb);
            return sb.toString();
        }

        private String replacePlaceholders(String html, Map<String, String> data) {
            String processedHtml = html;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                processedHtml = processedHtml.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            return processedHtml;
        }
}