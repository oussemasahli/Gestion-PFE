import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class PdfExporter {

    public static void exportDocumentToPdf(DocumentAdministratif document) throws Exception {
        Document pdfDocument = new Document();
        PdfWriter.getInstance(pdfDocument, new FileOutputStream(document.getId() + ".pdf"));
        pdfDocument.open();

        pdfDocument.add(new Paragraph("Document Administratif"));
        pdfDocument.add(new Paragraph("ID: " + document.getId()));
        pdfDocument.add(new Paragraph("Type: " + document.getType()));
        pdfDocument.add(new Paragraph("Contenu: " + document.getContent()));
        pdfDocument.add(new Paragraph("Date: " + document.getDate()));
        pdfDocument.add(new Paragraph("Projet: " + (document.getProjet() != null ? document.getProjet().getTitre() : "Aucun")));

        pdfDocument.close();
    }
}