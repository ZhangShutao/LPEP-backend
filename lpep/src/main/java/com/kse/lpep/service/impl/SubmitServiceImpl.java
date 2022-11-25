package com.kse.lpep.service.impl;

import com.kse.lpep.common.exception.NoSuchRecordException;
import com.kse.lpep.common.exception.NotAuthorizedException;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.*;
import com.kse.lpep.service.ISubmitService;
import com.kse.lpep.service.dto.JudgeTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张舒韬
 * @since 2022/11/24
 */
@Service
@Slf4j
@Transactional
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmitServiceImpl implements ISubmitService {

    @Autowired
    IProgQuestionMapper progQuestionMapper;

    @Autowired
    IPhaseMapper phaseMapper;

    @Autowired
    IUserMapper userMapper;

    @Autowired
    IUserGroupMapper userGroupMapper;

    @Autowired
    IExperMapper experMapper;

    @Autowired
    IRunnerMapper runnerMapper;

    @Autowired
    IGroupMapper groupMapper;

    @Autowired
    ICaseMapper caseMapper;

    @Autowired
    IProgSubmitMapper progSubmitMapper;

    @Autowired
    ISubmitMapper submitMapper;

    /**
     * 检查用户是否有提交或修改某个编程问题的权限
     * @param user 用户对象
     * @param progQuestion 编程问题对象
     * @return 有权限则返回true
     * @throws NoSuchRecordException 问题所属的群组或实验不存在
     */
    private Boolean isUserAuthorizedProgQuestion(User user, ProgQuestion progQuestion) throws NoSuchRecordException {
        UserGroup userGroup = userGroupMapper.getByUserIdAndGroupId(user.getId(), progQuestion.getGroupId());
        return userGroup != null;
    }

    /**
     * 将一个字符串保存到临时文件
     * @param str 待保存的字符串
     * @return 所得的临时文件
     * @throws IOException 创建临时文件时出错
     */
    private File saveProgramAsTempFile(String str) throws IOException {

        File programFile = File.createTempFile("aspTemp", ".lp");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(programFile.toPath())));
        writer.write(str);
        writer.flush();
        writer.close();
        return programFile;
    }

    /**
     * 生成与提交程序对应的测试任务，每组测试数据对应一个任务
     * @param submit 程序提交记录
     * @return 生成的测试任务
     * @throws IOException 创建测试任务时发生文件读写错误
     */
    private List<JudgeTask> generateJudgeTasks(ProgSubmit submit) throws IOException {
        List<Case> cases = caseMapper.selectByQuestionIdOrderedByNumber(submit.getQuestionId());

        ProgQuestion question = progQuestionMapper.selectById(submit.getQuestionId());
        Runner runner = runnerMapper.selectById(question.getRunnerId());
        Phase phase = phaseMapper.selectById(question.getPhaseId());
        Exper exper = experMapper.selectById(phase.getExperId());
        String inputDir = exper.getWorkspace() + File.pathSeparator + "test" + File.pathSeparator + "input";
        String standardOutputDir = exper.getWorkspace() + File.pathSeparator + "test" + File.pathSeparator + "std_out";

        File userSourceFile = saveProgramAsTempFile(submit.getSourceCode());

        List<JudgeTask> taskList = new ArrayList<>();
        for (Case testCase : cases) {
            JudgeTask task = new JudgeTask(submit.getId(), testCase.getNumber(),
                    userSourceFile.getAbsolutePath(),
                    testCase.generateInputPath(inputDir),
                    testCase.generateOutputPath(standardOutputDir),
                    "",
                    runner.getCommand(),
                    question.getTimeLimit());
            taskList.add(task);
            log.debug("Judge task for case {} of submit {} is added to queue", testCase.getNumber(), submit.getId());
        }
        return taskList;
    }

    @Override
    public List<JudgeTask> submitProgram(String userId, String problemId, String code)
            throws NoSuchRecordException, NotAuthorizedException, IOException {

        User user = userMapper.selectById(userId);
        ProgQuestion progQuestion = progQuestionMapper.selectById(problemId);
        if (user == null) {
            throw new NoSuchRecordException(String.format("用户id \"%s\" 不存在", userId));
        }
        if (progQuestion == null) {
            throw new NoSuchRecordException(String.format("编程题 \"%s\" 不存在", problemId));
        }


        ProgSubmit submit = new ProgSubmit(user.getId(), progQuestion.getId(), ProgSubmit.NOT_TESTED, code);
        progSubmitMapper.insert(submit);

        return generateJudgeTasks(submit);
    }

    @Override
    public Boolean updateProgramSubmitState(String submitId, JudgeTask.Status status) throws NoSuchRecordException {
        return progSubmitMapper.updateStatusById(submitId, JudgeTask.STATUS_MAP.get(status));
    }

    @Override
    public Boolean abortProgram(String userId, String problemId) throws NoSuchRecordException, NotAuthorizedException {
        ProgSubmit submit = new ProgSubmit(userId, problemId, ProgSubmit.ABORTED, "");
        int ret = progSubmitMapper.insert(submit);
        return ret == 1;
    }

    @Override
    public Boolean submitQuestionnaire(String userId, String phaseId, String answer) throws NoSuchRecordException, NotAuthorizedException {
        return null;
    }
}
