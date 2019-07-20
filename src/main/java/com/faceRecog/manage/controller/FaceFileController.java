package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.FaceFile;
import com.faceRecog.manage.service.FaceFileService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.fileUpload.CommonFileUploadServlet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/**
 * @ClassName: FaceFileController
 * @Description: 人脸识别文件 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class FaceFileController {

    @Autowired
    private FaceFileService faceFileService;


    /**
     * 添加人脸识别图像
     *
     * @param faceFile
     * @throws Exception
     */
    @RequestMapping("/insertFaceFile")
    public Result insertFaceFile(FaceFile faceFile, HttpServletRequest request) throws Exception {
        Result result;
        if (StrKit.isBlank(faceFile.getEmpId())) {
            return Result.responseError("员工id不能为空");
        }
        /*if (StrKit.isBlank(faceFile.getValidTime())) {
            return Result.responseError("有效期不能为空");
        }*/
        faceFile.setId(UUIDGenerator.getUUID());
        faceFile.setCreateTime(new Date());
        faceFile.setStatus("1");
        faceFile.setCreateUserId("1");

        //接收图片
        CommonFileUploadServlet uploadServlet = new CommonFileUploadServlet();
        JSONObject jsonObj = uploadServlet.upload(request);
        JSONArray jsonArray = (JSONArray) jsonObj.get("fileList");
        String photoUrl = jsonArray.getJSONObject(0).get("srcFilePath").toString();
        if (StrKit.notBlank(photoUrl)) {
            faceFile.setPhotoUrl(photoUrl);
        } else {
            return Result.responseError("图片路径为空");
        }
        String photoSize = jsonArray.getJSONObject(0).get("fileSize").toString();
        if (StrKit.notBlank(photoSize)) {
            faceFile.setPhotoSize(photoSize);
        } else {
            result = Result.responseError("图片大小为空");
            return result;
        }

        int insertResult = faceFileService.insertFaceFile(faceFile);
        if (insertResult > 0) {
            result = Result.responseSuccess("添加成功");
        } else {
            result = Result.responseError("添加失败");
        }
        return result;
    }
}
