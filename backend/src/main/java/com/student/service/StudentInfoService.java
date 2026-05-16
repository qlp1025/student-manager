package com.student.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.dto.StudentQueryDTO;
import com.student.entity.ClassInfo;
import com.student.entity.StudentInfo;
import com.student.mapper.ClassInfoMapper;
import com.student.mapper.StudentInfoMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoMapper studentMapper;
    private final ClassInfoMapper classMapper;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StudentInfo findById(Long id) {
        return studentMapper.findById(id);
    }

    public List<StudentInfo> findAll() {
        return studentMapper.findAll();
    }

    public List<StudentInfo> findByCondition(StudentQueryDTO queryDTO) {
        return studentMapper.findByCondition(queryDTO.getKeyword(), queryDTO.getClassId(), queryDTO.getStatus());
    }

    public PageInfo<StudentInfo> findPage(StudentQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<StudentInfo> list = studentMapper.findByCondition(
                queryDTO.getKeyword(), queryDTO.getClassId(), queryDTO.getStatus());
        return new PageInfo<>(list);
    }

    public List<StudentInfo> findByClassId(Long classId) {
        return studentMapper.findByClassId(classId);
    }

    @Transactional
    public void insert(StudentInfo student) {
        studentMapper.insert(student);
        updateClassStudentCount(student.getClassId());
    }

    @Transactional
    public void update(StudentInfo student) {
        StudentInfo old = studentMapper.findById(student.getId());
        studentMapper.update(student);
        if (old.getClassId() != null && !old.getClassId().equals(student.getClassId())) {
            updateClassStudentCount(old.getClassId());
        }
        if (student.getClassId() != null) {
            updateClassStudentCount(student.getClassId());
        }
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        StudentInfo student = studentMapper.findById(id);
        studentMapper.updateStatus(id, status);
        if (student.getClassId() != null) {
            updateClassStudentCount(student.getClassId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        StudentInfo student = studentMapper.findById(id);
        studentMapper.deleteById(id);
        if (student.getClassId() != null) {
            updateClassStudentCount(student.getClassId());
        }
    }

    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            StudentInfo student = studentMapper.findById(id);
            if (student != null && student.getClassId() != null) {
                studentMapper.deleteById(id);
                updateClassStudentCount(student.getClassId());
            }
        }
        studentMapper.batchDelete(ids);
    }

    public int count() {
        return studentMapper.count();
    }

    @Transactional
    public int importStudents(MultipartFile file) throws IOException {
        List<ClassInfo> classes = classMapper.findAll();
        Map<String, Long> classNameToId = new HashMap<>();
        for (ClassInfo c : classes) {
            classNameToId.put(c.getClassName(), c.getId());
        }

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int count = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String studentNo = getCellValue(row.getCell(0));
            String name = getCellValue(row.getCell(1));
            String genderStr = getCellValue(row.getCell(2));
            String ageStr = getCellValue(row.getCell(3));
            String className = getCellValue(row.getCell(4));
            String phone = getCellValue(row.getCell(5));
            String address = getCellValue(row.getCell(6));
            String enrollDateStr = getCellValue(row.getCell(7));
            String idCard = getCellValue(row.getCell(8));
            String fatherName = getCellValue(row.getCell(9));
            String fatherPhone = getCellValue(row.getCell(10));
            String motherName = getCellValue(row.getCell(11));
            String motherPhone = getCellValue(row.getCell(12));

            if (studentNo == null || name == null || className == null) {
                continue;
            }

            StudentInfo student = new StudentInfo();
            student.setStudentNo(studentNo);
            student.setName(name);
            student.setGender("男".equals(genderStr) ? 1 : 0);
            if (ageStr != null && !ageStr.isEmpty()) {
                student.setAge(Integer.parseInt(ageStr));
            }
            Long classId = classNameToId.get(className);
            student.setClassId(classId);
            student.setPhone(phone);
            student.setAddress(address);
            if (enrollDateStr != null && !enrollDateStr.isEmpty()) {
                student.setEnrollDate(LocalDate.parse(enrollDateStr, DATE_FORMATTER));
            }
            student.setIdCard(idCard);
            student.setFatherName(fatherName);
            student.setFatherPhone(fatherPhone);
            student.setMotherName(motherName);
            student.setMotherPhone(motherPhone);
            student.setStatus(1);

            studentMapper.insert(student);
            count++;
            if (classId != null) {
                updateClassStudentCount(classId);
            }
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

    private void updateClassStudentCount(Long classId) {
        if (classId != null) {
            int count = studentMapper.countByClassId(classId);
            classMapper.updateStudentCount(classId, count);
        }
    }
}
