package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Group;
import com.faceRecog.manage.model.vo.GroupVO;
import com.faceRecog.manage.service.GroupService;
import com.faceRecog.manage.util.excelUtils.ExportPOIUtils;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: GroupController
 * @Description: 分组管理 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class GroupController {

    @Autowired
    private GroupService groupService;

    /**
     * 添加分组
     *
     * @param group
     * @return
     * @throws Exception
     */
    @RequestMapping("/insertGroup")
    public Result insertGroup(Group group) throws Exception {
        Result result;
        group.setId(UUIDGenerator.getUUID());
        group.setCreateTime(new Date());
        group.setStatus("1");
        //判断分组名称是否重复
        int nameCount = groupService.selectByGroupName(group.getGroupName());
        if (nameCount > 0) {
            result = Result.responseError("分组名称重复");
            return result;
        }
        int insertResult = groupService.insertGroup(group);
        // 返回当前添加分组信息
        Group insertGroup = groupService.selectOneById(group.getId());
        if (insertResult > 0) {
            result = Result.responseSuccess("添加成功", insertGroup);
        } else {
            result = Result.responseError("添加失败");
        }

        return result;
    }


    /**
     * 修改分组
     *
     * @param group
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateGroup")
    public Result updateGroup(Group group) throws Exception {
        Result result;
        //判断分组名称是否重复 除去自己
        int nameCount = groupService.selectByNameExceptOwn(group.getGroupName());
        if (nameCount > 0) {
            result = Result.responseError("分组名称重复");
            return result;
        }
        int insertResult = groupService.updateGroup(group);
        if (insertResult > 0) {
            result = Result.responseSuccess("修改成功");
        } else {
            result = Result.responseError("修改失败");
        }
        return result;
    }


    /**
     * 查询所有分组
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/selectAllGroup")
    public Result selectAllGroup() throws Exception {
        Result result;
        List<GroupVO> groupList = groupService.selectAllGroup();
        if (StrKit.notNull(groupList)) {
            result = Result.responseSuccess("查询成功", groupList);
        } else {
            result = Result.responseError("无数据");
        }
        return result;
    }


    /**
     * 删除分组
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteGroup")
    public Result deleteGroup(@RequestParam String id) throws Exception {
        Result result;
        int deleteResult = groupService.deleteGroup(id);
        if (deleteResult > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }




    /**
     * @Description 导出分组信息
     * @Author Capejor
     * @Date 2019-05-24 9:23
     **/
    @RequestMapping("/exportGroup")
    public void exportList(HttpServletResponse response) throws Exception{

        String fileName = "分组信息列表";

        List<GroupVO> groupList = groupService.selectAllGroup();

        // 列名
        String columnNames[] = { "ID", "分组名称", "创建人", "状态" };
        // map中的key
        String keys[] = { "id", "groupName", "createUserId", "status" };
        try {
            ExportPOIUtils.start_download(response, fileName, groupList,columnNames, keys);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


