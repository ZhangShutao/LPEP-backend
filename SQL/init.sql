truncate table  t_case;
truncate table  t_exper;
truncate table  t_group;
truncate table  t_phase;
truncate table  t_prog_question;
truncate table  t_prog_submit;
truncate table  t_question;
truncate table  t_runner;
truncate table  t_submit;
truncate table  t_training_material;
truncate table  t_user;
truncate table  t_user_footprint;
truncate table  t_user_group;

insert  into  t_user (id, username,  password,  realname, is_admin)   values( replace(UUID(),'-',''),  'admin',   '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',  'admin', '1');
INSERT INTO `t_runner` (`id`, `name`, `exe_path`, `command`) VALUES
('1281de716d4011ed9c63c8f750fceeb7', 'cdlsolver', 'cdlsolver', 'cdlsolver'),
('3f5f66736d4011ed9c63c8f750fceeb7', 'clingo', 'clingo', 'clingo 0');
