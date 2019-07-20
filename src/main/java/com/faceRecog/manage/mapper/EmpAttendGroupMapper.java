package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.EmpAttendGroup;
import com.faceRecog.manage.model.Employee;
import com.faceRecog.manage.model.vo.EmpAttendGroupVO;

public interface EmpAttendGroupMapper {
    int deleteByPrimaryKey(String id);

    int insert(EmpAttendGroup record);

    int insertSelective(EmpAttendGroup record);

    EmpAttendGroup selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EmpAttendGroup record);

    int updateByPrimaryKey(EmpAttendGroup record);
    
    
    /**
     * 
    * @Title: selectEmpInfoByAgId 
    * @Description: 根据考勤组id查询员工信息
    * @param agId
    * @return List<Employee>
    * @author xya
    * @date 2019年5月16日下午8:30:41
     */
    List<Map<String, Object>>selectEmpInfoByAgId(String agId);
    
    
    int insertBatchEmpAttendGroup(@Param("agId")String agId,@Param("empIds")List<String> empIds,@Param("applyTime")String applyTime);
    
    /**
     * 
    * @Title: deleteAttendGroupLeader 
    * @Description: 删除人员考情组中间表 
    * @param agId
    * @return int
    * @author xya
    * @date 2019年5月20日下午8:15:08
     */
    int deleteEmpAttendGroupByAgId(String agId);
    
    /**
     * 
    * @Title: selectEmpAgInfoByAgId 
    * @Description: 查询考勤组人员信息 
    * @param agId
    * @return List<EmpAttendGroup>
    * @author xya
    * @date 2019年5月21日上午10:44:11
     */
    EmpAttendGroup selectEmpAgInfoByAgId(String agId);
    
    /**
     * 
    * @Title: deleteBatchEmpAttendGroupByEmpId 
    * @Description: 批量删除员工考勤组信息 
    * @param empIdLs
    * @return int
    * @author xya
    * @date 2019年5月22日下午8:44:58
     */
    int deleteBatchEmpAttendGroupByEmpId(@Param("empIdLs")List<String> empIdLs);
    
    /**
     * 
    * @Title: selectEmpAttendGroupByEmpId 
    * @Description: 根据员工id查询员工绑定的考勤组 
    * @param empId
    * @return EmpAttendGroup
    * @author xya
    * @date 2019年5月24日下午3:46:27
     */
    EmpAttendGroupVO selectEmpAttendGroupByEmpId(String empId);
    
    /**
     * 
    * @Title: deleteEmpAttendGroupByEmpId 
    * @Description: 根据员工id删除员工考勤组数据 
    * @param empId
    * @return int
    * @author xya
    * @date 2019年6月14日下午9:31:55
     */
    int deleteEmpAttendGroupByEmpId(String empId);
}