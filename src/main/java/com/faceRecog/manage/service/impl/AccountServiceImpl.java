package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.AccountMapper;
import com.faceRecog.manage.model.Account;
import com.faceRecog.manage.model.vo.AccountBalanceVO;
import com.faceRecog.manage.model.vo.AccountTopUpVO;
import com.faceRecog.manage.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Capejor
 * @className: AccountServiceImpl
 * @Description: TODO
 * @date 2019-05-17 14:54
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    /*@Override
    public int selectCount(String empId) throws Exception {
        return accountMapper.selectCount(empId);
    }*/

    @Override
    public Account selectLastAccount(String empId) throws Exception {
        return accountMapper.selectLastAccount(empId);
    }

    @Override
    public int changeAccount(Account account) throws Exception {
        return accountMapper.insertSelective(account);
    }

    @Override
    public List<AccountBalanceVO> selectBalance(String deptId,String empName) throws Exception {
        return accountMapper.selectBalance(deptId,empName);
    }

    @Override
    public List<AccountBalanceVO> selectAllBalance(String empName) throws Exception {
        return accountMapper.selectAllBalance(empName);
    }

    @Override
    public List<AccountTopUpVO> selectAllTopUp(Map<String,Object> map) throws Exception {
        return accountMapper.selectAllTopUp(map);
    }

    @Override
    public List<AccountTopUpVO> selectConsumeRecord(Map<String, Object> map) throws Exception {
        return accountMapper.selectConsumeRecord(map);
    }
}
