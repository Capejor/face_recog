package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Visitor;

import com.faceRecog.manage.model.serverVO.VisitServer;
import com.faceRecog.manage.model.vo.VisitVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VisitorMapper {
    int deleteByPrimaryKey(String id);

    int insert(Visitor record);

    int insertSelective(Visitor record);

    Visitor selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Visitor record);

    int updateByPrimaryKey(Visitor record);

    //判断是否已登记或已预约 当天
    int selectByPhoneAndVisitTime(@Param("phone") String phone,@Param("visitTime") String visitTime);

    //判断是否已登记或已预约 除去自己
    int selectByPhoneAndAppointTimeExceptOwn(@Param("id") String id, @Param("phone") String phone, @Param("appointTime") String appointTime);

    List<VisitVO> selectVisitor(@Param("phone") String phone);

    //String selectIdByPhone(String phone);

    List<VisitVO> selectVisitorByDate(@Param("today") String today);

    int selectStatusByVisitId(String id);

    int updateApproveStatus(@Param("id") String id, @Param("approveStatus") String approveStatus);

    //批量删除访客
    int deleteByIds(@Param("ids") String[] ids);

    List<VisitVO> selectVisitorRecord(Map<String, Object> map);

    List<VisitServer> selectVisitAuthBySn(@Param("sn") String sn, @Param("todayStr") String todayStr);
}