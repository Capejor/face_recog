package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Card;

import java.util.List;

public interface CardService {

    //设备端 录入卡片 判断卡号是否重复
    int selectCountByCardNum(String cardNum) throws Exception;

    //设备端 判断卡号是否被占用
    int selectCountByCardId(String cardId) throws Exception;

    //设备端 录入卡片
    int insertCard(Card card) throws Exception;

    //删除卡片
    int deleteCardById(String id) throws Exception;

    //查询未占用卡片
    List<Card> selectNotOccupyCard() throws Exception;

    //查询所有卡片
    List<Card> selectAllCard() throws Exception;

}
