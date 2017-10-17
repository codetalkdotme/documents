package com.newcare.fnd.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newcare.core.pagination.Page;
import com.newcare.doc.service.HouseholdHeaderService;
import com.newcare.fnd.mapper.AreaStationMapper;
import com.newcare.fnd.mapper.BuildingMapper;
import com.newcare.fnd.pojo.community.AreaStation;
import com.newcare.fnd.pojo.community.AreaStationExample;
import com.newcare.fnd.pojo.community.Building;
import com.newcare.fnd.pojo.community.BuildingExample;
import com.newcare.fnd.service.community.IBuildingService;

/**
 * Created by Administrator on 2017/6/6.
 */
@Service("buildingService")
public class BuildingServiceImpl implements IBuildingService {

	@Autowired
	private BuildingMapper buildingMapper;

	@Autowired
	private HouseholdHeaderService householdHeaderService;

	@Autowired
	private AreaStationMapper areaStationMapper;

	@Override
	public Page<Building> getPageBuildings(Page<Building> page) {
		// 查询数据库, 分页
		page.getSearchMap().put("offset", page.getPageOffset());
		page.getSearchMap().put("limit", page.getPageSize());
		page.setContent(buildingMapper.search(page.getSearchMap()));
		page.setTotal(buildingMapper.count(page.getSearchMap()));
		return page;
	}

	@Override
	public Building get(Long buildingId) {
		return buildingMapper.selectByPrimaryKey(buildingId);
	}

	@Override
	public Building save(Building building) {
		building.setBuildingStatus(Boolean.TRUE);
		// 修改
		resetBuildingArea(building);
		buildingMapper.insert(building);
		return building;
	}

	private void resetBuildingArea(Building building) {
		AreaStationExample example = new AreaStationExample();
		AreaStationExample.Criteria criteria = example.createCriteria();
		criteria.andStationIdEqualTo(building.getStationId());
		List<AreaStation> areaStations = areaStationMapper.selectByExample(example);
		for (AreaStation areaStation : areaStations) {
			if (areaStation.getAreaType().intValue() == 1) {
				building.setBuildingProvinceCode(areaStation.getAreaCode());
			} else if (areaStation.getAreaType().intValue() == 2) {
				building.setBuildingCityCode(areaStation.getAreaCode());
			} else if (areaStation.getAreaType().intValue() == 3) {
				building.setBuildingDistrictCode(areaStation.getAreaCode());
			} else if (areaStation.getAreaType().intValue() == 4) {
				building.setBuildingStreetCode(areaStation.getAreaCode());
			}
		}
	}

	@Override
	public int update(Building building) {
		// 修改
		resetBuildingArea(building);
		return buildingMapper.updateByPrimaryKey(building);
	}

	@Override
	public List<Building> getBuildingByStationId(Long stationId) {
		BuildingExample example = new BuildingExample();
		BuildingExample.Criteria criteria = example.createCriteria();
		criteria.andStationIdEqualTo(stationId);
		return buildingMapper.selectByExample(example);
	}

	@Override
	@Transactional
	public int delete(Long buildingId) {
		Building building = new Building();
		building.setBuildingId(buildingId);
		building.setBuildingStatus(false);
		buildingMapper.updateByPrimaryKeySelective(building);

		return householdHeaderService.deleteByBuildingId(buildingId);
	}

	@Override
	public List<Building> getDetailsCountByBuildingIds(List<Long> ids) {
		return buildingMapper.getDetailsCountByBuildingIds(ids);
	}
	
}
