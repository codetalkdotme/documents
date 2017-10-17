package com.newcare.fnd.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.fnd.mapper.GroundMapper;
import com.newcare.fnd.pojo.Lookup;
import com.newcare.fnd.pojo.community.Ground;
import com.newcare.fnd.pojo.community.ServiceStation;
import com.newcare.fnd.service.ILookupService;
import com.newcare.fnd.service.community.IGroundService;
import com.newcare.fnd.service.community.IServiceStationService;

@Service("groundService")
public class GroundServiceImpl implements IGroundService {

	@Autowired
	private GroundMapper groundMapper;

	@Autowired
	private IServiceStationService serviceStationService;

	@Autowired
	private ILookupService lookupService;

	@Override
	public Page<Ground> getPageGround(Page<Ground> page) {
		// 查询数据库, 分页
		if (page.getPageSize() != -1) {
			page.getSearchMap().put("offset", page.getPageOffset());
			page.getSearchMap().put("limit", page.getPageSize());
		}
		page.setContent(groundMapper.search(page.getSearchMap()));
		page.setTotal(groundMapper.count(page.getSearchMap()));
		return page;
	}

	@Override
	public Ground get(Long id) {
		return groundMapper.selectByPrimaryKey(id.intValue());
	}

	@Override
	public int save(Ground ground) {
		ground.setGroundStatus(true);
		return groundMapper.insert(ground);
	}

	@Override
	public int delete(Long id) {
		Ground ground = get(id);
		ground.setGroundStatus(false);
		return groundMapper.updateByPrimaryKey(ground);
	}

	@Override
	public int update(Ground ground) {
		return groundMapper.updateByPrimaryKey(ground);
	}

	@Override
	public Page<Ground> resetPage(Page<Ground> pages) {
		if (pages.getContent() != null && pages.getContent().size() > 0) {
			List<ServiceStation> serviceStations = serviceStationService.getAll(Long.parseLong(pages.getSearchMap().get(Constants.LOGIN_ORIZATION_ID).toString()));
			List<Lookup> groundTypes = lookupService.getLookupsByCategory(Constants.LOOKUPS_GROUND_TYPE);
			for (Ground ground : pages.getContent()) {
				ground.setServiceStations(serviceStations);
				ground.setGroundTypes(groundTypes);
			}
		}
		return pages;
	}
}
