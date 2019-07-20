package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Attendance;
import com.faceRecog.manage.model.vo.TimeFrameVO;

import java.util.List;
import java.util.Map;

public interface AttendanceService {

    int insertAttendance(Attendance attendance, TimeFrameVO timeFrameVO) throws Exception;

    int updateAttendance(Attendance attendance, TimeFrameVO timeFrameVO) throws Exception;

    List<Map<String,Object>> selectAttendance() throws Exception;

    int deleteAttendance(String id) throws Exception;

    int selectCountByAttName(String attName)throws Exception;

    int selectCountByAttNameExceptOwn(String id,String attName) throws Exception;

    int selectCountByAttIds(String id) throws Exception;
    
    Map<String, Object> selectAttendInfoByAtId(String atId)throws Exception;
}
