package org.ricky.core.common.converter.file;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.ceil;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/28
 * @className PptxToPDFConverter
 * @desc PPTX转PDF
 */
public class PptxToPDFConverter extends FileConverter {

    /**
     * 幻灯片集合
     */
    private XSLFSlide[] slides;

    public PptxToPDFConverter(InputStream inStream, OutputStream outStream, boolean showOutputMessages, boolean closeStreamsWhenComplete) {
        super(inStream, outStream, showOutputMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        Dimension pgSize = processSlides();
        double zoom = 2;
        AffineTransform at = new AffineTransform();
        at.setToScale(zoom, zoom);

        Document document = new Document(new Rectangle(pgSize.width, pgSize.height));
        PdfWriter writer = PdfWriter.getInstance(document, outStream);
        document.open();

        for (int i = 0; i < slides.length; i++) {
            XSLFSlide slide = slides[i];
            uniformFont(slide); // 统一字体

            // 绘制幻灯片内容
            BufferedImage bufImg = new BufferedImage((int) ceil(pgSize.width * zoom), (int) ceil(pgSize.height * zoom), TYPE_INT_RGB);
            Graphics2D graphics = bufImg.createGraphics();
            graphics.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

            graphics.setTransform(at);
            graphics.setPaint(getSlideBGColor(i));
            graphics.fill(new Rectangle2D.Float(0, 0, pgSize.width, pgSize.height));
            slide.draw(graphics);
            graphics.dispose();

            // 创建 PDF 页面并添加图像
            Image image = Image.getInstance(bufImg, null);
            document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
            document.newPage();
            image.setAbsolutePosition(0, 0);
            document.add(image);
        }

        document.close();
        writer.close();
    }

    private Dimension processSlides() throws IOException {
        XMLSlideShow ppt = new XMLSlideShow(inStream);
        Dimension dimension = ppt.getPageSize();
        slides = ppt.getSlides().toArray(new XSLFSlide[0]);
        return dimension;
    }

    /**
     * 统一字体，解决中文乱码
     */
    private static void uniformFont(XSLFSlide slide) {
        for (XSLFShape shape : slide.getShapes()) {
            if (!(shape instanceof XSLFTextShape textShape)) {
                continue;
            }
            for (XSLFTextParagraph textParagraph : textShape.getTextParagraphs()) {
                for (XSLFTextRun textRun : textParagraph.getTextRuns()) {
                    textRun.setFontFamily("宋体");
                }
            }
        }
    }

    private Color getSlideBGColor(int index) {
        return slides[index].getBackground().getFillColor();
    }

    private void drawOntoThisGraphic(int index, Graphics2D graphics) {
        slides[index].draw(graphics);
    }

}
