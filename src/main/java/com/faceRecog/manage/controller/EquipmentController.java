package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Equipment;
import com.faceRecog.manage.model.User;
import com.faceRecog.manage.service.EquipmentService;
import com.faceRecog.manage.service.InstructionRecService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentController
 * @Description: 设备模块 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class EquipmentController {


    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private InstructionRecService instructionRecService;


    /**
     * @Description 添加设备
     * @Author Capejor
     * @Date 2019-05-25 14:04
     **/
    @RequestMapping("/insertEquipment")
    public Result insertEquipment(Equipment equipment) throws Exception {
        if (StrKit.isBlank(equipment.getSn())) {
            return Result.responseError("设备编号不能为空！");
        } else if (StrKit.isBlank(equipment.getEqName())) {
            return Result.responseError("设备名称不能为空！");
        } else if (StrKit.isBlank(equipment.getType())) {
            return Result.responseError("设备类型不能为空！");
        } else if (StrKit.isBlank(equipment.getDirection())) {
            return Result.responseError("方向不能为空！");
        }
        Equipment eqRec = equipmentService.selectEquipmentBySn(equipment.getSn());
        if (eqRec != null) {
            Result result = Result.responseError("设备已注册！");
            result.setCode(2);
            return result;
        }
        // 判断昵称是否占用
        int count = equipmentService.selectCountByEqName(equipment.getEqName());
        if (count > 0) {
            return Result.responseError("该设备名称被占用！");
        }
        equipment.setCreateTime(new Date());
        equipment.setId(UUIDGenerator.getUUID());
        equipment.setStatus("1");//1为正常状态
        equipment.setCreaterId("1");
        equipment.setSoftVersion("0");
        int affectNum = equipmentService.insertEquipment(equipment);
        if (affectNum < 0) {
            return Result.responseError("注册失败！");
        }
        return Result.responseSuccess("注册成功！");
    }


    /**
     * 修改设备
     **/
    /*@RequestMapping("/updateEqName")
    public Result updateEqName(Equipment equipment) throws Exception {
        Result result;
        if (StrKit.isBlank(equipment.getEqName())) {
            return Result.responseError("设备昵称不能为空！");
        } else if (StrKit.isBlank(equipment.getId())) {
            return Result.responseError("设备id不能为空！");
        }
        // 判断昵称是否占用
        int count = equipmentService.selectEqNickNameExistByPrimaryKey(equipment.getId(), equipment.getEqName());
        if (count > 0) {
            return Result.responseError("该设备名称被占用！");
        }
        equipment.setUpdateTime(new Date());
        int insertResult = equipmentService.updateEquipment(equipment);
        if (insertResult > 0) {
            result = Result.responseSuccess("修改成功");
        } else {
            result = Result.responseError("修改失败");
        }
        return result;
    }*/


    /**
     * @Description 修改设备
     * @Author Capejor
     * @Date 2019-05-25 14:32
     **/
    @RequestMapping("/updateEquipment")
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
        int insertResult = equipmentService.updateEquipment(equipment);
        if (insertResult > 0) {
            result = Result.responseSuccess("修改成功");
        } else {
            result = Result.responseError("修改失败");
        }
        return result;
    }

    /**
     * 查询所有设备
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/selectEquipment")
    public Result selectEquipment() throws Exception {
        Result result;
        List<Map<String, Object>> equipList = equipmentService.selectEquipment();
        if (equipList != null && equipList.size() > 0) {
            result = Result.responseSuccess("查询成功", equipList);
        } else {
            result = Result.responseSuccess("无数据", equipList);
        }
        return result;
    }

    /**
     * 批量删除设备
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteEquipment")
    public Result deleteEquipment(@RequestParam("ids[]") String[] ids, String password) throws Exception {
        if (StrKit.isBlank(password)) {
            return Result.responseError("密码不能为空！");
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return equipmentService.deleteEquipment(user, ids, password);
    }

    /**
     * @param instruction
     * @param sn
     * @return Result
     * @Title: remoteSocket
     * @Description: 远程操控设备
     * @author xya
     * @date 2019年5月9日下午3:11:57
     */
    @RequestMapping("/remoteSocket")
    public Result remoteSocket(String instruction, String sn, String password) throws Exception {
        if (StrKit.isBlank(instruction)) {
            return Result.responseError("指令不能为空！");
        } else if (StrKit.isBlank(sn)) {
            return Result.responseError("sn号不能为空！");
        }
        String[] sns = sn.split(",");
        if (sns.length < 1) {
            return Result.responseError("sn号不能为空！");
        }
        /*if (affectNum < 0) {
            return Result.responseError("发送失败！");
        }
        result = Result.responseSuccess("发送成功！");*/
        return instructionRecService.insertInstructionRec(instruction, sns, password);
    }
}
