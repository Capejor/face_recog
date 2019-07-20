package com.faceRecog.manage.controller;

import com.faceRecog.manage.callable.UpdateEquipUpgradeCallable;
import com.faceRecog.manage.model.Upgrade;
import com.faceRecog.manage.service.UpgradeService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.fileUpload.CommonFileUploadServlet;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * @author Capejor
 * @className: UpgradeController
 * @Description: TODO
 * @date 2019-06-04 15:49
 */
@RestController
public class UpgradeController {

    @Autowired
    private UpgradeService upgradeService;


    /**
     * @Description 文件上传
     * @Author Capejor
     * @Date 2019-06-04 16:23
     **/
    @RequestMapping("/uploadFile")
    public Result uploadFile(@RequestParam String equipId, HttpServletRequest request) throws Exception {
        Result result;
        if (StrKit.isBlank(equipId)) {
            return Result.responseError("设备id不能为空");
        }
        //文件上传
        CommonFileUploadServlet commonFileUploadServlet = new CommonFileUploadServlet();
        JSONObject jsonObject = commonFileUploadServlet.upload(request);
        int uploadResult = upgradeService.uploadFile(equipId, jsonObject);
        //判断服务器保存文件是否成功
        if (uploadResult > 0) {
            result = Result.responseSuccess("上传成功！");
        } else {
            result = Result.responseError("上传失败！");
        }
        return result;
    }


    /**
     * @Description 添加文件版本
     * @Author Capejor
     * @Date 2019-06-05 11:26
     **/
    @RequestMapping("/updateUpgrade")
    public Result updateUpgrade(Upgrade upgrade) throws Exception {
        Result result;
        if (StrKit.isBlank(upgrade.getId())) {
            return Result.responseError("文件id不能为空");
        }
        if (StrKit.isBlank(upgrade.getVersion())) {
            return Result.responseError("软件版本不能为空");
        }
        if (StrKit.isBlank(upgrade.getVersionDesc())) {
            return Result.responseError("版本描述不能为空");
        }
        upgrade.setUpdateTime(new Date());
        int updateNum = upgradeService.updateUpgrade(upgrade);
        if (updateNum > 0) {
            result = Result.responseSuccess("操作成功！");
        } else {
            result = Result.responseError("操作失败！");
        }
        return result;
    }


    /**
     * @Description 根据设备id查询文件
     * @Author Capejor
     * @Date 2019-06-05 11:35
     **/
    @RequestMapping("/selectUpgradeByEquipId")
    public Result selectUpgradeByEquipId(@RequestParam(required = false) String equipId) throws Exception {
        Result result;
        List<Upgrade> upgradeList = upgradeService.selectUpgrade(equipId);
        if (upgradeList != null && upgradeList.size() > 0) {
            result = Result.responseSuccess("查询成功", upgradeList);
        } else {
            result = Result.responseError("无数据", upgradeList);
        }
        return result;
    }


    /**
     * @Description 升级设备
     * @Author Capejor
     * @Date 2019-06-20 9:40
     **/
    @RequestMapping("/upgradeEquipment")
    public Result upgradeEquipment(String sn) throws Exception {
        Result result;
        if (StrKit.isBlank(sn)) {
            return Result.responseError("设备编号不能为空");
        }
        int affectNum = upgradeService.upgradeEquipment(sn);
        if (affectNum > 0) {
            result = Result.responseSuccess("发送成功！");
            return result;
        } else {
            result = Result.responseError("发送失败！");
            return result;
        }

    }
}
