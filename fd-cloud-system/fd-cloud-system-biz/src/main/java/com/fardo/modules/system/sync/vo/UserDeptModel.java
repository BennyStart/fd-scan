package com.fardo.modules.system.sync.vo;

import lombok.Data;

/**
 * TODO
 *
 * @author guohh
 * @date 2022/4/29-9:54
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Data
public class UserDeptModel {

    /**
     * 'user表uid'
     */
    private String userUid;

    /**
     * '部门id'
     */
    private String deptId;

    /**
     * '是否主组织机构'  1是 其他不是
     */
    private String masterOu;

}
