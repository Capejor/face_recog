package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.PeriodAttendMapper;
import com.faceRecog.manage.mapper.PeriodMapper;
import com.faceRecog.manage.model.Period;
import com.faceRecog.manage.model.PeriodAttend;
import com.faceRecog.manage.service.PeriodService;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * @author Capejor
 * @className: PeriodServiceImpl
 * @Description: TODO
 * @date 2019-05-16 10:34
 */
@Service
public class PeriodServiceImpl implements PeriodService {

    @Autowired
    private PeriodMapper periodMapper;

    @Autowired
    private PeriodAttendMapper periodAttendMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPeriod(Period period) throws Exception {
        return periodMapper.insertSelective(period);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePeriod(Period period) throws Exception {
        return periodMapper.updateByPrimaryKeySelective(period);
    }

    @Override
    public List<Map<String, Object>> selectPeriod() throws Exception {
        return periodMapper.selectPeriod();
    }

    @Override
    public int deletePeriod(String[] ids) throws Exception {
        return periodMapper.deletePeriod(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPeriodAttend(String periodId, String attendId, int dayNum) {
        int affectNum;
        //查询当前周期的天数
        int dayCount = periodMapper.selectDayNumByPeriodId(periodId);
        for (int i = 1; i <= dayNum; i++) {
            PeriodAttend periodAttend = new PeriodAttend();
            periodAttend.setId(UUIDGenerator.getUUID());
            periodAttend.setAttendId(attendId);
            periodAttend.setPeriodId(periodId);
            periodAttend.setSort(i+dayCount);
            affectNum = periodAttendMapper.insertSelective(periodAttend);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
        }
        //修改当前周期的天数
        int newDayNum = dayNum + dayCount;
        Map<String, Object> map = new HashMap();
        map.put("periodId", periodId);
        map.put("newDayNum", newDayNum);
        affectNum = periodMapper.updateByDayNum(map);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        return affectNum;
    }

    @Override
    public int deletePeriodAttend(String id, String periodId) throws Exception {
        int affectNum;
        affectNum = periodAttendMapper.deleteByPrimaryKey(id);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        //查询当前周期的天数
        int dayCount = periodMapper.selectDayNumByPeriodId(periodId);
        int newDayNum = dayCount - 1;
        //修改当前周期的天数
        Map<String, Object> map = new HashMap();
        map.put("periodId", periodId);
        map.put("newDayNum", newDayNum);
        affectNum = periodMapper.updateByDayNum(map);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        return affectNum;
    }

    @Override
    public List<Map<String, Object>> selectPeriodAttend(String periodId) throws Exception {
        return periodAttendMapper.selectPeriodAttend(periodId);
    }

    @Override
    public int selectCountByPeriodName(String periodName) throws Exception {
        return periodMapper.selectCountByPeriodName(periodName);
    }

    @Override
    public int selectCountByPeriodNameExceptOwn(String id, String periodName) throws Exception {
        return periodMapper.selectCountByPeriodNameExceptOwn(id,periodName);
    }
}
