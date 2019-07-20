package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Card;
import com.faceRecog.manage.service.CardService;
import com.faceRecog.manage.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: CardController
 * @Description: 卡片 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class CardController {

    @Autowired
    private CardService cardService;

    /**
     * 根据id删除卡片信息
     * @param id
     */
    @RequestMapping("/deleteCard")
    public Result deleteCard(@RequestParam String id) throws Exception {
        Result result;
        int deleteResult = cardService.deleteCardById(id);
        if (deleteResult >0){
            result = Result.responseSuccess("删除成功");
        }else {
            result = Result.responseError("删除失败");
        }
        return result;
    }


    /**
     * @Description 查询未占用卡片
     * @Author Capejor
     * @Date 2019-05-31 19:39
     **/
    @RequestMapping("/selectNotOccupyCard")
    public Result selectNotOccupyCard() throws Exception {
        Result result;
        List<Card> cardList = cardService.selectNotOccupyCard();
        if (cardList != null && cardList.size() > 0) {
            result = Result.responseSuccess("查询成功", cardList);
        } else {
            result = Result.responseSuccess("无数据", cardList);
        }
        return result;
    }







    

}
