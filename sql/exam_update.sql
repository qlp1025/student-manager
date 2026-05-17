-- 试题/考试表
CREATE TABLE IF NOT EXISTS exam (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试ID',
    exam_name VARCHAR(100) NOT NULL COMMENT '考试名称',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- 初始化考试数据
INSERT INTO exam (exam_name, remark) VALUES
('期中考试', '2024学年第一学期期中考试'),
('期末考试', '2024学年第一学期期末考试'),
('月考', '每月一次的综合测试');

-- 给成绩表添加 exam_id 字段
ALTER TABLE score_info ADD COLUMN exam_id BIGINT COMMENT '考试ID';
ALTER TABLE score_info ADD FOREIGN KEY (exam_id) REFERENCES exam(id);

-- 更新现有成绩数据，设置默认考试
UPDATE score_info SET exam_id = 1 WHERE exam_id IS NULL;
