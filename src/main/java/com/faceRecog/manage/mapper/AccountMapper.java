package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Account;
import com.faceRecog.manage.model.vo.AccountBalanceVO;
import com.faceRecog.manage.model.vo.AccountTopUpVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AccountMapper {
    int deleteByPrimaryKey(String id);

    int insert(Account record);

    int insertSelective(Account record);

    Account selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);


    //int selectCount(String empId);

    Account selectLastAccount(String empId);

    List<AccountBalanceVO> selectBalance(@Param("deptId") String deptId,@Param("empName") String empName);

    List<AccountBalanceVO> selectAllBalance(@Param("empName")String empName);

    List<AccountTopUpVO> selectAllTopUp(Map<String,Object> map);

    List<AccountTopUpVO> selectConsumeRecord(Map<String,Object> map);

    int deleteByEmpId(@Param("empIds") String[] empIds);
}