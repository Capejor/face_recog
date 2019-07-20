package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Attendance;
import com.faceRecog.manage.model.vo.TimeFrameVO;
import com.faceRecog.manage.service.AttendanceService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @ClassName: AttendanceController
 * @Description: 考勤管理 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 添加考勤
     *
     * @throws Exception
     */
    @RequestMapping("/insertAttendance")
    public Result insertAttendance(Attendance attendance, TimeFrameVO timeFrameVO) throws Exception {
        Result result;

        if (StrKit.isBlank(attendance.getName())) {
            return Result.responseError("班次名称不能为空");
        }
        int count = attendanceService.selectCountByAttName(attendance.getName());
        if (count > 0) {
            return Result.responseError("班次名称不能重复");
        }
        if (StrKit.isBlank(attendance.getType())) {
            return Result.responseError("班次类型不能为空");
        }
        if (StrKit.isBlank(attendance.getAllowLate())) {
            return Result.responseError("允许迟到时间不能为空");
        }
        if (StrKit.isBlank(attendance.getAllowEarly())) {
            return Result.responseError("允许早退时间不能为空");
        }
        if (StrKit.isBlank(attendance.getWorkTime())) {
            return Result.responseError("计出勤不能为空");
        }
        if (StrKit.isBlank(attendance.getAfterOvertime())) {
            return Result.responseError("下班后计加班时间不能为空");
        }
        if (timeFrameVO.getAttendTime() == null || timeFrameVO.getAttendTime().size() <= 0) {
            result = Result.responseError("上班时间不能为空");
            return result;
        }
        if (timeFrameVO.getCloseTime() == null || timeFrameVO.getCloseTime().size() <= 0) {
            result = Result.responseError("下班时间不能为空");
            return result;
        }
        if (timeFrameVO.getIsOvertime() == null || timeFrameVO.getIsOvertime().size() <= 0) {
            result = Result.responseError("是否加班不能为空");
            return result;
        }

        attendance.setId(UUIDGenerator.getUUID());
        attendance.setCreateTime(new Date());
        attendance.setCreateUserId("1");
        attendance.setStatus("1");
        int insertResult = attendanceService.insertAttendance(attendance, timeFrameVO);
        if (insertResult > 0) {
            result = Result.responseSuccess("班次添加成功");
        } else {
            result = Result.responseError("班次添加失败");
        }
        return result;
    }

    /**
     * 修改考勤
     *
     * @throws Exception
     */
    @RequestMapping("/updateAttendance")
    public Result updateAttendance(Attendance attendance, TimeFrameVO timeFrameVO) throws Exception {
        Result result;
        if (StrKit.isBlank(attendance.getId())) {
            return Result.responseError("班次id不能为空");
        }
        if (StrKit.isBlank(attendance.getName())) {
            return Result.responseError("班次名称不能为空");
        }

        if (StrKit.isBlank(attendance.getType())) {
            return Result.responseError("班次类型不能为空");
        }
        if (StrKit.isBlank(attendance.getAllowLate())) {
            return Result.responseError("允许迟到时间不能为空");
        }
        if (StrKit.isBlank(attendance.getAllowEarly())) {
            return Result.responseError("允许早退时间不能为空");
        }
        if (StrKit.isBlank(attendance.getWorkTime())) {
            return Result.responseError("计出勤不能为空");
        }
        if (StrKit.isBlank(attendance.getAfterOvertime())) {
            return Result.responseError("下班后计加班时间不能为空");
        }
        if (timeFrameVO.getAttendTime() == null && timeFrameVO.getAttendTime().size() <= 0) {
            result = Result.responseError("上班时间不能为空");
            return result;
        }
        if (timeFrameVO.getCloseTime() == null && timeFrameVO.getCloseTime().size() <= 0) {
            result = Result.responseError("下班时间不能为空");
            return result;
        }
        if (timeFrameVO.getIsOvertime() == null && timeFrameVO.getIsOvertime().size() <= 0) {
            result = Result.responseError("是否加班不能为空");
            return result;
        }
        int count = attendanceService.selectCountByAttNameExceptOwn(attendance.getId(), attendance.getName());
        if (count > 0) {
            return Result.responseError("班次名称不能重复");
        }
        attendance.setUpdateTime(new Date());
        int updateResult = attendanceService.updateAttendance(attendance, timeFrameVO);
        if (updateResult > 0) {
            result = Result.responseSuccess("班次修改成功");
        } else {
            result = Result.responseError("班次修改失败");
        }
        return result;
    }


    /**
     * 查询全部考勤
     *
     * @throws Exception
     */
    @RequestMapping("/selectAttendance")
    public Result selectAttendance() throws Exception {
        Result result;
        List<Map<String, Object>> attVOList = attendanceService.selectAttendance();
        if (attVOList != null && attVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", attVOList);
        } else {
            result = Result.responseSuccess("无数据", attVOList);
        }
        return result;
    }


    /**
     * 删除考勤
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteAttendance")
    public Result deleteAttendance(@RequestParam String id) throws Exception {
        Result result;
        int count = attendanceService.selectCountByAttIds(id);
        if (count > 0) {
            return Result.responseError("当前班次绑定周期，不能删除！");
        }
        int deleteNum = attendanceService.deleteAttendance(id);
        if (deleteNum > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseSuccess("删除失败");
        }
        return result;
    }


}
