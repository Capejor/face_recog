package com.faceRecog.manage.controller;


import com.faceRecog.manage.model.Visitor;
import com.faceRecog.manage.model.vo.VisitAuthVO;
import com.faceRecog.manage.model.vo.VisitVO;
import com.faceRecog.manage.service.VisitorService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class VisitorController {

    @Autowired
    private VisitorService visitorService;


    /**
     * @Description: 访客预约
     * @author Capejor
     * @date 2019年5月15日
     */
    @RequestMapping("/insertAppointVisitor")
    public Result insertAppointVisitor(Visitor visitor) throws Exception {
        Result result;
        if (StrKit.isBlank(visitor.getName())) {
            return Result.responseError("访客姓名不能为空");
        }
        if (StrKit.isBlank(visitor.getSex())) {
            return Result.responseError("访客性别不能为空");
        }
        if (StrKit.isBlank(visitor.getPhone())) {
            return Result.responseError("访客电话不能为空");
        }
        if (StrKit.isBlank(visitor.getEmpName())) {
            return Result.responseError("被访人姓名不能为空");
        }
        if (StrKit.isBlank(visitor.getEmpPhone())) {
            return Result.responseError("被访人电话不能为空");
        }
        if (StrKit.isBlank(visitor.getAppointTime())) {
            return Result.responseError("预约来访时间不能为空");
        }
        //判断是否已登记或已预约 当天
        int selectCount = visitorService.selectByPhoneAndVisitTime(visitor.getPhone(),visitor.getVisitTime());
        if (selectCount > 0) {
            return Result.responseError("该电话当天已经预约或登记，请改天再约");
        }
        visitor.setId(UUIDGenerator.getUUID());
        visitor.setCreateTime(new Date());
        visitor.setStatus("1");
        visitor.setIsAppointed("1");//是否来访（0：未预约，1：已预约，2：未来访，3：已来访）
        visitor.setApproveStatus("2");//审核状态（0：未通过，1：通过，2：等待审批，3：已过期）
        visitor.setVisitTime(visitor.getAppointTime().substring(0,10));
        visitor.setLeaveTime("");
        int insertResult = visitorService.insertAppointVisitor(visitor);
        if (insertResult > 0) {
            result = Result.responseSuccess("访客预约成功");
        } else {
            result = Result.responseError("访客预约失败");
        }
        return result;
    }


    /**
     * @Description: 访客登记
     * @author Capejor
     * @date 2019年5月15日
     */
    @RequestMapping("/insertVisitor")
    public Result insertVisitor(Visitor visitor, VisitAuthVO visitAuthVO, HttpServletRequest request) throws Exception {
        Result result;
        if (StrKit.isBlank(visitor.getName())) {
            return Result.responseError("访客姓名不能为空");
        }
        if (StrKit.isBlank(visitor.getPhone())) {
            return Result.responseError("访客电话不能为空");
        }
        if (StrKit.isBlank(visitor.getEmpName())) {
            return Result.responseError("被访人姓名不能为空");
        }
        if (StrKit.isBlank(visitor.getEmpPhone())) {
            return Result.responseError("被访人电话不能为空");
        }
        if (visitAuthVO.getStartTime() == null || visitAuthVO.getStartTime().size() <= 0) {
            return Result.responseError("访客通行开始不能为空");
        }
        if (visitAuthVO.getEndTime() == null || visitAuthVO.getEndTime().size() <= 0) {
            return Result.responseError("访客通行结束不能为空");
        }
        if (visitAuthVO.getEquipId() == null || visitAuthVO.getEquipId().size() <= 0) {
            return Result.responseError("设备id不能为空");
        }
        //判断是否已登记或已预约 当天
        int selectCount = visitorService.selectByPhoneAndVisitTime(visitor.getPhone(),visitor.getVisitTime());
        if (selectCount > 0) {
            return Result.responseError("该电话当天已经预约或登记，请改天再约");
        }
        //接收图片
        /*CommonFileUploadServlet uploadServlet = new CommonFileUploadServlet();
        JSONObject jsonObj = uploadServlet.upload(request);
        JSONArray jsonArray = (JSONArray) jsonObj.get("fileList");
        String photo = jsonArray.getJSONObject(0).get("srcFilePath").toString();
        if (StrKit.isBlank(photo)) {
            return Result.responseError("图片路径不能为空");
        }*/
        //visitor.setPhoto(photo);
        visitor.setPhoto("12345");
        visitor.setId(UUIDGenerator.getUUID());
        visitor.setCreateTime(new Date());
        visitor.setStatus("1");
        visitor.setIsAppointed("3");//是否来访（0：未预约，1：已预约，2：未来访，3：已来访）
        visitor.setAppointTime("");
        visitor.setApproveStatus("");
        visitor.setLeaveTime("");
        int insertResult = visitorService.insertVisitor(visitor, visitAuthVO);
        if (insertResult > 0) {
            result = Result.responseSuccess("访客登记成功");
        } else {
            result = Result.responseError("访客登记失败");
        }
        return result;
    }

    /**
     * @Description 编辑访客信息
     * @Author Capejor
     * @Date 2019-06-19 10:37
     **/
    @RequestMapping("/updateVisitor")
    public Result updateVisitor(Visitor visitor, VisitAuthVO visitAuthVO, HttpServletRequest request) throws Exception {
        Result result;
        int insertResult;
        //接收图片
        /*CommonFileUploadServlet uploadServlet = new CommonFileUploadServlet();
        JSONObject jsonObj = uploadServlet.upload(request);
        JSONArray jsonArray = (JSONArray) jsonObj.get("fileList");
        String photo = jsonArray.getJSONObject(0).get("srcFilePath").toString();
        if (StrKit.isBlank(photo)) {
            return Result.responseError("图片路径不能为空");
        }*/
        //visitor.setPhoto(photo);
        visitor.setIsAppointed("3");//是否来访（0：未预约，1：已预约，2：未来访，3：已来访）

        //判断是否已登记或已预约 除去自己
        int appointNum = visitorService.selectByPhoneAndAppointTimeExceptOwn(visitor.getId(), visitor.getPhone(),visitor.getAppointTime());
        if (appointNum > 0) {
            return Result.responseError("当前电话已登记或已预约，请更换号码！");
        }
        //判断访客是否有权限
        int authNum = visitorService.selectByVisitId(visitor.getId());
        if (authNum > 0) {
            insertResult = visitorService.updateVisitor(visitor, visitAuthVO);
        } else {
            insertResult = visitorService.updateVisitorAndAuth(visitor, visitAuthVO);
        }
        if (insertResult > 0) {
            result = Result.responseSuccess("访客修改成功");
        } else {
            result = Result.responseError("访客修改失败");
        }
        return result;

    }


    /**
     * @Description: 查询预约访客
     * @author Capejor
     * @date 2019年5月15日
     */
    @RequestMapping("/selectVisitor")
    public Result selectVisitor(@RequestParam(required = false) String phone) throws Exception {
        Result result;
        List<VisitVO> visitAuthVOList = visitorService.selectVisitor(phone);
        if (visitAuthVOList != null && visitAuthVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", visitAuthVOList);
        } else {
            result = Result.responseSuccess("无数据", visitAuthVOList);
        }
        return result;
    }


    /**
     * @Description: 查询今日来访访客
     * @author Capejor
     * @date 2019年5月15日
     */
    @RequestMapping("/selectTodayVisitor")
    public Result selectTodayVisitor() throws Exception {
        Result result;
        List<Map<String, Object>> visitAuthVOList = visitorService.selectVisitorByDate();
        if (visitAuthVOList != null && visitAuthVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", visitAuthVOList);
        } else {
            result = Result.responseSuccess("无数据", visitAuthVOList);
        }
        return result;
    }

    /**
     * @Description: 审核
     * @author Capejor
     * @date 2019年5月15日
     */
    @RequestMapping("/updateApproveStatus")
    public Result approveAuth(String id, String approveStatus) throws Exception {
        Result result;
        int statusNum = visitorService.selectStatusByVisitId(id);
        if (statusNum > 0) {
            return Result.responseError("该访客预约已过期，审核无效！");
        }
        int updateNum = visitorService.updateApproveStatus(id, approveStatus);
        if (updateNum > 0) {
            result = Result.responseSuccess("审批成功");
        } else {
            result = Result.responseError("审批失败");
        }
        return result;
    }

    /**
     * @Description: 批量删除访客
     * @author Capejor
     * @date 2019年5月15日
     */
    @RequestMapping("/deleteVisitor")
    public Result deleteVisitor(@RequestParam("ids[]") String[] ids) throws Exception {
        Result result;
        int deleteNum = visitorService.deleteVisitor(ids);
        if (deleteNum > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }


    /**
     * @Description: 条件查询访客记录
     * @author Capejor
     * @date 2019年5月15日
     */
    @RequestMapping("/selectVisitorRecord")
    public Result selectVisitorRecord(@RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime,
                                      @RequestParam(required = false) String searchParam) throws Exception {
        Result result;
        Map<String, Object> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("searchParam", searchParam);
        List<VisitVO> visitAuthVOList = visitorService.selectVisitorRecord(map);
        if (visitAuthVOList != null && visitAuthVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", visitAuthVOList);
        } else {
            result = Result.responseSuccess("无数据", visitAuthVOList);
        }
        return result;
    }


}
