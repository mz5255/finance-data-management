package cn.com.mz.app.finance.common.utils;

import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件处理工具类
 *
 * @author 马震
 * @version 1.0
 * @date 2025/1/3
 */
@Slf4j
public class FileUtils {

    /**
     * 读取CSV文件并动态生成Map（根据表头）
     *
     * @param tempFile 临时文件
     * @return Map列表，以第一行作为字段名
     */
    public static List<Map<String, Object>> readCsvToMapListWithHeader(File tempFile) {
        CsvReader csvReader = null;
        try {
            csvReader = CsvUtil.getReader();
            List<CsvRow> allRows = csvReader.read(tempFile).getRows();

            if (allRows.isEmpty()) {
                return List.of();
            }

            // 获取表头
            List<String> headers = allRows.get(0);

            // 处理数据行
            return allRows.stream()
                .skip(1) // 跳过表头
                .map(rowList -> {
                    Map<String, Object> rowMap = new HashMap<>();
                    for (int i = 0; i < headers.size() && i < rowList.size(); i++) {
                        String header = headers.get(i).trim(); // 去除空格
                        String value = rowList.get(i);
                        rowMap.put(header, value);
                    }
                    return rowMap;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("CSV文件读取失败: {}", tempFile.getName(), e);
            throw new RuntimeException("CSV文件读取失败", e);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (Exception e) {
                    log.warn("CSV读取器关闭失败", e);
                }
            }
            deleteTempFile(tempFile);
        }
    }

    /**
     * 读取Excel文件并转换为Map列表
     *
     * @param tempFile 临时文件
     * @return Map列表
     */
    public static List<Map<String, Object>> readExcelToMapList(File tempFile) {
        ExcelReader reader = null;
        try {
            reader = ExcelUtil.getReader(tempFile);
            return reader.readAll();
        } catch (Exception e) {
            log.error("Excel文件读取失败: {}", tempFile.getName(), e);
            throw new RuntimeException("Excel文件读取失败", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    log.warn("Excel读取器关闭失败", e);
                }
            }
            deleteTempFile(tempFile);
        }
    }

    /**
     * 根据文件类型读取文件内容（动态表头）
     *
     * @param file     上传的文件
     * @param tempFile 转换后的临时文件
     * @return Map列表，以表头作为字段名
     */
    public static List<Map<String, Object>> readFileToMapListWithHeader(MultipartFile file, File tempFile) {
        try {
            String fileName = file.getOriginalFilename();

            if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
                return readCsvToMapListWithHeader(tempFile);
            } else {
                return readExcelToMapList(tempFile);
            }
        } catch (Exception e) {
            log.error("文件读取失败: {}", file.getOriginalFilename(), e);
            deleteTempFile(tempFile);
            throw new RuntimeException("文件读取失败", e);
        }
    }

    /**
     * 通用的CSV读取方法，支持自定义列映射
     *
     * @param tempFile      临时文件
     * @param columnMapping 列索引到字段名的映射
     * @return Map列表
     */
    public static List<Map<String, Object>> readCsvWithMapping(File tempFile, Map<Integer, String> columnMapping) {
        CsvReader csvReader = null;
        try {
            csvReader = CsvUtil.getReader();
            return csvReader.read(tempFile).getRows().stream()
                .skip(1)
                .map(rowList -> {
                    Map<String, Object> rowMap = new HashMap<>();
                    columnMapping.forEach((index, fieldName) -> {
                        if (rowList.size() > index) {
                            rowMap.put(fieldName, rowList.get(index));
                        }
                    });
                    return rowMap;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("CSV文件读取失败: {}", tempFile.getName(), e);
            throw new RuntimeException("CSV文件读取失败", e);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (Exception e) {
                    log.error("CSV读取器关闭失败", e);
                }
            }
            deleteTempFile(tempFile);
        }
    }

    /**
     * 将MultipartFile转换为临时文件
     *
     * @param multipartFile 上传的文件
     * @param tempDir       临时目录路径，为null时使用系统默认临时目录
     * @return 临时文件
     */
    public static File convertToTempFile(MultipartFile multipartFile, String tempDir) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        String fileName = multipartFile.getOriginalFilename();
        String suffix = getFileExtension(fileName);
        if (StringUtils.isBlank(suffix)) {
            suffix = ".tmp";
        }

        File tempFile = null;
        try {
            if (StringUtils.isNotBlank(tempDir)) {
                // 使用指定目录
                File dir = new File(tempDir);
                if (!dir.exists()) {
                    boolean created = dir.mkdirs();
                    log.info("创建临时目录: {}, 结果: {}", tempDir, created);
                }
                tempFile = new File(dir, IdUtil.simpleUUID() + suffix);
            } else {
                // 使用系统临时目录
                tempFile = File.createTempFile(IdUtil.simpleUUID(), suffix);
            }

            multipartFile.transferTo(tempFile);
            log.info("临时文件创建成功: {}", tempFile.getAbsolutePath());
            return tempFile;

        } catch (IOException e) {
            log.error("临时文件创建失败: {}", fileName, e);
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            throw new RuntimeException("临时文件创建失败", e);
        }
    }

    /**
     * 将MultipartFile转换为临时文件（使用系统默认临时目录）
     *
     * @param multipartFile 上传的文件
     * @return 临时文件
     */
    public static File convertToTempFile(MultipartFile multipartFile) {
        return convertToTempFile(multipartFile, null);
    }

    /**
     * 一站式文件读取：转换临时文件 + 读取内容
     *
     * @param multipartFile 上传的文件
     * @param tempDir       临时目录，为null时使用系统默认
     * @return Map列表，以表头作为字段名
     */
    public static List<Map<String, Object>> readFileDirectly(MultipartFile multipartFile, String tempDir) {
        File tempFile = convertToTempFile(multipartFile, tempDir);
        return readFileToMapListWithHeader(multipartFile, tempFile);
    }

    /**
     * 一站式文件读取（使用系统默认临时目录）
     *
     * @param multipartFile 上传的文件
     * @return Map列表，以表头作为字段名
     */
    public static List<Map<String, Object>> readFileDirectly(MultipartFile multipartFile) {
        return readFileDirectly(multipartFile, null);
    }

    /**
     * 删除临时文件
     */
    private static void deleteTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            try {
                boolean deleted = tempFile.delete();
                if (deleted) {
                    log.debug("临时文件删除成功: {}", tempFile.getName());
                } else {
                    log.warn("临时文件删除失败: {}", tempFile.getName());
                }
            } catch (Exception e) {
                log.warn("删除临时文件时发生异常: {}", tempFile.getName(), e);
            }
        }
    }

    /**
     * 判断是否为CSV文件
     */
    public static boolean isCsvFile(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".csv");
    }

    /**
     * 判断是否为Excel文件
     */
    public static boolean isExcelFile(String fileName) {
        return fileName != null &&
            (fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls"));
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 导出对象列表为Excel文件
     *
     * @param dataList   数据列表
     * @param headers    表头数组
     * @param fieldNames 字段名数组（与表头对应）
     * @param fileName   文件名
     * @param response   HTTP响应对象
     */
    public static <T> void exportToExcel(List<T> dataList, String[] headers, String[] fieldNames,
                                         String fileName, HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("数据导出");

            // 创建表头
            createExcelHeader(sheet, headers);

            // 填充数据
            fillExcelData(sheet, dataList, fieldNames);

            // 设置响应头
            setExcelResponse(response, fileName);

            // 写入响应
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();

            log.info("Excel导出成功，共{}条数据", dataList.size());

        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    /**
     * 导出Map列表为Excel文件
     */
    public static void exportMapListToExcel(List<Map<String, Object>> dataList, String[] headers,
                                            String[] fieldNames, String fileName, HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("数据导出");

            // 创建表头
            createExcelHeader(sheet, headers);

            // 填充Map数据
            fillMapData(sheet, dataList, fieldNames);

            // 设置响应头
            setExcelResponse(response, fileName);

            // 写入响应
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();

            log.info("Excel导出成功，共{}条数据", dataList.size());

        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    /**
     * 创建Excel表头
     */
    private static void createExcelHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            sheet.setColumnWidth(i, 15 * 256);
        }
    }

    /**
     * 填充对象数据到Excel
     */
    private static <T> void fillExcelData(Sheet sheet, List<T> dataList, String[] fieldNames) {
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T data = dataList.get(i);

            for (int j = 0; j < fieldNames.length; j++) {
                Cell cell = row.createCell(j);
                Object value = getFieldValue(data, fieldNames[j]);
                cell.setCellValue(value != null ? value.toString() : "");
            }
        }
    }

    /**
     * 填充Map数据到Excel
     */
    private static void fillMapData(Sheet sheet, List<Map<String, Object>> dataList, String[] fieldNames) {
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> data = dataList.get(i);

            for (int j = 0; j < fieldNames.length; j++) {
                Cell cell = row.createCell(j);
                Object value = data.get(fieldNames[j]);
                cell.setCellValue(value != null ? value.toString() : "");
            }
        }
    }

    /**
     * 设置Excel响应头
     */
    private static void setExcelResponse(HttpServletResponse response, String fileName) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
    }

    /**
     * 通过反射获取字段值
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            // 支持Record类型
            if (obj.getClass().isRecord()) {
                return obj.getClass().getMethod(fieldName).invoke(obj);
            }

            // 支持普通类
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            log.warn("获取字段值失败: {}.{}", obj.getClass().getSimpleName(), fieldName);
            return null;
        }
    }
}