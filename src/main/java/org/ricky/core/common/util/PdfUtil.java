package org.ricky.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className PdfUtil
 * @desc
 */
@Slf4j
public class PdfUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * 提取PDF文本并写入文件（追加模式）
     */
    public static void extractTextToFile(InputStream is, String outputTextPath) throws IOException {
        if (is == null) {
            log.error("输入流不能为空");
            return;
        }

        try (PDDocument document = PDDocument.load(is);
             FileWriter writer = new FileWriter(outputTextPath, true)) {

            // 检查PDF是否允许提取内容
            AccessPermission permission = document.getCurrentAccessPermission();
            if (!permission.canExtractContent()) {
                throw new IOException("PDF文件禁止提取文本内容");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            // 合并所有页面文本，减少IO操作
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());
            String text = stripper.getText(document)
                    .replaceAll("\\s{2,}", ",")  // 合并多个空格或换行为逗号
                    .replaceAll("\n", "");

            writer.write(text.trim());
        } catch (Exception e) {
            log.error("提取PDF文本失败", e);
            throw e;
        }
    }

    /**
     * 将PDF每页转换为PNG图片
     *
     * @param pdfPath   PDF文件路径
     * @param outputDir 图片输出目录
     * @param zoomScale 缩放比例（建议1.0~3.0）
     */
    public static void convertToImages(String pdfPath, String outputDir, float zoomScale) throws IOException {
        Path dirPath = Paths.get(outputDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = renderer.renderImage(page, zoomScale);
                String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
                String outputPath = Paths.get(outputDir, timestamp + "_page_" + (page + 1) + ".png").toString();
                ImageIO.write(image, "PNG", new File(outputPath));
            }
        } catch (IOException e) {
            log.error("PDF转图片失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 生成PDF缩略图（第一页）
     *
     * @param is         PDF输入流
     * @param outputPath 缩略图输出路径
     * @param zoomScale  缩放比例
     */
    public static void generateThumbnail(InputStream is, String outputPath, float zoomScale) throws IOException {
        try (PDDocument document = PDDocument.load(is)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImage(0, zoomScale);
            ImageIO.write(image, "PNG", new File(outputPath));
        } catch (IOException e) {
            log.error("生成缩略图失败: {}", e.getMessage());
            throw e;
        }
    }

    public static void generateThumbnail(InputStream is, String outputPath) throws IOException {
        generateThumbnail(is, outputPath, 2.0f);
    }

}
