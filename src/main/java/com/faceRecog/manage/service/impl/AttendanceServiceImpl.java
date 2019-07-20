package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.AttendanceMapper;
import com.faceRecog.manage.mapper.PeriodAttendMapper;
import com.faceRecog.manage.mapper.TimeFrameMapper;
import com.faceRecog.manage.model.Attendance;
import com.faceRecog.manage.model.PeriodAttend;
import com.faceRecog.manage.model.TimeFrame;
import com.faceRecog.manage.model.vo.AttendVO;
import com.faceRecog.manage.model.vo.TimeFrameVO;
import com.faceRecog.manage.service.AttendanceService;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private TimeFrameMapper timeFrameMapper;

    @Autowired
    private PeriodAttendMapper periodAttendMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAttendance(Attendance attendance, TimeFrameVO timeFrameVO) throws Exception {
        int insertNum = attendanceMapper.insertSelective(attendance);
        int timeNum = 0;
        if (insertNum > 0) {
            timeFrameVO.getAttendTime().stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
            timeFrameVO.getCloseTime().stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
            timeFrameVO.getInAcorss().stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
            timeFrameVO.getIsOvertime().stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
            timeFrameVO.getOutAcorss().stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
            if (timeFrameVO.getAttendTime() == null || timeFrameVO.getAttendTime().size() == 0) {
                return -1;
            } else if (timeFrameVO.getCloseTime() == null || timeFrameVO.getCloseTime().size() == 0) {
                return -1;
            } else if (timeFrameVO.getInAcorss() == null || timeFrameVO.getCloseTime().size() == 0) {
                return -1;
            } else if (timeFrameVO.getIsOvertime() == null || timeFrameVO.getCloseTime().size() == 0) {
                return -1;
            } else if (timeFrameVO.getOutAcorss() == null || timeFrameVO.getCloseTime().size() == 0) {
                return -1;
            }

            for (int i = 0; i < timeFrameVO.getAttendTime().size(); i++) {
                TimeFrame timeFrame = new TimeFrame();
                timeFrame.setId(UUIDGenerator.getUUID());
                timeFrame.setAttendId(attendance.getId());
                timeFrame.setAttendTime(timeFrameVO.getAttendTime().get(i));
                timeFrame.setCloseTime(timeFrameVO.getCloseTime().get(i));
                timeFrame.setIsOvertime(timeFrameVO.getIsOvertime().get(i));
                timeFrame.setSort(String.valueOf(i + 1));
                timeFrame.setInAcross(timeFrameVO.getInAcorss().get(i));
                timeFrame.setOutAcross(timeFrameVO.getOutAcorss().get(i));
                timeNum = timeFrameMapper.insertSelective(timeFrame);
            }
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        return timeNum;
    }

    /**
     * @Description 修改班次
     * @Author Capejor
     * @Date 2019-06-13 20:37
     **/
    @Override
    public int updateAttendance(Attendance attendance, TimeFrameVO timeFrameVO) throws Exception {
        int affectNum;
        affectNum = attendanceMapper.updateByPrimaryKeySelective(attendance);
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        affectNum = timeFrameMapper.deleteByAttendId(attendance.getId());
        if (affectNum == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        for (int i = 0; i < timeFrameVO.getAttendTime().size(); i++) {
            TimeFrame timeFrame = new TimeFrame();
            timeFrame.setId(UUIDGenerator.getUUID());
            timeFrame.setAttendId(attendance.getId());
            timeFrame.setAttendTime(timeFrameVO.getAttendTime().get(i));
            timeFrame.setCloseTime(timeFrameVO.getCloseTime().get(i));
            timeFrame.setIsOvertime(timeFrameVO.getIsOvertime().get(i));
            //timeFrame.setSort(timeFrameVO.getSort().get(i));
            timeFrame.setSort(String.valueOf(i + 1));
            affectNum = timeFrameMapper.insertSelective(timeFrame);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
        }
        return affectNum;
    }


    /**
     * @Description 查询班次
     * @Author Capejor
     * @Date 2019-06-13 20:38
     **/
    @Override
    public List<Map<String, Object>> selectAttendance() throws Exception {
        List<AttendVO> attendList = attendanceMapper.selectAllAttend();
        List<Map<String, Object>> attList = new ArrayList<>();
        if (attendList == null && attendList.size() < 1) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();

        //Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();
        for (AttendVO attendVO : attendList) {
            String idStr = attendVO.getId();
            idList.add(idStr);
        }
        for (int i = 0; i < idList.size() - 1; i++) {
            for (int j = idList.size() - 1; j > i; j--) {
                if (idList.get(i).equals(idList.get(j))) {
                    idList.remove(i);
                }
            }
        }
        for (String idStr : idList) {
            Map<String, Object> map = new HashMap<>();
            List attendTimeList = new ArrayList();
            List closeTimeList = new ArrayList();
            List isOverTimeList = new ArrayList();
            List sortList = new ArrayList();
            for (AttendVO attendVO : attendList) {
                String attStr = attendVO.getId();
                if (attStr.equals(idStr)) {
                    attendTimeList.add(attendVO.getAttendTime());
                    closeTimeList.add(attendVO.getCloseTime());
                    isOverTimeList.add(attendVO.getIsOvertime());
                    sortList.add(attendVO.getSort());
                    map.put("attendTime", attendTimeList);
                    map.put("closeTime", closeTimeList);
                    map.put("isOverTime", isOverTimeList);
                    map.put("sort", sortList);
                    map.put("id", attendVO.getId());
                    map.put("name", attendVO.getName());
                    map.put("type", attendVO.getType());
                    map.put("workTime", attendVO.getWorkTime());
                    map.put("allowEarly", attendVO.getAllowEarly());
                    map.put("allowLate", attendVO.getAllowLate());
                    map.put("afterOvertime", attendVO.getAfterOvertime());
                    //map.put("isOvertime", attendVO.getIsOvertime());
                    // 计迟到时间
                    if (attendVO.getAttendTime() != null && attendVO.getAttendTime().length() > 0) {
                        calendar.setTime(sdf.parse(attendVO.getAttendTime()));
                        calendar.add(Calendar.MINUTE, Integer.parseInt(attendVO.getAllowLate()));
                        map.put("lateTime", sdf.format(calendar.getTime()));
                    } else {
                        map.put("lateTime", "");
                    }
                    // 计早退时间
                    if (attendVO.getCloseTime() != null && attendVO.getCloseTime().length() > 0) {
                        calendar.setTime(sdf.parse(attendVO.getCloseTime()));
                        calendar.add(Calendar.MINUTE, -(Integer.parseInt(attendVO.getAllowEarly())));
                        map.put("earlyTime", sdf.format(calendar.getTime()));
                    } else {
                        map.put("earlyTime", "");
                    }
                }

            }
            attList.add(map);
        }
        return attList;
    }

    @Override
    public int deleteAttendance(String id) throws Exception {
        return attendanceMapper.deleteAttendance(id);
    }

    @Override
    public int selectCountByAttName(String attName) throws Exception {
        return attendanceMapper.selectCountByAttName(attName);
    }

    @Override
    public int selectCountByAttNameExceptOwn(String id, String attName) throws Exception {
        return attendanceMapper.selectCountByAttNameExceptOwn(id, attName);
    }

    @Override
    public int selectCountByAttIds(String id) throws Exception {
        return periodAttendMapper.selectCountByAttIds(id);
    }

    /* (non Javadoc)
     * @Title: selectAttendInfoByAtId
     * @Description: TODO
     * @param atId
     * @return
     * @throws Exception
     * @see com.faceRecog.manage.service.AttendanceService#selectAttendInfoByAtId(java.lang.String)
     */
    @Override
    public Map<String, Object> selectAttendInfoByAtId(String atId) throws Exception {

        return attendanceMapper.selectAttendInfoByAtId(atId);
    }

}
