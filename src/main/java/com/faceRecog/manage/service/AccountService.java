package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Account;
import com.faceRecog.manage.model.vo.AccountBalanceVO;
import com.faceRecog.manage.model.vo.AccountTopUpVO;

import java.util.List;
import java.util.Map;

public interface AccountService {

    //int selectCount(String empId) throws Exception;

    Account selectLastAccount(String empId) throws Exception;

    int changeAccount(Account account) throws Exception;

    List<AccountBalanceVO> selectBalance(String deptId,String empName) throws Exception;

    List<AccountBalanceVO> selectAllBalance(String empName) throws Exception;

    List<AccountTopUpVO> selectAllTopUp(Map<String,Object> map) throws Exception;

    List<AccountTopUpVO> selectConsumeRecord(Map<String,Object> map) throws Exception;
}
