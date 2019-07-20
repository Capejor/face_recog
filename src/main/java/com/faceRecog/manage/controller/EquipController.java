package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Equip;
import com.faceRecog.manage.service.EquipService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author Capejor
 * @className: EquipController
 * @Description: TODO
 * @date 2019-07-19 16:10
 */
@RestController
public class EquipController {

    @Autowired
    private EquipService equipService;

    /**
     * @Description 设备通讯 设备注册
     * @Author Capejor
     * @Date 2019-07-19 16:18
     **/
    @RequestMapping("/insertEquip")
    public Result insertEquip(Equip equip) throws Exception{
        Result result;
        if (StrKit.isBlank(equip.getEquipName())){
            return Result.responseError("设备名称不能为空");
        }
        if (StrKit.isBlank(equip.getSn())){
            return Result.responseError("设备编号不能为空");
        }
        if (StrKit.isBlank(equip.getType())){
            return Result.responseError("设备类型不能为空");
        }
        if (StrKit.isBlank(equip.getSoftVersion())){
            return Result.responseError("软件版本不能为空");
        }
        equip.setId(UUIDGenerator.getUUID());
        equip.setPassword("000000");
        equip.setRoomNum("0000");
        equip.setCreateUserId("1");
        equip.setStatus("1");
        equip.setCreateTime(new Date());
        int affectNum = equipService.insertEquip(equip);
        if (affectNum >0){
            result = Result.responseSuccess("设备注册成功");
        }else {
            result = Result.responseError("设注册失败");
        }
        return result;
    }



    /**
     * @Description 绑定设备
     * @Author Capejor
     * @Date 2019-07-19 17:07
     **/
    @RequestMapping("/bindEquipment")
    public Result  bindEquipment(String sn,String bindSn) throws Exception{
        Result result;
        int affectNum = equipService.bindEquipment(sn,bindSn);
        if (affectNum >0){
            result = Result.responseSuccess("绑定成功");
        }else {
            result = Result.responseError("绑定失败");
        }
        return result;
    }

    /**
     * @Description 修改房间号
     * @Author Capejor
     * @Date 2019-07-19 18:57
     **/
    @RequestMapping("/updateRoomNum")
    public Result  updateRoomNum(String sn,String roomNum) throws Exception{
        Result result;
        int affectNum = equipService.updateRoomNum(sn,roomNum);
        if (affectNum >0){
            result = Result.responseSuccess("修改成功");
        }else {
            result = Result.responseError("修改失败");
        }
        return result;
    }

    /**
     * @Description TODO 
     * @Author Capejor
     * @Date 2019-07-19 18:46
     **/
    @RequestMapping("/selectEquipByRoomNum")
    public Result selectEquipByRoomNum(String roomNum)throws Exception{
        Result result;
        List<Equip> equipList = equipService.selectEquipment(roomNum);
        if (equipList != null && equipList.size()>0){
            result = Result.responseSuccess("查询成功",equipList);
        }else {
            result = Result.responseSuccess("无数据",equipList);
        }
        return result;
    }

}
