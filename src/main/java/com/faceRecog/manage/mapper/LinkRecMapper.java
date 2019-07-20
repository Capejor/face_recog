package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.LinkRec;

public interface LinkRecMapper {
    int deleteByPrimaryKey(String id);

    int insert(LinkRec record);

    int insertSelective(LinkRec record);

    LinkRec selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(LinkRec record);

    int updateByPrimaryKey(LinkRec record);
    
    
    //自定义的mapper
    
    /**
     * 
    * @Title: updateLinkRecStateBySn 
    * @Description: 根据sn修改设备连接状态
    * @param linkRec
    * @return int
    * @author xya
    * @date 2019年5月7日下午5:32:28
     */
    int updateLinkRecStateBySn(LinkRec linkRec);
    
    /**
     * 
    * @Title: selectLinkRecBySn 
    * @Description: 根据sn号查询设备连接信息
    * @param sn
    * @return LinkRec
    * @author xya
    * @date 2019年5月7日下午8:43:53
     */
    LinkRec selectLinkRecBySn(String sn);
}