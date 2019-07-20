package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Visitor;
import com.faceRecog.manage.model.serverVO.VisitServer;
import com.faceRecog.manage.model.vo.VisitAuthVO;
import com.faceRecog.manage.model.vo.VisitVO;

import java.util.List;
import java.util.Map;

public interface VisitorService {

    int insertAppointVisitor(Visitor visitor) throws Exception;

    //判断是否已登记或已预约 当天
    int selectByPhoneAndVisitTime(String phone, String Time) throws Exception;

    //判断是否已登记或已预约 除去自己
    int selectByPhoneAndAppointTimeExceptOwn(String id, String phone, String appointTime) throws Exception;

    int selectByVisitId(String VisitId) throws Exception;

    int insertVisitor(Visitor visitor, VisitAuthVO visitAuthVO) throws Exception;

    int updateVisitorAndAuth(Visitor visitor, VisitAuthVO visitAuthVO) throws Exception;

    int updateVisitor(Visitor visitor, VisitAuthVO visitAuthVO) throws Exception;

    List<VisitVO> selectVisitor(String phone) throws Exception;

    List<Map<String, Object>> selectVisitorByDate() throws Exception;

    int selectStatusByVisitId(String id) throws Exception;

    int updateApproveStatus(String id, String approveStatus) throws Exception;

    int deleteVisitor(String[] ids) throws Exception;

    List<VisitVO> selectVisitorRecord(Map<String, Object> map) throws Exception;

    List<VisitServer> selectVisitAuthBySn(String sn) throws Exception;


}
