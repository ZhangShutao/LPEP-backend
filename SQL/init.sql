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
truncate table  t_user_group;


insert  into  t_user (username,  password,  realname, is_admin)   values('admin',  'admin', 'admin',  '1');