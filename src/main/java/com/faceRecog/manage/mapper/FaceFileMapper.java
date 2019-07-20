package com.faceRecog.manage.mapper;


import com.faceRecog.manage.model.FaceFile;

public interface FaceFileMapper {
    int deleteByPrimaryKey(String id);

    int insert(FaceFile record);

    int insertSelective(FaceFile record);

    FaceFile selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FaceFile record);

    int updateByPrimaryKey(FaceFile record);


    //判断该员工是否已经录入照片
    int selectPhotoNum(String empId);

    //删除已录入照片
    int deleteByEmpId(String empId);
}