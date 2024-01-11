package com.juaracoding.pcmspringbootcsr.util;

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;

public class PdfGeneratorLibre {

    private String [] strExceptionArr = new String[2];

    public PdfGeneratorLibre() {
        strExceptionArr[0] = "PdfGeneratorLibre";
    }

    public void generate(String strTitle,String[] strHeader, String[][] strBody , HttpServletResponse response,String orientation)  {
        try
        {
            Document document = new Document(PageSize.A4);
            if(orientation!=null)
            {
                document.setPageSize(PageSize.A2.rotate());
            }
            Rectangle footer = new Rectangle(30f, 30f, PageSize.A4.getRight(30f), 140f);
            footer.setBorder(Rectangle.BOX);
            footer.setBorderColor(Color.black);
            footer.setBorderWidth(2f);

            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

//            Image image = Image.getInstance("https://www.iconarchive.com/show/noto-emoji-travel-places-icons-by-google/42598-rocket-icon.html");
            Image image = Image.getInstance("https://icons.iconarchive.com/icons/acidrums4/betelgeuse/128/Categories-applications-education-language-icon.png");

            image.scaleAbsolute(100f,100f);
            document.add(image);

            Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
            fontTiltle.setSize(20);
            Paragraph paragraph = new Paragraph(strTitle, fontTiltle);
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(paragraph);
            PdfPTable table = new PdfPTable(strHeader.length);
            table.setWidthPercentage(100f);
            int[] intWidths = new int[strHeader.length];
            for(int i=0;i<strHeader.length;i++)
            {
                intWidths[i]=3;
            }
            table.setWidths(intWidths);
            table.setSpacingBefore(5);
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(CMYKColor.LIGHT_GRAY);// INI DIGANTI BIAR GAK SAMA
            cell.setPadding(5);
            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
            font.setColor(CMYKColor.WHITE);
            for(int i=0;i<strHeader.length;i++)
            {
                cell.setPhrase(new Phrase(strHeader[i], font));
//                cell.setBackgroundColor(Color.LIGHT_GRAY);// INI DIGANTI BIAR GAK SAMA
                cell.setBackgroundColor(Color.GRAY);// INI DIGANTI BIAR GAK SAMA
                table.addCell(cell);
            }

            // Iterating the list of students
            for(int i=0;i<strBody.length;i++)
            {
                for(int j=0;j<strBody[i].length;j++)
                {
                    table.addCell(strBody[i][j]);
                }
            }

            document.add(table);
            document.close();
        }
        catch(Exception e)
        {
            strExceptionArr[1] = "generate(String[] strHeader,String[][] strBody ,HttpServletResponse response) --- LINE 59";
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
        }
    }
}
