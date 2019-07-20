package com.faceRecog.manage.model.vo;


/**
 * @author Capejor
 * @className: CardVO
 * @Description: TODO
 * @date 2019-05-23 11:54
 */
public class CardVO {

    private String id;

    private String cardNum;


    private String inUse;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }


    public String getInUse() {
        return inUse;
    }

    public void setInUse(String inUse) {
        this.inUse = inUse;
    }
}
