package com.faceRecog.manage.model.serverVO;

public class EmpServer {

    private String id;

    private String name;

    private String photoUrl;

    private String photoSize;

    private String cardNum;
    
    private String depetName;

    
    
    public String getDepetName() {
		return depetName;
	}

	public void setDepetName(String depetName) {
		this.depetName = depetName;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(String photoSize) {
        this.photoSize = photoSize;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
}
