package org.example.classpiserver.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

@Component
public class AttachmentTextExtractor {

    private static final int MAX_CHARS = 12000;
    private static final Set<String> TEXT_EXTENSIONS = Set.of(".txt");
    private static final Set<String> PDF_EXTENSIONS = Set.of(".pdf");
    private static final Set<String> DOCX_EXTENSIONS = Set.of(".docx");
    private static final Set<String> XLSX_EXTENSIONS = Set.of(".xlsx");
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    private final FileStorageService fileStorageService;

    public AttachmentTextExtractor(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public String describeForAi(String attachmentUrl, String attachmentName) {
        if (attachmentUrl == null || attachmentUrl.isBlank()) {
            return "";
        }
        String name = attachmentName == null || attachmentName.isBlank() ? "附件" : attachmentName.trim();
        Path path = resolveStoredPath(attachmentUrl);
        if (path == null || !Files.isRegularFile(path)) {
            return "学生附件：" + name + "（文件未找到，请手动下载查看）";
        }
        String ext = extension(name, path);
        if (IMAGE_EXTENSIONS.contains(ext)) {
            return "学生提交了图片附件「" + name + "」。当前 AI 无法直接识别图片内容，请结合文字说明或下载附件后人工批阅。";
        }
        if (Set.of(".zip", ".rar", ".7z", ".doc", ".xls", ".ppt", ".pptx").contains(ext)) {
            return "学生附件「" + name + "」为压缩包或旧版 Office/PPT 格式，系统暂无法自动提取正文，请下载后查看。";
        }
        try {
            String extracted = extractText(path, ext);
            if (extracted == null || extracted.isBlank()) {
                return "学生附件「" + name + "」未能提取到有效文字，请下载后查看。";
            }
            return "学生附件「" + name + "」提取内容：\n" + truncate(extracted);
        } catch (Exception ex) {
            return "学生附件「" + name + "」读取失败（" + ex.getMessage() + "），请下载后查看。";
        }
    }

    private Path resolveStoredPath(String attachmentUrl) {
        String normalized = attachmentUrl.trim().replace('\\', '/');
        if (!normalized.startsWith("/uploads/")) {
            return null;
        }
        Path relative = Path.of(normalized.substring("/uploads/".length()));
        Path resolved = fileStorageService.getUploadRoot().resolve(relative).normalize();
        if (!resolved.startsWith(fileStorageService.getUploadRoot())) {
            return null;
        }
        return resolved;
    }

    private String extension(String originalName, Path path) {
        String name = originalName;
        if (name == null || !name.contains(".")) {
            name = path.getFileName().toString();
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return name.substring(dot).toLowerCase(Locale.ROOT);
    }

    private String extractText(Path path, String ext) throws Exception {
        if (TEXT_EXTENSIONS.contains(ext)) {
            return Files.readString(path, StandardCharsets.UTF_8);
        }
        if (PDF_EXTENSIONS.contains(ext)) {
            return extractPdf(path);
        }
        if (DOCX_EXTENSIONS.contains(ext)) {
            return extractDocx(path);
        }
        if (XLSX_EXTENSIONS.contains(ext)) {
            return extractXlsx(path);
        }
        return "";
    }

    private String extractPdf(Path path) throws Exception {
        try (PDDocument document = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    private String extractDocx(Path path) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = Files.newInputStream(path);
             XWPFDocument document = new XWPFDocument(in)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.isBlank()) {
                    sb.append(text.trim()).append('\n');
                }
            }
        }
        return sb.toString();
    }

    private String extractXlsx(Path path) throws Exception {
        StringBuilder sb = new StringBuilder();
        DataFormatter formatter = new DataFormatter();
        try (InputStream in = Files.newInputStream(path);
             Workbook workbook = new XSSFWorkbook(in)) {
            for (Sheet sheet : workbook) {
                sb.append("【").append(sheet.getSheetName()).append("】\n");
                for (Row row : sheet) {
                    StringBuilder rowText = new StringBuilder();
                    for (Cell cell : row) {
                        String value = formatter.formatCellValue(cell);
                        if (value != null && !value.isBlank()) {
                            if (rowText.length() > 0) {
                                rowText.append('\t');
                            }
                            rowText.append(value.trim());
                        }
                    }
                    if (rowText.length() > 0) {
                        sb.append(rowText).append('\n');
                    }
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private String truncate(String text) {
        String normalized = text.replace("\r\n", "\n").trim();
        if (normalized.length() <= MAX_CHARS) {
            return normalized;
        }
        return normalized.substring(0, MAX_CHARS) + "\n…（附件内容过长，已截断）";
    }
}
