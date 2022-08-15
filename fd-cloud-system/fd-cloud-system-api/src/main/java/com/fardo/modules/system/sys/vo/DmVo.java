package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)DmVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/6/30 10:05
 * 描　述：
 */
@Data
public class DmVo {
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("名称")
    private String mc;

    public DmVo() {
    }

    public DmVo(String id, String mc) {
        this.id = id;
        this.mc = mc;
    }
}
