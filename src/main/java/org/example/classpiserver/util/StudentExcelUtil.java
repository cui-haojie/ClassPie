package org.example.classpiserver.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class StudentExcelUtil {

    private static final String DEFAULT_PASSWORD = "ClassPi123";
    private static final DataFormatter FORMATTER = new DataFormatter();

    private StudentExcelUtil() {
    }

    public static class StudentRow {
        private int rowNum;
        private String account;
        private String name;
        private String statusNumber;
        private String password;

        public int getRowNum() {
            return rowNum;
        }

        public void setRowNum(int rowNum) {
            this.rowNum = rowNum;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatusNumber() {
            return statusNumber;
        }

        public void setStatusNumber(String statusNumber) {
            this.statusNumber = statusNumber;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String resolvedPassword() {
            if (password != null && !password.isBlank()) {
                return password.trim();
            }
            return DEFAULT_PASSWORD;
        }
    }

    public static List<StudentRow> parseImportFile(MultipartFile file) throws IOException {
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
            if (!columns.containsKey("account")) {
                throw new IllegalArgumentException("缺少「账号」列，请使用系统模板");
            }

            List<StudentRow> rows = new ArrayList<>();
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowBlank(row)) {
                    continue;
                }
                StudentRow studentRow = new StudentRow();
                studentRow.setRowNum(i + 1);
                studentRow.setAccount(readCell(row, columns.get("account")));
                if (columns.containsKey("name")) {
                    studentRow.setName(readCell(row, columns.get("name")));
                }
                if (columns.containsKey("statusNumber")) {
                    studentRow.setStatusNumber(readCell(row, columns.get("statusNumber")));
                }
                if (columns.containsKey("password")) {
                    studentRow.setPassword(readCell(row, columns.get("password")));
                }
                if (studentRow.getAccount() == null || studentRow.getAccount().isBlank()) {
                    continue;
                }
                rows.add(studentRow);
            }
            if (rows.isEmpty()) {
                throw new IllegalArgumentException("Excel 中没有有效的学生数据");
            }
            return rows;
        }
    }

    public static byte[] buildTemplateBytes() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("学生名单");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("账号");
            header.createCell(1).setCellValue("姓名");
            header.createCell(2).setCellValue("学号");
            header.createCell(3).setCellValue("密码");

            Row sample = sheet.createRow(1);
            sample.createCell(0).setCellValue("stu2024001");
            sample.createCell(1).setCellValue("张三");
            sample.createCell(2).setCellValue("2024001001");
            sample.createCell(3).setCellValue("ClassPi123");

            for (int i = 0; i < 4; i++) {
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
            if (text.contains("账号") || text.equalsIgnoreCase("account")) {
                columns.put("account", cell.getColumnIndex());
            } else if (text.contains("姓名") || text.equalsIgnoreCase("name")) {
                columns.put("name", cell.getColumnIndex());
            } else if (text.contains("学号") || text.equalsIgnoreCase("status_number") || text.equalsIgnoreCase("studentid")) {
                columns.put("statusNumber", cell.getColumnIndex());
            } else if (text.contains("密码") || text.equalsIgnoreCase("password")) {
                columns.put("password", cell.getColumnIndex());
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
