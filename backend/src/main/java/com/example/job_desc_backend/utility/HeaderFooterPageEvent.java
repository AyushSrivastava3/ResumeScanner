package com.example.job_desc_backend.utility;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
    private Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable header = new PdfPTable(1);
        try {
            header.setWidths(new int[]{24});
            header.setTotalWidth(527);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(40);
            header.getDefaultCell().setBorder(Rectangle.BOTTOM);
            header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            header.addCell(new Phrase("Resume Feedback", headerFont));

            header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
}
