package com.faceRecog.manage.clientApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.faceRecog.manage.callable.UpdateEquipUpgradeCallable;
import com.faceRecog.manage.model.*;
import com.faceRecog.manage.model.serverVO.EmpServer;
import com.faceRecog.manage.model.serverVO.VisitServer;
import com.faceRecog.manage.service.*;
import com.faceRecog.manage.util.fileUpload.CommonFileUploadServlet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import javax.servlet.http.HttpServletRequest;

/**
 * ClassName: ApiServer
 *
 * @author xya
 * @Description: 设备接口  接口访问方式 ：http://ip:端口/项目名/webservice/方法名
 * @date 2018年9月14日
 */
@RestController
@RequestMapping("/api")
public class ApiServer {

	private Logger logger = LoggerFactory.getLogger(ApiServer.class);

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private BasicParamService basicParamService;

    @Autowired
    private SignRecordService signRecordService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CardService cardService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private UpgradeService upgradeService;

    @Autowired
    private AccessAuthService accessAuthService;


    /**
     * @param equipment
     * @return Result
     * @Title: insertEquipment
     * @Description:设备注册
     * @author xya
     * @date 2019年5月7日下午1:50:07
     */
    @RequestMapping("/insertEquipment")
    public Result insertEquipment(Equipment equipment) throws Exception {
        /*if (StrKit.isBlank(equipment.getMac())) {
            return Result.responseError("mac地址不能为空！");
        } */
        if (StrKit.isBlank(equipment.getSoftVersion())) {
            return Result.responseError("软件版本号不能为空！");
        } else if (StrKit.isBlank(equipment.getSn())) {
            return Result.responseError("设备编号不能为空！");
        } else if (StrKit.isBlank(equipment.getEqName())) {
            return Result.responseError("设备名称不能为空！");
        }

        Equipment eqRec = equipmentService.selectEquipmentBySn(equipment.getSn());
        if (eqRec != null) {
            Result result = Result.responseError("设备已注册！");
            result.setCode(2);
            return result;
        }
        equipment.setCreateTime(new Date());
        equipment.setId(UUIDGenerator.getUUID());
        equipment.setStatus("1");//1为正常状态
        int affectNum = equipmentService.insertEquipment(equipment);
        if (affectNum < 0) {
            return Result.responseError("注册失败！");
        }
        return Result.responseSuccess("注册成功！");
    }

    /**
     * @param equipment
     * @return
     * @throws Exception Result
     * @Title: updateEquipmentInfo
     * @Description: 修改设备信息
     * @author xya
     * @date 2019年5月8日上午10:39:21
     */
    @RequestMapping("/updateEquipmentInfo")
    public Result updateEquipmentInfo(Equipment equipment) throws Exception {
        Result result;
        if (StrKit.isBlank(equipment.getSn())) {
            return Result.responseError("设备编号不能为空！");
        }
        // 判断昵称是否占用
        int count = equipmentService.selectEqNickNameExistBySn(equipment.getSn(), equipment.getEqName());
        if (count > 0) {
            return Result.responseError("该昵称被占用！");
        }
        equipment.setUpdateTime(new Date());
        int insertResult = equipmentService.updateEquipmentInfoBySn(equipment);
        if (insertResult > 0) {
            result = Result.responseSuccess("修改成功");
        } else {
            result = Result.responseError("修改失败");
        }
        return result;
    }

    /**
     * @Description 根据设备编号查询设备信息
     * @Author Capejor
     * @Date 2019-05-25 16:11
     **/
    @RequestMapping("/selectEquipmentBySn")
    public Result selectEquipmentBySn(@RequestParam String sn) throws Exception {
        Result result;
        Equipment equipment = equipmentService.selectEquipmentBySn(sn);
        if (StrKit.notNull(equipment)) {
            result = Result.responseSuccess("查询成功", equipment);
        } else {
            result = Result.responseError("无数据", equipment);
        }
        return result;
    }

    /**
     * @Description 判断设备是否注册
     * @Author Capejor
     * @Date 2019-05-25 15:13
     **/
    @RequestMapping("/judgeEquipmentRegister")
    public Result judgeEquipmentRegister(@RequestParam String sn) throws Exception {
        if (StrKit.isBlank(sn)) {
            return Result.responseError("设备编号不能为空");
        }
        int count = equipmentService.judgeEquipmentRegister(sn);
        if (count > 0) {
            return Result.responseSuccess("设备已注册！");
        }
        return Result.responseError("设备未注册！");
    }


    /**
     * @Description: 打卡
     * @author Capejor
     * @date 2019年5月10日
     */
    @RequestMapping("/signIn")
    public Result signIn(SignRecord signRecord) throws Exception {
        Result result = null;
        if (signRecord.getSignInOne() == null && signRecord.getSignInTwo() == null && signRecord.getSignInThree() == null) {
            return Result.responseError("签到时间不能为空");
        } else if (StrKit.isBlank(signRecord.getEmpId())) {
            return Result.responseError("员工id不能为空！");
        }
        result = signRecordService.insertSignRecord(signRecord, CommonConstant.签到.getValue());
        return result;
    }


    /**
     * @Description 查询设备的所有员工门禁权限
     * @Author Capejor
     * @Date 2019-05-28 9:27
     **/
    @RequestMapping("/selectEmpAuthBySn")
    public Result selectEmpAuthBySn(@RequestParam String sn) throws Exception {
        Result result;
        List<Map<String, Object>> authList = accessAuthService.selectEmpAuthBySn(sn);
        if (authList != null && authList.size() > 0) {
            result = Result.responseSuccess("查询成功", authList);
        } else {
            result = Result.responseSuccess("无数据", new ArrayList<>());
        }
        return result;
    }


    /**
     * @Description 查询设备的所有访客门禁权限
     * @Author Capejor
     * @Date 2019-05-28 9:35
     **/
    @RequestMapping("/selectVisitAuthBySn")
    public Result selectVisitAuthBySn(@RequestParam String sn) throws Exception {
        Result result;
        List<VisitServer> authList = visitorService.selectVisitAuthBySn(sn);
        if (authList != null && authList.size() > 0) {
            result = Result.responseSuccess("查询成功", authList);
        } else {
            result = Result.responseSuccess("无数据", new ArrayList<>());
        }
        return result;
    }


    /**
     * @Description: 设备端 查询所有员工
     * @author Capejor
     * @date 2019年5月14日
     */
    @RequestMapping("/selectAllEmp")
    public Result selectAllEmp() throws Exception {
        Result result;
        List<EmpServer> empVOList = employeeService.selectAllEmp();
        if (empVOList != null && empVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", empVOList);
        } else {
            result = Result.responseSuccess("无数据", empVOList);
        }
        return result;
    }


    /**
     * @Description: 打卡
     * @author Capejor
     * @date 2019年5月10日
     */
    @RequestMapping("/signOut")
    public Result signOut(SignRecord signRecord) throws Exception {
        Result result = null;
        if (signRecord.getSignOutOne() == null && signRecord.getSignOutTwo() == null && signRecord.getSignOutThree() == null) {
            return Result.responseError("签退时间不能为空");
        } else if (StrKit.isBlank(signRecord.getEmpId())) {
            return Result.responseError("员工id不能为空！");
        }
        result = signRecordService.insertSignRecord(signRecord, CommonConstant.签退.getValue());
        return result;
    }

    /**
     * @return
     * @throws Exception Result
     * @Title: selectAllEmpWorkSchedule
     * @Description:查询所有员工的上班下班排程
     * @author xya
     * @date 2019年5月15日下午4:06:20
     */
    @RequestMapping("/selectAllEmpWorkSchedule")
    public Result selectAllEmpWorkSchedule() throws Exception {
        Result result = null;
        List<Map<String, Object>> workScheduleList = employeeService.selectAllEmpWorkSchedule();
        if (workScheduleList != null && workScheduleList.size() > 0 && workScheduleList.get(0) != null) {
            result = Result.responseSuccess("查询成功！", workScheduleList);
        } else {
            result = Result.responseSuccess("查询成功！暂无数据！", workScheduleList);
        }
        return result;
    }


    /**
     * 根据 equipId 查询设备基本参数
     *
     * @author Capejor
     * @date 2019年5月14日
     */
    @RequestMapping("/selectBasicByEquipId")
    public Result selectBasicParam(@RequestParam String equipId) throws Exception {
        Result result;
        if (StrKit.isBlank(equipId)) {
            return Result.responseError("设备id不能为空!");
        }
        Map<String, Object> basicParamMap = basicParamService.selectBasicByEquipId(equipId);
        if (StrKit.notNull(basicParamMap)) {
            result = Result.responseSuccess("查询成功", basicParamMap);
        } else {
            result = Result.responseSuccess("无数据", basicParamMap);
        }
        return result;
    }


    /**
     * @return
     * @throws Exception Result
     * @Title: freeSign
     * @Description: 未添加到考勤组人员打卡通道
     * @author xya
     * @date 2019年5月22日下午5:31:11
     */
    @RequestMapping("/freeSign")
    public Result freeSign(Date signTime, String signDate, String empId, String signType) throws Exception {
        Result result = null;
        if (!StrKit.notNull(signTime)) {
            return Result.responseError("打卡时间不能为空!");
        } else if (StrKit.isBlank(signDate)) {
            return Result.responseError("打卡日期不能为空!");
        } else if (StrKit.isBlank(empId)) {
            return Result.responseError("员工id不能为空!");
        } else if (StrKit.isBlank(signType)) {
            return Result.responseError("员工类型不能为空!");
        }
        result = signRecordService.insertFreeSign(signTime, signDate, empId, signType);
        return result;
    }


    /**
     * @Description 查询所有卡片
     * @Author Capejor
     * @Date 2019-05-31 19:39
     **/
    @RequestMapping("/selectAllCard")
    public Result selectAllCard() throws Exception {
        Result result;
        List<Card> cardList = cardService.selectAllCard();
        if (cardList != null && cardList.size() > 0) {
            result = Result.responseSuccess("查询成功", cardList);
        } else {
            result = Result.responseSuccess("无数据", cardList);
        }
        return result;
    }

    /**
     * @Description 录入卡片
     * @Author Capejor
     * @Date 2019-05-23 11:30
     **/
    @RequestMapping("/insertCard")
    public Result insertCard(Card card) throws Exception {
        Result result;
        if (StrKit.isBlank(card.getCardNum())) {
            return Result.responseError("卡号不能为空");
        }
        card.setId(UUIDGenerator.getUUID());
        card.setInUse("0");
        card.setCreateTime(new Date());
        card.setCreateUserId("1");
        card.setStatus("1");
        int count = cardService.selectCountByCardNum(card.getCardNum());
        if (count > 0) {
            return Result.responseError("卡号重复,请更换卡片!!!");
        }
        int insertResult = cardService.insertCard(card);
        if (insertResult > 0) {
            result = Result.responseSuccess("卡片添加成功");
        } else {
            result = Result.responseError("卡片添加失败");
        }
        return result;
    }

    /**
     * @return
     * @throws Exception Result
     * @Title: selectAllEmpWorkSchedule
     * @Description:查询所有员工的固定班制上班下班排程
     * @author xya
     * @date 2019年5月15日下午4:06:20
     */
    @RequestMapping("/selectAllEmpWorkPeriodSchedule")
    public Result selectAllEmpWorkPeriodSchedule() throws Exception {
        Result result = null;
        List<Map<String, Object>> workScheduleList = employeeService.selectAllEmpWorkPeriodSchedule();
        if (workScheduleList != null && workScheduleList.size() > 0 && workScheduleList.get(0) != null) {
            result = Result.responseSuccess("查询成功！", workScheduleList);
        } else {
            result = Result.responseSuccess("查询成功！暂无数据！", workScheduleList);
        }
        return result;
    }

    /**
     * @return
     * @throws Exception Result
     * @Title: selectAllEmpAttendInfo
     * @Description: 查询所有员工所属的考勤班制类型信息
     * @author xya
     * @date 2019年5月29日上午8:55:47
     */
    @RequestMapping("/selectAllEmpAttendInfo")
    public Result selectAllEmpAttendInfo() throws Exception {
        Result result = null;
        List<Map<String, Object>> empAttendList = employeeService.selectAllEmpAttendInfo();
        if (empAttendList != null && empAttendList.size() > 0 && empAttendList.get(0) != null) {
            result = Result.responseSuccess("查询成功！", empAttendList);
        } else {
            result = Result.responseSuccess("查询成功！暂无数据！", empAttendList);
        }
        return result;
    }


    /**
     * @Description 录入员工
     * @Author Capejor
     * @Date 2019-05-31 19:50
     **/
    @RequestMapping("/insertEmpServer")
    public Result insertEmpServer(Employee employee, EmployeeInfo employeeInfo, FaceFile faceFile, @RequestParam String cardId, HttpServletRequest request) throws Exception {
        Result result;
        if (StrKit.isBlank(employee.getName())) {
            return Result.responseError("员工姓名不能为空");
        }
        if (StrKit.isBlank(employee.getPhone())) {
            return Result.responseError("员工电话不能为空");
        }
        if (StrKit.isBlank(cardId)) {
            return Result.responseError("员工卡id不能为空");
        }

        //判断卡号是否被占用
        int inUseNum = cardService.selectCountByCardId(cardId);
        if (inUseNum > 0) {
            return Result.responseError("卡号被占用");
        }
        employee.setDeptId("0");
        employee.setId(UUIDGenerator.getUUID());
        employee.setCreateTime(new Date());
        employee.setStatus("1");
        employee.setCreateUserId("1");
        employee.setIsDimiss("1");
        employee.setFaceReg("1");
        //详情
        employeeInfo.setEmerPhone("130-0000-0000");
        employeeInfo.setId(UUIDGenerator.getUUID());
        employeeInfo.setEmpId(employee.getId());
        employeeInfo.setCreateTime(new Date());
        employeeInfo.setStatus("1");
        employeeInfo.setCreateUserId(employee.getCreateUserId());

        //接收图片
        CommonFileUploadServlet uploadServlet = new CommonFileUploadServlet();
        JSONObject jsonObj = uploadServlet.upload(request);
        JSONArray jsonArray = (JSONArray) jsonObj.get("fileList");
        String photoUrl = jsonArray.getJSONObject(0).get("srcFilePath").toString();
        if (StrKit.isBlank(photoUrl)) {
            return Result.responseError("图片路径为空");
        }
        String photoSize = jsonArray.getJSONObject(0).get("fileSize").toString();
        if (StrKit.isBlank(photoSize)) {
            return Result.responseError("图片大小为空");
        }
        faceFile.setPhotoUrl(photoUrl);
        faceFile.setPhotoSize(photoSize);
        /*faceFile.setPhotoUrl("1");
        faceFile.setPhotoSize("1");*/
        faceFile.setValidTime("---");
        faceFile.setId(UUIDGenerator.getUUID());
        faceFile.setEmpId(employee.getId());
        faceFile.setCreateTime(new Date());
        faceFile.setStatus("1");
        faceFile.setCreateUserId("1");
        int insertResult = employeeService.insertEmpServer(employee, employeeInfo, faceFile, cardId);
        if (insertResult > 0) {
            result = Result.responseSuccess("添加成功");
        } else {
            result = Result.responseError("添加失败");
        }
        return result;
    }


    /**
     * @param empId
     * @param state
     * @return
     * @throws Exception Result
     * @Title: updateEmpFaceRegState
     * @Description: 修改员工人脸注册的状态
     * @author xya
     * @date 2019年6月10日下午2:27:46
     */
    @RequestMapping("/updateEmpFaceRegState")
    public Result updateEmpFaceRegState(String empId, String state) throws Exception {
        Result result = null;
        if (StrKit.isBlank(empId)) {
            return Result.responseError("员工id不能为空！");
        } else if (StrKit.isBlank(state)) {
            return Result.responseError("注册状态不能为空！");
        }

        int affectNum = employeeService.updateEmpFaceRegState(empId, state);
        if (affectNum < 0) {
            return Result.responseError("修改失败！");
        }
        result = Result.responseSuccess("修改成功！");
        return result;
    }


    /**
     * @Description 下载文件
     * @Author Capejor
     * @Date 2019-06-10 20:21
     **/
    @RequestMapping(value = "/downloadFile")
    public Result downloadFile(String id, String percent, String downKb) {
        Result result;
        if (StrKit.isBlank(id)) {
            return Result.responseError("文件id不能为空！");
        } else if (StrKit.isBlank(percent)) {
            return Result.responseError("进度百分比不能为空！");
        } else if (StrKit.isBlank(downKb)) {
            return Result.responseError("下载速度kb不能为空！");
        }
        Future<Result> results;
        try {//newFixedThreadPool(500)
            ExecutorService executeService = Executors.newFixedThreadPool(500);
            results = executeService.submit(new UpdateEquipUpgradeCallable(id, percent, downKb));
            result = results.get();
            executeService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.responseError("升级失败！");
        }
        return result;
    }

    /**
     * @Description 查询上传文件
     * @Author Capejor
     * @Date 2019-06-10 20:55
     **/
    @RequestMapping("/selectUpgrade")
    public Result selectUpgrade(String sn) throws Exception {
        Result result;
        if (StrKit.isBlank(sn)) {
            return Result.responseError("设备编号为空!");
        }
        //根据sn查询设备id
        String equipId = equipmentService.selectEquipIdBySn(sn);
        if (StrKit.isBlank(equipId)) {
            return Result.responseError("设备id为空!");
        }
        List<Upgrade> upgradeList = upgradeService.selectUpgrade(equipId);
        if (upgradeList != null && upgradeList.size() > 0) {
            result = Result.responseSuccess("查询成功", upgradeList);
        } else {
            result = Result.responseError("无数据", upgradeList);
        }
        return result;
    }

}
