package cn.com.mz.app.finance.ai.service.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * AI 文件处理服务
 * 用于处理上传的文件，提取内容供 AI 分析
 *
 * @author mz
 */
@Slf4j
@Service
public class AiFileProcessService {

    // 文件大小限制
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_FILES = 5;
    private static final long MAX_TOTAL_SIZE = 20 * 1024 * 1024; // 20MB

    // 支持的文件类型
    private static final Set<String> SUPPORTED_TYPES = Set.of(
            // 图片
            "png", "jpg", "jpeg", "gif", "webp",
            // 文档
            "pdf", "txt", "md", "docx",
            // 数据
            "csv", "xlsx", "xls",
            // 代码/配置
            "json", "xml", "yaml", "yml", "sql", "html", "css", "js", "ts", "java", "py"
    );

    // 图片类型
    private static final Set<String> IMAGE_TYPES = Set.of("png", "jpg", "jpeg", "gif", "webp");

    /**
     * 处理上传的文件列表
     *
     * @param files 上传的文件数组
     * @return 处理结果，包含文本内容和图片 Base64
     */
    public FileProcessResult processFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return new FileProcessResult("", Collections.emptyList());
        }

        // 校验
        validateFiles(files);

        StringBuilder textContent = new StringBuilder();
        List<ImageData> images = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String fileName = file.getOriginalFilename();
            String extension = getFileExtension(fileName);

            log.info("Processing file: {}, type: {}", fileName, extension);

            try {
                if (IMAGE_TYPES.contains(extension)) {
                    // 图片类型：转为 Base64
                    String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                    String mimeType = getMimeType(extension);
                    images.add(new ImageData(fileName, base64, mimeType));

                    textContent.append(String.format("\n[图片 %d: %s]\n", i + 1, fileName));

                } else {
                    // 文档类型：提取文本
                    String content = extractTextContent(file, extension);
                    textContent.append(String.format("\n--- 文件 %d: %s ---\n%s\n",
                            i + 1, fileName, content));
                }

            } catch (Exception e) {
                log.error("Failed to process file: {}", fileName, e);
                textContent.append(String.format("\n[文件 %d: %s - 读取失败: %s]\n",
                        i + 1, fileName, e.getMessage()));
            }
        }

        return new FileProcessResult(textContent.toString(), images);
    }

    /**
     * 校验文件
     */
    private void validateFiles(MultipartFile[] files) {
        if (files.length > MAX_FILES) {
            throw new IllegalArgumentException("最多支持上传 " + MAX_FILES + " 个文件");
        }

        long totalSize = 0;
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("文件不能为空");
            }

            long size = file.getSize();
            if (size > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("文件 " + file.getOriginalFilename() +
                        " 超过大小限制 (最大 10MB)");
            }
            totalSize += size;

            String extension = getFileExtension(file.getOriginalFilename());
            if (!SUPPORTED_TYPES.contains(extension)) {
                throw new IllegalArgumentException("不支持的文件类型: " + extension);
            }
        }

        if (totalSize > MAX_TOTAL_SIZE) {
            throw new IllegalArgumentException("文件总大小超过限制 (最大 20MB)");
        }
    }

    /**
     * 提取文件文本内容
     */
    private String extractTextContent(MultipartFile file, String extension) throws Exception {
        return switch (extension) {
            case "txt", "md", "json", "xml", "yaml", "yml", "sql", "html", "css", "js", "ts", "java", "py" ->
                    extractPlainText(file);
            case "csv" -> extractCsv(file);
            case "xlsx", "xls" -> extractExcel(file, extension);
            case "docx" -> extractDocx(file);
            case "pdf" -> extractPdf(file);
            default -> "[不支持的文件类型]";
        };
    }

    /**
     * 提取纯文本
     */
    private String extractPlainText(MultipartFile file) throws Exception {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 提取 CSV 内容
     */
    private String extractCsv(MultipartFile file) throws Exception {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        String[] lines = content.split("\n");
        int maxLines = Math.min(lines.length, 100); // 最多显示 100 行
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < maxLines; i++) {
            result.append(lines[i]).append("\n");
        }
        if (lines.length > 100) {
            result.append("... (共 ").append(lines.length).append(" 行，仅显示前 100 行)");
        }
        return result.toString();
    }

    /**
     * 提取 Excel 内容
     */
    private String extractExcel(MultipartFile file, String extension) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = extension.equals("xlsx") ?
                     new XSSFWorkbook(is) : WorkbookFactory.create(is)) {

            StringBuilder result = new StringBuilder();
            int sheetCount = workbook.getNumberOfSheets();

            for (int s = 0; s < Math.min(sheetCount, 3); s++) { // 最多处理 3 个 sheet
                Sheet sheet = workbook.getSheetAt(s);
                result.append("Sheet: ").append(sheet.getSheetName()).append("\n");

                int rowCount = 0;
                for (Row row : sheet) {
                    if (rowCount++ >= 50) { // 每个 sheet 最多 50 行
                        result.append("... (更多行省略)\n");
                        break;
                    }
                    StringBuilder rowData = new StringBuilder();
                    for (Cell cell : row) {
                        String value = getCellValue(cell);
                        rowData.append(value).append("\t");
                    }
                    result.append(rowData).append("\n");
                }
                result.append("\n");
            }

            return result.toString();
        }
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ?
                    cell.getLocalDateTimeCellValue().toString() :
                    String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    /**
     * 提取 Word 文档内容
     */
    private String extractDocx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {

            StringBuilder result = new StringBuilder();
            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText();
                if (StringUtils.isNotBlank(text)) {
                    result.append(text).append("\n");
                }
            }
            return result.toString();
        }
    }

    /**
     * 提取 PDF 内容（简单实现，仅支持文本型 PDF）
     */
    private String extractPdf(MultipartFile file) throws Exception {
        // 注意：需要添加 PDF 解析依赖
        // 这里简单返回提示信息
        return "[PDF 文件内容，请安装 PDF 解析库以提取文本内容]";
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 获取 MIME 类型
     */
    private String getMimeType(String extension) {
        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    /**
     * 文件处理结果
     */
    public record FileProcessResult(String textContent, List<ImageData> images) {}

    /**
     * 图片数据
     */
    public record ImageData(String fileName, String base64Data, String mimeType) {}
}
