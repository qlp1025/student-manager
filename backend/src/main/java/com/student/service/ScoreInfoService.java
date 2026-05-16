package com.student.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.dto.ScoreQueryDTO;
import com.student.entity.ScoreInfo;
import com.student.entity.StudentInfo;
import com.student.entity.Subject;
import com.student.mapper.ScoreInfoMapper;
import com.student.mapper.StudentInfoMapper;
import com.student.mapper.SubjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScoreInfoService {

    private final ScoreInfoMapper scoreMapper;
    private final StudentInfoMapper studentMapper;
    private final SubjectMapper subjectMapper;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ScoreInfo findById(Long id) {
        return scoreMapper.findById(id);
    }

    public List<ScoreInfo> findAll() {
        return scoreMapper.findAll();
    }

    public List<ScoreInfo> findByStudentId(Long studentId) {
        return scoreMapper.findByStudentId(studentId);
    }

    public List<ScoreInfo> findByCondition(ScoreQueryDTO queryDTO) {
        return scoreMapper.findByCondition(
                queryDTO.getStudentId(),
                queryDTO.getClassId(),
                queryDTO.getSubjectId(),
                queryDTO.getMinScore(),
                queryDTO.getMaxScore(),
                queryDTO.getSubjectIds()
        );
    }

    public PageInfo<ScoreInfo> findPage(ScoreQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<ScoreInfo> list = scoreMapper.findByCondition(
                queryDTO.getStudentId(),
                queryDTO.getClassId(),
                queryDTO.getSubjectId(),
                queryDTO.getMinScore(),
                queryDTO.getMaxScore(),
                queryDTO.getSubjectIds()
        );
        return new PageInfo<>(list);
    }

    @Transactional
    public void insert(ScoreInfo score) {
        scoreMapper.insert(score);
    }

    @Transactional
    public void update(ScoreInfo score) {
        scoreMapper.update(score);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        scoreMapper.updateStatus(id, status);
    }

    @Transactional
    public void deleteById(Long id) {
        scoreMapper.deleteById(id);
    }

    @Transactional
    public void batchInsert(List<ScoreInfo> scores) {
        scoreMapper.batchInsert(scores);
    }

    public BigDecimal getTotalScore(Long studentId) {
        return scoreMapper.getTotalScoreByStudentId(studentId);
    }

    public List<ScoreInfo> findRanking(Long classId, List<Long> subjectIds) {
        return scoreMapper.findStudentRanking(classId, subjectIds);
    }

    @Transactional
    public int importScores(MultipartFile file) throws IOException {
        List<StudentInfo> students = studentMapper.findAll();
        Map<String, Long> studentNoToId = new HashMap<>();
        for (StudentInfo s : students) {
            studentNoToId.put(s.getStudentNo(), s.getId());
        }

        List<Subject> subjects = subjectMapper.findAll();
        Map<String, Long> subjectNameToId = new HashMap<>();
        for (Subject sub : subjects) {
            subjectNameToId.put(sub.getSubjectName(), sub.getId());
        }

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int count = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String studentNo = getCellValue(row.getCell(0));
            String subjectName = getCellValue(row.getCell(1));
            String scoreStr = getCellValue(row.getCell(2));
            String examDateStr = getCellValue(row.getCell(3));
            String statusStr = getCellValue(row.getCell(4));

            if (studentNo == null || subjectName == null || scoreStr == null) {
                continue;
            }

            Long studentId = studentNoToId.get(studentNo);
            Long subjectId = subjectNameToId.get(subjectName);

            if (studentId == null || subjectId == null) {
                continue;
            }

            ScoreInfo score = new ScoreInfo();
            score.setStudentId(studentId);
            score.setSubjectId(subjectId);
            score.setScore(new BigDecimal(scoreStr));
            if (examDateStr != null && !examDateStr.isEmpty()) {
                score.setExamDate(LocalDate.parse(examDateStr, DATE_FORMATTER));
            }
            score.setStatus(statusStr != null && statusStr.equals("0") ? 0 : 1);

            scoreMapper.insert(score);
            count++;
        }

        workbook.close();
        return count;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER);
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return null;
        }
    }
}
