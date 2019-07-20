package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.CardMapper;
import com.faceRecog.manage.model.Card;
import com.faceRecog.manage.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardMapper cardMapper;


    /**
     * @Description 设备端 录入卡片 判断卡号是否重复
     * @Author Capejor
     * @Date 2019-06-18 10:05
     **/
    @Override
    public int selectCountByCardNum(String cardNum) throws Exception {
        return cardMapper.selectCountByCardNum(cardNum);
    }

    @Override
    public int selectCountByCardId(String cardId) throws Exception {
        return cardMapper.selectCountByCardId(cardId);
    }

    /**
     * @Description 设备端 录入卡片
     * @Author Capejor
     * @Date 2019-06-18 10:03
     **/
    @Override
    public int insertCard(Card card) throws Exception {
        return cardMapper.insertSelective(card);
    }

    /**
     * @Description 根据id删除卡片信息
     * @Author Capejor
     * @Date 2019-06-18 10:13
     **/
    @Override
    public int deleteCardById(String id) throws Exception {
        return cardMapper.deleteByPrimaryKey(id);
    }

    /**
     * @Description 查询未占用卡片
     * @Author Capejor
     * @Date 2019-06-18 10:15
     **/
    @Override
    public List<Card> selectNotOccupyCard() throws Exception {
        return cardMapper.selectNotOccupyCard();
    }


    /**
     * @Description 设备端 查询所有卡片
     * @Author Capejor
     * @Date 2019-06-18 10:15
     **/
    @Override
    public List<Card> selectAllCard() throws Exception {
        return cardMapper.selectAllCard();
    }


}
