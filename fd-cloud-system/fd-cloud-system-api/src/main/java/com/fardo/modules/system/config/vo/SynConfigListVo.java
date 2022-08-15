package com.fardo.modules.system.config.vo;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SynConfigListVo {

    @Valid
    private List<SynConfigVo> configs;

}
