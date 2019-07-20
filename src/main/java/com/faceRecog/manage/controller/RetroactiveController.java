package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.EmpRetroact;
import com.faceRecog.manage.model.Retroactive;
import com.faceRecog.manage.model.vo.EmpRetVO;
import com.faceRecog.manage.service.RetroactiveService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @ClassName: RetroactiveController
 * @Description: 补签卡 前端控制器
 * @author: Capejor
 * @date: 2019年5月8日
 */
@RestController
public class RetroactiveController {

    @Autowired
    private RetroactiveService retroactiveService;

    /**
     * 添加补签卡
     *
     * @param retroactive
     * @param empRetroact
     * @return
     * @throws Exception
     */
    @RequestMapping("/insertRetroactive")
    public Result insertRetroactive(Retroactive retroactive, EmpRetroact empRetroact,String attendType,String signType) throws Exception {
        Result result;
        retroactive.setId(UUIDGenerator.getUUID());
        retroactive.setCreateTime(new Date());
        retroactive.setStatus("1");
        retroactive.setCreateUserId("1");
        if (StrKit.isBlank(retroactive.getRetroactTime())) {
            return Result.responseError("补签时间不能为空");
        }
        if (StrKit.isBlank(retroactive.getType())) {
            return Result.responseError("补签类型不能为空");
        }
        if (StrKit.isBlank(retroactive.getReason())) {
            return Result.responseError("补签原因不能为空");
        }
        //中间表
        empRetroact.setId(UUIDGenerator.getUUID());
        empRetroact.setRetroactId(retroactive.getId());
        retroactive.setRetroactTime(retroactive.getRetroactTime().replaceAll("/", "-"));
        int insertResult = retroactiveService.insertRetroactive(retroactive, empRetroact,attendType,signType);
        if (insertResult > 0) {
            result = Result.responseSuccess("补签卡添加成功");
        } else {
            result = Result.responseError("补签卡添加失败");
        }
        return result;
    }


    /**
     * @Description 根据部门id查询补签卡
     * @Author Capejor
     * @Date 2019-06-18 8:43
     **/
    @RequestMapping("/selectRetroactiveByDeptId")
    public Result selectRetroactiveByDeptId(@RequestParam String deptId) throws Exception {
        Result result;
        List<EmpRetVO> retList;
        if ("-1".equals(deptId)){
            retList = retroactiveService.selectAllRetroactive();
        }else {
            retList = retroactiveService.selectRetroactiveByDeptId(deptId);
        }
        if (retList != null && retList.size() > 0) {
            result = Result.responseSuccess("查询成功", retList);
        } else {
            result = Result.responseSuccess("无数据", retList);
        }
        return result;
    }

    /**
     * @Description 模糊搜索全公司员工补签卡
     * @Author Capejor
     * @Date 2019-06-18 8:54
     **/
    @RequestMapping("/selectRetroactiveByParams")
    public Result selectRetroactiveByParams(@RequestParam(required = false) String empName, @RequestParam(required = false) String startTime,
                                    @RequestParam(required = false) String endTime) throws Exception {
        Result result;
        Map<String, Object> map = new HashMap<>();
        map.put("empName", empName);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        List<EmpRetVO> retList = new ArrayList<>();
        if (StrKit.isBlank(empName)){
            return Result.responseSuccess("无数据", retList);
        }else {
            retList = retroactiveService.selectByParams(map);
        }
        if (retList != null && retList.size() > 0) {
            result = Result.responseSuccess("查询成功", retList);
        } else {
            result = Result.responseSuccess("无数据", retList);
        }
        return result;
    }


    /**
     * 批量删除补签卡
     *
     * @param retIds
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteRetroactive")
    public Result deleteRetroactive(@RequestParam("retIds[]") String[] retIds) throws Exception {
        Result result;
        int deleteResult = retroactiveService.deleteRetroactive(retIds);
        if (deleteResult > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }


}
