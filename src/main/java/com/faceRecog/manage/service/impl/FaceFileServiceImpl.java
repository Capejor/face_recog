package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.EmployeeMapper;
import com.faceRecog.manage.mapper.EquipmentMapper;
import com.faceRecog.manage.mapper.FaceFileMapper;
import com.faceRecog.manage.mapper.InstructionRecMapper;
import com.faceRecog.manage.model.Equipment;
import com.faceRecog.manage.model.FaceFile;
import com.faceRecog.manage.model.InstructionRec;
import com.faceRecog.manage.model.User;
import com.faceRecog.manage.service.FaceFileService;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import net.sf.json.JSONObject;

import java.util.Date;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;

@Service
public class FaceFileServiceImpl implements FaceFileService {

    @Autowired
    private FaceFileMapper faceFileMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private SpringWebSocketHandler websocket;
    
    @Autowired
    private InstructionRecMapper instructionRecMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFaceFile(FaceFile faceFile) throws Exception {
        //判断该员工是否已经录入照片
        int photoNum = faceFileMapper.selectPhotoNum(faceFile.getEmpId());
        int faceRegNum = 0;
        //如果该员工已录入照片 删除已录入照片
        if (photoNum > 0) {
            int deleteNum = faceFileMapper.deleteByEmpId(faceFile.getEmpId());
            if (deleteNum > 0) {
                faceRegNum= faceFileMapper.insertSelective(faceFile);
            }else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
        } else {
            //如果该员工没有录入照片 直接添加
            int insertNum = faceFileMapper.insertSelective(faceFile);
            if (insertNum > 0) {
                //将员工faceReg字段修改为"1":"已注册"
                faceRegNum = employeeMapper.updateFaceReg(faceFile.getEmpId());
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
        }
        
        // 获取当前登入人员的id
        User user=(User) SecurityUtils.getSubject().getPrincipal();
        String usId="";
        if(user!=null){
        	usId=user.getId();
        }else{
        	usId="1";
        }
        List<Equipment> eqList=equipmentMapper.selectAllEquipmentByUsId(usId);
        if(eqList!=null && eqList.size()>0 && eqList.get(0)!=null){
        	for(Equipment equipment: eqList){
        		// 新增指令发送记录 发送socket消息给设备端
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn(equipment.getSn());
                instructionRec.setInstruction(CommonConstant.UPDATE_FACE.getValue());
                faceRegNum = instructionRecMapper.insertSelective(instructionRec);
                if (faceRegNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_FACE.getValue()));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));
        	}
        }
        
        return faceRegNum;
    }
}
