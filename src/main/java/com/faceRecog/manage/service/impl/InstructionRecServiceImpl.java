/**
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 *
 * @Package: com.faceRecog.manage.service.impl
 * @author: Administrator
 * @date: 2019年5月9日 下午3:19:14
 */
package com.faceRecog.manage.service.impl;

import java.util.Date;

import com.faceRecog.manage.model.User;
import com.faceRecog.manage.util.PasswordHelper;
import com.faceRecog.manage.util.StrKit;
import org.apache.shiro.SecurityUtils;
import org.aspectj.apache.bcel.generic.Instruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.InstructionRecMapper;
import com.faceRecog.manage.model.InstructionRec;
import com.faceRecog.manage.service.InstructionRecService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import net.sf.json.JSONObject;

/**
 * @ClassName: InstructionServiceImpl
 * @Description: 指令发送记录
 * @author: xya
 * @date: 2019年5月9日 下午3:19:14  
 */
@Service
public class InstructionRecServiceImpl implements InstructionRecService {

    @Autowired
    private InstructionRecMapper instructionRecMapper;

    @Autowired
    public SpringWebSocketHandler websocket;

    /* (non Javadoc)
     * @Title: insertInstructionRec
     * @Description: TODO
     * @param instructionRec
     * @return
     * @throws Exception
     * @see com.faceRecog.manage.service.InstructionRecService#insertInstructionRec(com.faceRecog.manage.model.InstructionRec)
     */
	/*@Override
	@Transactional
	public int insertInstructionRec(String instruction ,String []sns) throws Exception {
		int affectNum=0;
		//先清空发送记录
		affectNum=instructionRecMapper.deleteInstructionRec();
		if(affectNum<0){
			return -1;
		}
		for(String sn:sns){
			InstructionRec instructionRec=new InstructionRec();
			instructionRec.setId(UUIDGenerator.getUUID());
			instructionRec.setCreateTime(new Date());
			instructionRec.setStatus("-1");
			instructionRec.setSn(sn);
			instructionRec.setInstruction(instruction);
			affectNum =instructionRecMapper.insertSelective(instructionRec);
			if(affectNum<0){
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return -1;
			}
			JSONObject jsonObj=new JSONObject();
			jsonObj.put("type",Integer.valueOf(CommonConstant.TYPE.getValue()));
			jsonObj.put("code", Integer.valueOf(instruction));
			jsonObj.put("id",instructionRec.getId());
			jsonObj.put("sendTime", new Date().getTime());
			//发送指令
			websocket.sendMessageToUser(sn, new TextMessage(jsonObj.toString()));
		}
		return 1;
	}*/
    @Override
    @Transactional
    public Result insertInstructionRec(String instruction, String[] sns, String password) throws Exception {
        int affectNum;
        if ("5002".equals(instruction)) {
            if (StrKit.isBlank(password)) {
                return Result.responseError("密码不能为空！");
            }
            // 原始密码加密
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            PasswordHelper passwordHelper = new PasswordHelper();
            String oldPassword_md5 = passwordHelper.encryptPassword(password, user.getSalt());
            //判断原密码是否输入正确
            if (oldPassword_md5.equals(user.getPwd())) {
                //先清空发送记录
                affectNum = instructionRecMapper.deleteInstructionRec();
                if (affectNum < 0) {
                    return Result.responseError("清空失败");
                }
                for (String sn : sns) {
                    InstructionRec instructionRec = new InstructionRec();
                    instructionRec.setId(UUIDGenerator.getUUID());
                    instructionRec.setCreateTime(new Date());
                    instructionRec.setStatus("-1");
                    instructionRec.setSn(sn);
                    instructionRec.setInstruction(instruction);
                    affectNum = instructionRecMapper.insertSelective(instructionRec);
                    if (affectNum < 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return Result.responseError("添加失败");
                    }
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                    jsonObj.put("code", Integer.valueOf(instruction));
                    jsonObj.put("id", instructionRec.getId());
                    jsonObj.put("sendTime", new Date().getTime());
                    //发送指令
                    websocket.sendMessageToUser(sn, new TextMessage(jsonObj.toString()));
                }
                return Result.responseSuccess("发送成功");
            } else {
                return Result.responseError("密码输入错误！");
            }
        } else {
            //先清空发送记录
            affectNum = instructionRecMapper.deleteInstructionRec();
            if (affectNum < 0) {
                return Result.responseError("清空失败");
            }
            for (String sn : sns) {
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn(sn);
                instructionRec.setInstruction(instruction);
                affectNum = instructionRecMapper.insertSelective(instructionRec);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("添加失败");
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(instruction));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(sn, new TextMessage(jsonObj.toString()));
            }
            return Result.responseSuccess("发送成功");
        }

    }

    /* (non Javadoc)
     * @Title: updateInstructionRec
     * @Description: TODO
     * @param id
     * @return
     * @throws Exception
     * @see com.faceRecog.manage.service.InstructionRecService#updateInstructionRec(java.lang.String)
     */
    @Override
    public int updateInstructionRecStatus(String id) throws Exception {

        return instructionRecMapper.updateInstructionRecStatus(id);
    }


}
