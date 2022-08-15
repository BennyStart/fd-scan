package com.fardo.modules.system.depart.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.constant.CacheConstant;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.exception.ApiException;
import com.fardo.common.exception.FdException;
import com.fardo.common.system.vo.DictModel;
import com.fardo.common.util.PinyinUtil;
import com.fardo.modules.system.area.entity.SysAreaFdEntity;
import com.fardo.modules.system.area.mapper.SysAreaFdMapper;
import com.fardo.modules.system.config.service.ISysDictService;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.enums.DepartResultCodeEnum;
import com.fardo.modules.system.depart.mapper.SysDepartMapper;
import com.fardo.modules.system.depart.model.DepartIdModel;
import com.fardo.modules.system.depart.model.SysDepartModel;
import com.fardo.modules.system.depart.model.SysDepartTreeModel;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.depart.vo.SysDepartExcelVo;
import com.fardo.modules.system.depart.vo.SysDepartParamVo;
import com.fardo.modules.system.depart.vo.SysDepartVo;
import com.fardo.modules.system.util.FindsDepartsChildrenUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * <p>
 *
 * @Author Steve
 * @Since 2019-01-22
 */
@Slf4j
@Service
public class SysDepartServiceImpl extends ServiceImpl<SysDepartMapper, SysDepartEntity> implements ISysDepartService {

    @Resource
    private SysDepartMapper departMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ISysDictService sysDictService;
    @Autowired
    private SysAreaFdMapper hxAreaMapper;

    @Override
    public IPage<SysDepartModel> getPage(SysDepartParamVo sysDepartParamVo) {
        if (!StringUtils.isEmpty(sysDepartParamVo.getPath())) {
            sysDepartParamVo.setPath(String.format("%s%%", sysDepartParamVo.getPath()));
        }else{
            if(!StringUtils.isEmpty(sysDepartParamVo.getId())) {
                SysAreaFdEntity area = hxAreaMapper.getByAreaCode(sysDepartParamVo.getId());
                if(!ObjectUtils.isEmpty(area)) {
                    sysDepartParamVo.setOriginalAreaCode(area.getAreaCode());
                }
            }
        }
        if (!StringUtils.isEmpty(sysDepartParamVo.getDepartName())) {
            sysDepartParamVo.setDepartName(String.format("%%%s%%", sysDepartParamVo.getDepartName()));
        }
        if (!StringUtils.isEmpty(sysDepartParamVo.getOrgCode())) {
            sysDepartParamVo.setOrgCode(String.format("%%%s%%", sysDepartParamVo.getOrgCode()));
        }
        return this.departMapper.getPageList(new Page(sysDepartParamVo.getPageNo(), sysDepartParamVo.getPageSize()), sysDepartParamVo);
    }

    /**
     * queryTreeList 对应 queryTreeList 查询所有的部门数据,以树结构形式响应给前端
     */
    @Cacheable(value = CacheConstant.SYS_DEPARTS_CACHE)
    @Override
    public List<SysDepartTreeModel> queryTreeList(String departId) {
        LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<SysDepartEntity>();
        if (!StringUtils.isEmpty(departId)) {
            query.or().likeRight(SysDepartEntity::getPath, departId);
        }
        query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SysDepartEntity> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
        return listResult;
    }

    @Override
    public List<SysDepartModel> querySubDeptList(String departId) {
        List<SysDepartModel> records = new ArrayList<>();
        LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<>();
        if (StringUtils.isEmpty(departId)) {
            query.isNull(SysDepartEntity::getParentId);
        } else {
            query.eq(SysDepartEntity::getParentId, departId);
        }
        query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
        query.orderByAsc(SysDepartEntity::getOrgCode);
        List<SysDepartEntity> list = this.list(query);
        if (!CollectionUtils.isEmpty(list)) {
            List<String> ids = new ArrayList<>(list.size());
            list.forEach(d -> {
                ids.add(d.getId());
                records.add(new SysDepartModel(d));
            });
            query = new LambdaQueryWrapper<>();
            query.in(SysDepartEntity::getParentId, ids);
            query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.select(SysDepartEntity::getParentId);
            List<SysDepartEntity> parentList = this.list(query);
            Set<String> parentIds = new HashSet<>(list.size());
            parentList.forEach(d -> {
                parentIds.add(d.getParentId());
            });
            records.forEach(r -> {
                if (!parentIds.contains(r.getId())) {
                    r.setLeaf(true);
                }
            });
        }
        return records;
    }

    @Override
    public List<SysDepartModel> querySubDeptList(String departId, String originalAreaCode, Integer level) {
        List<SysDepartModel> records = new ArrayList<>();
        if(level == null) {
            level = 0;
        }
        if(level <=2) {
            LambdaQueryWrapper<SysAreaFdEntity> query = new LambdaQueryWrapper<>();
            if (StringUtils.isEmpty(departId)) {
                query.eq(SysAreaFdEntity::getSupCode,"0");
            } else {
                query.eq(SysAreaFdEntity::getSupCode, departId);
            }
            query.eq(SysAreaFdEntity::getDeleted, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(SysAreaFdEntity::getSort);
            List<SysAreaFdEntity> list = hxAreaMapper.selectList(query);
            if (!CollectionUtils.isEmpty(list)) {
                SysDepartModel model;
                for(SysAreaFdEntity a : list) {
                    model = new SysDepartModel(a);
                    model.setLevel(level+1);
                    records.add(model);
                }
                if(level == 2) {
                    SysAreaFdEntity area = hxAreaMapper.getByAreaCode(departId);
                    area.setContent("市级");
                    model = new SysDepartModel(area);
                    model.setLevel(level+1);
                    records.add(0,model);
                }
            }
        }else{
            LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<>();
            query.eq(SysDepartEntity::getOriginalAreaCode, originalAreaCode);
            if(level == 3) {
                query.eq(SysDepartEntity::getOrgType, "1");
            } else {
                query.eq(SysDepartEntity::getParentId, departId);
            }
            query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(SysDepartEntity::getOrgCode);
            List<SysDepartEntity> list = this.list(query);
            if (!CollectionUtils.isEmpty(list)) {
                SysDepartModel model;
                for(SysDepartEntity d : list) {
                    model = new SysDepartModel(d);
                    model.setLevel(level + 1);
                    records.add(model);
                }
            }
        }
        return records;
    }

    @Override
    public List<SysDepartModel> queryDeptListByKeyword(String keyword, int limitSize) {
        List<SysDepartModel> records = new ArrayList<>();
        if (!StringUtils.isEmpty(keyword)) {
            LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<>();
            query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.isNotNull(SysDepartEntity::getOriginalAreaCode);
            if (Validator.isGeneralWithChinese(keyword)) {
                //如果是中文，匹配部门名称
                query.like(SysDepartEntity::getDepartName, "%" + keyword + "%");
            } else if (Validator.isLetter(keyword)) {
                //如果是字母，匹配部门首字母小写
                query.likeRight(SysDepartEntity::getDepartNamePinyinAbbr, keyword.toLowerCase());
            } else if (Validator.isNumber(keyword)) {
                //如果是数字，匹配部门机构代码
                query.likeRight(SysDepartEntity::getOrgCode, keyword);
            } else {
                return records;
            }
            IPage<SysDepartEntity> page = new Page<>(1, limitSize);
            this.page(page, query);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                page.getRecords().forEach(d -> {
                    records.add(new SysDepartModel(d));
                });
            }
        }
        return records;
    }

    @Override
    public SysDepartVo getDepartDetail(String id) {
        SysDepartEntity sysDepart = this.getById(id);
        if (sysDepart == null) {
            throw new ApiException(ResultCode.INVALIDPARAMETER);
        }
        SysDepartVo vo = new SysDepartVo();
        BeanUtils.copyProperties(sysDepart, vo);
        //父级机构id不为空时，把父级机构名称赋值进去，让前端展示
        if (!StringUtils.isEmpty(vo.getParentId())) {
            SysDepartEntity parent = this.getById(sysDepart.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getDepartName());
                vo.setParentAreaCode(parent.getAreaCode());
            }
        }
        return vo;
    }

    @Cacheable(value = CacheConstant.SYS_DEPART_IDS_CACHE)
    @Override
    public List<DepartIdModel> queryDepartIdTreeList() {
        LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<SysDepartEntity>();
        query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        List<SysDepartEntity> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<DepartIdModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToDepartIdTreeList(list);
        return listResult;
    }

    /**
     * saveDepartData 对应 add 保存用户在页面添加的新的部门对象数据
     */
    @Override
    @Transactional
    public String saveDepartData(SysDepartEntity sysDepart) {
        //判断机构代码是否重复
        if (findDepartByOrgCode(sysDepart.getOrgCode()) != null) {
            throw new ApiException(DepartResultCodeEnum.ORGCODE_EXIST);
        }
        sysDepart.setSysCode(getSysCode());
        String parentId = sysDepart.getParentId();
        if (!StringUtils.isEmpty(parentId)) {
            SysDepartEntity parentDepart = this.getById(parentId);
            if (parentDepart == null) {
                throw new ApiException(DepartResultCodeEnum.PARENT_NO_EXIST);
            }
            sysDepart.setPath(parentDepart.getPath() + sysDepart.getSysCode());
        } else {
            sysDepart.setParentId(null);
            sysDepart.setPath(sysDepart.getSysCode());
        }
        sysDepart.setDepartNamePinyinAbbr(PinyinUtil.getPinYinHeadChar(sysDepart.getDepartName()));
        sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0);
        this.save(sysDepart);
        return sysDepart.getId();
    }

    /**
     * removeDepartDataById 对应 delete方法 根据ID删除相关部门数据
     *
     */
    /*
     * @Override
     *
     * @Transactional public boolean removeDepartDataById(String id) {
     * System.out.println("要删除的ID 为=============================>>>>>"+id); boolean
     * flag = this.removeById(id); return flag; }
     */

    /**
     * updateDepartDataById 对应 edit 根据部门主键来更新对应的部门数据
     */
    @Override
    @Transactional
    public void updateDepartDataById(SysDepartEntity sysDepart) {
        SysDepartEntity oldDepart = this.getById(sysDepart.getId());
        if (oldDepart == null) {
            throw new ApiException(ResultCode.INVALIDPARAMETER);
        }
        if (sysDepart.getId().equals(sysDepart.getParentId())) {
            throw new ApiException(DepartResultCodeEnum.PARENT_NO_BE_SELF);
        }
        SysDepartEntity orgCodeDepart = findDepartByOrgCode(sysDepart.getOrgCode());
        if (orgCodeDepart != null && !orgCodeDepart.getId().equals(oldDepart.getId())) {
            throw new ApiException(DepartResultCodeEnum.ORGCODE_EXIST);
        }
        String parentId = sysDepart.getParentId();
        if (!StringUtils.isEmpty(parentId)) {
            SysDepartEntity parentDepart = this.getById(parentId);
            if (parentDepart == null) {
                throw new ApiException(DepartResultCodeEnum.PARENT_NO_EXIST);
            }
            if (parentDepart.getPath().startsWith(oldDepart.getPath())) {
                throw new ApiException(DepartResultCodeEnum.PARENT_NO_BE_CHIRLDREN);
            }
            sysDepart.setPath(parentDepart.getPath() + oldDepart.getSysCode());
        } else {
            sysDepart.setParentId(null);
            sysDepart.setPath(oldDepart.getSysCode());
        }
        sysDepart.setDepartNamePinyinAbbr(PinyinUtil.getPinYinHeadChar(sysDepart.getDepartName()));
        sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0);
        this.updateById(sysDepart);
        //上级机构变化，更新下级节点path
        if (StrUtil.compare(oldDepart.getParentId(), sysDepart.getParentId(), true) != 0) {
            //找出该单位所有的下级节点
            LambdaQueryWrapper<SysDepartEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.likeRight(SysDepartEntity::getPath, oldDepart.getPath());
            List<SysDepartEntity> list = this.list(queryWrapper);
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(d -> {
                    d.setPath(d.getPath().replaceFirst(oldDepart.getPath(), sysDepart.getPath()));
                });
                this.updateBatchById(list);
            }
        }
    }

    @Override
    public List<String> getSubDepIdsByDepId(String departId) {
        return this.baseMapper.getSubDepIdsByDepId(departId);
    }

    @Override
    public List<String> getMySubDepIdsByDepId(String departIds) {
        //根据部门id获取所负责部门
        String[] codeArr = this.getMyDeptParentOrgCode(departIds);
        return this.baseMapper.getSubDepIdsByOrgCodes(codeArr);
    }

    /**
     * <p>
     * 根据关键字搜索相关的部门数据
     * </p>
     */
    @Override
    public List<SysDepartTreeModel> searhBy(String keyWord, String myDeptSearch, String departIds) {
        LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<SysDepartEntity>();
        List<SysDepartTreeModel> newList = new ArrayList<>();
        //myDeptSearch不为空时为我的部门搜索，只搜索所负责部门
        if (!StringUtil.isNullOrEmpty(myDeptSearch)) {
            //departIds 为空普通用户或没有管理部门
            if (StringUtil.isNullOrEmpty(departIds)) {
                return newList;
            }
            //根据部门id获取所负责部门
            String[] codeArr = this.getMyDeptParentOrgCode(departIds);
            for (int i = 0; i < codeArr.length; i++) {
                query.or().likeRight(SysDepartEntity::getOrgCode, codeArr[i]);
            }
            query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        }
        query.like(SysDepartEntity::getDepartName, keyWord);
        //update-begin--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索回显优化--------------------
        SysDepartTreeModel model = new SysDepartTreeModel();
        List<SysDepartEntity> departList = this.list(query);
        if (departList.size() > 0) {
            for (SysDepartEntity depart : departList) {
                model = new SysDepartTreeModel(depart);
                model.setChildren(null);
                //update-end--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索功回显优化----------------------
                newList.add(model);
            }
            return newList;
        }
        return null;
    }

    /**
     * 根据部门id删除并且删除其可能存在的子级任何部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logicalDelete(String id) {
        SysDepartEntity depart = this.getById(id);
        if (depart != null) {
            int count = departMapper.getDepartCountForId(id);
            if (count > 0) {
                throw new ApiException(DepartResultCodeEnum.DELETE_FAIL_CHIRLDREN_EXIST);
            }
            //判断该机构及下级机构是否存在用户
            String path = depart.getPath() + "%";
            count = departMapper.getUserCountForPath(path);
            if (count == 0) {
                LambdaUpdateWrapper<SysDepartEntity> updateWrapper = new LambdaUpdateWrapper<SysDepartEntity>();
                updateWrapper.set(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_1);
                updateWrapper.likeRight(SysDepartEntity::getPath, path);
                this.update(updateWrapper);
            } else {
                throw new ApiException(DepartResultCodeEnum.DELETE_FAIL_USER_EXIST);
            }
        }
        return true;
    }

    /**
     * delete 方法调用
     *
     * @param id
     * @param idList
     */
    private void checkChildrenExists(String id, List<String> idList) {
        LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<SysDepartEntity>();
        query.eq(SysDepartEntity::getParentId, id);
        query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SysDepartEntity> departList = this.list(query);
        if (departList != null && departList.size() > 0) {
            for (SysDepartEntity depart : departList) {
                idList.add(depart.getId());
                this.checkChildrenExists(depart.getId(), idList);
            }
        }
    }

    @Override
    public List<SysDepartEntity> queryUserDeparts(String userId) {
        return baseMapper.queryUserDeparts(userId);
    }

    @Override
    public List<SysDepartEntity> queryDepartsByUsername(String username) {
        return baseMapper.queryDepartsByUsername(username);
    }

    /**
     * 根据用户所负责部门ids获取父级部门编码
     *
     * @param departIds
     * @return
     */
    private String[] getMyDeptParentOrgCode(String departIds) {
        //根据部门id查询所负责部门
        LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<SysDepartEntity>();
        query.eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.in(SysDepartEntity::getId, Arrays.asList(departIds.split(",")));
        query.orderByAsc(SysDepartEntity::getOrgCode);
        List<SysDepartEntity> list = this.list(query);
        //查找根部门
        if (list == null || list.size() == 0) {
            return null;
        }
        String orgCode = this.getMyDeptParentNode(list);
        String[] codeArr = orgCode.split(",");
        return codeArr;
    }

    /**
     * 获取负责部门父节点
     *
     * @param list
     * @return
     */
    private String getMyDeptParentNode(List<SysDepartEntity> list) {
        Map<String, String> map = new HashMap<>();
        //1.先将同一公司归类
        for (SysDepartEntity dept : list) {
            String code = dept.getOrgCode().substring(0, 3);
            if (map.containsKey(code)) {
                String mapCode = map.get(code) + "," + dept.getOrgCode();
                map.put(code, mapCode);
            } else {
                map.put(code, dept.getOrgCode());
            }
        }
        StringBuffer parentOrgCode = new StringBuffer();
        //2.获取同一公司的根节点
        for (String str : map.values()) {
            String[] arrStr = str.split(",");
            parentOrgCode.append(",").append(this.getMinLengthNode(arrStr));
        }
        return parentOrgCode.substring(1);
    }

    /**
     * 获取同一公司中部门编码长度最小的部门
     *
     * @param str
     * @return
     */
    private String getMinLengthNode(String[] str) {
        int min = str[0].length();
        String orgCode = str[0];
        for (int i = 1; i < str.length; i++) {
            if (str[i].length() <= min) {
                min = str[i].length();
                orgCode = orgCode + "," + str[i];
            }
        }
        return orgCode;
    }

    /**
     * 获取当前部门顶级部门id
     *
     * @param departId
     * @return
     */
    @Override
    public String getTopDepartId(String departId) {
        SysDepartEntity entity = this.getById(departId);
        if (entity == null) {
            return "";
        }
        if (StringUtils.isEmpty(entity.getParentId())) {
            return entity.getId();
        }
        if (entity.getParentId().equals(entity.getId())) {
            return entity.getId();
        }
        SysDepartEntity parent = this.getById(entity.getParentId());
        if("1".equals(parent.getOrgType())) {
            return parent.getId();
        }
        if (parent != null) {
            return this.getTopDepartId(parent.getId());
        }
        return "";
    }

    /**
     * 获取当前部门顶级部门信息
     *
     * @param departId
     * @return
     */
    @Override
    public SysDepartEntity getTopDepart(String departId) {
        String topDepartId = getTopDepartId(departId);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(topDepartId)){
            return this.getById(topDepartId);
        }
        return null;
    }

    @Override
    public SysDepartEntity findDepartByOrgCode(String orgCode) {
        if (StringUtils.isEmpty(orgCode)) {
            throw new FdException("参数缺失");
        }
        LambdaQueryWrapper<SysDepartEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepartEntity::getOrgCode, orgCode);
        queryWrapper.eq(SysDepartEntity::getDelFlag, "0");
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(List<SysDepartExcelVo> list) {
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() != list.stream().map(SysDepartExcelVo::getOrgCode).distinct().count()) {
                throw new RuntimeException("文件中存在组织机构编码重复的数据，请检查");
            }
            List<SysDepartEntity> poList = this.list();
            long rootCount = poList.stream().filter(sysDepartEntity -> StringUtils.isEmpty(sysDepartEntity.getParentId())).count();
            HashMap<String, SysDepartEntity> orgCodeIdMap = new HashMap<>(poList.size() + list.size());
            for (SysDepartEntity po : poList) {
                orgCodeIdMap.put(po.getOrgCode(), po);
            }
            List<DictModel> dictModels = sysDictService.queryDictItemsByCode("ORG_TYPE");
            int idx = 1;
            for (SysDepartExcelVo vo : list) {
                idx++;
                if (StringUtils.isEmpty(vo.getOrgCode())) {
                    throw new RuntimeException("第" + idx + "行组织机构编码不能为空");
                }
                if (StringUtils.isEmpty(vo.getOrgCode())) {
                    throw new RuntimeException("第" + idx + "行组织机构名称不能为空");
                }
                if (StringUtils.isEmpty(vo.getOrgCode())) {
                    throw new RuntimeException("第" + idx + "行组织机构别名不能为空");
                }
                if (!StringUtils.isEmpty(vo.getOrgType())) {
                    String orgType = dictModels.stream().filter(dict -> (dict.getText().equals(vo.getOrgType()))).map(DictModel::getValue).collect(Collectors.joining());
                    if (StringUtils.isEmpty(orgType)) {
                        throw new RuntimeException("第" + idx + "行机构类型出现非法值");
                    }
                    vo.setOrgType(orgType);
                }
                if (orgCodeIdMap.containsKey(vo.getOrgCode())) {
                    throw new ApiException(DepartResultCodeEnum.ORGCODE_EXIST);
                }
                if (StringUtils.isEmpty(vo.getParentId()) || "-1".equals(vo.getParentId())) {
                    if (rootCount > 0) {
                        throw new RuntimeException("顶级机构已存在");
                    }
                    vo.setParentId("");
                    rootCount++;
                } else {
                    if (!orgCodeIdMap.containsKey(vo.getParentId())) {
                        throw new RuntimeException("第" + idx + "行上级机构代码不存在");
                    }
                }
                SysDepartEntity sysDepart = new SysDepartEntity();
                sysDepart.setOrgCode(vo.getOrgCode());
                sysDepart.setDepartName(vo.getDepartName());
                sysDepart.setDepartNameAbbr(vo.getDepartNameAbbr());
                sysDepart.setDepartNameAlias(vo.getDepartNameAlias());
                sysDepart.setAreaCode(vo.getAreaCode());
                sysDepart.setParentId(orgCodeIdMap.get(vo.getParentId()).getId());
                sysDepart.setOrgType(vo.getOrgType());
                sysDepart.setSysCode(getSysCode());
                if (!StringUtils.isEmpty(vo.getParentId())) {
                    SysDepartEntity parentDepart = orgCodeIdMap.get(vo.getParentId());
                    if (parentDepart == null) {
                        throw new ApiException(DepartResultCodeEnum.PARENT_NO_EXIST);
                    }
                    sysDepart.setPath(parentDepart.getPath() + sysDepart.getSysCode());
                } else {
                    sysDepart.setParentId(null);
                    sysDepart.setPath(sysDepart.getSysCode());
                }
                sysDepart.setDepartNamePinyinAbbr(PinyinUtil.getPinYinHeadChar(sysDepart.getDepartName()));
                sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0);
                this.save(sysDepart);
                orgCodeIdMap.put(sysDepart.getOrgCode(), sysDepart);
            }
        }
    }

    @Override
    public List<SysDepartEntity> getSysDepartEntity() {
        return baseMapper.getSysDepartEntity();
    }

    @Override
    public SysDepartEntity getSysDepartEntityInfo(String sysCode) {
        return baseMapper.getSysDepartEntityInfo(sysCode);
    }

    /**
     * 获取系统代码
     *
     * @return
     */
    private String getSysCode() {
        String syscode = com.fardo.common.util.StringUtil.random(6);
        SysDepartEntity departEntity = departMapper.getBySysCode(syscode);
        while (departEntity != null) {
            syscode = com.fardo.common.util.StringUtil.random(6);
            departEntity = departMapper.getBySysCode(syscode);
        }
        log.info("生成部门SYS_CODE【{}】",syscode);
        return syscode;
    }

}
