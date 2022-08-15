package com.fardo.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.fardo.modules.system.depart.model.SysDepartTreeModel;
import com.fardo.modules.system.depart.service.ISysDepartService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @(#)SysDepartServiceTest <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/9 17:35
 * 描　述：
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SysDepartServiceTest {

    @Autowired
    private ISysDepartService sysDepartService;

    @Test
    public void queryMyDeptTreeList() {
        List<SysDepartTreeModel> list = sysDepartService.queryTreeList("1");
        log.info(JSON.toJSONString(list));
    }
}
