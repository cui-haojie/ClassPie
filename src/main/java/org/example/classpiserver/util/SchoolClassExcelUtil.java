package org.example.classpiserver.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class SchoolClassExcelUtil {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private SchoolClassExcelUtil() {
    }

    public static class ClassRow {
        private int rowNum;
        private String name;
        private String mechanism;

        public int getRowNum() {
            return rowNum;
        }

        public void setRowNum(int rowNum) {
            this.rowNum = rowNum;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMechanism() {
            return mechanism;
        }

        public void setMechanism(String mechanism) {
            this.mechanism = mechanism;
        }
    }

    public static List<ClassRow> parseImportFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请上传 Excel 文件");
        }
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
            throw new IllegalArgumentException("仅支持 .xlsx 或 .xls 文件");
        }

        try (InputStream in = file.getInputStream(); Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new IllegalArgumentException("Excel 中没有工作表");
            }
            Row header = sheet.getRow(sheet.getFirstRowNum());
            if (header == null) {
                throw new IllegalArgumentException("Excel 表头为空");
            }
            Map<String, Integer> columns = mapHeaderColumns(header);
            if (!columns.containsKey("name")) {
                throw new IllegalArgumentException("缺少「班级名称」列，请使用系统模板");
            }

            List<ClassRow> rows = new ArrayList<>();
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowBlank(row)) {
                    continue;
                }
                ClassRow classRow = new ClassRow();
                classRow.setRowNum(i + 1);
                classRow.setName(readCell(row, columns.get("name")));
                if (columns.containsKey("mechanism")) {
                    classRow.setMechanism(readCell(row, columns.get("mechanism")));
                }
                if (classRow.getName() == null || classRow.getName().isBlank()) {
                    continue;
                }
                rows.add(classRow);
            }
            if (rows.isEmpty()) {
                throw new IllegalArgumentException("Excel 中没有有效的班级数据");
            }
            return rows;
        }
    }

    public static byte[] buildTemplateBytes() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("班级名单");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("班级名称");
            header.createCell(1).setCellValue("所属学校");

            Row sample1 = sheet.createRow(1);
            sample1.createCell(0).setCellValue("软件 2201");
            sample1.createCell(1).setCellValue("重庆理工大学");

            Row sample2 = sheet.createRow(2);
            sample2.createCell(0).setCellValue("计科 2202");
            sample2.createCell(1).setCellValue("重庆理工大学");

            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static Map<String, Integer> mapHeaderColumns(Row header) {
        Map<String, Integer> columns = new HashMap<>();
        for (Cell cell : header) {
            String text = readCell(header, cell.getColumnIndex()).replace(" ", "");
            if (text.contains("班级名称") || text.contains("班级名") || text.equalsIgnoreCase("name")
                    || text.contains("行政班")) {
                columns.put("name", cell.getColumnIndex());
            } else if (text.contains("所属学校") || text.contains("学校") || text.contains("机构")
                    || text.equalsIgnoreCase("mechanism") || text.contains("单位")) {
                columns.put("mechanism", cell.getColumnIndex());
            }
        }
        return columns;
    }

    private static boolean isRowBlank(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK && !readCell(row, cell.getColumnIndex()).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private static String readCell(Row row, Integer index) {
        if (index == null) {
            return "";
        }
        Cell cell = row.getCell(index);
        if (cell == null) {
            return "";
        }
        return FORMATTER.formatCellValue(cell).trim();
    }
}
