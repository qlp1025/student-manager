package com.student.util;

import com.student.entity.StudentInfo;
import com.student.entity.ScoreInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static byte[] exportStudents(List<StudentInfo> students) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("学生信息");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "性别", "年龄", "班级", "手机号", "地址", "入学日期", "身份证号",
                    "父亲姓名", "父亲电话", "母亲姓名", "母亲电话", "状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int rowNum = 1;
            for (StudentInfo student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getStudentNo());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getGender() != null && student.getGender() == 1 ? "男" : "女");
                row.createCell(3).setCellValue(student.getAge() != null ? student.getAge() : 0);
                row.createCell(4).setCellValue(student.getClassName() != null ? student.getClassName() : "");
                row.createCell(5).setCellValue(student.getPhone() != null ? student.getPhone() : "");
                row.createCell(6).setCellValue(student.getAddress() != null ? student.getAddress() : "");
                row.createCell(7).setCellValue(student.getEnrollDate() != null ? student.getEnrollDate().format(DATE_FORMATTER) : "");
                row.createCell(8).setCellValue(student.getIdCard() != null ? student.getIdCard() : "");
                row.createCell(9).setCellValue(student.getFatherName() != null ? student.getFatherName() : "");
                row.createCell(10).setCellValue(student.getFatherPhone() != null ? student.getFatherPhone() : "");
                row.createCell(11).setCellValue(student.getMotherName() != null ? student.getMotherName() : "");
                row.createCell(12).setCellValue(student.getMotherPhone() != null ? student.getMotherPhone() : "");
                row.createCell(13).setCellValue(student.getStatus() != null && student.getStatus() == 1 ? "启用" : "禁用");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 4000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static byte[] exportScores(List<ScoreInfo> scores) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("成绩信息");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "科目", "分数", "考试日期", "状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int rowNum = 1;
            for (ScoreInfo score : scores) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(score.getStudentNo() != null ? score.getStudentNo() : "");
                row.createCell(1).setCellValue(score.getStudentName() != null ? score.getStudentName() : "");
                row.createCell(2).setCellValue(score.getClassName() != null ? score.getClassName() : "");
                row.createCell(3).setCellValue(score.getSubjectName() != null ? score.getSubjectName() : "");
                row.createCell(4).setCellValue(score.getScore() != null ? score.getScore().doubleValue() : 0.0);
                row.createCell(5).setCellValue(score.getExamDate() != null ? score.getExamDate().format(DATE_FORMATTER) : "");
                row.createCell(6).setCellValue(score.getStatus() != null && score.getStatus() == 1 ? "有效" : "作废");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 4000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static byte[] createStudentTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("学生信息导入模板");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号*", "姓名*", "性别(男/女)*", "年龄", "班级名称*", "手机号", "地址", "入学日期(yyyy-MM-dd)", "身份证号",
                    "父亲姓名", "父亲电话", "母亲姓名", "母亲电话"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            Row exampleRow = sheet.createRow(1);
            String[] examples = {"2024001", "张三", "男", "16", "高一(1)班", "13800138000", "北京市朝阳区", "2024-09-01", "110101200801011234",
                    "张父", "13900139000", "张母", "13900139001"};
            for (int i = 0; i < examples.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellValue(examples[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFont(font);
                cell.setCellStyle(style);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 4500);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static byte[] createScoreTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("成绩导入模板");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号*", "科目名称*", "分数*", "考试日期(yyyy-MM-dd)*", "状态(1有效/0作废)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            Row exampleRow = sheet.createRow(1);
            String[] examples = {"2024001", "语文", "85.5", "2024-12-20", "1"};
            for (int i = 0; i < examples.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellValue(examples[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFont(font);
                cell.setCellStyle(style);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 4500);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
