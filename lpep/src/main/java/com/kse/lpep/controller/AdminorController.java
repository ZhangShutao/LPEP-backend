package com.kse.lpep.controller;

import com.kse.lpep.common.exception.ElementDuplicateException;
import com.kse.lpep.common.exception.InsertException;
import com.kse.lpep.controller.vo.*;
import com.kse.lpep.service.IAdminService;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.ITrainingMaterialService;
import com.kse.lpep.service.IUserService;
import com.kse.lpep.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @GetMapping("/listallexper")
    public BaseResponse listAllExper(int pageIndex, int pageSize){
        BaseResponse response = new BaseResponse();
        ExperInfoPage data = experService.getAllExper(pageIndex, pageSize);
        response.setStatus(200).setMsg("管理员查询所有实验数据").setData(data);
        return response;
    }

    // 情况1：该实验已经结束，数据传输错误
    // 情况2：正常情况
    @GetMapping("/startexper")
    public BaseResponse startExper(String experId){
        BaseResponse response = new BaseResponse();
        response.setStatus(200);
        try{
            int currentStatus = experService.queryExperCurrentStatus(experId);
            experService.modifyExperStatus(experId, currentStatus, 2);
            response.setMsg("管理员开始实验成功").setData(1);
        }catch (NullPointerException e){
            response.setMsg("管理员开始实验失败").setData(0);
        }finally {
            return response;
        }
    }

    @GetMapping("/endexper")
    public BaseResponse endExper(String experId){
        BaseResponse response = new BaseResponse();
        response.setStatus(200);
        try{
            int currentStatus = experService.queryExperCurrentStatus(experId);
            experService.modifyExperStatus(experId, currentStatus, 1);
            response.setMsg("管理员结束实验成功").setData(1);
        }catch (NullPointerException e){
            response.setMsg("管理员结束实验失败").setData(0);
        }finally {
            return response;
        }
    }


    // 列举所有培训教材
    @GetMapping("listalltrainingmaterial")
    public BaseResponse listAllTrainingMaterial(int pageIndex, int pageSize){
        BaseResponse response = new BaseResponse();
        response.setStatus(200);
        try{
            QueryTrainingMaterialInfoPage data = trainingMaterialService.queryAllMaterialInfo(pageIndex, pageSize);
            response.setData(data).setMsg("管理员查询所有培训材料成功");
        }catch (NullPointerException e){
            response.setMsg("管理员查询所有培训材料失败");
        }finally {
            return response;
        }
    }

    // 删除指定的培训教材
    @GetMapping("deletetrainingmaterial")
    public BaseResponse deleteTrainingMaterialById(String id){
        BaseResponse response = new BaseResponse();
        response.setStatus(204);
        int status = trainingMaterialService.removeTrainingMaterialById(id);
        if(status == 1){
            response.setMsg("删除成功");
        }else{
            response.setMsg("删除失败").setStatus(214);
        }
        return response;
    }
    // 新增培训教材
    @PostMapping("createtrainingmaterial")
    public BaseResponse createTrainingMaterial(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "experId") String experId,
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "file") MultipartFile file
            ){
        BaseResponse response = new BaseResponse();
        QueryTrainingMaterialInfo queryTrainingMaterialInfo = new QueryTrainingMaterialInfo();
        /*
        1.校验：文件title存在；experId和groupId不存在或者不对应;
         */
        int status = trainingMaterialService.vaildStatus(name, experId, groupId);
        if(status == 1){
            response.setStatus(211).setMsg("该培训材料已存在");
        }else if(status == 2){
            response.setStatus(211).setMsg("实验名和组别传入错误");
        }else if(status == 0){
            try{
                // 2.操作
                QueryTrainingMaterialInfo data = trainingMaterialService
                        .createTrainingMaterial(name, experId, groupId, file);
                response.setStatus(201).setMsg("新增培训材料成功").setData(data);
            }catch (NullPointerException e){
                response.setStatus(211).setMsg("上传文件失败");
            }
        }
        return response;
    }

    // 管理员分页查询tester
    @GetMapping("listalltester")
    public BaseResponse listAllTester(int pageIndex, int pageSize){
        BaseResponse response = new BaseResponse();
        response.setStatus(200).setMsg("列举所有参试人员的信息");
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

    // 管理员创建新用户
    @PostMapping("createuser")
    public BaseResponse createUser(@RequestBody CreateUserRequest request){
        BaseResponse response = new BaseResponse();
        response.setStatus(201);
        TesterInfo testerInfo = userService.createNewUser(request.getUsername(),
                request.getRealname(), request.getIsAdmin());
        if(testerInfo == null){
            response.setMsg("该用户已存在").setStatus(211);
        }else{
            response.setMsg("新建用户成功").setData(testerInfo);
        }
        return response;
    }

    // 管理员删除用户
    @GetMapping("deletetester")
    public BaseResponse deleteTester(String userId){
        BaseResponse response = new BaseResponse();
        response.setStatus(204).setMsg("删除成功");
        int data = userService.deleteUser(userId);
        if(data == 0){
            response.setMsg("删除失败");
        }
        response.setData(data);
        return response;
    }

    // 管理员添加用户到某个实验某个组
    @PostMapping("addtestertoexper")
    public BaseResponse addTesterToExper(@RequestBody AddTesterToExperRequest request){
        BaseResponse response = new BaseResponse();
        int data = adminService.addTesterToExper(request.getUserId(), request.getExperId(), request.getGroupId());
        response.setStatus(200).setMsg("添加成功");
        if(data == 0){
            response.setMsg("添加失败");
        }
        response.setData(data);
        return response;
    }


    /**
     * 管理员分页查询用户未分配的实验接口（返回个人）
     * @param request
     * @return
     * @see ExperInfoPage
     */
    @PostMapping("getnotinexpers")
    public BaseResponse getTesterNotInExpers(@RequestBody QueryNotInExperRequest request){
        BaseResponse response = new BaseResponse();
        response.setStatus(200).setMsg("获取用户未参与的实验");
        ExperInfoPage data = experService.queryNotInExpers(request.getUserId(), request.getPageIndex(),
                request.getPageSize());
        response.setData(data);
        return response;
    }

    // 管理员创建实验
    @PostMapping("createexper")
    public BaseResponse createExper(@RequestBody CreateExperRequest request){
        BaseResponse response = new BaseResponse();
        response.setStatus(211);
//        String finalTime = request.getStartDate()+ " " + request.getStartTime();
//        String finalTime = request.getStartDate().substring(0, 10) + " " + request.getStartTime().substring(0, 10);
        try{
            CreateExperResult data = adminService.createExper(request.getCreatorId(),
                    request.getExperName(), request.getStartTime(), request.getWorkspace(),
                    request.getGroupInfoList(), request.getPhaseInfoList());
            response.setStatus(201).setMsg("创建实验成功").setData(data);
        }catch (ElementDuplicateException e){
            response.setMsg(e.getMessage());
        }catch (InsertException e1){
            response.setMsg(e1.getMessage());
        }
        return response;
    }

    @PostMapping("addnonprogquestion")
    public BaseResponse addNonProgQuestion(@RequestBody AddNonProgQuestionRequest request){
        BaseResponse response = new BaseResponse();
        try{
            adminService.addQuestionTypeNonProg(request.getExperId(), request.getGroupName(),
                    request.getPhaseNumber(), request.getAddNonProgQuestionInfoList());
            response.setStatus(201).setMsg("添加问题成功");
        }catch (InsertException e){
            response.setMsg(e.getMessage()).setStatus(211);
        }
        return response;
    }

    /**
     *
     * @param req
     * @return
     */
    @PostMapping("addprogquestion")
    public BaseResponse addProgQuestion(AddProgQuestionRequest req){
        BaseResponse response = new BaseResponse();
        AddProgQuestionDto addProgQuestionDto = new AddProgQuestionDto();
        addProgQuestionDto.setQuestionNumber(req.getQuestionNumber()).setPhaseNumber(req.getPhaseNumber())
                .setContent(req.getContent()).setExperId(req.getExperId()).setGroupName(req.getGroupName())
                .setRuntimeLimit(req.getRuntimeLimit()).setTimeLimit(req.getTimeLimit())
                .setRunnerId(req.getRunnerId());
        try{
            int data = adminService.addQuestionTypeProg(addProgQuestionDto, req.getCaseIds());
            response.setStatus(201).setData(data);
        }catch (NullPointerException | ElementDuplicateException e){
            response.setStatus(211).setMsg(e.getMessage()).setStatus(0);
        }
        return response;
    }



}
