package com.fardo.modules.system.sync.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.util.DateUtils;
import com.fardo.common.util.PasswordUtil;
import com.fardo.common.util.PinyinUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.common.util.dynamic.db.DynamicDBUtil;
import com.fardo.modules.system.area.entity.SysAreaEntity;
import com.fardo.modules.system.area.service.ISysAreaService;
import com.fardo.modules.system.ay.service.BlAySyncService;
import com.fardo.modules.system.config.entity.SysPrivateDataEntity;
import com.fardo.modules.system.config.service.ISysPrivateDataService;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.mapper.SysDepartMapper;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.mapper.SysRoleMapper;
import com.fardo.modules.system.sync.service.ITzfwDataSyncService;
import com.fardo.modules.system.sync.vo.BjfwDataSyncVo;
import com.fardo.modules.system.sync.vo.UserDeptModel;
import com.fardo.modules.system.user.entity.SysUserDepartEntity;
import com.fardo.modules.system.user.entity.SysUserDepartRolesEntity;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.entity.SysUserRoleEntity;
import com.fardo.modules.system.user.mapper.SysUserDepartMapper;
import com.fardo.modules.system.user.mapper.SysUserDepartRolesMapper;
import com.fardo.modules.system.user.service.ISysUserDepartRolesService;
import com.fardo.modules.system.user.service.ISysUserDepartService;
import com.fardo.modules.system.user.service.ISysUserRoleService;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.znbl.ywxt.entity.BlDmAyEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
// @Transactional(rollbackFor = Exception.class)
public class TzfwDataSyncServiceImpl implements ITzfwDataSyncService {

    private static final String SELECT_DEPART_SQL = "select * from sys_dept_info_fd where update_time > ?";
    private static final String ONE_SELECT_DEPART_SQL = "select * from sys_dept_info_fd where op != 'D' and update_time > ?";
    private static final String SELECT_DEPART_INFO_SQL = "select * from sys_dept_info_fd where dept_code = ?";
    private static final String SELECT_USER_SQL = "select * from syn_user_info_fd where cd_time > ? limit ?,?";
    private static final String ONE_SELECT_USER_SQL = "select * from syn_user_info_fd where op != 'D' and cd_time > ? limit ?,?";
    private static final String SELECT_DEPT_USER_SQL = "select * from sys_users_depts_fd where op != 'D' and user_uid = ?";

    private static final int BATCH_SIZE = 10000;
    private static final String loginModes = "[\"1\",\"2\"]";
    private static final String SF_DEFAULT_AREACODE = "33";

    private static final String SELECT_AY_SQL = "select * from syn_audit_punish_basis_info_fd where cd_time > ?";

    @Autowired
    private ISysDepartService departService;
    @Autowired
    private ISysPrivateDataService privateDataService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysUserDepartService userDepartService;
    @Autowired
    private ISysUserDepartRolesService userDepartRolesService;
    @Autowired
    private ISysUserRoleService userRoleService;
    @Resource
    private SysUserDepartMapper sysUserDepartMapper;
    @Resource
    private SysUserDepartRolesMapper sysUserDepartRolesMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysDepartMapper departMapper;
    @Autowired
    private ISysAreaService sysAreaService;
    @Autowired
    private BlAySyncService blAySyncService;


    @Override
    public void syncDepartAndUserData() {
        BjfwDataSyncVo config = new BjfwDataSyncVo();
        SysPrivateDataEntity data = privateDataService.getById("TZFW_JG_YH_SJTB");
        String currentTime = DateUtils.date2Str(DateUtils.datetimeFormat.get());
        //标志第一次执行
        boolean flag = true;
        if (!ObjectUtils.isEmpty(data) && StringUtil.isNotEmpty(data.getData())) {
            config = JSON.toJavaObject(JSON.parseObject(data.getData()), BjfwDataSyncVo.class);
            flag = false;
        } else {
            data = new SysPrivateDataEntity("TZFW_JG_YH_SJTB", null);
        }
        try {
            //同步部门信息
            boolean b = syncDepartData(config, flag);
            //部门同步成功再去同步用户避免找不到用户
            if (b) {
                syncUserData(config, flag);
            }
            syncAyData(config);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            config.setUserUpdateTime(currentTime);
            config.setDepartUpdateTime(currentTime);
            config.setAyUpdateTime(currentTime);
            data.setData(JSON.toJSONString(config));
            privateDataService.saveOrUpdate(data);
            log.info("异步处理path。。。");
            //异步处理syscode、path
            ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();
            fixedThreadPool.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        log.info("异步处理中。。。");
                        handleDepartPathData();
                    } catch (Exception e) {
                        log.error("异步处理path和sysCode失败，请检查日志！");
                        log.error(e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }));
        }
    }

    public void syncAyData(BjfwDataSyncVo config) {
        //当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String format1 = format.format(new Date());
        String time = getQueryTime(config.getAyUpdateTime());
        log.info("开始同步台州案由数据,最后一次更新时间：{}", time);
        long t = System.currentTimeMillis();
        log.info("案由增量量同步：{}，时间：{}", SELECT_AY_SQL, time);
        List<Map<String, Object>> list = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, SELECT_AY_SQL, time);
        log.info("获取案由条数，{}", list.size());
        log.info("开始处理案由数据");
        if (!CollectionUtils.isEmpty(list)) {
            for (Map<String, Object> map : list) {
                String task_code = (String) map.get("task_code");
                BlDmAyEntity byId = blAySyncService.getById(task_code);
                if (ObjectUtils.isEmpty(byId)){
                    log.info("新增案由");
                    BlDmAyEntity blDmAyEntity = new BlDmAyEntity();
                    blDmAyEntity.setId(task_code);
                    String task_name = (String) map.get("task_name");
                    blDmAyEntity.setMc(task_name);
                    String punish_basis = (String) map.get("punish_basis");
                    if (StringUtils.isNotBlank(punish_basis)){
                        punish_basis = punish_basis.replace("\"","");
                    }
                    blDmAyEntity.setFl(punish_basis);
                    blDmAyEntity.setXh(null);
                    String cdOperation = (String) map.get("cd_operation");
                    String bz = "0";
                    if (StringUtils.isNotBlank(cdOperation)){
                        if (!cdOperation.equals("D")){
                            bz = "1";
                        }
                    }
                    blDmAyEntity.setBz(bz);
                    blDmAyEntity.setZjkbh(null);
                    blDmAyEntity.setAybz("综合行政执法");
                    blDmAyEntity.setPy(PinyinUtil.getPinYinHeadChar(task_name));
                    blDmAyEntity.setCs("0");
                    blDmAyEntity.setSource("用户案由");
                    String punish_detail = (String) map.get("punish_detail");
                    if (StringUtils.isNotBlank(punish_detail)){
                        punish_detail = punish_detail.replace("\"","");
                    }
                    blDmAyEntity.setFltw(punish_detail);
                    blDmAyEntity.setDy((String) map.get("areacode"));
                    blDmAyEntity.setCreateBy("e9ca23d68d884d4ebb19d07889727dae");
                    blDmAyEntity.setCreateTime(format1);
                    blAySyncService.save(blDmAyEntity);
                }else {
                    log.info("更新案由");
                    String task_name = (String) map.get("task_name");
                    byId.setMc(task_name);
                    String punish_basis = (String) map.get("punish_basis");
                    if (StringUtils.isNotBlank(punish_basis)){
                        punish_basis = punish_basis.replace("\"","");
                    }
                    byId.setFl(punish_basis);
                    String cdOperation = (String) map.get("cd_operation");
                    String bz = "0";
                    if (StringUtils.isNotBlank(cdOperation)){
                        if (!cdOperation.equals("D")){
                            bz = "1";
                        }
                    }
                    byId.setBz(bz);
                    byId.setPy(PinyinUtil.getPinYinHeadChar(task_name));
                    byId.setCs("0");
                    String punish_detail = (String) map.get("punish_detail");
                    if (StringUtils.isNotBlank(punish_detail)){
                        punish_detail = punish_detail.replace("\"","");
                    }
                    byId.setFltw(punish_detail);
                    byId.setDy((String) map.get("areacode"));
                    byId.setUpdateBy("e9ca23d68d884d4ebb19d07889727dae");
                    byId.setUpdateTime(format1);
                    blAySyncService.saveOrUpdate(byId);
                }
            }
        }
        log.info("结束同步台州执法案由数据，本次共更新记录{}条，耗时{}秒", list.size(), (System.currentTimeMillis() - t) / 1000);
    }

    public boolean syncDepartData(BjfwDataSyncVo config, boolean flag) {
        //机构数量不多全部加载
        String time = getQueryTime(config.getDepartUpdateTime());
        log.info("开始同步台州执法部门数据,最后一次更新时间：{}", time);
        long t = System.currentTimeMillis();
        log.info("开始获取部门数据");
        List<Map<String, Object>> list = null;
        if (flag) {
            log.info("第一次部门全量同步：{}，时间：{}", ONE_SELECT_DEPART_SQL, time);
            list = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, ONE_SELECT_DEPART_SQL, time);
            log.info("获取部门条数，{}", list.size());
        } else {
            log.info("部门增量量同步：{}，时间：{}", SELECT_DEPART_SQL, time);
            list = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, SELECT_DEPART_SQL, time);
            log.info("获取部门条数，{}", list.size());
        }
        log.info("开始处理部门数据");
        List<SysDepartEntity> departList = wrapDepartList(list);
        if (!CollectionUtils.isEmpty(departList)) {
            log.info("开始更新部门数据");
            departService.saveOrUpdateBatch(departList);
        }
        log.info("结束同步台州执法部门数据，本次共更新记录{}条，耗时{}秒", departList.size(), (System.currentTimeMillis() - t) / 1000);
        return true;
    }

    public String getQueryTime(String time) {
        if (StringUtil.isBlank(time)) {
            return "2000-01-01 00:00:00";
        }
        return time;
    }

    public void syncUserData(BjfwDataSyncVo config, boolean flag) {
        String time = getQueryTime(config.getUserUpdateTime());
        log.info("开始同步台州执法用户数据,最后一次更新时间：{}", time);
        long t = System.currentTimeMillis();
        SysRoleEntity role = sysRoleMapper.getRoleByCode("PTMJ");
        if (ObjectUtils.isEmpty(role)) {
            log.warn("默认角色【PTMJ】被删除");
            return;
        }
        String roleId = role.getId();
        int idx = 0;
        List<Map<String, Object>> list = null;
        if (flag) {
            log.info("第一次用户全量同步：{}，{}，{}，{}", ONE_SELECT_USER_SQL, time, idx, BATCH_SIZE);
            list = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, ONE_SELECT_USER_SQL, time, idx, BATCH_SIZE);
            log.info("获取用户条数，{}", list.size());
        } else {
            log.info("用户增量同步：{}，{}，{}，{}", SELECT_USER_SQL, time, idx, BATCH_SIZE);
            list = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, SELECT_USER_SQL, time, idx, BATCH_SIZE);
            log.info("获取用户条数，{}", list.size());
        }
        log.info("同步条数，同步时间：{}，{}", list.size(), time);
        while (!CollectionUtils.isEmpty(list)) {
            List<SysUserEntity> userList = wrapUserList(list);
            List<SysUserEntity> toAddUserList = new ArrayList<>(userList.size());
            List<SysUserRoleEntity> toAddUrList = new ArrayList<>(userList.size());
            List<SysUserDepartEntity> toAddUdList = new ArrayList<>(userList.size());
            List<SysUserDepartRolesEntity> toAddUdrList = new ArrayList<>(userList.size());
            for (SysUserEntity vo : userList) {
                SysUserEntity po = userService.getById(vo.getId());
                //新增用户
                if (ObjectUtils.isEmpty(po)) {
                    SysUserRoleEntity ur = new SysUserRoleEntity(vo.getId(), roleId);
                    toAddUrList.add(ur);
                    //处理多部门角色
                    List<Map<String, Object>> userDeptList = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, SELECT_DEPT_USER_SQL, vo.getUserUid());
                    if (!CollectionUtils.isEmpty(userDeptList)) {
                        log.info("处理多部门");
                        if (userDeptList.size() > 1) {
                            vo.setOtherPositionFlag(true);
                        }
                        List<UserDeptModel> userDeptModels = wrapUserDeptList(userDeptList);
                        for (UserDeptModel userDeptModel : userDeptModels) {
                            SysUserDepartEntity ud = new SysUserDepartEntity(userDeptModel.getUserUid(), userDeptModel.getDeptId());
                            toAddUdList.add(ud);
                            SysUserDepartRolesEntity udr = new SysUserDepartRolesEntity(userDeptModel.getUserUid(), userDeptModel.getDeptId(), roleId);
                            toAddUdrList.add(udr);
                            //设置主部门
                            if (userDeptModel.getMasterOu().equals("1")) {
                                vo.setDepartId(userDeptModel.getDeptId());
                            }
                        }
                    } else {
                        vo.setOtherPositionFlag(false);
                        SysUserDepartEntity ud = new SysUserDepartEntity(vo.getId(), vo.getDepartId());
                        toAddUdList.add(ud);
                        SysUserDepartRolesEntity udr = new SysUserDepartRolesEntity(vo.getId(), vo.getDepartId(), roleId);
                        toAddUdrList.add(udr);
                    }
                    toAddUserList.add(vo);
                } else {
                    //更新用户
                    po.setUsername(vo.getUsername());
                    po.setRealname(vo.getRealname());
                    po.setRealnamePy(vo.getRealnamePy());
                    po.setPoliceNo(vo.getPoliceNo());
                    po.setDelFlag(vo.getDelFlag());
                    po.setStatus(vo.getStatus());
                    po.setMobilephone(vo.getMobilephone());
                    String deptId = vo.getDepartId();
                    if (StringUtils.isNotBlank(vo.getDepartId()) && StringUtils.isNotBlank(po.getDepartId())) {
                        //这边是笔录那边删除后对那个用户角色也清空了 ，如果删除数据是用户数据是存在的只是del_flag逻辑删除，角色那些删除了，同步的时候需要处理
                        userRoleService.deleteByUserId(vo.getId());
                        SysUserRoleEntity ur = new SysUserRoleEntity(vo.getId(), roleId);
                        toAddUrList.add(ur);
                        //处理多部门角色
                        List<Map<String, Object>> userDeptList = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, SELECT_DEPT_USER_SQL, vo.getUserUid());
                        if (!CollectionUtils.isEmpty(userDeptList)) {
                            if (userDeptList.size() > 1) {
                                po.setOtherPositionFlag(true);
                            }
                            log.info("处理多部门");
                            List<UserDeptModel> userDeptModels = wrapUserDeptList(userDeptList);
                            sysUserDepartMapper.deleteByUserId(vo.getId());
                            sysUserDepartRolesMapper.deleteByUserId(vo.getId());
                            for (UserDeptModel userDeptModel : userDeptModels) {
                                SysUserDepartEntity ud = new SysUserDepartEntity(userDeptModel.getUserUid(), userDeptModel.getDeptId());
                                toAddUdList.add(ud);
                                SysUserDepartRolesEntity udr = new SysUserDepartRolesEntity(userDeptModel.getUserUid(), userDeptModel.getDeptId(), roleId);
                                toAddUdrList.add(udr);
                                //设置主部门
                                if (userDeptModel.getMasterOu().equals("1")) {
                                    deptId = userDeptModel.getDeptId();
                                }
                            }
                        } else {
                            po.setOtherPositionFlag(false);
                            sysUserDepartMapper.deleteByUserId(vo.getId());
                            SysUserDepartEntity ud = new SysUserDepartEntity(vo.getId(), vo.getDepartId());
                            toAddUdList.add(ud);
                            sysUserDepartRolesMapper.deleteByUserId(vo.getId());
                            SysUserDepartRolesEntity udr = new SysUserDepartRolesEntity(vo.getId(), vo.getDepartId(), roleId);
                            toAddUdrList.add(udr);
                        }
                    }
                    log.info("原来的部门，{}", po.getDepartId());
                    po.setDepartId(deptId);
                    log.info("更新的部门，{}", vo.getDepartId());
                    userService.updateById(po);
                }
            }
            if (!CollectionUtils.isEmpty(toAddUserList)) {
                userService.saveBatch(toAddUserList);
            }
            if (!CollectionUtils.isEmpty(toAddUrList)) {
                userRoleService.saveBatch(toAddUrList);
            }
            if (!CollectionUtils.isEmpty(toAddUdList)) {
                userDepartService.saveBatch(toAddUdList);
            }
            if (!CollectionUtils.isEmpty(toAddUdrList)) {
                userDepartRolesService.saveBatch(toAddUdrList);
            }
            log.info("同步台州执法用户数据，本次共更新记录{}条", userList.size());
            idx = idx + BATCH_SIZE;
            list = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, SELECT_USER_SQL, time, idx, BATCH_SIZE);
        }
        log.info("结束同步台州执法用户数据，共耗时{}秒", (System.currentTimeMillis() - t) / 1000);
    }


    public List<SysDepartEntity> wrapDepartList(List<Map<String, Object>> list) {
        List<SysDepartEntity> departList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            LambdaQueryWrapper<SysAreaEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(SysAreaEntity::getEnable, SysConstants.YES);
            List<SysAreaEntity> areaList = sysAreaService.list(lambdaQueryWrapper);
            departList = new ArrayList<>(list.size());
            for (Map<String, Object> map : list) {
                String dept_code = (String) map.get("dept_code");
                QueryWrapper<SysDepartEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("org_code", dept_code);
                SysDepartEntity sysDepartEntity = departMapper.selectOne(queryWrapper);
                String deptId = null;
                String parentId = null;
                String sourceDeptId = null;
                if (ObjectUtils.isEmpty(sysDepartEntity)) {
                    log.info("新增部门");
                    //新增
                    deptId = stringToMD5("" + map.get("dept_id"));
                    String parentDeptCode = "" + map.get("parent_dept_code");
                    if (!"0".equals(parentDeptCode) || StringUtils.isNotBlank(parentDeptCode)) {
                        //根据部门code查部门id
                        List<Map<String, Object>> list1 = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, SELECT_DEPART_INFO_SQL, parentDeptCode);
                        if (!CollectionUtils.isEmpty(list1)) {
                            Map<String, Object> objectMap = list1.get(0);
                            parentId = stringToMD5("" + objectMap.get("dept_id"));
                        }
                    }
                } else {
                    log.info("修改部门");
                    //修改
                    deptId = sysDepartEntity.getId();
                    parentId = sysDepartEntity.getParentId();
                    sourceDeptId = sysDepartEntity.getDeptId();
                }
                SysDepartEntity po = new SysDepartEntity();
                po.setId(deptId);
                Long depId = (Long) map.get("dept_id");
                String dept_id = String.valueOf(depId);
                if (StringUtils.isNotBlank(sourceDeptId)) {
                    dept_id = sourceDeptId + "," + dept_id;
                }
                po.setDeptId(dept_id);
                po.setParentId(parentId);
                String departName = (String) map.get("dept_name");
                po.setDepartName(departName);
                po.setDepartNamePinyinAbbr(PinyinUtil.getPinYinHeadChar(departName));
                po.setDepartNameAbbr(departName);
                po.setDepartNameAlias(departName);
                po.setOrgType((String) map.get("type"));
                po.setOrgCode(dept_code);
                Boolean enabled1 = (Boolean) map.get("enabled");
                if (enabled1) {
                    po.setDelFlag(0);
                } else {
                    po.setDelFlag(1);
                }
                // 保存源区域代码
                po.setOriginalAreaCode((String) map.get("area_code"));
                // 处理源区域代码
                if (StringUtils.isNotBlank(po.getOriginalAreaCode())) {
                    String areaCode = po.getOriginalAreaCode().replaceAll("0+$", "");
                    // XX10 的0也会被替代,故需要判断是否长度为偶数
                    if (StringUtils.length(areaCode) % 2 != 0) {
                        areaCode = StringUtils.rightPad(areaCode, StringUtils.length(areaCode) + 1, "0");
                    }
                    String targetAreaCode = null;
                    tag:
                    for (SysAreaEntity item : areaList) {
                        if (item.getId().equals(areaCode)) {
                            targetAreaCode = item.getId();
                            break tag;
                        }
                    }
                    po.setAreaCode(StringUtils.isNotBlank(targetAreaCode) ? targetAreaCode : SF_DEFAULT_AREACODE);
                }
                departList.add(po);
            }
        }
        return departList;
    }

    public void getSysDepartEntity(List<SysDepartEntity> tempList) {
        int index = 0;
        for (SysDepartEntity sysDepartEntity1 : tempList) {
            index++;
            String sysCode = generatePath(index, sysDepartEntity1.getSysCode());
            SysDepartEntity sysDepartEntity = departMapper.selectById(sysDepartEntity1.getId());
            sysDepartEntity.setSysCode(sysCode);
            if ("1".equals(sysDepartEntity.getOrgType())) {
                log.info("异步处理机构:", index);
                sysDepartEntity.setPath(sysCode);
            } else if ("2".equals(sysDepartEntity.getOrgType())) {
                log.info("异步处理部门:", index);
                //二级部门也需要生成
                String sysCodeChild = generatePath(index, sysDepartEntity1.getSysCode());
                String sysCode1 = "";
                //因为还没有完全保存数据库在这边要临时处理path需要获取临时保存的数据
                if (!CollectionUtils.isEmpty(tempList)) {
                    for (SysDepartEntity departEntity : tempList) {
                        if (departEntity.getId().equals(sysDepartEntity.getParentId())) {
                            sysCode1 = departEntity.getSysCode();
                        }
                    }
                }
                sysDepartEntity.setPath(sysCode1 + sysCodeChild);
            }
            departMapper.updateById(sysDepartEntity);
        }
    }

    /**
     * 生成sys_code
     *
     * @return
     */
    public String generatePath(int i, String sysCode) {
        //判断机构列表是否不为空
        if (!ObjectUtils.isEmpty(sysCode)) {
            //判断下机构代码是否为空咯
            if (StringUtils.isNotBlank(sysCode)) {
                //如果不为空就在原有的基础上递增加1把
                Integer index = Integer.valueOf(sysCode) + i;
                //然后需要在去查一次数据库看下是否有重复
                String s = new DecimalFormat("00000").format(index);
                s = s.substring(s.length() - 5, s.length());
                //递归查询
                String repeat = repeat(s);
                return repeat;

            }
        }
        //当没有数据时给个默认值去递归查询重复
        String repeat = repeat("00000");
        return repeat;
    }

    /**
     * 递归查询sys_code是否重复
     *
     * @return
     */
    public String repeat(String sysCode) {
        SysDepartEntity departEntity = departMapper.getSysDepartEntityInfo(sysCode);
        if (ObjectUtils.isEmpty(departEntity)) {
            return sysCode;
        } else {
            //如果不为空就在原有的基础上递增加1把
            Integer index = Integer.valueOf(departEntity.getSysCode()) + 1;
            //然后需要在去查一次数据库看下是否有重复
            String s = new DecimalFormat("00000").format(index);
            s = s.substring(s.length() - 5, s.length());
            return repeat(s);
        }
    }

    /**
     * 处理多部门
     *
     * @param list
     * @return
     */
    public List<UserDeptModel> wrapUserDeptList(List<Map<String, Object>> list) {
        List<UserDeptModel> userDeptList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            userDeptList = new ArrayList<>(list.size());
            for (Map<String, Object> map : list) {
                UserDeptModel model = new UserDeptModel();
                model.setUserUid(stringToMD5("" + map.get("user_uid")));
                //查下部门id
                QueryWrapper<SysDepartEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("org_code", "" + map.get("dept_code"));
                SysDepartEntity sysDepartEntity = departMapper.selectOne(queryWrapper);
                log.info("根据code查询返回多部门部门信息，{}", sysDepartEntity);
                if (!ObjectUtils.isEmpty(sysDepartEntity)) {
                    String id = sysDepartEntity.getId();
                    model.setDeptId(id);
                } else {
                    log.info("用户：【" + map.get("user_uid") + "】没有从部门视图查到数据，{}", map.get("dept_code"));
                }
                //对方可能为空
                Object master_ou1 = map.get("master_ou");
                if (ObjectUtils.isEmpty(master_ou1)) {
                    model.setMasterOu("0");
                } else {
                    Boolean master_ou = (Boolean) master_ou1;
                    if (master_ou) {
                        model.setMasterOu("1");
                    } else {
                        model.setMasterOu("0");
                    }
                }
                userDeptList.add(model);
            }
        }
        return userDeptList;
    }


    public List<SysUserEntity> wrapUserList(List<Map<String, Object>> list) {
        List<SysUserEntity> userList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            userList = new ArrayList<>(list.size());
            for (Map<String, Object> map : list) {
                SysUserEntity po = new SysUserEntity();
                po.setId(stringToMD5("" + map.get("user_uid")));
                po.setUserUid("" + map.get("user_uid"));
                po.setUsername((String) map.get("user_name"));
                if ("admin".equals(po.getUsername())) {
                    continue;
                }
                po.setRealname((String) map.get("nick_name"));
                po.setRealnamePy(PinyinUtil.getPinYinHeadChar(po.getRealname()));
                po.setMobilephone((String) map.get("phone"));
                po.setPoliceNo((String) map.get("executor_id"));
                po.setIdcard((String) map.get("id_card"));
                //根据部门code查部门id
                log.info("根据code查询部门，{}", map.get("dept_code"));
                QueryWrapper<SysDepartEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("org_code", "" + map.get("dept_code"));
                SysDepartEntity sysDepartEntity = departMapper.selectOne(queryWrapper);
                log.info("根据code查询返回部门信息，{}", sysDepartEntity);
                if (!ObjectUtils.isEmpty(sysDepartEntity)) {
                    String id = sysDepartEntity.getId();
                    po.setDepartId(id);
                } else {
                    log.info("用户：【" + map.get("user_name") + ",警号：" + map.get("executor_id") + "】没有从部门查到数据，{}", map.get("dept_code"));
                }
                // List<Map<String, Object>> dept_code = DynamicDBUtil.findList(ITzfwDataSyncService.DB_KEY, SELECT_DEPART_INFO_SQL, "" + map.get("dept_code"));
                // log.info("根据code查询返回部门信息，{}",dept_code);
                // if (!CollectionUtils.isEmpty(dept_code)) {
                //     Map<String, Object> objectMap = dept_code.get(0);
                //     po.setDepartId(stringToMD5("" + objectMap.get("dept_id")));
                // }else {
                //     log.info("用户：【" + map.get("user_name") + ",警号：" + map.get("executor_id") + "】没有从部门视图查到数据，{}",map.get("dept_code"));
                // }
                String status = (String) map.get("op");
                if ("D".equals(status)) {
                    po.setStatus(CommonConstant.USER_FREEZE);
                    po.setDelFlag(1);
                } else {
                    po.setStatus(CommonConstant.USER_UNFREEZE);
                    po.setDelFlag(0);
                }
                po.setUserIdentity(0);
                po.setType("1");
                po.setLoginModes(loginModes);
                po.setOtherPositionFlag(false);
                po.setSex("0");
                userList.add(po);
            }
        }
        return userList;
    }

    /**
     * 使用JDK自带的API实现
     *
     * @param text
     * @return
     */
    public static String stringToMD5(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    text.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public void handleDepartPathData() {
        Long start = System.currentTimeMillis();
        log.info("部门路径旧数据处理begin");
        // 从数据库获取所有部门数据
        List<SysDepartEntity> list = departService.list();
        // 维护部门路径
        if (!CollectionUtils.isEmpty(list)) {
            //重新生成sysCode
            HashSet<String> sysCodeSet = new HashSet<>(list.size());
            for (SysDepartEntity item : list) {
                String syscode = com.fardo.common.util.StringUtil.random(6);
                while (sysCodeSet.contains(syscode)) {
                    syscode = com.fardo.common.util.StringUtil.random(6);
                }
                item.setSysCode(syscode);
                item.setPath(null);
                sysCodeSet.add(syscode);
            }
            for (SysDepartEntity item : list) {
                recursivelyFindSuperior(list, item);
            }
            departService.updateBatchById(list);
        }
        log.info("部门路径旧数据处理end.用时:" + (System.currentTimeMillis() - start) / 1000 + "秒。");
    }

    private void recursivelyFindSuperior(List<SysDepartEntity> allDepartList, SysDepartEntity dept) {
        log.info("正在处理[{}]部门的部门路径数据,部门编号为[{}]", new String[]{dept.getDepartName(), dept.getOrgCode()});
        // 先找到自己的上级部门 更新自己的部门路径
        Optional<SysDepartEntity> parentDepart = allDepartList.stream().filter(vo -> vo.getId().equals(dept.getParentId())).findFirst();
        // 有上级部门编号,但没有找到自己的上级部门
        if (!parentDepart.isPresent()) {
            log.warn("未找到上级编号为[{}]的部门,是否为顶级部门", dept.getParentId());
            dept.setPath(dept.getSysCode());
            return;
        } else {
            // 若上级部门也没有部门路径,继续递归查找上级部门的部门维护其路径
            if (com.fardo.common.util.StringUtil.isEmpty(parentDepart.get().getPath())) {
                recursivelyFindSuperior(allDepartList, parentDepart.get());
            }
            dept.setPath(parentDepart.get().getPath().concat(CommonConstant.CHAR_SPLIT_COMMA).concat(dept.getSysCode()));
        }
    }

}
