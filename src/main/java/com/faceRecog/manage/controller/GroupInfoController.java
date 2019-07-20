package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.GroupInfo;
import com.faceRecog.manage.model.vo.GroupInfoVO;
import com.faceRecog.manage.service.GroupInfoService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: GroupInfoController
 * @Description: 分组详情 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class GroupInfoController {

    @Autowired
    private GroupInfoService groupInfoService;

    /**
     * 添加分组详情
     *
     * @param groupInfo
     * @return
     * @throws Exception
     */
    @RequestMapping("/insertGroupInfo")
    public Result insertGroupInfo(GroupInfo groupInfo) throws Exception {
        Result result;
        groupInfo.setId(UUIDGenerator.getUUID());
        groupInfo.setStatus("1");
        groupInfo.setCreateTime(new Date());
        groupInfo.setCreateUserId("1");
        if (StrKit.isBlank(groupInfo.getName())) {
            result = Result.responseError("分组详情名称不能为空");
            return result;
        }
        if (StrKit.isBlank(groupInfo.getName())) {
            result = Result.responseError("分组详情序号不能为空");
            return result;
        }
        if (StrKit.isBlank(groupInfo.getGroupId())) {
            result = Result.responseError("分组id不能为空");
            return result;
        }
        //分组详情名称不能重复
        int nameCount = groupInfoService.selectByName(groupInfo.getName(), groupInfo.getGroupId());
        if (nameCount > 0) {
            result = Result.responseError("分组详情名称重复");
            return result;
        }
        //分组详情排序不能重复
        int sortCount = groupInfoService.selectBySort(groupInfo.getSort(), groupInfo.getGroupId());
        if (sortCount > 0) {
            result = Result.responseError("分组详情序号重复");
            return result;
        }
        if ("1".equals(groupInfo.getGroupId())){
            return Result.responseError("名族不能添加操作");
        }
        if ("2".equals(groupInfo.getGroupId())){
            return Result.responseError("籍贯不能添加操作");
        }
        int insertResult = groupInfoService.insertGroupInfo(groupInfo);
        if (insertResult > 0) {
            result = Result.responseSuccess("添加成功");
        } else {
            result = Result.responseError("添加失败");
        }
        return result;
    }


    /**
     * 修改分组详情
     *
     * @param groupInfo
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateGroupInfo")
    public Result updateGroupInfo(GroupInfo groupInfo) throws Exception {
        Result result = null;
        if (StrKit.isBlank(groupInfo.getName())) {
            return Result.responseError("分组详情名称不能为空");
        }
        if (StrKit.isBlank(groupInfo.getName())) {
            return Result.responseError("分组详情序号不能为空");
        }
        if (StrKit.isBlank(groupInfo.getGroupId())) {
            return Result.responseError("分组id不能为空");

        }
        if ("1".equals(groupInfo.getGroupId())){
            return Result.responseError("名族不能修改操作");
        }
        if ("2".equals(groupInfo.getGroupId())){
            return Result.responseError("籍贯不能修改操作");
        }
        groupInfo.setUpdateTime(new Date());
        //分组详情名称不能重复 除去自己
        int nameCount = groupInfoService.selectByNameExceptOwn(groupInfo.getName(), groupInfo.getGroupId(), groupInfo.getId());
        //分组详情排序不能重复  除去自己
        int sortCount = groupInfoService.selectBySortExceptOwn(groupInfo.getSort(), groupInfo.getGroupId(), groupInfo.getId());

        int insertResult;
        if (nameCount > 0 && sortCount == 0) {
            result = Result.responseError("分组名称重复，不能修改");
        }
        if (nameCount == 0 && sortCount > 0) {
            result = Result.responseError("分组序号重复，不能修改");
        }

        if (nameCount == 0 && sortCount == 0) {
            insertResult = groupInfoService.updateGroupInfo(groupInfo);
            if (insertResult > 0) {
                result = Result.responseSuccess("修改成功");
            } else {
                result = Result.responseError("修改失败");
            }
        }
        if (nameCount > 0 && sortCount > 0) {
            result = Result.responseError("分组名称、分组序号都重复，不能修改！！！");
        }


        return result;
    }


    /* *//**
     * 根据 分组id 查询分组详情
     * @return
     * @throws Exception
     *//*
    @RequestMapping("/selectInfoByGroupId")
    public Result selectInfoByGroupId(@RequestParam String groupId) throws Exception {
        Result result;
        List<GroupInfoVO> groupInfoList = groupInfoService.selectByGroupId(groupId);
        if (StrKit.notNull(groupInfoList)) {
            result = Result.responseSuccess("查询成功", groupInfoList);
        } else {
            result = Result.responseSuccess("无数据");
        }
        return result;
    }*/

    /**
     * 根据 分组id 查询分组详情
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/selectInfoByGroupId")
    public Result selectInfoByGroupId(@RequestParam String groupId) throws Exception {
        Result result;
        List<GroupInfoVO> groupInfoList = groupInfoService.selectByGroupId(groupId);
        if (groupInfoList != null && groupInfoList.size() > 0) {
            result = Result.responseSuccess("查询成功", groupInfoList);
        } else {
            result = Result.responseSuccess("无数据", groupInfoList);
        }
        return result;
    }


    /**
     * 批量删除分组详情
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteGroupInfo")
    public Result deleteGroupInfo(@RequestParam("ids[]") String[] ids) throws Exception {
        Result result;
        if (ids.length <= 0) {
            result = Result.responseError("传入参数为空");
            return result;
        }
        for (int i = 0; i <ids.length ; i++) {
            if ("1".equals(ids[i])){
                return Result.responseError("名族不能删除操作");
            }
            if ("2".equals(ids[i])){
                return Result.responseError("籍贯不能删除操作");
            }
        }

        int deleteResult = groupInfoService.deleteGroupInfo(ids);
        if (deleteResult > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }
}
