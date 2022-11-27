package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kse.lpep.common.exception.NoSuchRecordException;
import com.kse.lpep.common.exception.NotAuthorizedException;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.*;
import com.kse.lpep.service.ISubmitService;
import com.kse.lpep.service.dto.JudgeTask;
import com.kse.lpep.service.dto.ProgramSubmitInfo;
import com.kse.lpep.service.dto.QuestionnaireItemReply;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张舒韬
 * @since 2022/11/24
 */
@Service
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmitServiceImpl implements ISubmitService {

    @Autowired
    IProgQuestionMapper progQuestionMapper;

    @Autowired
    IQuestionMapper questionMapper;

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

    @Autowired
    IUserFootprintMapper userFootprintMapper;

    /**
     * 检查用户是否有提交或修改某个编程问题的权限
     * @param userId 用户对象
     * @param progQuestionId 编程问题对象
     * @return 有权限则返回true
     * @throws NoSuchRecordException 问题所属的群组或实验不存在
     */
    private Boolean isUserAuthorizedProgQuestion(String userId, String progQuestionId) throws NoSuchRecordException {
        User user = userMapper.selectById(userId);
        ProgQuestion progQuestion = progQuestionMapper.selectById(progQuestionId);
        if (user == null) {
            throw new NoSuchRecordException(String.format("用户 %s 不存在", userId));
        } else if (progQuestion == null) {
            throw new NoSuchRecordException(String.format("编程题 %s 不存在", progQuestionId));
        } else {
            UserGroup userGroup = userGroupMapper.getByUserIdAndGroupId(userId, progQuestion.getGroupId());
            Exper exper = experMapper.selectById(progQuestion.getExperId());
            return (userGroup != null) && (exper.getState().equals(Exper.RUNNING));
        }
    }

    /**
     * 检查用户是否有提交当前问卷阶段的权限
     * @param userId 用户id
     * @param phaseId 问卷阶段id
     * @return 用户有提交权限则返回true，否则返回false
     * @throws NoSuchRecordException 用户或阶段不存在，或该阶段不是问卷阶段
     */
    private Boolean isUserAuthorizedQuestionnaire(String userId, String phaseId) throws NoSuchRecordException {
        User user = userMapper.selectById(userId);
        Phase phase = phaseMapper.selectById(phaseId);
        if (user == null) {
            throw new NoSuchRecordException(String.format("用户 %s 不存在", userId));
        } else if (phase == null) {
            throw new NoSuchRecordException(String.format("阶段 %s 不存在", phaseId));
        } else if (!phase.getType().equals(Phase.QUESTIONNAIRE)) {
            throw new NoSuchRecordException(String.format("阶段 %s 不是问卷调查阶段", phaseId));
        } else {
            List<UserGroup> userGroups = userGroupMapper.getByUserIdAndExperId(userId, phase.getExperId());
            Exper exper = experMapper.selectById(phase.getExperId());
            return (!userGroups.isEmpty()) && (exper.getState().equals(Exper.RUNNING));
        }
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
        String inputDir = exper.getWorkspace() + File.separator + "test" + File.separator + "input";
        String standardOutputDir = exper.getWorkspace() + File.separator + "test" + File.separator + "std_out";

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

        if (!isUserAuthorizedProgQuestion(userId, problemId)) {
            throw new NotAuthorizedException(String.format("用户 %s 没有提交问题 %s 的权限", userId, problemId));
        } else {
            ProgSubmit submit = new ProgSubmit(userId, problemId, ProgSubmit.NOT_TESTED, code);
            progSubmitMapper.insert(submit);

            return generateJudgeTasks(submit);
        }
    }

    @Override
    public Boolean updateProgramSubmitState(String submitId, JudgeTask.Status status) throws NoSuchRecordException {
        if (progSubmitMapper.selectById(submitId) == null) {
            throw new NoSuchRecordException("编程题提交记录不存在");
        } else {
            return progSubmitMapper.updateStatusById(submitId, JudgeTask.STATUS_MAP.get(status));
        }
    }

    @Override
    public Boolean abortProgram(String userId, String problemId) throws NoSuchRecordException, NotAuthorizedException {
        if (!isUserAuthorizedProgQuestion(userId, problemId)) {
            throw new NotAuthorizedException(String.format("用户 %s 没有放弃问题 %s 的权限", userId, problemId));
        } else {
            ProgSubmit submit = new ProgSubmit(userId, problemId, ProgSubmit.ABORTED, "");
            int ret = progSubmitMapper.insert(submit);
            return ret == 1;
        }
    }

    @Override
    public Boolean submitQuestionnaire(String userId, String phaseId, String answer) throws NoSuchRecordException, NotAuthorizedException {
        if (!isUserAuthorizedQuestionnaire(userId, phaseId)) {
            throw new NotAuthorizedException(String.format("用户 %s 没有参与问卷阶段 %s 的权限", userId, phaseId));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<QuestionnaireItemReply> replies = objectMapper.readValue(answer, new TypeReference<List<QuestionnaireItemReply>>() {});
            for (QuestionnaireItemReply reply : replies) {
                if (questionMapper.selectById(reply.getQuestionId()) == null) {
                    throw new NoSuchRecordException(String.format("问题 %s 不存在", reply.getQuestionId()));
                } else {
                    Submit submit = new Submit(userId, reply.getQuestionId(), reply.getReply());
                    int ret = submitMapper.insert(submit);
                    if (ret != 1) {
                        return false;
                    }
                }
            }
            return true;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void modifyProgUserFootprint(String userId, String questionId) {
        Question question = questionMapper.selectById(questionId);
        String experId = question.getExperId();
        int currentQuestionNumber = question.getNumber();
        String phaseId = question.getPhaseId();
        int currentPhaseNumber = phaseMapper.selectById(phaseId).getPhaseNumber();
        UpdateWrapper<UserFootprint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).eq("exper_id", experId);
        UserFootprint userFootprint = new UserFootprint();
        userFootprint.setCurrentPhaseNumber(currentPhaseNumber)
                .setCurrentQuestionNumber(currentQuestionNumber + 1);
        userFootprintMapper.update(userFootprint, updateWrapper);
    }

    @Override
    public List<ProgramSubmitInfo> listProgramSubmitInfo(String userId, String questionId, int pageIndex, int pageSize)
            throws NotAuthorizedException, NoSuchRecordException {
        if (!isUserAuthorizedProgQuestion(userId, questionId)) {
            throw new NotAuthorizedException(String.format("用户 %s 没有解答问题 %s 的权限", userId, questionId));
        }

        List<ProgSubmit> submits = progSubmitMapper.pageFindByUserIdAndProgQuestionIdOrderBySubmitTimeDesc(
                userId, questionId, pageIndex, pageSize);

        List<ProgramSubmitInfo> submitInfoList = new ArrayList<>();
        for (ProgSubmit submit : submits) {
            String submitTime = new SimpleDateFormat("yyyy-MM-dd").format(submit.getSubmitTime());
            submitInfoList.add(new ProgramSubmitInfo(submit.getId(),
                    ProgramSubmitInfo.statusMap.get(submit.getStatus()),
                    submitTime));
        }
        return submitInfoList;
    }
}
