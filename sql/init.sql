-- 学生管理系统数据库初始化脚本
-- 数据库创建
CREATE DATABASE IF NOT EXISTS student_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_manager;

-- ----------------------------
-- 1. 角色表 sys_role
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色标识',
    description VARCHAR(200) DEFAULT NULL COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 初始化角色数据
INSERT INTO sys_role (role_name, role_key, description) VALUES
('超级管理员', 'admin', '拥有系统全部权限'),
('教师', 'teacher', '负责学生管理、成绩录入等'),
('学生', 'student', '查看个人信息和成绩');

-- ----------------------------
-- 2. 用户表 sys_user
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    avatar VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始化用户数据 (密码是 123456 的BCrypt加密)
INSERT INTO sys_user (username, password, real_name, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, 1),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张老师', 2, 1),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李老师', 2, 1),
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王小明', 3, 1),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李小红', 3, 1);

-- ----------------------------
-- 3. 班级表 class_info
-- ----------------------------
DROP TABLE IF EXISTS class_info;
CREATE TABLE class_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    teacher_id BIGINT DEFAULT NULL COMMENT '班主任ID',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 初始化班级数据
INSERT INTO class_info (class_name, grade, teacher_id, student_count) VALUES
('高一(1)班', '高一', 2, 0),
('高一(2)班', '高一', 2, 0),
('高二(1)班', '高二', 3, 0),
('高二(2)班', '高二', 3, 0),
('高三(1)班', '高三', 2, 0);

-- ----------------------------
-- 4. 科目表 subject
-- ----------------------------
DROP TABLE IF EXISTS subject;
CREATE TABLE subject (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '科目ID',
    subject_name VARCHAR(50) NOT NULL COMMENT '科目名称',
    teacher_id BIGINT DEFAULT NULL COMMENT '任课老师ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科目表';

-- 初始化科目数据
INSERT INTO subject (subject_name, teacher_id) VALUES
('语文', 2),
('数学', 2),
('英语', 3),
('物理', 3),
('化学', 2),
('生物', 3),
('历史', 2),
('地理', 3),
('政治', 2);

-- ----------------------------
-- 5. 学生信息表 student_info
-- ----------------------------
DROP TABLE IF EXISTS student_info;
CREATE TABLE student_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT DEFAULT NULL COMMENT '性别 0女 1男',
    age INT DEFAULT NULL COMMENT '年龄',
    class_id BIGINT DEFAULT NULL COMMENT '班级ID',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    address VARCHAR(200) DEFAULT NULL COMMENT '住址',
    enroll_date DATE DEFAULT NULL COMMENT '入学时间',
    id_card VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    father_name VARCHAR(50) DEFAULT NULL COMMENT '父亲姓名',
    father_phone VARCHAR(20) DEFAULT NULL COMMENT '父亲联系方式',
    father_id_card VARCHAR(20) DEFAULT NULL COMMENT '父亲身份证号',
    mother_name VARCHAR(50) DEFAULT NULL COMMENT '母亲姓名',
    mother_phone VARCHAR(20) DEFAULT NULL COMMENT '母亲联系方式',
    mother_id_card VARCHAR(20) DEFAULT NULL COMMENT '母亲身份证号',
    remark TEXT DEFAULT NULL COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 初始化学生数据
INSERT INTO student_info (student_no, name, gender, age, class_id, phone, address, enroll_date, id_card) VALUES
('2024001', '王小明', 1, 16, 1, '13800138001', '北京市海淀区', '2024-09-01', '110101200801011234'),
('2024002', '李小红', 0, 16, 1, '13800138002', '北京市朝阳区', '2024-09-01', '110101200801022345'),
('2024003', '张伟', 1, 17, 2, '13800138003', '北京市东城区', '2024-09-01', '110101200701013456'),
('2024004', '刘芳', 0, 17, 2, '13800138004', '北京市西城区', '2024-09-01', '110101200701024567'),
('2023011', '陈刚', 1, 18, 3, '13800138005', '北京市丰台区', '2023-09-01', '110101200601015678'),
('2023012', '周婷', 0, 18, 3, '13800138006', '北京市石景山区', '2023-09-01', '110101200601026789');

-- ----------------------------
-- 6. 成绩表 score_info
-- ----------------------------
DROP TABLE IF EXISTS score_info;
CREATE TABLE score_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    subject_id BIGINT NOT NULL COMMENT '科目ID',
    score DECIMAL(5,2) NOT NULL COMMENT '分数',
    exam_date DATE NOT NULL COMMENT '考试时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0作废 1有效',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES student_info(id),
    FOREIGN KEY (subject_id) REFERENCES subject(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 初始化成绩数据
INSERT INTO score_info (student_id, subject_id, score, exam_date, status) VALUES
(1, 1, 85.5, '2024-12-20', 1),
(1, 2, 92.0, '2024-12-20', 1),
(1, 3, 88.5, '2024-12-21', 1),
(2, 1, 90.0, '2024-12-20', 1),
(2, 2, 87.5, '2024-12-20', 1),
(2, 3, 95.0, '2024-12-21', 1),
(3, 1, 78.0, '2024-12-20', 1),
(3, 2, 82.5, '2024-12-20', 1),
(3, 4, 88.0, '2024-12-21', 1),
(4, 1, 92.5, '2024-12-20', 1),
(4, 2, 95.0, '2024-12-20', 1),
(4, 5, 90.0, '2024-12-21', 1);

-- ----------------------------
-- 7. 公告表 notice
-- ----------------------------
DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    publisher VARCHAR(50) DEFAULT NULL COMMENT '发布人',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0下架 1发布',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 初始化公告数据
INSERT INTO notice (title, content, publisher, publish_time, status) VALUES
('欢迎使用学生管理系统', '欢迎各位老师和学生使用本系统，祝大家工作学习顺利！', '系统管理员', '2024-01-01 08:00:00', 1),
('期末考试安排通知', '本校将于2025年1月15日开始期末考试，请各位同学做好复习准备。', '教务处', '2024-12-01 10:00:00', 1),
('元旦放假通知', '根据学校安排，元旦放假时间为2025年1月1日至1月3日。', '学校办公室', '2024-12-20 09:00:00', 1);

-- ----------------------------
-- 8. 操作日志表 sys_log
-- ----------------------------
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '操作人',
    operation VARCHAR(100) DEFAULT NULL COMMENT '操作类型',
    method VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    params TEXT DEFAULT NULL COMMENT '请求参数',
    ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- 9. 更新班级学生人数
-- ----------------------------
UPDATE class_info SET student_count = (SELECT COUNT(*) FROM student_info WHERE class_id = class_info.id);
