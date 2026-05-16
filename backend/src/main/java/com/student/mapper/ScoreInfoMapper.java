package com.student.mapper;

import com.student.entity.ScoreInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.math.BigDecimal;

@Mapper
public interface ScoreInfoMapper {
    ScoreInfo findById(@Param("id") Long id);
    List<ScoreInfo> findAll();
    List<ScoreInfo> findByStudentId(@Param("studentId") Long studentId);
    List<ScoreInfo> findByCondition(@Param("studentId") Long studentId, @Param("classId") Long classId,
                                     @Param("subjectId") Long subjectId, @Param("minScore") Double minScore,
                                     @Param("maxScore") Double maxScore);
    int insert(ScoreInfo score);
    int update(ScoreInfo score);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int deleteById(@Param("id") Long id);
    int batchInsert(@Param("list") List<ScoreInfo> scores);
    BigDecimal getTotalScoreByStudentId(@Param("studentId") Long studentId);
    List<ScoreInfo> findStudentRanking(@Param("classId") Long classId, @Param("subjectIds") List<Long> subjectIds);
}
