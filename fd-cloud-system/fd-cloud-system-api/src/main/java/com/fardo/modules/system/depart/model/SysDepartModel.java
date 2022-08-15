package com.fardo.modules.system.depart.model;

import com.fardo.common.aspect.annotation.Dict;
import com.fardo.modules.system.area.entity.SysAreaFdEntity;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 部门表 存储树结构数据的实体类
 * <p>
 * 
 * @Author Steve
 * @Since 2019-01-22 
 */
@Data
public class SysDepartModel implements Serializable{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="id",name="id")
    private String id;
    @ApiModelProperty(value="父级机构id",name="parentId")
    private String parentId;
    @ApiModelProperty(value="机构名称",name="departName")
    private String departName;
    @ApiModelProperty(value="机构名称拼音首字母缩写",name="departNamePinyinAbbr")
    private String departNamePinyinAbbr;
    @ApiModelProperty(value="机构路径",name="path")
    private String path;
    @ApiModelProperty(value="机构简称",name="departNameAbbr")
    private String departNameAbbr;
    @Dict(dicCode = "ORG_TYPE", conver = true)
    @ApiModelProperty(value="机构类型",name="orgType")
    private String orgType;
    @ApiModelProperty(value="机构编码",name="orgCode")
    private String orgCode;
    @ApiModelProperty(value="是否删除（1-是，0-否）",name="delFlag")
    private Integer delFlag;
    @ApiModelProperty(value="是否叶子节点",name="leaf")
    private boolean leaf;
    @ApiModelProperty(value="新增区划代码（T_SYS_AREA）",name="areaCode")
    private String areaCode;
    @ApiModelProperty("台州部门原始区域代码")
    private String originalAreaCode;
    @ApiModelProperty(value = "等级", example = "1")
    private Integer level;
    /**
     * 将SysDepart对象转换成SysDepartTreeModel对象
     * @param sysDepart
     */
	public SysDepartModel(SysDepartEntity sysDepart) {
        this.id = sysDepart.getId();
        this.parentId = sysDepart.getParentId();
        this.departName = sysDepart.getDepartName();
        this.departNamePinyinAbbr = sysDepart.getDepartNamePinyinAbbr();
        this.departNameAbbr = sysDepart.getDepartNameAbbr();
        this.path = sysDepart.getPath();
        this.orgType = sysDepart.getOrgType();
        this.orgCode = sysDepart.getOrgCode();
        this.delFlag = sysDepart.getDelFlag();
        this.areaCode = sysDepart.getAreaCode();
        this.originalAreaCode = sysDepart.getOriginalAreaCode();
    }

    public SysDepartModel(SysAreaFdEntity area) {
        this.id = area.getAreaCode();
        this.parentId = area.getSupCode();
        this.departName = area.getContent();
        this.orgCode = area.getAreaCode();
        this.delFlag =Integer.parseInt(area.getDeleted());
        this.areaCode = area.getGbAreaCode();
        this.originalAreaCode = area.getAreaCode();
    }

    public SysDepartModel() { }

    /**
     * 重写equals方法
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (o == null || getClass() != o.getClass()) {
			return false;
		}
        SysDepartModel model = (SysDepartModel) o;
        return Objects.equals(id, model.id) &&
                Objects.equals(parentId, model.parentId) &&
                Objects.equals(departName, model.departName) &&
                Objects.equals(departNamePinyinAbbr, model.departNamePinyinAbbr) &&
                Objects.equals(departNameAbbr, model.departNameAbbr) &&
                Objects.equals(path, model.path) &&
                Objects.equals(orgType, model.orgType) &&
                Objects.equals(orgCode, model.orgCode) &&
                Objects.equals(delFlag, model.delFlag) &&
                Objects.equals(areaCode, model.areaCode)&&
                Objects.equals(originalAreaCode, model.originalAreaCode)&&
                Objects.equals(level, model.level);
    }
    
    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, departName, departNamePinyinAbbr, departNameAbbr,
        		path, orgType, orgCode, delFlag,areaCode, originalAreaCode,level);
    }

}
