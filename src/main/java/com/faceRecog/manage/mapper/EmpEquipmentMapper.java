package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.EmpEquipment;


public interface EmpEquipmentMapper {
    int deleteByPrimaryKey(String id);

    int insert(EmpEquipment record);

    int insertSelective(EmpEquipment record);

    EmpEquipment selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EmpEquipment record);

    int updateByPrimaryKey(EmpEquipment record);


    int updateByEmpIdAndEqId(EmpEquipment empEquipment);
    
    int deleteEmpEquipByEmpIdAndEquipId(@Param("empId")String empId,@Param("equipId") String equipId);
    //int deleteEmpEquipByEmpIdAndEquipId(Map<String,Object> map);

    //
    int selectCountById(String id);
    
    /**
     * 
    * @Title: selectBatchEmpEquipmentByAuthId 
    * @Description: 根據权限id查询人员设备权限绑定信息 
    * @param ids
    * @return List<EmpEquipment>
    * @author xya
    * @date 2019年6月14日下午8:54:30
     */
    List<Map<String, Object>>selectBatchEmpEquipmentByAuthId(@Param("ids")String []ids);
}