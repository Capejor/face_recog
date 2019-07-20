package com.faceRecog.manage.model.vo;

public class EmpVO {

    private String id;

    private String name;

    private String sex;

    private String education;

    private String birth;

    private String idCard;

    private String nation;

    private String nativePlace;

    private String phone;

    private String address;

    private String employTime;

    private String jobPost;

    private String duty;

    private String faceReg;
    //部门
    private String deptName;
    //分组
    private String groupName;

    private String isDimiss;



    // 详情
    private String politics;

    private String isMarried;

    private String blood;

    private String emerPer;

    private String emerPhone;

    private String carNo;

    private String laborCon;

    private String laborConNo;

    private String remark;

    //卡片
    //private String pwd;

    //private Date startTime;

    //private Date endTime;

    //private String decimalism;

    private String cardId;

    private String cardNum;

    //人脸识别
    private String photoUrl;




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
        this.name = name == null ? null : name.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation == null ? null : nation.trim();
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace == null ? null : nativePlace.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getEmployTime() {
        return employTime;
    }

    public void setEmployTime(String employTime) {
        this.employTime = employTime;
    }

    public String getJobPost() {
        return jobPost;
    }

    public void setJobPost(String jobPost) {
        this.jobPost = jobPost == null ? null : jobPost.trim();
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getFaceReg() {
        return faceReg;
    }

    public void setFaceReg(String faceReg) {
        this.faceReg = faceReg;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPolitics() {
        return politics;
    }

    public void setPolitics(String politics) {
        this.politics = politics;
    }

    public String getIsMarried() {
        return isMarried;
    }

    public void setIsMarried(String isMarried) {
        this.isMarried = isMarried;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getEmerPer() {
        return emerPer;
    }

    public void setEmerPer(String emerPer) {
        this.emerPer = emerPer;
    }

    public String getEmerPhone() {
        return emerPhone;
    }

    public void setEmerPhone(String emerPhone) {
        this.emerPhone = emerPhone;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getLaborCon() {
        return laborCon;
    }

    public void setLaborCon(String laborCon) {
        this.laborCon = laborCon;
    }

    public String getLaborConNo() {
        return laborConNo;
    }

    public void setLaborConNo(String laborConNo) {
        this.laborConNo = laborConNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getIsDimiss() {
        return isDimiss;
    }

    public void setIsDimiss(String isDimiss) {
        this.isDimiss = isDimiss;
    }
}