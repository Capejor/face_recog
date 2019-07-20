package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Equipment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EquipmentMapper {
    int deleteByPrimaryKey(String id);

    int insert(Equipment record);

    int insertSelective(Equipment record);

    Equipment selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Equipment record);

    int updateByPrimaryKey(Equipment record);

    //
    List<Map<String, Object>> selectEquipment();

    //
    int deleteEquipment(@Param("ids") String[] ids);

    /**
     * @param sn
     * @return Equipment
     * @Title: selectEquipmentBySn
     * @Description: 查询设备信息根据sn号
     * @author xya
     * @date 2019年5月8日上午10:44:03
     */
    Equipment selectEquipmentBySn(String sn);

    /**
     * @param id
     * @param eqName
     * @return int
     * @Title: selectEqNickNameExistByPrimaryKey
     * @Description: 根据主键查询当前昵称是否被占用
     * @author xya
     * @date 2019年5月8日上午10:44:23
     */
    int selectEqNickNameExistByPrimaryKey(@Param("id") String id, @Param("eqName") String eqName);

    /**
     * @param sn
     * @param eqName
     * @return int
     * @Title: selectEqNickNameExistBySn
     * @Description: 根据sn号查询当前昵称是否被占用
     * @author xya
     * @date 2019年5月8日上午10:44:49
     */
    int selectEqNickNameExistBySn(@Param("sn") String sn, @Param("eqName") String eqName);

    /**
     * @param equipment
     * @return int
     * @Title: updateEquipmentInfoBySn
     * @Description: 根据设备编号修改设备信息
     * @author xya
     * @date 2019年5月8日上午10:57:55
     */
    int updateEquipmentInfoBySn(Equipment equipment);


    /**
     * @Description 判断设备名称是否占用
     * @Author Capejor
     * @Date 2019-05-25 15:22
     **/
    int selectCountByEqName(String eqName);

    /**
     * @Description 判断设备是否被注册
     * @Author Capejor
     * @Date 2019-05-25 15:23
     **/
    int judgeEquipmentRegister(String sn);
    
    /**
     * 
    * @Title: selectAllEquipmentByUsId 
    * @Description: 根據用户id查询所有的考勤设备 
    * @param usId
    * @return List<Equipment>
    * @author xya
    * @date 2019年6月6日下午3:02:52
     */
    List<Equipment> selectAllEquipmentByUsId(String usId);
    
    /**
     * 
    * @Title: selectAllOnLineEquipmentInfo 
    * @Description: 查询所有的在线的设备的信息 
    * @return List<Map<String,Object>>
    * @author xya
    * @date 2019年6月10日上午11:06:25
     */
    List<Map<String, Object>>selectAllOnLineEquipmentInfo();
    
    /**
     * 
    * @Title: updateEquipmentLineStateBySn 
    * @Description: 根据设备编号修改设备的在线状态 
    * @param sn
    * @return int
    * @author xya
    * @date 2019年6月10日上午11:36:51
     */
    int updateEquipmentLineStateBySn(@Param("sn")String sn,@Param("lineState")String lineState);


    //根据sn查询设备id
    String selectEquipIdBySn(String sn);
}