package com.faceRecog.manage.callable;

import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import com.faceRecog.manage.service.UpgradeService;
import com.faceRecog.manage.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateEquipUpgradeCallable implements Callable<Result> {

    @Autowired
    private UpgradeService upgradeService;

    // 静态初使化当前类
    public static UpdateEquipUpgradeCallable updateUpgradeFileProgressCallable;

    //注解@PostConstruct，这样方法就会在Bean初始化之后被Spring容器执行
    @PostConstruct
    public void init() {
        updateUpgradeFileProgressCallable = this;
    }

    public UpdateEquipUpgradeCallable() {

    }

    private String upgradeId;

    private String percent;

    private String downKb;

    public UpdateEquipUpgradeCallable(String upgradeId, String percent, String downKb) {
        this.upgradeId = upgradeId;
        this.percent = percent;
        this.downKb = downKb;
    }

    @Override
    public Result call() throws Exception {
        Result result;
        String downloadStatus = "";
        if (Integer.valueOf(percent) > 99) {
            downloadStatus = "1";
        } else {
            downloadStatus = "-1";
        }
        int affectNum = updateUpgradeFileProgressCallable.upgradeService.updateEquipUpgrade(upgradeId, percent, downKb, downloadStatus);
        if (affectNum > 0) {
            result = Result.responseSuccess("升级成功！");
        } else {
            result = Result.responseError("升级失败！");
        }
        return result;
    }


}