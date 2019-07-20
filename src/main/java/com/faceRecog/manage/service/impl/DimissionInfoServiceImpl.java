package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.*;
import com.faceRecog.manage.model.*;
import com.faceRecog.manage.model.vo.DimissInfoVO;
import com.faceRecog.manage.service.DimissionInfoService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;


import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;


import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DimissionInfoServiceImpl implements DimissionInfoService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DimissionInfoMapper dimissionInfoMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private InstructionRecMapper instructionRecMapper;


    @Autowired
    private SpringWebSocketHandler websocket;

    @Autowired
    private EmpCardMapper empCardMapper;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private EmpAttendGroupMapper empAttendGroupMapper;
    
    @Autowired
    private AttendGroupLeaderMapper attendGroupLeaderMapper;

    /**
     * 员工离职
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result empToDimissById(DimissionInfo dimissionInfo, String[] empIds, String[] cardIds) throws Exception {
        Result result=null;
        int affectResult = 0;
        //将员工 isDimiss 置为 1
        for (int i = 0; i < empIds.length; i++) {
            //将员工 isDimiss 置为 1
            affectResult = dimissionInfoMapper.empToDimissById(empIds[i]);
            if (affectResult==0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.responseError("离职失败");
            }
            // 离职信息表添加一条数据
            dimissionInfo.setId(UUIDGenerator.getUUID());
            dimissionInfo.setCreateTime(new Date());
            dimissionInfo.setStatus("1");
            dimissionInfo.setCreateUserId("1");
            dimissionInfo.setEmpId(empIds[i]);
            affectResult = dimissionInfoMapper.insertSelective(dimissionInfo);
            if (affectResult < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.responseError("离职失败");
            }
            //退卡 将卡片表 inUse 置为 0
            affectResult = cardMapper.inUseToNotOccupy(cardIds[i]);
            if (affectResult < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.responseError("离职失败");
            }
            //员工-卡片 删除
            affectResult = empCardMapper.deleteByCardId(cardIds[i]);
            if (affectResult < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.responseError("离职失败");
            }
            // 删除员工所属考勤组数据
            affectResult=empAttendGroupMapper.deleteEmpAttendGroupByEmpId(empIds[i]);
            if (affectResult <0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.responseError("离职失败");
            }
            affectResult=attendGroupLeaderMapper.deleteAttendGroupLeaderByEmpId(empIds[i]);
            if (affectResult < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.responseError("离职失败");
            }
        }

        User user=(User) SecurityUtils.getSubject().getPrincipal();
        String usId="";
        if(user!=null){
        	usId=user.getId();
        }else{
        	usId="1";
        }
        //查询所有的设备
        List<Equipment> eqList=equipmentMapper.selectAllEquipmentByUsId(usId);
        if (eqList != null && eqList.size() > 0 && eqList.get(0)!=null) {
            for (Equipment eqMap : eqList) {
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn(eqMap.getSn());
                instructionRec.setInstruction(CommonConstant.UPDATE_FACE.getValue());
                affectResult = instructionRecMapper.insertSelective(instructionRec);
                if (affectResult < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("离职失败");
                }
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setInstruction(CommonConstant.UPDATE_ATTENDANCE.getValue());
                if (affectResult < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("离职失败");
                }
                affectResult = instructionRecMapper.insertSelective(instructionRec);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_FACE.getValue()));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_ATTENDANCE.getValue()));
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
            }
        }
        result=Result.responseSuccess("离职成功！");
        return result;
    }

    @Override
    public String selectDimissTimeByEmpId(String empId) throws Exception {
        return dimissionInfoMapper.selectDimissTimeByEmpId(empId);
    }


    /**
     * 员工复职
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result dimissToEmpById(String empId, String employTime,String cardId) throws Exception {
        Result result=null;
        int affectResult;
        //员工表 isDimiss 置为 1
        Date createTime = new Date();
        Map<String,Object> map = new HashMap<>();
        map.put("empId",empId);
        map.put("employTime",employTime);
        map.put("createTime",createTime);
        affectResult = dimissionInfoMapper.dimissToEmpById(map);
        if (affectResult == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.responseError("复职失败！");
        }
        // 离职信息表删除一条数据
        affectResult = dimissionInfoMapper.deleteByEmpId(empId);
        if (affectResult < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.responseError("复职失败！");
        }
        //绑卡 将卡片表 inUse 置为 1
        affectResult = cardMapper.inUseToOccupy(cardId);
        if (affectResult < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.responseError("复职失败！");
        }
        //员工-卡片 添加
        EmpCard empCard = new EmpCard();
        empCard.setId(UUIDGenerator.getUUID());
        empCard.setEmpId(empId);
        empCard.setCardId(cardId);
        affectResult = empCardMapper.insertSelective(empCard);
        if (affectResult == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.responseError("复职失败！");
        }
        User user=(User) SecurityUtils.getSubject().getPrincipal();
        String usId="";
        if(user!=null){
        	usId=user.getId();
        }else{
        	usId="1";
        }
        //查询所有的设备
        List<Equipment> eqList=equipmentMapper.selectAllEquipmentByUsId(usId);
        if (eqList != null && eqList.size() > 0 && eqList.get(0)!=null) {
            for (Equipment eqMap : eqList) {
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn(eqMap.getSn());
                instructionRec.setInstruction(CommonConstant.UPDATE_FACE.getValue());
                affectResult = instructionRecMapper.insertSelective(instructionRec);
                if (affectResult < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("离职失败");
                }
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setInstruction(CommonConstant.UPDATE_ATTENDANCE.getValue());
                if (affectResult < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("离职失败");
                }
                affectResult = instructionRecMapper.insertSelective(instructionRec);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_FACE.getValue()));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_ATTENDANCE.getValue()));
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
            }
        }
        result= Result.responseSuccess("复职成功！");
        return result;
    }


    /**
     * 指定部门复职
     *
     * @param empId
     * @param deptId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result dimissToEmpUpdateDept(String empId, String deptId, String cardId,String employTime) throws Exception {
        Result result=null;
        int affectResult;
        //员工表 isDimiss 置为 1
        Date createTime = new Date();
        Map<String,Object> map = new HashMap<>();
        map.put("empId",empId);
        map.put("deptId",deptId);
        map.put("employTime",employTime);
        map.put("createTime",createTime);
        affectResult = dimissionInfoMapper.dimissToEmpUpdateDept(map);
        if (affectResult == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.responseError("复职失败！");
        }
        // 离职信息表删除一条数据
        affectResult = dimissionInfoMapper.deleteByEmpId(empId);
        if (affectResult <0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.responseError("复职失败！");
        }
        //绑卡 将卡片表 inUse 置为 1
        affectResult = cardMapper.inUseToOccupy(cardId);
        if (affectResult < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.responseError("复职失败！");
        }
        //员工-卡片 添加
        EmpCard empCard = new EmpCard();
        empCard.setId(UUIDGenerator.getUUID());
        empCard.setEmpId(empId);
        empCard.setCardId(cardId);
        affectResult = empCardMapper.insertSelective(empCard);
        if (affectResult == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.responseError("复职失败！");
        }
        
        User user=(User) SecurityUtils.getSubject().getPrincipal();
        String usId="";
        if(user!=null){
        	usId=user.getId();
        }else{
        	usId="1";
        }
        //查询所有的设备
        List<Equipment> eqList=equipmentMapper.selectAllEquipmentByUsId(usId);
        if (eqList != null && eqList.size() > 0 && eqList.get(0)!=null) {
            for (Equipment eqMap : eqList) {
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn(eqMap.getSn());
                instructionRec.setInstruction(CommonConstant.UPDATE_FACE.getValue());
                affectResult = instructionRecMapper.insertSelective(instructionRec);
                if (affectResult < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("离职失败");
                }
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setInstruction(CommonConstant.UPDATE_ATTENDANCE.getValue());
                if (affectResult < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("离职失败");
                }
                affectResult = instructionRecMapper.insertSelective(instructionRec);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_FACE.getValue()));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_ATTENDANCE.getValue()));
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
            }
        }
        result= Result.responseSuccess("复职成功！");
        return result;
    }



    /**
     * 批量删除离职员工
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteEmployeeById(String[] empIds) throws Exception {
        int affectNum;
        affectNum = employeeMapper.deleteById(empIds);
        if (affectNum <1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //清空账户信息
        /*affectNum = accountMapper.deleteByEmpId(empIds);
        if (affectNum<0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }*/
        return affectNum;
    }

    @Override
    public List<DimissInfoVO> selectAllDimissInfo() throws Exception {
        return dimissionInfoMapper.selectAllDimissInfo();
    }

    @Override
    public List<DimissInfoVO> selectDimissByDeptId(String deptId) throws Exception {
        return dimissionInfoMapper.selectDimissByDeptId(deptId);
    }

    @Override
    public List<DimissInfoVO> selectAllByParams(String searchParam) throws Exception {
        return dimissionInfoMapper.selectAllByParams(searchParam);
    }



}
