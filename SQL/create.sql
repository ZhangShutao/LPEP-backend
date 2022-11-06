drop database if exists lpep;
create database lpep;
use lpep;

drop table if exists t_exper;
drop table if exists t_group;
drop table if exists t_phase;
drop table if exists t_prog_question;
drop table if exists t_prog_submit;
drop table if exists t_question;
drop table if exists t_runner;
drop table if exists t_submit;
drop table if exists t_training_material;
drop table if exists t_user;
drop table if exists t_user_group;

CREATE TABLE `t_exper` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，记录所有的实验',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实验的具体名称',
  `creator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实验创建者的用户id，表t_user的id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '实验创建的时间',
  `start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '实验开始的时间',
  `conf_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '该次实验的配置文件的路径',
  `state` tinyint NOT NULL COMMENT '表示实验的状态：0还未进行；1已经结束；2正在进行；所有实验刚添加进来都是状态0，然后只有管理员手动测试和关闭才会触发更改状态，不会根据时间自己修改状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `title` (`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `exper_id_trigger` BEFORE INSERT ON `t_exper` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


CREATE TABLE `t_group` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，实验分组',
  `exper_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分组所属实验的ID，表t_exper的id',
  `title` varchar(255) NOT NULL COMMENT '该分组的组名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '该分组的创建时间',
  `training_material_id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL COMMENT '表t_training_material的主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `group_id_trigger` BEFORE INSERT ON `t_group` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


CREATE TABLE `t_phase` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，实验阶段',
  `exper_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_exper的id',
  `phase_number` tinyint DEFAULT NULL COMMENT '该阶段在所属实验中的顺序',
  `type` tinyint DEFAULT NULL COMMENT '0问卷 1编程',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `phase_id_trigger` BEFORE INSERT ON `t_phase` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


CREATE TABLE `t_prog_question` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，编程题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '编程题题目内容',
  `test_input_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '测试的输入的路径',
  `test_output_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '测试的输出的路径',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编程题创建的时间',
  `group_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_group的id',
  `exper_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_exper的id',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '实验备注信息',
  `runner_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_runner的id',
  `time_limit` tinyint NOT NULL COMMENT '该编程题的限制用时，单位为分钟',
  `runtime_limit` tinyint NOT NULL COMMENT '该编程题对runner运行的时间限制，单位为秒',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `prog_question_id_trigger` BEFORE INSERT ON `t_prog_question` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


CREATE TABLE `t_prog_submit` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，编程题提交记录',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_user的id',
  `question_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_question的id',
  `runner_output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '如实记录求解器的对每组测试数据的输出，包括报错信息，数据格式为json',
  `status` tinyint NOT NULL COMMENT '0.not tested;  1.testing;  2.accepted;  3.wrong answer;  4.sytanx error;  5.exceed the time limit; ',
  `source_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '测试人员提交的源码',
  `submit_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '测试人员提交代码的时间',
  `used_time` int NOT NULL COMMENT '测试人员从开始读题到本次提交所耗费的时间，单位为分钟',
  `runner_time` double NOT NULL COMMENT 'runner运行的时间，单位为秒',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `prog_submit_id_trigger` BEFORE INSERT ON `t_prog_submit` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


CREATE TABLE `t_question` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，客观类型题的id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题内容',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '选项（json列表）',
  `answer` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '问题答案',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '问题创建时间',
  `group_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题所属分组，表t_group的id',
  `exper_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题所属实验，表t_exper的id',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `type` tinyint NOT NULL COMMENT '问题类型（1选择 2问答）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `question_id_trigger` BEFORE INSERT ON `t_question` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


CREATE TABLE `t_runner` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，runner',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'runner的名字',
  `exe_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '可执行文件的路径',
  `command` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '默认执行命令',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `runner_id_trigger` BEFORE INSERT ON `t_runner` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;

CREATE TABLE `t_submit` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，客观类型题目的记录',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_user主键',
  `question_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_question主键',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户提交的答案，该部分答案只提交一次',
  `submit_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户提交答案的时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `submit_id_trigger` BEFORE INSERT ON `t_submit` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


CREATE TABLE `t_training_material` (
  `id` varchar(32) NOT NULL COMMENT '表主键，记录培训材料的绝对路径',
  `group_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '培训材料所属组别，表t_group主键',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '培训材料的名字',
  `absolute_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '培训材料的绝对路径',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '培训材料创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE DEFINER=`root`@`localhost` TRIGGER `train_mater_trigger` BEFORE INSERT ON `t_training_material` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');


CREATE TABLE `t_user` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表主键，用户表，保存所有用户和管理员',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户账号',
  `password` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户密码',
  `realname` varchar(30) NOT NULL COMMENT '记录用户真实姓名',
  `is_admin` tinyint(1) NOT NULL COMMENT '判断用户是否为管理员',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录用户创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `user_id_trigger` BEFORE INSERT ON `t_user` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


CREATE TABLE `t_user_group` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户分组id',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表t_user的主键id',
  `group_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表t_group的主键id',
  `exper_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表t_exper的主键id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE DEFINER=`root`@`localhost` TRIGGER `user_group_id_trigger` BEFORE INSERT ON `t_user_group` FOR EACH ROW SET new.id=REPLACE(UUID(),'-','');;


insert  into  t_user (username,  password,  realname, is_admin)   values('admin',  'admin', 'admin',  '1');


