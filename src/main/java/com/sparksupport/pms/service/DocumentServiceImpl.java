package com.sparksupport.pms.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sparksupport.pms.exception.ResourceNotFoundException;
import com.sparksupport.pms.model.Product;
import java.awt.Color;

import jakarta.servlet.http.HttpServletResponse;
 

@Service
public class DocumentServiceImpl implements DocumentService{

     private final ProductService productService;

    public DocumentServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void generateProductsPdf(HttpServletResponse response) throws Exception {
         try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            Paragraph title = new Paragraph("Product Table", font);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{1.5f, 3.5f, 5.0f, 2.5f, 2.5f, 3.0f});
            table.setSpacingBefore(10);

            // Table header
            String[] headers = {"ID", "Name", "Description", "Price", "Quantity", "Revenue"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell();
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setPadding(5);
                cell.setPhrase(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                table.addCell(cell);
            }

            // Table data
            for (Product product : productService.getAllProducts()) {
                table.addCell(String.valueOf(product.getId()));
                table.addCell(product.getName());
                table.addCell(product.getDescription());
                table.addCell(String.valueOf(product.getPrice()));
                table.addCell(String.valueOf(product.getQuantity()));
                double revenue = productService.getRevenueByProduct(product.getId());
                table.addCell(String.valueOf(revenue));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not generate PDF");
        }
    }

    @Override
    public void generateProductQrCode(Long id,HttpServletResponse response) throws Exception {
       Product product = productService.getProductById(id);
        if (product == null) {
            throw new  ResourceNotFoundException("Product not found");
        }
        String qrContent = "Product ID: " + product.getId() + ", Name: " + product.getName();
        int width = 300;
        int height = 300;
         QRCodeWriter qrCodeWriter = new  QRCodeWriter();
        try {
             BitMatrix bitMatrix = qrCodeWriter.encode(qrContent,
                     BarcodeFormat.QR_CODE, width, height);
            response.setContentType("image/png");
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG",
                    response.getOutputStream());
        } catch (WriterException e) {
            throw new RuntimeException("Could not generate QR code", e);
        }
    }

}
