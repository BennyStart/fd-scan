package com.fardo.modules.system.depart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.depart.model.DepartIdModel;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.model.SysDepartModel;
import com.fardo.modules.system.depart.model.SysDepartTreeModel;
import com.fardo.modules.system.depart.vo.SysDepartExcelVo;
import com.fardo.modules.system.depart.vo.SysDepartParamVo;
import com.fardo.modules.system.depart.vo.SysDepartVo;

import java.util.List;

/**
 * <p>
 * 部门表 服务实现类
 * <p>
 *
 * @Author:Steve
 * @Since：   2019-01-22
 */
public interface ISysDepartService extends IService<SysDepartEntity>{

    IPage<SysDepartModel> getPage(SysDepartParamVo sysDepartParamVo);
    /**
     * 查询指定部门信息,并分节点进行显示
     * 传空返回所有部门信息
     * @return
     */
    List<SysDepartTreeModel> queryTreeList(String departId);

    /**
     * 获取下级部门信息
     * 传空返回顶级部门信息
     * @param departId
     * @return
     */
    List<SysDepartModel> querySubDeptList(String departId);

    List<SysDepartModel> querySubDeptList(String departId, String originalAreaCode, Integer level);

    /**
     * 根据关键字查询机构信息
     * @param keyword 关键字
     * @param limitSize 返回记录数
     * @return
     */
    List<SysDepartModel> queryDeptListByKeyword(String keyword, int limitSize);

    /**
     * 获取机构详情
     * @param id 机构id
     * @return
     */
    SysDepartVo getDepartDetail(String id);
    /**
     * 查询所有部门DepartId信息,并分节点进行显示
     * @return
     */
    public List<DepartIdModel> queryDepartIdTreeList();

    /**
     * 保存部门数据
     * @param sysDepart
     */
    String saveDepartData(SysDepartEntity sysDepart);

    /**
     * 更新depart数据
     * @param sysDepart
     * @return
     */
    void updateDepartDataById(SysDepartEntity sysDepart);

    /**
     * 删除depart数据
     * @param id
     * @return
     */
	/* boolean removeDepartDataById(String id); */

    /**
     * 根据关键字搜索相关的部门数据
     * @param keyWord
     * @return
     */
    List<SysDepartTreeModel> searhBy(String keyWord,String myDeptSearch,String departIds);

    /**
     * 根据部门id删除并删除其可能存在的子级部门
     * @param id
     * @return
     */
    boolean logicalDelete(String id);

    /**
     * 查询SysDepart集合
     * @param userId
     * @return
     */
	public List<SysDepartEntity> queryUserDeparts(String userId);

    /**
     * 根据用户名查询部门
     *
     * @param username
     * @return
     */
    List<SysDepartEntity> queryDepartsByUsername(String username);

    /**
     *  根据部门Id查询,当前和下级所有部门IDS
     * @param departId
     * @return
     */
    List<String> getSubDepIdsByDepId(String departId);

    /**
     * 获取我的部门下级所有部门IDS
     * @return
     */
    List<String> getMySubDepIdsByDepId(String departIds);

    /**
     * 获取当前部门顶级部门id
     * @return
     */
    String getTopDepartId(String departId);

    /**
     * 获取当前部门顶级部门信息
     * @return
     */
    SysDepartEntity getTopDepart(String departId);
    /**
     * 根据orgCode获取部门
     * @return
     */
    SysDepartEntity findDepartByOrgCode(String orgCode);

    /**
     * 导入excel
     * @param list
     */
    void importExcel(List<SysDepartExcelVo> list);

    List<SysDepartEntity> getSysDepartEntity();

    SysDepartEntity getSysDepartEntityInfo(String sysCode);
}
