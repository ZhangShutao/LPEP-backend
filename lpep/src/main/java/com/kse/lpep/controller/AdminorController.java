package com.kse.lpep.controller;

import com.kse.lpep.common.constant.ConstantCode;
import com.kse.lpep.common.exception.ElementDuplicateException;
import com.kse.lpep.common.exception.InsertException;
import com.kse.lpep.common.exception.SaveFileIOException;
import com.kse.lpep.common.utility.ValidUtil;
import com.kse.lpep.controller.vo.*;
import com.kse.lpep.service.IAdminService;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.ITrainingMaterialService;
import com.kse.lpep.service.IUserService;
import com.kse.lpep.service.dto.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("admin")
public class AdminorController {
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IExperService experService;
    @Autowired
    private ITrainingMaterialService trainingMaterialService;
    @Autowired
    private IUserService userService;


    // 管理员分页查询所有实验

    /**
     * 管理员分页查询所有实验接口
     * 200成功；后端没有失败；300数据校验失败
     * @param pageIndex
     * @param pageSize
     * @return ExperInfoPage
     */
    @GetMapping("/listallexper")
    public BaseResponse listAllExper(int pageIndex, int pageSize){
        BaseResponse response = new BaseResponse();
        if(pageIndex < 0 || pageSize <= 0){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("数据校验错误");
        }
        ExperInfoPage data = experService.getAllExper(pageIndex, pageSize);
        response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("管理员查询所有实验数据").setData(data);
        return response;
    }

    // 情况1：该实验已经结束，数据传输错误
    // 情况2：正常情况

    /**
     * 管理员开始实验接口
     * 成功200；失败210
     * @param experId
     * @return NONE
     */
    @GetMapping("/startexper")
    public BaseResponse startExper(String experId){
        BaseResponse response = new BaseResponse();
        try{
            int currentStatus = experService.queryExperCurrentStatus(experId);
            experService.modifyExperStatus(experId, currentStatus, 2);
            response.setMsg("管理员开始实验成功").setStatus(ConstantCode.QUERY_SUCCESS);
        }catch (NullPointerException e){
            response.setMsg("管理员开始实验失败").setStatus(ConstantCode.QUERY_FAIL);
        }
        return response;
    }

    /**
     * 管理员结束实验
     * 成功200；失败210；数据校验失败300
     * @param experId
     * @return
     */
    @GetMapping("/endexper")
    public BaseResponse endExper(String experId){
        BaseResponse response = new BaseResponse();
        if(StringUtils.isBlank(experId)){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("实验id不能为空");
            return response;
        }
        try{
            int currentStatus = experService.queryExperCurrentStatus(experId);
            experService.modifyExperStatus(experId, currentStatus, 1);
            response.setMsg("管理员结束实验成功").setStatus(ConstantCode.QUERY_SUCCESS);
        }catch (NullPointerException e){
            response.setMsg("管理员结束实验失败").setStatus(ConstantCode.QUERY_FAIL);
        }
        return response;
    }

    /**
     * 管理员分页查询所有培训教材接口
     * 状态码：成功200；失败210；校验失败300
     * @param pageIndex
     * @param pageSize
     * @return QueryTrainingMaterialInfoPage
     */
    @GetMapping("listalltrainingmaterial")
    public BaseResponse listAllTrainingMaterial(int pageIndex, int pageSize){
        BaseResponse response = new BaseResponse();
        if(pageIndex < 0 || pageSize <= 0){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("数据校验错误");
        }
        response.setStatus(ConstantCode.QUERY_SUCCESS);
        try{
            QueryTrainingMaterialInfoPage data = trainingMaterialService.queryAllMaterialInfo(pageIndex, pageSize);
            response.setData(data).setMsg("查询培训材料成功").setStatus(ConstantCode.QUERY_SUCCESS);
        }catch (NullPointerException e){
            response.setMsg("查询培训材料失败").setStatus(ConstantCode.QUERY_FAIL);
        }
        return response;
    }


    /**
     * 管理员删除指定id的培训教材
     * 功能介绍：只在数据库中删除，不做物理删除
     * 删除成功204；失败214；校验失败300
     * @param id
     * @return
     */
    @GetMapping("deletetrainingmaterial")
    public BaseResponse deleteTrainingMaterialById(String id){
        BaseResponse response = new BaseResponse();
        if(StringUtils.isBlank(id)){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("教材id为空");
            return response;
        }
        // 没有校验教材id是否存在，但是删除一个不存在的健不会有异常
        int status = trainingMaterialService.removeTrainingMaterialById(id);
        if(status == 1){
            response.setMsg("删除成功").setStatus(ConstantCode.DELETE_SUCCESS);
        }else{
            response.setMsg("删除失败").setStatus(ConstantCode.DELETE_FAIL);
        }
        return response;
    }

    /**
     * 管理员新增培训教材接口
     * 功能介绍：管理员上传教材文件，成功后返回该材料相关信息
     * 成功203；失败213
     * @param name
     * @param experId
     * @param groupId
     * @param file
     * @return QueryTrainingMaterialInfo
     */
    // 新增培训教材
    @PostMapping("createtrainingmaterial")
    public BaseResponse createTrainingMaterial(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "experId") String experId,
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "file") MultipartFile file
            ){
        // 没有校验后缀名
        BaseResponse response = new BaseResponse();
        // 1.校验：文件title存在；experId和groupId不存在或者不对应;
        int status = trainingMaterialService.validStatus(name, experId, groupId);
        if(status == 1){
            response.setStatus(ConstantCode.UPLOAD_FAIL).setMsg("该培训材料已存在");
        }else if(status == 2){
            response.setStatus(ConstantCode.UPLOAD_FAIL).setMsg("实验名和组别传入错误");
        }else if(status == 0){
            try{
                // 2.操作
                QueryTrainingMaterialInfo data = trainingMaterialService
                        .createTrainingMaterial(name, experId, groupId, file);
                response.setStatus(ConstantCode.UPLOAD_SUCCESS).setMsg("新增培训材料成功").setData(data);
            }catch (NullPointerException | ElementDuplicateException | SaveFileIOException e){
                response.setStatus(ConstantCode.UPLOAD_FAIL).setMsg(e.getMessage());
            }
        }
        return response;
    }

    // 管理员分页查询tester

    /**
     * 管理员分页查询所有非管理原用户接口
     * 成功200；校验失败300；没有失败
     * @param pageIndex
     * @param pageSize
     * @return TesterInfoPage
     */
    @GetMapping("listalltester")
    public BaseResponse listAllTester(int pageIndex, int pageSize){
        BaseResponse response = new BaseResponse();
        if(pageIndex < 0 || pageSize <= 0){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("数据校验错误");
        }
        response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("列举所有参试人员的信息");
        TesterInfoPage data = userService.queryAllTester(pageIndex, pageSize);
        response.setData(data);
        return response;
    }


//    @GetMapping("listalltester")
//    public BaseResponse<List<TesterInfo>> listAllTester(){
//        BaseResponse<List<TesterInfo>> response = new BaseResponse<>();
//        response.setStatus(200).setMsg("列举所有参试人员的信息");
//        List<TesterInfo> data = userService.queryAllTester();
//        response.setData(data);
//        return response;
//    }

    /**
     * 管理员创建新用户接口
     * 成功201；失败211；校验错误300
     * @param request
     * @param bindingResult
     * @return TesterInfo
     */
    @PostMapping("createuser")
    public BaseResponse createUser(@RequestBody @Valid CreateUserRequest request,
                                   BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        // 1.数据校验
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        TesterInfo testerInfo = userService.createNewUser(request.getUsername(),
                request.getRealname(), request.getIsAdmin());
        if(testerInfo == null){
            response.setMsg("该用户已存在").setStatus(ConstantCode.CREATE_FAIL);
        }else{
            response.setMsg("新建用户成功").setData(testerInfo).setStatus(ConstantCode.CREATE_SUCCESS);
        }
        return response;
    }



    /**
     * 管理员删除用户
     * 成功204；失败214；校验失败300
     * @param userId
     * @return none
     */
    @GetMapping("deletetester")
    public BaseResponse deleteTester(String userId){
        BaseResponse response = new BaseResponse();
        if(StringUtils.isBlank(userId)){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("用户id不能为空");
            return response;
        }
        if(userService.deleteUser(userId) == 0){
            response.setMsg("删除失败").setStatus(ConstantCode.DELETE_FAIL);
        }else{
            response.setStatus(ConstantCode.DELETE_SUCCESS).setMsg("删除成功");
        }
        return response;
    }

    /**
     * 管理员将某个用户添加到具体实验的具体组别接口
     * 成功200；失败210；校验失败300
     * @param request
     * @return
     */
    @PostMapping("addtestertoexper")
    public BaseResponse addTesterToExper(@RequestBody @Valid AddTesterToExperRequest request,
                                         BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        try{
            adminService.addTesterToExper(request.getUserId(), request.getExperId(), request.getGroupId());
            response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("添加成功");
        }catch (NullPointerException | ElementDuplicateException e){
            response.setMsg(e.getMessage()).setStatus(ConstantCode.QUERY_FAIL);
        }
        return response;
    }


    /**
     * 管理员分页查询用户未参与的进行中和未进行的实验接口
     * 成功200；校验出错300
     * @param request
     * @return
     * @see ExperInfoPage
     */
    @PostMapping("getnotinexpers")
    public BaseResponse getTesterNotInExpers(@RequestBody @Valid QueryNotInExperRequest request,
                                             BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("获取用户未参与的实验");
        // 这个没有校验，但是出错只会报空
        ExperInfoPage data = experService.queryNotInExpers(request.getUserId(), request.getPageIndex(),
                request.getPageSize());
        response.setData(data);
        return response;
    }


    /**
     * 管理员创建实验接口
     * 成功201；失败211；数据校验失败300
     * @param request
     * @return
     */
    @PostMapping("createexper")
    public BaseResponse createExper(@RequestBody @Valid CreateExperRequest request,
                                    BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
//        String finalTime = request.getStartDate()+ " " + request.getStartTime();
//        String finalTime = request.getStartDate().substring(0, 10) + " " + request.getStartTime().substring(0, 10)
        try{
            CreateExperResult data = adminService.createExper(request.getCreatorId(),
                    request.getExperName(), request.getStartTime(), request.getWorkspace(),
                    request.getGroupInfoList(), request.getPhaseInfoList());
            response.setStatus(ConstantCode.CREATE_SUCCESS).setMsg("创建实验成功").setData(data);
        }catch (ElementDuplicateException | InsertException e){
            response.setMsg(e.getMessage()).setStatus(ConstantCode.CREATE_FAIL);
        }
        return response;
    }

    /**
     * 管理员添加非编程类问题接口
     * 成功201；失败211；校验失败300
     * @param request
     * @param bindingResult
     * @return none
     */
    @PostMapping("addnonprogquestion")
    public BaseResponse addNonProgQuestion(@RequestBody @Valid AddNonProgQuestionRequest request,
                                           BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        try{
            adminService.addQuestionTypeNonProg(request.getExperId(), request.getGroupId(),
                    request.getPhaseNumber(), request.getAddNonProgQuestionInfoList());
            response.setStatus(ConstantCode.CREATE_SUCCESS).setMsg("添加问题成功");
        }catch (InsertException e){
            response.setMsg(e.getMessage()).setStatus(ConstantCode.CREATE_FAIL);
        }
        return response;
    }

    /**
     * 管理员添加编程问题接口
     * 201成功；211失败；300状态校验失败
     * @param req
     * @return
     */
    @PostMapping("addprogquestion")
    public BaseResponse addProgQuestion(@RequestBody @Valid AddProgQuestionRequest req,
                                        BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        try{
            adminService.addQuestionTypeProg(req.getExperId(), req.getGroupId(), req.getPhaseNumber(),
                    req.getAddProgQuestionInfoList());
            response.setStatus(ConstantCode.CREATE_SUCCESS).setMsg("问题创建成功");
        }catch (NullPointerException | ElementDuplicateException e){
            response.setStatus(ConstantCode.CREATE_FAIL).setMsg(e.getMessage());
        }
        return response;
    }

}
