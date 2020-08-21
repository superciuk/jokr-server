package com.joker.jokerapp.service;

import com.joker.jokerapp.entity.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

@Service(PrinterService.NAME)
public class PrinterServiceBean implements PrinterService {

    protected static final Logger log = org.slf4j.LoggerFactory.getLogger(PrinterServiceBean.class);

    static class TicketPrinter implements Printable {

        final  TableItem tableToPrint;
        final Ticket ticketToPrint;
        final PrinterGroup printerGroupToSendTicket;
        final boolean isGrillTicket;
        final boolean withFries;

        public TicketPrinter(TableItem table, Ticket ticket, PrinterGroup printerGroup, boolean isGrillTicketParam, boolean withFriesParam) {

            tableToPrint = table;
            ticketToPrint = ticket;
            printerGroupToSendTicket = printerGroup;
            isGrillTicket = isGrillTicketParam;
            withFries = withFriesParam;

        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

            if (pageIndex == 0) {

                Font font1 = new Font("ZapfDingbats", Font.BOLD, 18);
                Font font2 = new Font("ZapfDingbats", Font.PLAIN, 14);
                Font font3 = new Font("ZapfDingbats", Font.BOLD, 12);

                int xMin = (int) pageFormat.getImageableX() + 1;
                int y = 20;

                int yInc = 12;


                Graphics2D graphics2D = (Graphics2D) graphics;
                graphics2D.setFont(font1);
                graphics2D.drawString(printerGroupToSendTicket.toString().toUpperCase(), xMin + 70, y);
                y += 30;
                graphics2D.drawString("TAVOLO: ".concat(tableToPrint.getTableCaption()), xMin, y);
                y += 30;
                graphics2D.setFont(font3);
                graphics2D.drawString("Coperti: ".concat(tableToPrint.getCurrentOrder().getActualSeats().toString()), xMin, y);
                y += 20;
                java.util.Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                graphics2D.drawString("Time: ".concat(sdf.format(cal.getTime())), xMin, y);
                y += 30;

                if (isGrillTicket && !withFries) {

                    graphics2D.drawString("NO FRITTURE", xMin + 10, y);
                    y += 30;

                }

                graphics2D.setFont(font2);

                ticketToPrint.getOrderLines().sort(Comparator.comparing(OrderLine::getPosition));

                for (OrderLine line : ticketToPrint.getOrderLines()) {

                    if (line.getPrinterGroup().equals(printerGroupToSendTicket) && (line.getTicket().getTicketStatus().equals(TicketStatus.notSended))) {

                        if (!line.getIsModifier()) graphics2D.drawString(line.getQuantity().toString(), xMin, y);

                        int linesToDraw = Math.round(line.getItemName().length() / 20) + 1;

                        int spacePosition = 0;
                        int currentSpacePosition = 0;

                        for (int l = 1; l < linesToDraw; l++) {

                            String lineName = line.getItemName();

                            for (int i = spacePosition; i < line.getItemName().length(); i++) {

                                char c = lineName.charAt(i);

                                if (Character.isWhitespace(c)) {

                                    if (i > 20 * l) break;

                                    spacePosition = i;

                                }

                            }

                            graphics2D.drawString(lineName.substring(currentSpacePosition, spacePosition), xMin + font2.getSize(), y);

                            currentSpacePosition = spacePosition;

                            y = y + yInc + 4;

                        }

                        graphics2D.drawString(line.getItemName().substring(currentSpacePosition), xMin + font2.getSize(), y);

                        y = y + yInc + 8;

                    }

                }

                graphics2D.drawString(".", xMin, y);

                return Printable.PAGE_EXISTS;
            }

            return Printable.NO_SUCH_PAGE;

        }

    }

    static class BillPrinter implements Printable {

        protected static final Logger log = org.slf4j.LoggerFactory.getLogger(BillPrinter.class);
        final TableItem tableToPrint;

        public BillPrinter (TableItem table) {

            tableToPrint=table;

        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

            if (pageIndex == 0) {

                Font font1 = new Font("ZapfDingbats", Font.BOLD, 20);
                Font font2 = new Font("ZapfDingbats", Font.PLAIN, 10);
                Font font3 = new Font("ZapfDingbats", Font.BOLD, 11);

                int x;
                int xMin = (int) pageFormat.getImageableX() + 1;
                int y = 20;
                int paperWidth = (int) pageFormat.getImageableWidth();

                //int yInc1 = font1.getSize() / 2;
                int yInc2 = font1.getSize() / 2;
                int yInc3 = font1.getSize() / 2;

                Graphics2D graphics2D = (Graphics2D) graphics;

                BufferedImage bufferedImage = null;

                try {

                    //linux
                    bufferedImage = ImageIO.read(new File("/home/pi/logo.jpg"));

                    //windows
                    //bufferedImage = ImageIO.read(new File("c:\\logo.jpg"));

                } catch (Exception e) {
                    log.error("Error", e);
                }

                graphics2D.drawImage(bufferedImage, null, 16, -10);

                y += 80;
                graphics2D.setFont(font2);
                graphics2D.drawString("PRECONTO TAVOLO: ".concat(tableToPrint.getCurrentOrder().getTableItemCaption()), xMin, y);
                y += 20;

                graphics2D.drawLine(xMin, y, paperWidth, y);

                y = y + 2 * yInc2;

                for (Ticket ticket : tableToPrint.getCurrentOrder().getTickets()) {

                    ticket.getOrderLines().sort(Comparator.comparing(OrderLine::getPosition));

                    for (OrderLine line : ticket.getOrderLines()) {

                            if (!line.getIsModifier()) graphics2D.drawString(line.getQuantity().toString(), xMin, y);

                            int linesToDraw = Math.round(line.getItemName().length() / 24) + 1;

                            int spacePosition = 0;
                            int currentSpacePosition = 0;

                            for (int l = 1; l < linesToDraw; l++) {

                                String lineName = line.getItemName();

                                for (int i = spacePosition; i < line.getItemName().length(); i++) {

                                    char c = lineName.charAt(i);

                                    if (Character.isWhitespace(c)) {

                                        if (i > 24 * l) break;

                                        spacePosition = i;

                                    }

                                }

                                String stringToDraw = lineName.substring(currentSpacePosition, spacePosition);
                                AttributedString attributedString = new AttributedString(stringToDraw);
                                attributedString.addAttribute(TextAttribute.SIZE, font2.getSize());

                                if (line.getIsReversed()) attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);

                                graphics2D.drawString(attributedString.getIterator(), xMin + font2.getSize(), y);

                                currentSpacePosition = spacePosition;

                                if (l == 1 && !line.getIsModifier()) {

                                    if (line.getIsReversed()) {

                                        x = paperWidth - Math.multiplyExact(4, font2.getSize() - 3);
                                        graphics2D.drawString("0.00", x, y);

                                    } else {

                                        x = paperWidth - Math.multiplyExact(line.getPrice().toString().length(), font2.getSize() - 3);
                                        graphics2D.drawString(line.getPrice().toString(), x, y);

                                    }

                                }

                                y = y + yInc2 + 1;

                            }

                            String stringToDraw = line.getItemName().substring(currentSpacePosition);
                            AttributedString attributedString = new AttributedString(stringToDraw);
                            attributedString.addAttribute(TextAttribute.SIZE, font2.getSize());
                            if (line.getIsReversed()) attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                            graphics2D.drawString(attributedString.getIterator(), xMin + font2.getSize(), y);

                            if (currentSpacePosition == 0 && !line.getIsModifier()) {

                                x = paperWidth - Math.multiplyExact(line.getPrice().toString().length(), font2.getSize() - 3);
                                graphics2D.drawString(line.getPrice().toString(), x, y);

                            }

                            y = y + yInc2 + 4;

                       }
                }

                graphics2D.drawLine(xMin, y, paperWidth, y);
                y = y + 2 * yInc2;
                graphics2D.setFont(font3);
                graphics2D.drawString("SUBTOTALE", xMin, y);
                x = paperWidth - Math.multiplyExact(tableToPrint.getCurrentOrder().getCharge().toString().length(), font3.getSize() - 3);
                graphics2D.drawString(tableToPrint.getCurrentOrder().getCharge().toString(), x, y);
                y = y + yInc3 + 3;
                graphics2D.drawString("SERVIZIO", xMin, y);
                x = paperWidth - Math.multiplyExact(tableToPrint.getCurrentOrder().getTaxes().toString().length(), font3.getSize() - 3);
                graphics2D.drawString(tableToPrint.getCurrentOrder().getTaxes().toString(), x, y);
                y = y + yInc3 + 20;

                graphics2D.setFont(font1);

                graphics2D.drawString("TOTALE", xMin, y);
                x = paperWidth - 4 - Math.multiplyExact(tableToPrint.getCurrentOrder().getCharge().add(tableToPrint.getCurrentOrder().getTaxes()).toString().length(), font1.getSize() - 7);
                graphics2D.drawString(tableToPrint.getCurrentOrder().getCharge().add(tableToPrint.getCurrentOrder().getTaxes()).toString(), x, y);
                y = y + yInc3 + 10;

                graphics2D.setFont(font2);

                graphics2D.drawString("Coperti: ".concat(tableToPrint.getCurrentOrder().getActualSeats().toString()), xMin, y);
                y = y + 2 * yInc3;
                graphics2D.setFont(font2);
                graphics2D.drawString("NON FISCALE", 60, y);

                return Printable.PAGE_EXISTS;

            }

            return Printable.NO_SUCH_PAGE;

        }

    }

    @Override
    public void printTicket(TableItem tableToPrint, Ticket ticketToPrint) {

        PrinterGroup printerGroupToSendTicket;

        boolean withFries = false;
        boolean isGrillTicket = false;

        for (PrinterGroup printerGroup: PrinterGroup.values()) {

            printerGroupToSendTicket = printerGroup;

            boolean printerGroupLinesExixts = false;

            for (OrderLine line: ticketToPrint.getOrderLines())
                if (line.getPrinterGroup().equals(printerGroupToSendTicket)) printerGroupLinesExixts = true;

            if (printerGroupToSendTicket.equals(PrinterGroup.Fryer) && printerGroupLinesExixts) withFries = true;
            if (printerGroupToSendTicket.equals(PrinterGroup.Grill)) isGrillTicket = true;

            if (printerGroupLinesExixts) {

                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

                PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);

                if (printServices[0] != null) {

                    MediaPrintableArea mpa = new MediaPrintableArea(1, 1, 74, 2000, MediaPrintableArea.MM);

                    PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                    printRequestAttributeSet.add(MediaSizeName.ISO_A0);
                    printRequestAttributeSet.add(mpa);

                    DocAttributeSet docAttributeSet = new HashDocAttributeSet();
                    docAttributeSet.add(MediaSizeName.ISO_A0);

                    docAttributeSet.add(mpa);

                    PrinterServiceBean.TicketPrinter ticketPrinter = new TicketPrinter(tableToPrint, ticketToPrint, printerGroupToSendTicket, isGrillTicket, withFries);

                    DocPrintJob docPrintJob = printServices[0].createPrintJob();
                    SimpleDoc doc1 = new SimpleDoc(ticketPrinter, flavor, docAttributeSet);

                    try {

                        docPrintJob.print(doc1, printRequestAttributeSet);

                    } catch (PrintException e) {

                        log.error("Error", e);

                    }

                }

            }

        }
    }

    @Override
    public boolean printBill(TableItem tableToPrint) {

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);

        if (printServices[0] != null) {

            MediaPrintableArea mpa = new MediaPrintableArea(1, 1, 74, 2000, MediaPrintableArea.MM);

            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.ISO_A0);
            printRequestAttributeSet.add(mpa);

            DocAttributeSet docAttributeSet = new HashDocAttributeSet();
            docAttributeSet.add(MediaSizeName.ISO_A0);

            docAttributeSet.add(mpa);

            PrinterServiceBean.BillPrinter bill = new PrinterServiceBean.BillPrinter(tableToPrint);

            DocPrintJob docPrintJob = printServices[0].createPrintJob();
            SimpleDoc doc1 = new SimpleDoc(bill, flavor, docAttributeSet);

            try {

                docPrintJob.print(doc1, printRequestAttributeSet);

            } catch (PrintException e) {

                log.error("Error", e);
                return false;

            }

        }

        return true;

    }

}