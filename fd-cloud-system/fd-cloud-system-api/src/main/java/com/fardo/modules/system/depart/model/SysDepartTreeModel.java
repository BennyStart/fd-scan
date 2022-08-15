package com.fardo.modules.system.depart.model;

import com.fardo.modules.system.depart.entity.SysDepartEntity;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 部门表 存储树结构数据的实体类
 * <p>
 * 
 * @Author Steve
 * @Since 2019-01-22 
 */
public class SysDepartTreeModel implements Serializable{
	
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="是否叶子节点",name="isLeaf")
    private boolean isLeaf;
    // 以下所有字段均与SysDepart相同
    @ApiModelProperty(value="id",name="id")
    private String id;
    @ApiModelProperty(value="父级节点id",name="parentId")
    private String parentId;
    @ApiModelProperty(value="机构名称",name="departName")
    private String departName;
    @ApiModelProperty(value="机构名称拼音首字母缩写",name="departNamePinyinAbbr")
    private String departNamePinyinAbbr;
    @ApiModelProperty(value="机构路径",name="path")
    private String path;
    @ApiModelProperty(value="机构简称",name="departNameAbbr")
    private String departNameAbbr;
    @ApiModelProperty(value="机构类型",name="orgType")
    private String orgType;
    @ApiModelProperty(value="机构编码",name="orgCode")
    private String orgCode;
    @ApiModelProperty(value="是否删除（1-是，0-否）",name="delFlag")
    private Integer delFlag;
    @ApiModelProperty(value="机构子节点",name="children")
    private List<SysDepartTreeModel> children = new ArrayList<>();


    /**
     * 将SysDepart对象转换成SysDepartTreeModel对象
     * @param sysDepart
     */
	public SysDepartTreeModel(SysDepartEntity sysDepart) {
        this.id = sysDepart.getId();
        this.parentId = sysDepart.getParentId();
        this.departName = sysDepart.getDepartName();
        this.departNamePinyinAbbr = sysDepart.getDepartNamePinyinAbbr();
        this.departNameAbbr = sysDepart.getDepartNameAbbr();
        this.path = sysDepart.getPath();
        this.orgType = sysDepart.getOrgType();
        this.orgCode = sysDepart.getOrgCode();
        this.delFlag = sysDepart.getDelFlag();
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isleaf) {
         this.isLeaf = isleaf;
    }

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SysDepartTreeModel> getChildren() {
        return children;
    }

    public void setChildren(List<SysDepartTreeModel> children) {
        if (children==null){
            this.isLeaf=true;
        }
        this.children = children;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

	public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getDepartNameAbbr() {
        return departNameAbbr;
    }

    public void setDepartNameAbbr(String departNameAbbr) {
        this.departNameAbbr = departNameAbbr;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getDepartNamePinyinAbbr() {
        return departNamePinyinAbbr;
    }

    public void setDepartNamePinyinAbbr(String departNamePinyinAbbr) {
        this.departNamePinyinAbbr = departNamePinyinAbbr;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SysDepartTreeModel() { }

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
        SysDepartTreeModel model = (SysDepartTreeModel) o;
        return Objects.equals(id, model.id) &&
                Objects.equals(parentId, model.parentId) &&
                Objects.equals(departName, model.departName) &&
                Objects.equals(departNamePinyinAbbr, model.departNamePinyinAbbr) &&
                Objects.equals(departNameAbbr, model.departNameAbbr) &&
                Objects.equals(path, model.path) &&
                Objects.equals(orgType, model.orgType) &&
                Objects.equals(orgCode, model.orgCode) &&
                Objects.equals(delFlag, model.delFlag) &&
                Objects.equals(children, model.children);
    }
    
    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {

        return Objects.hash(id, parentId, departName, departNamePinyinAbbr, departNameAbbr,
        		path, orgType, orgCode, delFlag, children);
    }

}
