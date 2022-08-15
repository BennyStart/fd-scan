package com.fardo.modules.system.vo;

import lombok.Data;

/**
 * @(#)TabColumnVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/7/14 9:01
 * 描　述：
 */
@Data
public class TabColumnVo {
    private String columnName;
    private String dataType;
    private String dataLength;
    private String comments;
}
