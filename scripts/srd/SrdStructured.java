import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * QA 0.7.0-preqa.2 (A5, L1, L2): dumps every text line of a PDF with its layout so the SRD can be
 * shown with headings, tables and paragraphs. One record per line:
 *
 *   L \t x0 \t xEnd \t y \t fontSize \t boldPrefixChars \t allBold \t text
 *
 * Gaps between words wider than a few spaces become "\u001f" (cell separators). Pages start with
 * "@@PAGE n". Usage: java -cp pdfbox... SrdStructured.java in.pdf out.txt
 */
public class SrdStructured extends PDFTextStripper {
    private final PrintStream out;
    private final List<String> words = new ArrayList<>();
    private final List<List<TextPosition>> wordPositions = new ArrayList<>();

    SrdStructured(PrintStream out) {
        this.out = out;
        setSortByPosition(false);
    }

    public static void main(String[] args) throws Exception {
        try (PDDocument doc = Loader.loadPDF(new File(args[0]));
             PrintStream out = new PrintStream(new File(args[1]), StandardCharsets.UTF_8)) {
            SrdStructured stripper = new SrdStructured(out);
            for (int p = 1; p <= doc.getNumberOfPages(); p++) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                out.println("@@PAGE " + p);
                stripper.getText(doc);
                stripper.flush();
            }
        }
    }

    @Override
    protected void writeString(String text, List<TextPosition> positions) {
        words.add(text);
        wordPositions.add(new ArrayList<>(positions));
    }

    @Override
    protected void writeLineSeparator() {
        flush();
    }

    @Override
    protected void writeParagraphEnd() {
        flush();
    }

    @Override
    protected void writeWordSeparator() {
    }

    @Override
    protected void startPage(PDPage page) {
    }

    private static boolean bold(TextPosition t) {
        String name = t.getFont() == null || t.getFont().getName() == null ? "" : t.getFont().getName().toLowerCase();
        return name.contains("bold") || name.contains("black") || name.contains("semibold") || name.contains("heavy") || name.contains("demi");
    }

    void flush() {
        if (words.isEmpty()) return;
        StringBuilder text = new StringBuilder();
        float x0 = Float.MAX_VALUE, xEnd = 0, y = 0, size = 0;
        int boldPrefix = 0;
        boolean stillBold = true;
        boolean allBold = true;
        TextPosition previous = null;
        for (int w = 0; w < words.size(); w++) {
            List<TextPosition> ps = wordPositions.get(w);
            if (ps.isEmpty()) continue;
            TextPosition first = ps.get(0);
            if (previous != null) {
                float gap = first.getXDirAdj() - (previous.getXDirAdj() + previous.getWidthDirAdj());
                float space = Math.max(previous.getWidthOfSpace(), 1.5f);
                boolean sameLine = Math.abs(first.getYDirAdj() - previous.getYDirAdj()) < 2.5f;
                text.append(sameLine && gap > Math.max(3.2f * space, 7f) ? "\u001f" : " ");
                if (stillBold && bold(first)) boldPrefix++;
            }
            for (TextPosition t : ps) {
                x0 = Math.min(x0, t.getXDirAdj());
                xEnd = Math.max(xEnd, t.getXDirAdj() + t.getWidthDirAdj());
                size = Math.max(size, t.getFontSizeInPt());
                if (y == 0) y = t.getYDirAdj();
                boolean b = bold(t);
                if (!b) {
                    allBold = false;
                    stillBold = false;
                } else if (stillBold) {
                    boldPrefix++;
                }
            }
            text.append(words.get(w));
            previous = ps.get(ps.size() - 1);
        }
        words.clear();
        wordPositions.clear();
        String line = text.toString().replace('\t', ' ').trim();
        if (line.isEmpty()) return;
        out.printf("L\t%.1f\t%.1f\t%.1f\t%.1f\t%d\t%b\t%s%n", x0, xEnd, y, size, boldPrefix, allBold, line);
    }
}
