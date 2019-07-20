package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.AccessAuth;
import com.faceRecog.manage.model.serverVO.EmpAuthServer;
import com.faceRecog.manage.model.vo.AccessTimeVO;
import com.faceRecog.manage.model.vo.AuthVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccessAuthMapper {
    int deleteByPrimaryKey(String id);

    int insert(AccessAuth record);

    int insertSelective(AccessAuth record);

    AccessAuth selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AccessAuth record);

    int updateByPrimaryKey(AccessAuth record);

    //查询所有门禁
    List<AccessAuth> selectAllAccessAuth();

    //批量删除门禁
    int deleteByIds(@Param("ids") String[] ids);

    //门禁名称不能重复
    int selectCountByAuthName(String authName);

    //门禁名称不能重复 出去自己
    int selectCountByAuthNameExceptOwn(@Param("id") String id, @Param("authName") String authName);

    //查询全公司员工门禁权限
    List<AuthVO> selectAllEmpAuth();

    //查询当前部门员工门禁权限
    List<AuthVO> selectAuthByDeptId(String deptId);

    //模糊搜索全公司员工权限
    List<AuthVO> selectEmpAuthByParams(@Param("searchParam") String searchParam);

    //根据门禁id查询时间
    List<AccessTimeVO> selectTimezoneById(String id);

    //查询当前设备门禁权限
    List<EmpAuthServer> selectEmpAuthBySn(@Param("sn") String sn);

    //启用禁用
    int updateStatus(@Param("id") String id,@Param("status") String status);


}