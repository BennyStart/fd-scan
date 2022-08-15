/**
 * QuerySpecifiedLevelAreaDataVo;
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：luyf
 * 时　间：2021/12/27
 * 描　述：创建
 */
package com.fardo.modules.system.area.vo;

import lombok.Data;

/**
 * 作者:luyf
 * 文件名:QuerySpecifiedLevelAreaDataVo
 * 版本号:1.0
 * 创建日期:2021/12/27
 * 描述:通过区域编号查询指定等级区域Vo
 */
@Data
public class QuerySpecifiedLevelAreaDataVo {

    /**
     * 区域编号
     **/
    private String id;
    /**
     * 查询到区域等级为止.只能往下级查 不能往上级查.<br>
     * 传入id为-1(顶级区域),则areaLevel可以为PROVINCE/CITY/COUNTY<br>
     * 传入id长度为2位(33 浙江省 省级区域),则areaLevel可以为CITY/COUNTY<br>
     * 传入id长度为4位(3301 杭州市 市级区域),则areaLevel可以为COUNTY<br>
     * 传入id长度为6位(市辖区 市辖区 区/县级区域),则areaLevel参数无效,且只会返回自身区域信息<br>
     * 例如:传入id为33(浙江省),areaLevel为COUNTY<br>
     * 则查询出浙江省下所有市级区域 和 区/县级区域<br>
     * 例如:传入id为33(浙江省),areaLevel为CITY<br>
     * 则仅查询出浙江省下所有市级区域<br>
     **/
    private AreaLevel areaLevel;
    /**
     * 返回的数据是否包含自身节点
     **/
    private boolean containSelf;


    /**
     * 功能描述：区域等级<br>
     *
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2021/12/27 15:47
     **/
    public enum AreaLevel {
        /**
         * 省
         **/
        PROVINCE,
        /**
         * 市
         **/
        CITY,
        /**
         * 区
         **/
        COUNTY;
    }

    public QuerySpecifiedLevelAreaDataVo() {
    }

    public QuerySpecifiedLevelAreaDataVo(String id, AreaLevel areaLevel, boolean containSelf) {
        this.id = id;
        this.areaLevel = areaLevel;
        this.containSelf = containSelf;
    }
}
