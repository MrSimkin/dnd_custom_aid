import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Dumps a PDF's outline as TSV lines "depth\tpage\ttitle", then every page's text as
 * "@@PAGE n" blocks. Usage: java -cp ... SrdOutline.java in.pdf out.txt
 */
public class SrdOutline {
    public static void main(String[] args) throws Exception {
        try (PDDocument doc = Loader.loadPDF(new File(args[0]));
             PrintStream out = new PrintStream(new File(args[1]), StandardCharsets.UTF_8)) {
            out.println("@@OUTLINE");
            if (doc.getDocumentCatalog().getDocumentOutline() != null) {
                walk(doc, doc.getDocumentCatalog().getDocumentOutline(), 0, out);
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);
            for (int p = 1; p <= doc.getNumberOfPages(); p++) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                out.println("@@PAGE " + p);
                out.print(stripper.getText(doc));
            }
        }
    }

    private static void walk(PDDocument doc, PDOutlineNode node, int depth, PrintStream out) throws Exception {
        PDOutlineItem item = node.getFirstChild();
        while (item != null) {
            PDPage page = item.findDestinationPage(doc);
            int index = page == null ? -1 : doc.getPages().indexOf(page) + 1;
            out.println(depth + "\t" + index + "\t" + item.getTitle().replace('\t', ' ').trim());
            walk(doc, item, depth + 1, out);
            item = item.getNextSibling();
        }
    }
}
