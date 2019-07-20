package com.faceRecog.manage.service;

import com.faceRecog.manage.model.EmpRetroact;
import com.faceRecog.manage.model.Retroactive;
import com.faceRecog.manage.model.vo.EmpRetVO;

import java.util.List;
import java.util.Map;

public interface RetroactiveService {

    int insertRetroactive(Retroactive retroactive, EmpRetroact empRetroact,String attendType,String signType) throws Exception;

    List<EmpRetVO> selectAllRetroactive()throws Exception;

    List<EmpRetVO> selectRetroactiveByDeptId(String deptId)throws Exception;

    List<EmpRetVO> selectByParams(Map<String,Object> map)throws Exception;

    int deleteRetroactive(String[] retIds) throws Exception;


}
