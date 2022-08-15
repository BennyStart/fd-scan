package com.fardo.modules.system.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.system.vo.DictQuery;
import com.fardo.modules.system.config.entity.SysDictEntity;
import com.fardo.modules.system.config.entity.SysDictItemEntity;
import com.fardo.modules.system.config.mapper.SysDictItemMapper;
import com.fardo.modules.system.config.mapper.SysDictMapper;
import com.fardo.modules.system.base.model.TreeSelectModel;
import lombok.extern.slf4j.Slf4j;
import com.fardo.common.system.vo.DictModel;
import com.fardo.modules.system.config.service.ISysDictService;
import com.fardo.common.constant.CacheConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictEntity> implements ISysDictService {

    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private SysDictItemMapper sysDictItemMapper;
	@Autowired
	public RedisTemplate<String, Object> redisTemplate;

	/**
	 * 通过查询指定code 获取字典
	 * @param code
	 * @return
	 */
	@Override
	@Cacheable(value = CacheConstant.SYS_DICT_CACHE,key = "#code")
	public List<DictModel> queryDictItemsByCode(String code) {
		log.info("无缓存dictCache的时候调用这里！");
		return sysDictMapper.queryDictItemsByCode(code);
	}

	@Override
	public Map<String, List<DictModel>> queryAllDictItems() {
		Map<String, List<DictModel>> res = new HashMap<String, List<DictModel>>();
		List<SysDictEntity> ls = sysDictMapper.selectList(null);
		LambdaQueryWrapper<SysDictItemEntity> queryWrapper = new LambdaQueryWrapper<SysDictItemEntity>();
		queryWrapper.eq(SysDictItemEntity::getStatus, 1);
		queryWrapper.orderByAsc(SysDictItemEntity::getSortOrder);
		List<SysDictItemEntity> sysDictItemEntityList = sysDictItemMapper.selectList(queryWrapper);

		for (SysDictEntity d : ls) {
			List<DictModel> dictModelList = sysDictItemEntityList.stream().filter(s -> d.getId().equals(s.getDictId())).map(item -> {
				DictModel dictModel = new DictModel();
				dictModel.setText(item.getItemText());
				dictModel.setValue(item.getItemValue());
				return dictModel;
			}).collect(Collectors.toList());
			res.put(d.getDictCode(), dictModelList);
		}
		log.debug("-------登录加载系统字典-----" + res.toString());
		return res;
	}

	/**
	 * 通过查询指定code 获取字典值text
	 * @param code
	 * @param key
	 * @return
	 */

	@Override
	@Cacheable(value = CacheConstant.SYS_DICT_CACHE,key = "#code+':'+#key")
	public String queryDictTextByKey(String code, String key) {
		log.info("无缓存dictText的时候调用这里！");
		return sysDictMapper.queryDictTextByKey(code, key);
	}

	/**
	 * 通过查询指定table的 text code 获取字典
	 * dictTableCache采用redis缓存有效期10分钟
     *
	 * @param table
	 * @param text
	 * @param code
	 * @return
	 */
	@Override
	//@Cacheable(value = CacheConstant.SYS_DICT_TABLE_CACHE)
	public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
		log.info("无缓存dictTableList的时候调用这里！");
		return sysDictMapper.queryTableDictItemsByCode(table,text,code);
	}

	@Override
	public List<DictModel> queryTableDictItemsByCodeAndFilter(String table, String text, String code, String filterSql) {
		log.info("无缓存dictTableList的时候调用这里！");
		return sysDictMapper.queryTableDictItemsByCodeAndFilter(table,text,code,filterSql);
	}
	
	/**
	 * 通过查询指定table的 text code 获取字典值text
	 * dictTableCache采用redis缓存有效期10分钟
	 * @param table
	 * @param text
	 * @param code
	 * @param key
	 * @return
	 */
	@Override
	@Cacheable(value = CacheConstant.SYS_DICT_TABLE_CACHE)
	public String queryTableDictTextByKey(String table,String text,String code, String key) {
		log.info("无缓存dictTable的时候调用这里！");
		return sysDictMapper.queryTableDictTextByKey(table,text,code,key);
	}

	/**
	 * 通过查询指定table的 text code 获取字典，包含text和value
	 * dictTableCache采用redis缓存有效期10分钟
	 * @param table
	 * @param text
	 * @param code
	 * @param keyArray
	 * @return
	 */
	@Override
	@Cacheable(value = CacheConstant.SYS_DICT_TABLE_CACHE)
	public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
		List<DictModel> dicts = sysDictMapper.queryTableDictByKeys(table, text, code, keyArray);
		List<String> texts = new ArrayList<>(dicts.size());
		// 查询出来的顺序可能是乱的，需要排个序
		for (String key : keyArray) {
			for (DictModel dict : dicts) {
				if (key.equals(dict.getValue())) {
					texts.add(dict.getText());
					break;
				}
			}
		}
		return texts;
	}

    /**
     * 根据字典类型id删除关联表中其对应的数据
     */
    @Override
    public boolean deleteByDictId(SysDictEntity sysDictEntity) {
        sysDictEntity.setDelFlag(CommonConstant.DEL_FLAG_1);
        return  this.updateById(sysDictEntity);
    }

    @Override
    @Transactional
    public Integer saveMain(SysDictEntity sysDictEntity, List<SysDictItemEntity> sysDictItemEntityList) {
		int insert=0;
    	try{
			 insert = sysDictMapper.insert(sysDictEntity);
			if (sysDictItemEntityList != null) {
				for (SysDictItemEntity entity : sysDictItemEntityList) {
					entity.setDictId(sysDictEntity.getId());
					entity.setStatus(1);
					sysDictItemMapper.insert(entity);
				}
			}
		}catch(Exception e){
			return insert;
		}
		return insert;
    }

	@Override
	public List<DictModel> queryAllDepartBackDictModel() {
		return baseMapper.queryAllDepartBackDictModel();
	}

	@Override
	public List<DictModel> queryAllUserBackDictModel() {
		return baseMapper.queryAllUserBackDictModel();
	}
	
	@Override
	public List<DictModel> queryTableDictItems(String table, String text, String code, String keyword) {
		return baseMapper.queryTableDictItems(table, text, code, "%"+keyword+"%");
	}

	@Override
	public List<TreeSelectModel> queryTreeList(Map<String, String> query, String table, String text, String code, String pidField, String pid, String hasChildField) {
		return baseMapper.queryTreeList(query,table, text, code, pidField, pid,hasChildField);
	}

	@Override
	public void deleteOneDictPhysically(String id) {
		this.baseMapper.deleteOneById(id);
		this.sysDictItemMapper.delete(new LambdaQueryWrapper<SysDictItemEntity>().eq(SysDictItemEntity::getDictId,id));
	}

	@Override
	public void updateDictDelFlag(int delFlag, String id) {
		baseMapper.updateDictDelFlag(delFlag,id);
	}

	@Override
	public List<SysDictEntity> queryDeleteList() {
		return baseMapper.queryDeleteList();
	}

	@Override
	public List<DictModel> queryDictTablePageList(DictQuery query, int pageSize, int pageNo) {
		Page page = new Page(pageNo,pageSize,false);
		Page<DictModel> pageList = baseMapper.queryDictTablePageList(page, query);
		return pageList.getRecords();
	}

	@Override
	public void refleshCache() {
		Set keys = redisTemplate.keys(CacheConstant.SYS_DICT_CACHE + "*");
		Set keys2 = redisTemplate.keys(CacheConstant.SYS_DICT_TABLE_CACHE + "*");
		long count = redisTemplate.delete(keys);
		count = redisTemplate.delete(keys2);
	}
}
