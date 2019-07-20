package com.faceRecog.manage.mapper;


import com.faceRecog.manage.model.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DepartmentMapper {
    int deleteByPrimaryKey(String id);

    int insert(Department record);

    int insertSelective(Department record);

    Department selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Department record);

    int updateByPrimaryKey(Department record);


    List<Map<String, Object>> selectTopDept();

    List<Map<String, Object>> selectOneById(String parentId);

    //查询下级部门信息
    List<Department> selectChildDeptById(String id);

    //查询当前部门及下级部门信息
    List<Department> selectDeptByParentIdOrId(String id);

    //判断部门名称是否重复
    int selectByDeptNameAndParentId(@Param("deptName") String deptName, @Param("parentId") String parentId);

    //判断部门名称是否重复 除去自己
    int selectByDeptNameAndParentIdExceptOwn(@Param("id") String id ,@Param("deptName") String deptName, @Param("parentId") String parentId) throws Exception;

    /**
     * @return List<Department>
     * @Title: selectAllDepart
     * @Description: 查询所有的部门
     * @author xya
     * @date 2019年5月17日下午1:56:04
     */
    List<Department> selectAllChildDepartByParentId(String deptId);

    /**
     * @return Department
     * @Title: selectFirstDepart
     * @Description: 查询最顶级的部门
     * @author xya
     * @date 2019年5月17日下午2:12:21
     */
    Department selectFirstDepart();
    
    List<Department> queryDeptUserTreeList(String id);

}