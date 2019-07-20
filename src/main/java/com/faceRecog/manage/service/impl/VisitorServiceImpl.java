package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.EquipmentMapper;
import com.faceRecog.manage.mapper.InstructionRecMapper;
import com.faceRecog.manage.mapper.VisitAuthMapper;
import com.faceRecog.manage.mapper.VisitEquipMapper;
import com.faceRecog.manage.mapper.VisitorMapper;
import com.faceRecog.manage.model.Equipment;
import com.faceRecog.manage.model.InstructionRec;
import com.faceRecog.manage.model.VisitAuth;
import com.faceRecog.manage.model.VisitEquip;
import com.faceRecog.manage.model.Visitor;

import com.faceRecog.manage.model.serverVO.VisitServer;
import com.faceRecog.manage.model.vo.VisitAuthVO;
import com.faceRecog.manage.model.vo.VisitVO;
import com.faceRecog.manage.service.VisitorService;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private VisitAuthMapper visitAuthMapper;

    @Autowired
    private VisitEquipMapper visitEquipMapper;

    @Autowired
    private SpringWebSocketHandler websocket;

    @Autowired
    private InstructionRecMapper instructionRecMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public int insertAppointVisitor(Visitor visitor) throws Exception {
        return visitorMapper.insertSelective(visitor);
    }


    /**
     * @Description 判断是否已登记或已预约 当天
     * @Author Capejor
     * @Date 2019-06-19 11:14
     **/
    @Override
    public int selectByPhoneAndVisitTime(String phone,String visitTime) throws Exception {
        return visitorMapper.selectByPhoneAndVisitTime(phone, visitTime);
    }

    /**
     * @Description 判断是否已登记或已预约 除去自己
     * @Author Capejor
     * @Date 2019-06-19 11:16
     **/
    @Override
    public int selectByPhoneAndAppointTimeExceptOwn(String id, String phone, String appointTime) throws Exception {
        return visitorMapper.selectByPhoneAndAppointTimeExceptOwn(id, phone, appointTime);
    }

    @Override
    public int selectByVisitId(String visitId) throws Exception {
        return visitAuthMapper.selectByVisitId(visitId);
    }

    /**
     * @Description 添加访客
     * @Author Capejor
     * @Date 2019-06-15 15:25
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertVisitor(Visitor visitor, VisitAuthVO visitAuthVO) throws Exception {
        int affectNum;
        //添加访客
        affectNum = visitorMapper.insertSelective(visitor);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        for (int i = 0; i < visitAuthVO.getEquipId().size(); i++) {
            //访客权限
            VisitAuth visitAuth = new VisitAuth();
            visitAuth.setId(UUIDGenerator.getUUID());
            visitAuth.setVisitId(visitor.getId());
            visitAuth.setStartTime(visitAuthVO.getStartTime().get(i));
            visitAuth.setEndTime(visitAuthVO.getEndTime().get(i));
            visitAuth.setCreateTime(new Date());
            visitAuth.setStatus("1");
            affectNum = visitAuthMapper.insertSelective(visitAuth);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            //访客-设备 中间表
            VisitEquip visitEquip = new VisitEquip();
            visitEquip.setId(UUIDGenerator.getUUID());
            visitEquip.setVisitId(visitor.getId());
            visitEquip.setEquipId(visitAuthVO.getEquipId().get(i));
            visitEquip.setVisitAuthId(visitAuth.getId());
            affectNum = visitEquipMapper.insertSelective(visitEquip);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            // 查询设备信息 保存指令发送记录 发送更新访客信息指令到设备
            Equipment equipment = equipmentMapper.selectByPrimaryKey(visitAuthVO.getEquipId().get(i));
            if (equipment == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            // 新增指令发送记录 发送socket消息给设备端
            InstructionRec instructionRec = new InstructionRec();
            instructionRec.setId(UUIDGenerator.getUUID());
            instructionRec.setCreateTime(new Date());
            instructionRec.setStatus("-1");
            instructionRec.setSn(equipment.getSn());
            instructionRec.setInstruction(CommonConstant.UPDATE_VISITOR.getValue());
            affectNum = instructionRecMapper.insertSelective(instructionRec);
            if (affectNum < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
            jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_VISITOR.getValue()));
            jsonObj.put("id", instructionRec.getId());
            jsonObj.put("sendTime", new Date().getTime());
            //发送指令
            websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));
        }

        return affectNum;


    }

    /**
     * @Description 预约访客添加门禁权限
     * @Author Capejor
     * @Date 2019-06-15 16:11
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateVisitorAndAuth(Visitor visitor, VisitAuthVO visitAuthVO) throws Exception {
       /* String visitId = visitorMapper.selectIdByPhone(visitor.getPhone());
        visitor.setId(visitId);*/
        //修改访客
        int affectNum = visitorMapper.updateByPrimaryKeySelective(visitor);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        for (int i = 0; i < visitAuthVO.getEquipId().size(); i++) {
            //访客权限
            VisitAuth visitAuth = new VisitAuth();
            visitAuth.setId(UUIDGenerator.getUUID());
            visitAuth.setVisitId(visitor.getId());
            visitAuth.setStartTime(visitAuthVO.getStartTime().get(i));
            visitAuth.setEndTime(visitAuthVO.getEndTime().get(i));
            visitAuth.setCreateTime(new Date());
            visitAuth.setStatus("1");
            affectNum = visitAuthMapper.insertSelective(visitAuth);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            //访客-设备 中间表
            VisitEquip visitEquip = new VisitEquip();
            visitEquip.setId(UUIDGenerator.getUUID());
            visitEquip.setVisitId(visitor.getId());
            visitEquip.setEquipId(visitAuthVO.getEquipId().get(i));
            visitEquip.setVisitAuthId(visitAuth.getId());
            affectNum = visitEquipMapper.insertSelective(visitEquip);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            // 查询设备信息 保存指令发送记录 发送更新访客信息指令到设备
            Equipment equipment = equipmentMapper.selectByPrimaryKey(visitAuthVO.getEquipId().get(i));
            if (equipment == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            // 新增指令发送记录 发送socket消息给设备端
            InstructionRec instructionRec = new InstructionRec();
            instructionRec.setId(UUIDGenerator.getUUID());
            instructionRec.setCreateTime(new Date());
            instructionRec.setStatus("-1");
            instructionRec.setSn(equipment.getSn());
            instructionRec.setInstruction(CommonConstant.UPDATE_VISITOR.getValue());
            affectNum = instructionRecMapper.insertSelective(instructionRec);
            if (affectNum < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
            jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_VISITOR.getValue()));
            jsonObj.put("id", instructionRec.getId());
            jsonObj.put("sendTime", new Date().getTime());
            //发送指令
            websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));

        }
        return affectNum;
    }

    /**
     * @Description 修改访客
     * @Author Capejor
     * @Date 2019-06-15 15:25
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateVisitor(Visitor visitor, VisitAuthVO visitAuthVO) throws Exception {
       /* String visitId = visitorMapper.selectIdByPhone(visitor.getPhone());
        visitor.setId(visitId);*/
        int affectNum = visitorMapper.updateByPrimaryKeySelective(visitor);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //删除访客权限
        affectNum = visitAuthMapper.deleteVisitAuthByVisitId(visitor.getId());
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //删除访客设备中间表数据
        affectNum = visitEquipMapper.deleteVisitEquipByVisitId(visitor.getId());
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        for (int i = 0; i < visitAuthVO.getEquipId().size(); i++) {
            //访客权限
            VisitAuth visitAuth = new VisitAuth();
            visitAuth.setId(UUIDGenerator.getUUID());
            visitAuth.setVisitId(visitor.getId());
            visitAuth.setStartTime(visitAuthVO.getStartTime().get(i));
            visitAuth.setEndTime(visitAuthVO.getEndTime().get(i));
            visitAuth.setCreateTime(new Date());
            visitAuth.setStatus("1");
            affectNum = visitAuthMapper.insertSelective(visitAuth);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            //访客-设备 中间表
            VisitEquip visitEquip = new VisitEquip();
            visitEquip.setId(UUIDGenerator.getUUID());
            visitEquip.setVisitId(visitor.getId());
            visitEquip.setEquipId(visitAuthVO.getEquipId().get(i));
            visitEquip.setVisitAuthId(visitAuth.getId());
            affectNum = visitEquipMapper.insertSelective(visitEquip);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            // 查询设备信息 保存指令发送记录 发送更新访客信息指令到设备
            Equipment equipment = equipmentMapper.selectByPrimaryKey(visitAuthVO.getEquipId().get(i));
            if (equipment == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            // 新增指令发送记录 发送socket消息给设备端
            InstructionRec instructionRec = new InstructionRec();
            instructionRec.setId(UUIDGenerator.getUUID());
            instructionRec.setCreateTime(new Date());
            instructionRec.setStatus("-1");
            instructionRec.setSn(equipment.getSn());
            instructionRec.setInstruction(CommonConstant.UPDATE_VISITOR.getValue());
            affectNum = instructionRecMapper.insertSelective(instructionRec);
            if (affectNum < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
            jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_VISITOR.getValue()));
            jsonObj.put("id", instructionRec.getId());
            jsonObj.put("sendTime", new Date().getTime());
            //发送指令
            websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));

        }
        return affectNum;
    }


    @Override
    public List<VisitVO> selectVisitor(String phone) throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String persent = sdf.format(date);
        List<VisitVO> visitVOS = visitorMapper.selectVisitor(phone);
        for (VisitVO visitVO : visitVOS) {
            if (visitVO.getAppointTime().compareTo(persent) < 0) {
                visitorMapper.updateApproveStatus(visitVO.getId(), "3");
            }
        }
        return visitorMapper.selectVisitor(phone);
    }


   /* @Override
    public List<VisitVO> selectVisitor(String phone) throws Exception {
        List<VisitVO> visitVOList = visitorMapper.selectVisitor(phone);
        List<Map<String,Object>> visitList = new ArrayList<>();

        Set<String> idSet = new HashSet<>();
        for (VisitVO visitVO :visitVOList){
            String idStr = visitVO.getId();
            idSet.add(idStr);
        }
        for (String visitId:idSet){
            Map visitMap = new HashMap();
            List equipIdList = new ArrayList();
            List equipNameList = new ArrayList();
            List startTimeList = new ArrayList();
            List endTimeList = new ArrayList();
            for (VisitVO visitVO :visitVOList){
                String idStr = visitVO.getId();
                if (idStr.equals(visitId)) {
                    equipIdList.add(visitVO.getEquipId());
                    equipNameList.add(visitVO.getEmpName());
                    startTimeList.add(visitVO.getStartTime());
                    endTimeList.add(visitVO.getEndTime());
                    visitMap.put("equipIdList", equipIdList);
                    visitMap.put("equipNameList", equipNameList);
                    visitMap.put("startTimeList", startTimeList);
                    visitMap.put("endTimeList", endTimeList);
                    visitMap.put("id", visitVO.getId());
                    visitMap.put("name", visitVO.getName());
                    visitMap.put("sex", visitVO.getSex());
                    visitMap.put("idCard",visitVO.getIdCard() );
                    visitMap.put("phone", visitVO.getPhone());
                    visitMap.put("company",visitVO.getCompany() );
                    visitMap.put("carNum",visitVO.getCarNum() );
                    visitMap.put("visitorNum",visitVO.getVisitorNum() );
                    visitMap.put("visitReason",visitVO.getVisitReason() );
                    visitMap.put("appointTime",visitVO.getAppointTime() );
                    visitMap.put("approveStatus", visitVO.getApproveStatus());
                    visitMap.put("visitTime", visitVO.getVisitTime());
                    visitMap.put("leaveTime",visitVO.getLeaveTime() );
                    visitMap.put("isAppointed",visitVO.getIsAppointed() );
                    visitMap.put("photo",visitVO.getPhoto() );
                    visitMap.put("empName",visitVO.getEmpName() );
                    visitMap.put("empPhone", visitVO.getEmpPhone());
                    visitMap.put("equipName",visitVO.getEquipName());
                    visitMap.put("equipId",visitVO.getEquipId());

                }
            }
            visitList.add(visitMap);
        }
        return visitList;
    }*/

    @Override
    public List<Map<String, Object>> selectVisitorByDate() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(date);
        List<VisitVO> visitVOList = visitorMapper.selectVisitorByDate(today);
        List<Map<String, Object>> visitList = new ArrayList<>();

       /* Set<String> idSet = new HashSet<>();
        for (VisitVO visitVO : visitVOList) {
            String idStr = visitVO.getId();
            idSet.add(idStr);
        }*/

        List<String> idList = new ArrayList<>();
        for (VisitVO visitVO : visitVOList) {
            String idStr = visitVO.getId();
            idList.add(idStr);
        }
        for (int i = 0; i < idList.size() - 1; i++) {
            for (int j = idList.size() - 1; j > i; j--) {
                if (idList.get(i).equals(idList.get(j))) {
                    idList.remove(i);
                }
            }
        }
        for (String visitId : idList) {
            Map<String, Object> visitMap = new HashMap<>();
            List<String> equipIdList = new ArrayList<>();
            List<String> equipNameList = new ArrayList<>();
            List<String> startTimeList = new ArrayList<>();
            List<String> endTimeList = new ArrayList<>();
            for (VisitVO visitVO : visitVOList) {
                String idStr = visitVO.getId();
                if (idStr.equals(visitId)) {
                    equipIdList.add(visitVO.getEquipId());
                    equipNameList.add(visitVO.getEquipName());
                    startTimeList.add(visitVO.getStartTime());
                    endTimeList.add(visitVO.getEndTime());
                    visitMap.put("equipIdList", equipIdList);
                    visitMap.put("equipNameList", equipNameList);
                    visitMap.put("startTimeList", startTimeList);
                    visitMap.put("endTimeList", endTimeList);
                    visitMap.put("id", visitVO.getId());
                    visitMap.put("name", visitVO.getName());
                    visitMap.put("sex", visitVO.getSex());
                    visitMap.put("idCard", visitVO.getIdCard());
                    visitMap.put("phone", visitVO.getPhone());
                    visitMap.put("company", visitVO.getCompany());
                    visitMap.put("carNum", visitVO.getCarNum());
                    visitMap.put("visitorNum", visitVO.getVisitorNum());
                    visitMap.put("visitReason", visitVO.getVisitReason());
                    visitMap.put("appointTime", visitVO.getAppointTime());
                    visitMap.put("approveStatus", visitVO.getApproveStatus());
                    visitMap.put("visitTime", visitVO.getVisitTime());
                    visitMap.put("leaveTime", visitVO.getLeaveTime());
                    visitMap.put("isAppointed", visitVO.getIsAppointed());
                    visitMap.put("photo", visitVO.getPhoto());
                    visitMap.put("empName", visitVO.getEmpName());
                    visitMap.put("empPhone", visitVO.getEmpPhone());
                    visitMap.put("equipName", visitVO.getEquipName());
                    visitMap.put("equipId", visitVO.getEquipId());

                }
            }
            visitList.add(visitMap);
        }
        return visitList;
    }

    @Override
    public int selectStatusByVisitId(String id) throws Exception {
        return visitorMapper.selectStatusByVisitId(id);
    }

    @Override
    public int updateApproveStatus(String id, String approveStatus) throws Exception {
        return visitorMapper.updateApproveStatus(id, approveStatus);
    }

    @Override
    public int deleteVisitor(String[] ids) throws Exception {
        return visitorMapper.deleteByIds(ids);
    }

    @Override
    public List<VisitVO> selectVisitorRecord(Map<String, Object> map) throws Exception {
        return visitorMapper.selectVisitorRecord(map);
    }

    /**
     * @Description 查询设备的访客门禁权限
     * @Author Capejor
     * @Date 2019-05-28 9:35
     **/
    @Override
    public List<VisitServer> selectVisitAuthBySn(String sn) throws Exception {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(today);
        return visitorMapper.selectVisitAuthBySn(sn, todayStr);
    }


}
