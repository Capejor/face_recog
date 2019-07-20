package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.BasicParam;
import com.faceRecog.manage.service.BasicParamService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * @ClassName: BasicParamController
 * @Description: 设备基础参数 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class BasicParamController {

    @Autowired
    private BasicParamService basicParamService;


    /**
     * 添加设备基本参数
     * @param basicParam
     * @return
     * @throws Exception
     */
    @RequestMapping("/insertOrUpdateBasicParam")
    public Result insertOrUpdateBasicParam(BasicParam basicParam) throws Exception{
        Result result;
        int insertResult=0;
        
        if(StrKit.isBlank(basicParam.getEqId())){
        	return Result.responseError("设备id不能为空！");
        }else if(StrKit.isBlank(basicParam.getRecDistance())){
        	return Result.responseError("识别距离不能为空！");
        }else if(StrKit.isBlank(basicParam.getSerialPort())){
        	return Result.responseError("串口不能为空！");
        }else if(StrKit.isBlank(basicParam.getWigginsOutput())){
        	return Result.responseError("韦根输出不能为空！");
        }else if(StrKit.isBlank(basicParam.getSpeechPattern())){
        	return Result.responseError("语音模式不能为空！");
        }else if(StrKit.isBlank(basicParam.getDisplayPattern())){
        	return Result.responseError("显示模式不能为空！");
        }else if(StrKit.isBlank(basicParam.getFaceRec())){
        	return Result.responseError("多人脸识别模式不能为空！");
        }else if(StrKit.isBlank(basicParam.getStrangerSwitch())){
        	return Result.responseError("陌生人开关不能为空！");
        }else if(StrKit.isBlank(basicParam.getStrangerSwitch())){
        	return Result.responseError("陌生人开关不能为空！");
        }else if(StrKit.isBlank(basicParam.getStrangerSpePat())){
        	return Result.responseError("陌生人语音模式不能为空！");
        }else if(StrKit.isBlank(basicParam.getPhotoSecLevel())){
        	return Result.responseError("照片防伪等级不能为空！");
        }
        if(StrKit.isBlank(basicParam.getId())){
        	 basicParam.setId(UUIDGenerator.getUUID());
             basicParam.setCreateTime(new Date());
             basicParam.setCreateUserId("1");
             basicParam.setStatus("1");
             insertResult = basicParamService.insertBasicParam(basicParam);
        }else{
        	  basicParam.setUpdateTime(new Date());
              insertResult = basicParamService.updateBasicParam(basicParam);
        }
       
        if (insertResult >0){
            result = Result.responseSuccess("添加成功");
        }else {
            result = Result.responseError("添加失败");
        }
        return result;
    }

   

    /**
     * 根据 equipId 查询设备基本参数
     * @return
     * @throws Exception
     */
    @RequestMapping("/selectBasicByEquipId")
    public Result selectBasicParam(String eqId) throws Exception{
        Result result;
        if(StrKit.isBlank(eqId)){
        	return Result.responseError("设备id不能为空!");
        }
        Map<String, Object> basicParamMap= basicParamService.selectBasicByEquipId(eqId);
        if (StrKit.notNull(basicParamMap)){
            result = Result.responseSuccess("查询成功",basicParamMap);
        }else {
            result = Result.responseSuccess("无数据");
        }
        return result;
    }


}
