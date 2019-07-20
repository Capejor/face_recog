package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Card;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CardMapper {
    int deleteByPrimaryKey(String id);

    int insert(Card record);

    int insertSelective(Card record);

    Card selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Card record);

    int updateByPrimaryKey(Card record);

    //判断卡号是否重复录入
    int selectCountByCardNum(@Param("cardNum") String cardNum);

    //设备端 判断卡号是否被占用
    int selectCountByCardId(String cardId);

    //查询未占用卡片
    List<Card> selectNotOccupyCard();

    //设备端 查询所有卡片
    List<Card> selectAllCard();

    //解绑员工 将 inUse 置为 0
    int inUseToNotOccupy(String cardId);

    //绑定员工 将 inUse 置为 1
    int inUseToOccupy(String cardId);
}