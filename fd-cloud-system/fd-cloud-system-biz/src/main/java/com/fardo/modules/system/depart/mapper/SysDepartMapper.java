package com.fardo.modules.system.depart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.modules.system.depart.model.SysDepartModel;
import com.fardo.modules.system.depart.model.SysDepartTreeModel;
import com.fardo.modules.system.depart.vo.SysDepartParamVo;
import org.apache.ibatis.annotations.Select;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 部门 Mapper 接口
 * <p>
 *
 * @Author: Steve
 * @Since：   2019-01-22
 */
public interface SysDepartMapper extends BaseMapper<SysDepartEntity> {

	IPage<SysDepartModel> getPageList(Page page, @Param("sysDepartParamVo")SysDepartParamVo sysDepartParamVo);

	/**
	 * 根据用户ID查询部门集合
	 */
	public List<SysDepartEntity> queryUserDeparts(@Param("userId") String userId);

	/**
	 * 根据用户名查询部门
	 *
	 * @param username
	 * @return
	 */
	public List<SysDepartEntity> queryDepartsByUsername(@Param("username") String username);

	@Select("select id from sys_depart where org_code=#{orgCode}")
	public String queryDepartIdByOrgCode(@Param("orgCode") String orgCode);

	@Select("select id,parent_id from sys_depart where id=#{departId}")
	public SysDepartEntity getParentDepartId(@Param("departId") String departId);

	/**
	 *  根据部门Id查询,当前和下级所有部门IDS
	 * @param departId
	 * @return
	 */
	List<String> getSubDepIdsByDepId(@Param("departId") String departId);

	/**
	 * 根据部门编码获取部门下所有IDS
	 * @param orgCodes
	 * @return
	 */
	List<String> getSubDepIdsByOrgCodes(@org.apache.ibatis.annotations.Param("orgCodes") String[] orgCodes);

	@Select("select count(*) from t_sys_depart d inner JOIN t_sys_user u on d.id = u.depart_id where d.path like #{path} AND u.del_flag = 0")
	int getUserCountForPath(@Param("path") String path);

	@Select("select count(*) from t_sys_depart where parent_id = #{id} and del_flag = 0")
	int getDepartCountForId(@Param("id") String id);

	@Select("select * from t_sys_depart where id = (select depart_id from t_sys_user where id = #{userId})")
	SysDepartEntity getUserMasterDepart(@Param("userId") String userId);

	List<String> getPahtByRoleIds(@org.apache.ibatis.annotations.Param("roleIds") List<String> roleIds);

	List<SysDepartEntity> getSysDepartEntity();

	SysDepartEntity getSysDepartEntityInfo(@Param("sysCode") String sysCode);

	SysDepartEntity getBySysCode(@Param("sysCode") String sysCode);

}
