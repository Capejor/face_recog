package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Employee;
import com.faceRecog.manage.model.serverVO.EmpServer;
import com.faceRecog.manage.model.vo.EmpVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmployeeMapper {
    int deleteByPrimaryKey(String id);

    int insert(Employee record);

    int insertSelective(Employee record);

    Employee selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Employee record);

    int updateByPrimaryKey(Employee record);

    //将员工移到默认分组  deptId改为1
    int updateByDeptId(String id);

    // 当前部门模糊搜索员工信息
    List<EmpVO> selectEmployee(String deptId);

    //全公司模糊搜索员工信息
    List<EmpVO> selectAllEmpByParam(@Param("searchParam") String searchParam);

    List<EmpVO> selectAllEmployee() ;

    //查询员工记录
    List<EmpVO> selectEmpRecord(Map<String, Object> map);

    //查询全公司员工记录
    List<EmpVO> selectAllEmpRecord(Map<String, Object> map);

    //批量删除 离职员工
    int deleteById(@Param("empIds") String[] empIds);

    //权限分配
    int allocationAuth(@Param("empIds") String[] empId, @Param("authId") String authId);

    //将员工faceReg字段修改为"1":"已注册"
    int updateFaceReg(String empId);

    //调动员工部门
    int updateDeptByEmp(Map<String,Object> map);

    //设备端 查询所有员工
    List<EmpServer> selectAllEmp();

    //导出员工
    List<EmpVO> exportEmployee();

    /**
     * @param dptId
     * @return int
     * @Title: selectEmployeeCountByDeptId
     * @Description: 根据部门id查询部门下是否包含员工
     * @author xya
     * @date 2019年5月10日上午10:26:40
     */
    int selectEmployeeCountByDeptId(String dptId);


    /**
     * @Description 查询当前部门离职人员数量
     * @Author Capejor
     * @Date 2019-05-28 19:57
     **/
    int selectDimissEmpCountByDeptId(String deptId);

    /**
     * @Description 查询当前部门所有人员数量
     * @Author Capejor
     * @Date 2019-06-06 15:17
     **/
    int selectAllEmpCountByDeptId(String deptId);



    int selectAuthCount(@Param("empId") String empId, @Param("eqId") String eqId);

    /**
     * @param empId
     * @return Map<String, Object>
     * @Title: selectAttendInfo
     * @Description: 根据人员id查询人员的考勤信息
     * @author xya
     * @date 2019年5月14日下午5:15:52
     */
    List<Map<String, Object>> selectAttendInfo(@Param("empId") String empId, @Param("dateStr") String dateStr);


    /**
     * @return List<Map < String, Object>>
     * @Title: selectAllEmpWorkSchedule
     * @Description: 查询员工的上班排程数据
     * @author xya
     * @date 2019年5月15日下午4:11:16
     */
    List<Map<String, Object>> selectAllEmpWorkSchedule();

    /**
     * @return List<Employee>
     * @Title: selectEmployeeByDeptId
     * @Description: 根据部门id查询部门下员工信息
     * @author xya
     * @date 2019年5月17日下午2:26:56
     */
    List<Employee> selectEmployeeByDeptId(String deptId);

    /**
     * @param deptIds
     * @return List<Employee>
     * @Title: selectBatchEmpByDeptId
     * @Description:批量查询员工根据部门id
     * @author xya
     * @date 2019年5月17日下午4:35:48
     */
    List<Employee> selectBatchEmpByDeptId(List<String> deptIds);

    /**
     * @return List<Employee>
     * @Title: selectAllEmpInfo
     * @Description:查询所有的员工
     * @author xya
     * @date 2019年5月20日下午4:55:30
     */
    List<Map<String, Object>> selectAllEmpInfo();

    /**
     * @return List<Map < String, Object>>
     * @Title: selectAllEmpWorkPeriodSchedule
     * @Description: 查询固定班次员工排班
     * @author xya
     * @date 2019年5月28日下午7:21:53
     */
    List<Map<String, Object>> selectAllEmpWorkPeriodSchedule();


    /**
     * @return List<Map < String, Object>>
     * @Title: selectAllEmpAttendInfo
     * @Description: 查询所有员工所属的考勤班制类型信息
     * @author xya
     * @date 2019年5月29日上午8:50:14
     */

    List<Map<String, Object>> selectAllEmpAttendInfo();

    /**
     * 
    * @Title: updateEmpFaceRegState 
    * @Description: 根据员工id修改员工人脸注册状态 
    * @param empId
    * @param state
    * @return int
    * @author xya
    * @date 2019年6月10日下午2:32:45
     */
    int updateEmpFaceRegState(@Param("empId")String empId,@Param("state")String state);


    int selectCountByIsDimiss(String empId) ;
    
    
    List<EmpServer> selectAllEmpDetail();
    
    
    List<Map<String, Object>>selectEmpInfoByParam(@Param("empIds")String empIds[]);
}