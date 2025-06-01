package com.lucio.erp_new_app_3.services.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfGeneratorService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    public byte[] generatePdfFromThymeleaf(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);

        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur de génération PDF", e);
        }
    }

    // public PdfGeneratorService(TemplateEngine templateEngine) {
    //     this.templateEngine = templateEngine;
    // }

    // public byte[] generatePdfFromThymeleaf(String templatePath, Map<String, Object> data) {
    //     Context context = new Context();
    //     context.setVariables(data);

    //     String htmlContent = templateEngine.process(templatePath, context);

    //     try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
    //         ITextRenderer renderer = new ITextRenderer();
    //         renderer.setDocumentFromString(htmlContent);
    //         renderer.layout();
    //         renderer.createPDF(outputStream);
    //         return outputStream.toByteArray();
    //     } catch (Exception e) {
    //         throw new RuntimeException("Erreur lors de la génération du PDF", e);
    //     }
    // }

}
