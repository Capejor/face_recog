package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Period;
import com.faceRecog.manage.service.PeriodService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Capejor
 * @className: PeriodController
 * @Description: TODO
 * @date 2019-05-16 10:33
 */
@RestController
public class PeriodController {

    @Autowired
    private PeriodService periodService;


    /**
     * @Description 添加周期
     * @Author Capejor
     * @Date 2019-05-16 11:03
     **/
    @RequestMapping("/insertPeriod")
    public Result insertPeriod(Period period) throws Exception {
        Result result;
        if (StrKit.isBlank(period.getPeriodName())) {
            return Result.responseError("周期名称不能为空");
        }
        int count = periodService.selectCountByPeriodName(period.getPeriodName());
        if (count >0){
            return Result.responseError("周期名称不能重复！");
        }
        period.setId(UUIDGenerator.getUUID());
        period.setCreateTime(new Date());
        period.setCreateUserId("1");
        period.setStatus("1");
        period.setDayNum(0);
        period.setType("2");// 排班制周期
        //int insertNum = periodService.insertPeriod(period,attendId);
        int insertNum = periodService.insertPeriod(period);
        if (insertNum > 0) {
            result = Result.responseSuccess("添加成功");
        } else {
            result = Result.responseError("添加失败");
        }
        return result;
    }

    /**
     * @Description 修改周期
     * @Author Capejor
     * @Date 2019-05-16 11:40
     **/
    @RequestMapping("/updatePeriod")
    public Result updatePeriod(Period period) throws Exception {
        Result result;
        if (StrKit.isBlank(period.getPeriodName())) {
            return Result.responseError("周期名称不能为空");
        }
        int count = periodService.selectCountByPeriodNameExceptOwn(period.getId(),period.getPeriodName());
        if (count >0){
            return Result.responseError("周期名称不能重复！");
        }
        period.setUpdateTime(new Date());
        //int insertNum = periodService.updatePeriod(period,attendId);
        int insertNum = periodService.updatePeriod(period);
        if (insertNum > 0) {
            result = Result.responseSuccess("修改成功");
        } else {
            result = Result.responseError("修改失败");
        }
        return result;
    }


    /**
     * @Description 查询周期
     * @Author Capejor
     * @Date 2019-05-16 13:51
     **/
    @RequestMapping("/selectPeriod")
    public Result selectPeriod() throws Exception {
        Result result;
        List<Map<String, Object>> periodList = periodService.selectPeriod();
        if (periodList != null && periodList.size() > 0) {
            result = Result.responseSuccess("查询成功", periodList);
        } else {
            result = Result.responseSuccess("无数据", periodList);
        }
        return result;
    }


    /**
     * @Description 批量删除周期
     * @Author Capejor
     * @Date 2019-05-16 14:02
     **/
    @RequestMapping("/deletePeriod")
    public Result deletePeriod(@RequestParam("ids[]") String ids[]) throws Exception {
        Result result;
        if (ids.length <= 0) {
            return Result.responseError("周期id不能为空！");
        }
        int deleteNum = periodService.deletePeriod(ids);
        if (deleteNum > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }


    /**
     * @Description 添加周期班次
     * @Author Capejor
     * @Date 2019-05-16 15:18
     **/
    @RequestMapping("/insertPeriodAttend")
    public Result insertPeriodAttend(@RequestParam String periodId, @RequestParam String attendId, @RequestParam int dayNum) throws Exception {
        Result result;
        if (StrKit.isBlank(periodId)) {
            return Result.responseError("周期id不能为空");
        }
        if (StrKit.isBlank(attendId)) {
            return Result.responseError("班次id不能为空");
        }
        if (StrKit.isBlank("" + dayNum)) {
            return Result.responseError("周期天数不能为空");
        }
        int insertNum = periodService.insertPeriodAttend(periodId, attendId, dayNum);
        if (insertNum > 0) {
            result = Result.responseSuccess("添加周期班次成功");
        } else {
            result = Result.responseError("添加周期班次失败");
        }
        return result;
    }


    /**
     * @Description 删除周期班次
     * @Author Capejor
     * @Date 2019-05-16 16:15
     **/
    @RequestMapping("/deletePeriodAttend")
    public Result deletePeriodAttend(@RequestParam String id,@RequestParam String periodId) throws Exception {
        Result result;
        int deleteNum = periodService.deletePeriodAttend(id,periodId);
        if (deleteNum > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }


    /**
     * @Description 查询周期班次
     * @Author Capejor
     * @Date 2019-05-16 19:09
     **/
    @RequestMapping("/selectPeriodAttend")
    public Result selectPeriodAttend(@RequestParam String periodId) throws Exception {
        Result result;
        List<Map<String, Object>> perAttList = periodService.selectPeriodAttend(periodId);
        if (perAttList != null && perAttList.size() > 0) {
            result = Result.responseSuccess("查询成功", perAttList);
        } else {
            result = Result.responseSuccess("无数据", perAttList);
        }
        return result;
    }


}
