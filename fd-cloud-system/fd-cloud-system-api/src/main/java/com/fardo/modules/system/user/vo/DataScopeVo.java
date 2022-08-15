package com.fardo.modules.system.user.vo;

import lombok.Data;

import java.util.List;

/**
 * @(#)DataScopeVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/4/14 17:39
 * 描　述：数据范围
 */
@Data
public class DataScopeVo {

    /**
     * 是否全部数据，all=true时，其他属性无效
     */
    private boolean all = false;

    /**
     * 是否负责区域的数据，目前只作用于在押人员
     */
    private boolean chargeArea = false;

    /**
     * 查找 likePath%的数据
     */
    private String likePath;

    /**
     * 自定义单位path
     */
    private List<String> specifyPaths;

    public static DataScopeVo getAllDataScopeVo(){
        DataScopeVo dataScopeVo = new DataScopeVo();
        dataScopeVo.setAll(true);
        return dataScopeVo;
    }
 }
