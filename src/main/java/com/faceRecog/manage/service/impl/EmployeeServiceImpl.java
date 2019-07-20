package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.*;
import com.faceRecog.manage.model.*;
import com.faceRecog.manage.model.serverVO.EmpServer;
import com.faceRecog.manage.model.vo.EmpVO;
import com.faceRecog.manage.service.EmployeeService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeInfoMapper employeeInfoMapper;


    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private InstructionRecMapper instructionRecMapper;


    @Autowired
    private SpringWebSocketHandler websocket;

    @Autowired
    private EmpEquipmentMapper empEquipmentMapper;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private EmpCardMapper empCardMapper;

    @Autowired
    private AttendGroupMapper attendGroupMapper;

    @Autowired
    private PeriodMapper periodMapper;

    @Autowired
    private AttendGroupPeriodMapper attendGroupPeriodMapper;

    @Autowired
    private PeriodAttendMapper periodAttendMapper;

    @Autowired
    private FaceFileMapper faceFileMapper;
    
    @Autowired
    private AttendanceMapper attendanceMapper;

    /**
     * 添加员工基本信息
     *
     * @param employee
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertEmployee(Employee employee, EmployeeInfo employeeInfo, String cardId) throws Exception {
        int affectNum;
        //添加员工
        affectNum = employeeMapper.insertSelective(employee);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //添加员工详情
        affectNum = employeeInfoMapper.insertSelective(employeeInfo);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
      /*  //添加账户
        Account account = new Account();
        account.setId(UUIDGenerator.getUUID());
        account.setEmpId(employee.getId());
        account.setType("0");
        account.setOriginalSum(0.0f);
        account.setAlterSum(0.0f);
        account.setBalance(0.0f);
        account.setAlterTime(new Date());
        account.setStatus("1");
        account.setCreateUserId("1");
        affectNum = accountMapper.insertSelective(account);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }*/
        //员工卡片
        EmpCard empCard = new EmpCard();
        empCard.setId(UUIDGenerator.getUUID());
        empCard.setEmpId(employee.getId());
        empCard.setCardId(cardId);
        affectNum = empCardMapper.insertSelective(empCard);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //卡片绑定员工
        affectNum = cardMapper.inUseToOccupy(cardId);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
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
		if(eqList!=null && eqList.size()>0 && eqList.get(0)!=null){
		 	for(Equipment equipment: eqList){
		 	// 新增指令发送记录 发送socket消息给设备端
		        InstructionRec instructionRec = new InstructionRec();
		        instructionRec.setId(UUIDGenerator.getUUID());
		        instructionRec.setCreateTime(new Date());
		        instructionRec.setStatus("-1");
		        instructionRec.setSn(equipment.getSn());
		        instructionRec.setInstruction(CommonConstant.UPDATE_FACE.getValue());
		        int deleteNum = instructionRecMapper.insertSelective(instructionRec);
		        if (deleteNum < 0) {
		            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		            return -1;
		        }
		        JSONObject jsonObjSend = new JSONObject();
		        jsonObjSend.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
		        jsonObjSend.put("code", Integer.valueOf(CommonConstant.UPDATE_FACE.getValue()));
		        jsonObjSend.put("id", instructionRec.getId());
		        jsonObjSend.put("sendTime", new Date().getTime());
		        //发送指令
		        websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObjSend.toString()));
		 	}
		 }
        return affectNum;
    }

    @Override
    public int insertEmpServer(Employee employee, EmployeeInfo employeeInfo, FaceFile faceFile, String cardId) throws Exception {
        int affectNum;
        //添加员工
        affectNum = employeeMapper.insertSelective(employee);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //添加员工详情
        affectNum = employeeInfoMapper.insertSelective(employeeInfo);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //添加账户
       /* Account account = new Account();
        account.setId(UUIDGenerator.getUUID());
        account.setEmpId(employee.getId());
        account.setType("0");
        account.setOriginalSum(0.0f);
        account.setAlterSum(0.0f);
        account.setBalance(0.0f);
        account.setAlterTime(new Date());
        account.setStatus("1");
        account.setCreateUserId("1");
        affectNum = accountMapper.insertSelective(account);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }*/
        //员工卡片
        EmpCard empCard = new EmpCard();
        empCard.setId(UUIDGenerator.getUUID());
        empCard.setEmpId(employee.getId());
        empCard.setCardId(cardId);
        affectNum = empCardMapper.insertSelective(empCard);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //卡片绑定员工
        affectNum = cardMapper.inUseToOccupy(cardId);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        affectNum = faceFileMapper.insertSelective(faceFile);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
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
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
            }
        }
        return affectNum;
    }


    /**
     * 修改员工信息
     */
    @Override
    public int updateEmployee(Employee employee, EmployeeInfo employeeInfo) throws Exception {
        int affectNum;
        affectNum = employeeMapper.updateByPrimaryKeySelective(employee);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        affectNum = employeeInfoMapper.updateByEmpId(employeeInfo);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
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
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
            }
        }
        return affectNum;
    }


    /**
     * 根部部门id查询员工
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<EmpVO> selectEmployee(String deptId) throws Exception {
        // 分页插件，对下面的第一个查询方法进行分页
//        PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
//        List<EmpVO> employeeList = employeeMapper.selectEmployee(map);
//        return new PageInfo<EmpVO>(employeeList);
        return employeeMapper.selectEmployee(deptId);
    }

    @Override
    public List<EmpVO> selectAllEmpByParam(String searchParam) throws Exception {
        return employeeMapper.selectAllEmpByParam(searchParam);
    }

    @Override
    public List<EmpVO> selectAllEmployee() throws Exception {
        return employeeMapper.selectAllEmployee();
    }

    /**
     * 查询员工记录
     *
     * @throws Exception
     */
    @Override
    public List<EmpVO> selectEmpRecord(Map<String, Object> map) throws Exception {
        return employeeMapper.selectEmpRecord(map);
    }

    @Override
    public List<EmpVO> selectAllEmpRecord(Map<String, Object> map) throws Exception {
        return employeeMapper.selectAllEmpRecord(map);
    }

    @Override
    @Transactional
    public int allocationAuth(String[] empIds, String authId, String[] eqIds) throws Exception {
        int affectNum = 0;
        for (String empId : empIds) {
            EmpEquipment empEquipment = new EmpEquipment();
            empEquipment.setEmpId(empId);
            for (String eqId : eqIds) {
                empEquipment.setEquipId(eqId);
                empEquipment.setId(UUIDGenerator.getUUID());
                empEquipment.setAuthId(authId);
                affectNum = empEquipmentMapper.insertSelective(empEquipment);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }

                // 查询设备信息 保存指令发送记录 发送更新访客信息指令到设备
                Equipment equipment = equipmentMapper.selectByPrimaryKey(eqId);
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
                instructionRec.setInstruction(CommonConstant.UPDATE_PASS_AUTH.getValue());
                affectNum = instructionRecMapper.insertSelective(instructionRec);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_PASS_AUTH.getValue()));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));
            }
        }
        //return employeeMapper.allocationAuth(empIds, authId);
        return affectNum;
    }

    @Override
    public int updateAuth(String[] empIds, String authId, String[] eqIds) throws Exception {
        int affectNum = 0;
        for (String empId : empIds) {
            EmpEquipment empEquipment = new EmpEquipment();
            empEquipment.setEmpId(empId);
            for (String eqId : eqIds) {
                empEquipment.setEquipId(eqId);
                empEquipment.setAuthId(authId);
                affectNum = empEquipmentMapper.updateByEmpIdAndEqId(empEquipment);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }

                // 查询设备信息 保存指令发送记录 发送更新访客信息指令到设备
                Equipment equipment = equipmentMapper.selectByPrimaryKey(eqId);
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
                instructionRec.setInstruction(CommonConstant.UPDATE_PASS_AUTH.getValue());
                affectNum = instructionRecMapper.insertSelective(instructionRec);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_PASS_AUTH.getValue()));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));
            }
        }
        //return employeeMapper.allocationAuth(empIds, authId);
        return affectNum;
    }

    @Override
    public Result deleteAllocationAuth(JSONObject jsonObject) throws Exception {
        int deleteNum = 0;
        //Result result = null;
        if (jsonObject != null && jsonObject.containsKey("authList") && jsonObject.getJSONArray("authList").size() > 0) {
            JSONArray jsonArr = jsonObject.getJSONArray("authList");
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject jsonObj = (JSONObject) jsonArr.get(i);
                deleteNum = empEquipmentMapper.deleteEmpEquipByEmpIdAndEquipId(jsonObj.getString("empId"), jsonObj.getString("equipId"));
                if (deleteNum < 0) {
                    return Result.responseError("删除失败！");
                }
                // 查询设备信息 保存指令发送记录 发送更新访客信息指令到设备
                Equipment equipment = equipmentMapper.selectByPrimaryKey(jsonObj.getString("equipId"));
                if (equipment == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("删除失败！");
                }
                // 新增指令发送记录 发送socket消息给设备端
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn(equipment.getSn());
                instructionRec.setInstruction(CommonConstant.UPDATE_PASS_AUTH.getValue());
                deleteNum = instructionRecMapper.insertSelective(instructionRec);
                if (deleteNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError("删除失败！");
                }
                JSONObject jsonObjSend = new JSONObject();
                jsonObjSend.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObjSend.put("code", Integer.valueOf(CommonConstant.UPDATE_PASS_AUTH.getValue()));
                jsonObjSend.put("id", instructionRec.getId());
                jsonObjSend.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObjSend.toString()));
            }
        } else {
            return Result.responseError("参数异常！");
        }
        return Result.responseSuccess("删除成功！");
    }







    //调动员工部门
    @Override
    public int updateDeptByEmp(String[] ids, String deptId) throws Exception {
		Map<String,Object> map = new HashMap<>();
		map.put("ids",ids);
		map.put("deptId",deptId);
         // 查询员工的创建人
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
                int affectNum = instructionRecMapper.insertSelective(instructionRec);
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
                websocket.sendMessageToUser(eqMap.getSn(), new TextMessage(jsonObj.toString()));
            }
        }
        return employeeMapper.updateDeptByEmp(map);
    }



    //设备端 查询所有员工
    @Override
    public List<EmpServer> selectAllEmp() throws Exception {
        return employeeMapper.selectAllEmpDetail();
    }

    @Override
    public List<EmpVO> exportEmployee() throws Exception {
        return employeeMapper.exportEmployee();
    }


    @Override
    public int selectAuthCount(String[] empIds, String[] eqIds) throws Exception {
        int selectCount = 0;
        for (String empId : empIds) {
            for (String eqId : eqIds) {
                selectCount = employeeMapper.selectAuthCount(empId, eqId);
            }
        }
        return selectCount;
    }


    /* (non Javadoc)
     * @Title: selectAllEmpWorkSchedule
     * @Description: TODO
     * @return
     * @throws Exception
     * @see com.faceRecog.manage.service.EmployeeService#selectAllEmpWorkSchedule()
     */
    @Override
    public List<Map<String, Object>> selectAllEmpWorkSchedule() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar c = Calendar.getInstance();

        List<Map<String, Object>> empWorkScheduleLst = employeeMapper.selectAllEmpWorkSchedule();
        if (empWorkScheduleLst != null && empWorkScheduleLst.size() > 0 && empWorkScheduleLst.get(0) != null) {
            for (Map<String, Object> empWorkSchedule : empWorkScheduleLst) {
                String inAcorss = (String) empWorkSchedule.get("inAcorss");
                String outAcross = (String) empWorkSchedule.get("outAcross");
                String workOnTime = (String) empWorkSchedule.get("workOnTime");
                String workOffTime = (String) empWorkSchedule.get("workOffTime");
                if (CommonConstant.次日.getValue().equals(inAcorss)) {// 上班跨天
                    c.setTime(fmt.parse(workOnTime));
                    c.add(Calendar.DATE, 1);
                    workOnTime = format.format(c.getTime());
                    empWorkSchedule.put("workOnTime", workOnTime);
                } else {
                    workOnTime = format.format(fmt.parse(workOnTime).getTime());
                    empWorkSchedule.put("workOnTime", workOnTime);
                }
                if (CommonConstant.次日.getValue().equals(outAcross)) {// 下班跨天
                    c.setTime(fmt.parse(workOffTime));
                    c.add(Calendar.DATE, 1);
                    workOffTime = format.format(c.getTime());
                    empWorkSchedule.put("workOffTime", workOffTime);
                } else {
                    workOffTime = format.format(fmt.parse(workOffTime).getTime());
                    empWorkSchedule.put("workOffTime", workOffTime);
                }
            }
        }
        return empWorkScheduleLst;
    }



    /* (non Javadoc)
     * @Title: selectAllEmpWorkPeriodSchedule
     * @Description: TODO
     * @return
     * @throws Exception
     * @see com.faceRecog.manage.service.EmployeeService#selectAllEmpWorkPeriodSchedule()
     */
    @Override
    public List<Map<String, Object>> selectAllEmpWorkPeriodSchedule() throws Exception {
        List<Map<String, Object>> paList = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        List<AttendGroup> agList = attendGroupMapper.selectFixedAttendGroup();
        if (agList != null && agList.size() > 0 && agList.get(0) != null) {
            for (AttendGroup attendGroup : agList) {
                Map<String, Object> map = new HashMap<String, Object>();
                paList = new ArrayList<Map<String, Object>>();
                Period period = periodMapper.selectPeriodByAgId(attendGroup.getId());
                map.put("attendId", period.getId());
                //查询周期排班班次
                List<Map<String, Object>> attendGroupPeriodLst = attendGroupPeriodMapper.selectAgPeriodInfoByAgId(period.getId());
                for (int i = 0; i < attendGroupPeriodLst.size(); i++) {
                    Map<String, Object> periodAttendMap = new HashMap<String, Object>();
                    Map<String, Object> agpMap = attendGroupPeriodLst.get(i);
                    List<Map<String, Object>> periodAttendList = periodAttendMapper.selectPeriodAttendDetailByPrIdAndSortEq((String) agpMap.get("prId"), (Integer) agpMap.get("sort"));
                    periodAttendMap.put("weekDay", (Integer) agpMap.get("sort"));
                    periodAttendMap.put("attends", periodAttendList);
                    paList.add(periodAttendMap);
                }
                map.put("scheduleAttends", paList);
                list.add(map);
            }
        }
        return list;
    }


    /* (non Javadoc)
     * @Title: selectAllEmpAttendInfo
     * @Description: TODO
     * @return
     * @throws Exception
     * @see com.faceRecog.manage.service.EmployeeService#selectAllEmpAttendInfo()
     */
    @Override
    public List<Map<String, Object>> selectAllEmpAttendInfo() throws Exception {
    	
    	List<Map<String, Object>> attendList=new ArrayList<Map<String, Object>>();
    	
    	// 查询固定班的员工考勤
    	List<Map<String, Object>> fixedAttendLst=attendanceMapper.selectFixedAttendEmpInfo();
    	//attendList.addAll(fixedAttendLst);
    	// 查询排班制员工考勤
    	List<Map<String, Object>> schedulLst=attendanceMapper.selectSchedulAttendEmpInfo();
    	fixedAttendLst.addAll(schedulLst);
    	// 查询无考勤组员工
    	List<Map<String, Object>> noAttendLst=attendanceMapper.selectNoAttendEmpInfo();
    	fixedAttendLst.addAll(noAttendLst);
    	//attendList.
        return fixedAttendLst;
    }

    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar c = Calendar.getInstance();
        String today = "209-05-28 08:50";
        try {
            c.setTime(fmt.parse(today));
            c.add(Calendar.DATE, 1);
            String newday = format.format(fmt.parse(today));
            System.out.println(newday);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non Javadoc)
     * @Title: updateEmpFaceRegState
     * @Description: TODO
     * @param empId
     * @param state
     * @return
     * @throws Exception
     * @see com.faceRecog.manage.service.EmployeeService#updateEmpFaceRegState(java.lang.String, java.lang.String)
     */
    @Override
    public int updateEmpFaceRegState(String empId, String state) throws Exception {

        return employeeMapper.updateEmpFaceRegState(empId, state);
    }

    @Override
    public int selectCountByIsDimiss(String empId) throws Exception {
        return employeeMapper.selectCountByIsDimiss(empId);
    }


}
