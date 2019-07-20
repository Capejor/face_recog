package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.EquipmentMapper;
import com.faceRecog.manage.mapper.InstructionRecMapper;
import com.faceRecog.manage.model.Equipment;
import com.faceRecog.manage.model.InstructionRec;
import com.faceRecog.manage.model.ReadMode;
import com.faceRecog.manage.model.User;
import com.faceRecog.manage.service.EquipmentService;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.PasswordHelper;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;
    
    @Autowired
    private SpringWebSocketHandler websocket;
    
    @Autowired
    private InstructionRecMapper instructionRecMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertEquipment(Equipment equipment) throws Exception {
    	int affectNum=0;
    	
    	// 新增指令发送记录 发送socket消息给设备端
        InstructionRec instructionRec = new InstructionRec();
        instructionRec.setId(UUIDGenerator.getUUID());
        instructionRec.setCreateTime(new Date());
        instructionRec.setStatus("-1");
        instructionRec.setSn(equipment.getSn());
        instructionRec.setInstruction(CommonConstant.UPDATE_FACE.getValue());
        affectNum = instructionRecMapper.insertSelective(instructionRec);
        if (affectNum < 0) {
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
        if(affectNum<0){
        	 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return equipmentMapper.insertSelective(equipment);
    }

    @Override
    public int updateEquipment(Equipment equipment) throws Exception {
    	int affectNum=0;
    	
    	// 新增指令发送记录 发送socket消息给设备端
        InstructionRec instructionRec = new InstructionRec();
        instructionRec.setId(UUIDGenerator.getUUID());
        instructionRec.setCreateTime(new Date());
        instructionRec.setStatus("-1");
        instructionRec.setSn(equipment.getSn());
        instructionRec.setInstruction(CommonConstant.UPDATE_EQUIPMENT.getValue());
        affectNum = instructionRecMapper.insertSelective(instructionRec);
        if (affectNum < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
        jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_EQUIPMENT.getValue()));
        jsonObj.put("id", instructionRec.getId());
        jsonObj.put("sendTime", new Date().getTime());
        //发送指令
        websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));
        if(affectNum<0){
        	 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return equipmentMapper.updateByPrimaryKeySelective(equipment);
    }

    @Override
    public  List<Map<String, Object>> selectEquipment() throws Exception {
        return equipmentMapper.selectEquipment();
    }

    @Override
    public Result deleteEquipment(User user, String[] ids, String password) throws Exception {
		int affectNum;
		Result result;
		// 原始密码加密
		PasswordHelper passwordHelper = new PasswordHelper();
		String oldPassword_md5 = passwordHelper.encryptPassword(password, user.getSalt());
		//判断原密码是否输入正确
		if (oldPassword_md5.equals(user.getPwd())) {
			affectNum =  equipmentMapper.deleteEquipment(ids);
			if (affectNum > 0) {
				result = Result.responseSuccess("设备删除成功！");
			} else {
				result = Result.responseError("设备删除失败！");
			}
		} else {
			result = Result.responseError("密码输入错误！");
		}
		return result;


    }

	/* (non Javadoc) 
	 * @Title: selectEquipmentBySn
	 * @Description: TODO
	 * @param sn
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.EquipmentService#selectEquipmentBySn(java.lang.String) 
	 */ 
	@Override
	public Equipment selectEquipmentBySn(String sn) throws Exception {
		 
		return equipmentMapper.selectEquipmentBySn(sn);
	}

	/* (non Javadoc) 
	 * @Title: selectEquipmentByPrimaryKey
	 * @Description: TODO
	 * @param id
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.EquipmentService#selectEquipmentByPrimaryKey(java.lang.String) 
	 */ 
	@Override
	public Equipment selectEquipmentByPrimaryKey(String id) throws Exception {
		 
		return equipmentMapper.selectByPrimaryKey(id);
	}

	/* (non Javadoc) 
	 * @Title: selectEqNickNameExistByPrimaryKey
	 * @Description: TODO
	 * @param id
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.EquipmentService#selectEqNickNameExistByPrimaryKey(java.lang.String) 
	 */ 
	@Override
	public int selectEqNickNameExistByPrimaryKey(String id,String eqName) throws Exception {
		 
		return equipmentMapper.selectEqNickNameExistByPrimaryKey(id, eqName);
	}

	/* (non Javadoc) 
	 * @Title: selectEqNickNameExistBySn
	 * @Description: TODO
	 * @param sn
	 * @param eqName
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.EquipmentService#selectEqNickNameExistBySn(java.lang.String, java.lang.String) 
	 */ 
	@Override
	public int selectEqNickNameExistBySn(String sn, String eqName) throws Exception {
		 
		return equipmentMapper.selectEqNickNameExistBySn(sn, eqName);
	}

	/* (non Javadoc) 
	 * @Title: updateEquipmentInfoBySn
	 * @Description: TODO
	 * @param equipment
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.EquipmentService#updateEquipmentInfoBySn(com.faceRecog.manage.model.Equipment) 
	 */ 
	@Override
	public int updateEquipmentInfoBySn(Equipment equipment) throws Exception {
		 
		return equipmentMapper.updateEquipmentInfoBySn(equipment);
	}

	/**
	 * @Description 判断设备名称是否占用
	 * @Author Capejor
	 * @Date 2019-05-25 14:10
	 **/
	@Override
	public int selectCountByEqName(String eqName) throws Exception {
		return equipmentMapper.selectCountByEqName(eqName);
	}

	/**
	 * @Description 判断设备是否被注册
	 * @Author Capejor
	 * @Date 2019-05-25 15:23
	 **/
	@Override
	public int judgeEquipmentRegister(String sn) throws Exception {
		return equipmentMapper.judgeEquipmentRegister(sn);
	}

	/* (non Javadoc) 
	 * @Title: selectAllOnLineEquipmentInfo
	 * @Description: TODO
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.EquipmentService#selectAllOnLineEquipmentInfo() 
	 */ 
	@Override
	public List<Map<String, Object>> selectAllOnLineEquipmentInfo() throws Exception {
		 
		return equipmentMapper.selectAllOnLineEquipmentInfo();
	}

	@Override
	public String selectEquipIdBySn(String sn) throws Exception {
		return equipmentMapper.selectEquipIdBySn(sn);
	}

}
