package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Account;
import com.faceRecog.manage.model.vo.AccountBalanceVO;
import com.faceRecog.manage.model.vo.AccountTopUpVO;
import com.faceRecog.manage.service.AccountService;
import com.faceRecog.manage.service.EmployeeService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author Capejor
 * @className: AccountController
 * @Description: TODO
 * @date 2019-05-17 14:52
 */
@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmployeeService employeeService;


    /**
     * @Description 账户充值
     * @Author Capejor
     * @Date 2019-05-17 15:27
     **/
    @RequestMapping("/insertAccount")
    public Result insertAccount(Account account) throws Exception {
        Result result;
        if (StrKit.isBlank(account.getEmpId())) {
            return Result.responseError("员工id不能为空");
        }
        if (StrKit.isBlank(account.getAlterSum().toString())) {
            return Result.responseError("充值金额不能为空");
        }
        int count = employeeService.selectCountByIsDimiss(account.getEmpId());
        if (count>0){
            return Result.responseError("当前员工已经离职，不能充值！");
        }
        account.setId(UUIDGenerator.getUUID());
        account.setAlterTime(new Date());
        account.setCreateUserId("1");
        account.setStatus("1");
        account.setType("1");
        //判断该员工是否为新账户
        // int countNum = accountService.selectCount(account.getEmpId());
        // if (countNum > 0) {
        //查询最后一条账户数据
        Account LastAccount = accountService.selectLastAccount(account.getEmpId());
        account.setOriginalSum(LastAccount.getBalance());
        account.setBalance(account.getAlterSum() + LastAccount.getBalance());
       /* } else {
            account.setOriginalSum(0f);
            account.setBalance(account.getAlterSum());
        }*/
        int insertNum = accountService.changeAccount(account);
        if (insertNum > 0) {
            result = Result.responseSuccess("充值成功");
        } else {
            result = Result.responseError("充值失败");
        }
        return result;
    }

    /**
     * @Description 查询余额
     * @Author Capejor
     * @Date 2019-05-17 17:18
     **/
    @RequestMapping("/selectBalance")
    public Result selectBalance(@RequestParam String deptId, @RequestParam(required = false) String empName) throws Exception {
        Result result;
        List<AccountBalanceVO> balanceVOList;
        if ("-1".equals(deptId)) {
            balanceVOList = accountService.selectAllBalance(empName);
        }else {
            balanceVOList = accountService.selectBalance(deptId, empName);
        }
        if (balanceVOList != null && balanceVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", balanceVOList);
        } else {
            result = Result.responseSuccess("无数据", balanceVOList);
        }
        return result;
    }


    /**
     * @Description 充值明细
     * @Author Capejor
     * @Date 2019-05-17 19:09
     **/
    @RequestMapping("/selectAllTopUp")
    public Result selectAllTopUp(@RequestParam String empId, @RequestParam(required = false) String startTime,
                                 @RequestParam(required = false) String endTime) throws Exception {
        Result result;
        Map<String, Object> map = new HashMap<>();
        map.put("empId", empId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        List<AccountTopUpVO> accountList = accountService.selectAllTopUp(map);
        if (accountList != null && accountList.size() > 0) {
            result = Result.responseSuccess("查询充值明细成功", accountList);
        } else {
            result = Result.responseSuccess("无数据", accountList);
        }
        return result;
    }

    /**
     * @Description 消费记录
     * @Author Capejor
     * @Date 2019-05-18 10:13
     **/
    @RequestMapping("/selectConsumeRecord")
    public Result selectConsumeRecord(@RequestParam String empId, @RequestParam(required = false) String startTime,
                                      @RequestParam(required = false) String endTime) throws Exception {
        Result result;
        Map<String, Object> map = new HashMap<>();
        map.put("empId", empId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        List<AccountTopUpVO> accountList = accountService.selectConsumeRecord(map);
        if (accountList != null && accountList.size() > 0) {
            result = Result.responseSuccess("查询消费记录成功", accountList);
        } else {
            result = Result.responseSuccess("无数据", accountList);
        }
        return result;
    }


    /**
     * @Description 消费
     * @Author Capejor
     * @Date 2019-05-17 20:38
     **/
    @RequestMapping("/consumeAccount")
    public Result consumeAccount(Account account) throws Exception {
        Result result;
        if (StrKit.isBlank(account.getEmpId())) {
            return Result.responseError("员工id不能为空");
        }
        if (StrKit.isBlank(account.getAlterSum().toString())) {
            return Result.responseError("消费金额不能为空");
        }
        int count = employeeService.selectCountByIsDimiss(account.getEmpId());
        if (count>0){
            return Result.responseError("当前员工已经离职，不能消费！");
        }
        account.setId(UUIDGenerator.getUUID());
        account.setAlterTime(new Date());
        account.setCreateUserId("1");
        account.setStatus("1");
        account.setType("-1");

        //查询最后一条账户数据
        Account LastAccount = accountService.selectLastAccount(account.getEmpId());
        //余额少于消费金额
        if (LastAccount.getBalance() < account.getAlterSum()) {
            return Result.responseError("余额不够，请充值!!!");
        }
        account.setOriginalSum(LastAccount.getBalance());
        account.setBalance(LastAccount.getBalance() - account.getAlterSum());
        int insertNum = accountService.changeAccount(account);
        if (insertNum > 0) {
            result = Result.responseSuccess("消费成功");
        } else {
            result = Result.responseError("消费失败");
        }
        return result;
    }

}
