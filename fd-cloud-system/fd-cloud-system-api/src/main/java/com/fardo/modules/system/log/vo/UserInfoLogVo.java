package com.fardo.modules.system.log.vo;

import com.fardo.modules.system.sys.vo.LoginUserVo;
import lombok.Data;

/**
 * @(#)UserInfoLogVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/7/22 10:19
 * 描　述：
 */
@Data
public class UserInfoLogVo {

    private String userId;
    private String xm;
    private String idCard;
    private String username;
    private String departId;
    private String departCode;
    private String departNmae;
    private String policeNo;

    public UserInfoLogVo() {
    }

    public UserInfoLogVo(LoginUserVo vo) {
        this.userId = vo.getId();
        this.xm = vo.getRealname();
        this.idCard = vo.getIdcard();
        this.username = vo.getUsername();
        this.departId = vo.getDepartVo().getId();
        this.departCode = vo.getDepartVo().getDepartCode();
        this.departNmae = vo.getDepartVo().getName();
        this.policeNo = vo.getPoliceNo();
    }

}
