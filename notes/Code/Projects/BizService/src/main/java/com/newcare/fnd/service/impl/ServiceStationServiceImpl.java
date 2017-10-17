package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.core.pagination.PagedData;
import com.newcare.doc.pojo.Staff;
import com.newcare.fnd.mapper.AreaStationMapper;
import com.newcare.fnd.mapper.OrganizationMapper;
import com.newcare.fnd.mapper.ServiceStationMapper;
import com.newcare.fnd.pojo.Organization;
import com.newcare.fnd.pojo.area.Area;
import com.newcare.fnd.pojo.community.AreaStation;
import com.newcare.fnd.pojo.community.AreaStationExample;
import com.newcare.fnd.pojo.community.ServiceStation;
import com.newcare.fnd.service.area.IAreaService;
import com.newcare.fnd.service.community.IServiceStationService;
import com.newcare.fnd.service.community.IStaffService;
import com.newcare.select.pojo.Select;

@Service("serviceStationService")
public class ServiceStationServiceImpl implements IServiceStationService {

	@Autowired
	private ServiceStationMapper serviceStationMapper;

	@Autowired
	private IStaffService staffService;

	@Autowired
	private IAreaService areaService;

	@Autowired
	private AreaStationMapper areaStationMapper;
	
	@Autowired
	private OrganizationMapper organizationMapper;

	@Override
	public ServiceStation get(Long id) {
		return serviceStationMapper.get(id);
	}

	@Transactional
	@Override
	public int save(ServiceStation serviceStation) {
		// 居委会放到从表
		int result = serviceStationMapper.save(serviceStation);
		updateAreaStation(serviceStation, false);
		return result;
	}

	private void updateAreaStation(ServiceStation serviceStation, boolean isDelete) {
		// 先删除
		AreaStationExample example = new AreaStationExample();
		AreaStationExample.Criteria criteria = example.createCriteria();
		criteria.andStationIdEqualTo(serviceStation.getId());
		areaStationMapper.deleteByExample(example);
		if (!isDelete) {
			List<String> areaCodes = new ArrayList<String>();
			if (StringUtils.isNotBlank(serviceStation.getArea_province())) {
				areaCodes.add(serviceStation.getArea_province());
			}
			if (StringUtils.isNotBlank(serviceStation.getArea_city())) {
				areaCodes.add(serviceStation.getArea_city());
			}
			if (StringUtils.isNotBlank(serviceStation.getArea_district())) {
				areaCodes.add(serviceStation.getArea_district());
			}
			if (StringUtils.isNotBlank(serviceStation.getArea_street())) {
				areaCodes.add(serviceStation.getArea_street());
			}
			if (StringUtils.isNotBlank(serviceStation.getArea_village())) {
				String[] villageCodes = serviceStation.getArea_village().split(",");
				for (String villageCode : villageCodes) {
					areaCodes.add(villageCode);
				}
			}
			// 循环添加
			for (String areaCode : areaCodes) {
				AreaStation areaStation = new AreaStation();
				areaStation.setAreaCode(areaCode);
				areaStation.setStationId(serviceStation.getId());
				areaStation.setAreaType(areaService.getAreaByCode(areaCode).getType());
				areaStationMapper.insert(areaStation);
			}
		}
	}

	@Transactional
	@Override
	public int delete(Long id) {
		ServiceStation station = get(id);
		station.setStatus(Constants.STATUS_STOP);
		int count = serviceStationMapper.update(station);
		updateAreaStation(station, true);
		return count;
	}

	@Transactional
	@Override
	public int update(ServiceStation serviceStation) {
		int count = serviceStationMapper.update(serviceStation);
		updateAreaStation(serviceStation, false);
		// 修改服务站的名字
		Organization organization =  organizationMapper.selectByPrimaryKey(serviceStation.getOrganization_id());
		if(organization!=null){
			organization.setOrganizationName(serviceStation.getName());
			organization.setUpdateDate(new Date());
			organizationMapper.updateByPrimaryKey(organization);
		}
		return count;
	}

	@Override
	public List<Select> getServiceStationMappers(Map<String, Object> condMap) {
		List<Select> mappers = new ArrayList<Select>();
		List<ServiceStation> serviceStations = serviceStationMapper.search(condMap);
		if (serviceStations != null && serviceStations.size() > 0) {
			for (ServiceStation serviceStation : serviceStations) {
				mappers.add(new Select(String.valueOf(serviceStation.getId()), serviceStation.getName(), serviceStation.getArea_district()));
			}
		}
		return mappers;
	}

	@Override
	public Page<ServiceStation> getPageStations(Page<ServiceStation> page) {
		if (page.getSearchMap() == null || (page.getSearchMap() != null && page.getSearchMap().size() == 0)) {
			new PagedData<ServiceStation>(page, getAll(Long.parseLong(page.getSearchMap().get(Constants.LOGIN_STAFF).toString())));
		} else {
			// 查询数据库, 分页
			if (page.getPageSize() != -1) {
				page.getSearchMap().put("offset", page.getPageOffset());
				page.getSearchMap().put("limit", page.getPageSize());
			}
			page.setContent(serviceStationMapper.search(page.getSearchMap()));
			page.setTotal(serviceStationMapper.count(page.getSearchMap()));
		}
		return page;
	}

	@Override
	public List<ServiceStation> getAll(Long organizationId) {

		Map<String, Object> map = new HashMap<>();
		map.put(Constants.LOGIN_ORIZATION_ID, organizationId);
		return serviceStationMapper.search(map);
	}

	/**
	 * for area load
	 */
	@Override
	public List<Area> getServiceStationAreas(Long loginOrganizationId) {
		List<Area> area = new ArrayList<Area>();
		List<ServiceStation> serviceStations = getAll(loginOrganizationId);
		if (serviceStations != null && serviceStations.size() > 0) {
			for (ServiceStation serviceStation : serviceStations) {
				area.add(new Area(String.valueOf(serviceStation.getId()), serviceStation.getName(), serviceStation.getArea_district()));
			}
		}
		return area;
	}

	@Override
	public List<ServiceStation> getServiceStationsByStaffId(Integer staffId) {
		List<Long> ids = getStaffStationIdsByStaffId(staffId.longValue());
		if (ids != null && ids.size() > 0) {
			return serviceStationMapper.getServiceStationsByIds(ids);
		}
		return new ArrayList<>();
	}

	@Override
	public List<Long> getStaffStationIdsByUserId(Long userId) {

		Staff staff = staffService.getStaffByUserId(userId);
		if (staff != null) {
			return getStaffStationIdsByStaffId(staff.getStaffId().longValue());
		}
		return null;
	}

	@Override
	public List<Long> getStaffStationIdsByStaffId(Long staffId) {

		List<Long> ids = serviceStationMapper.getStaffStationIdsByStaffId(staffId);
		if (ids != null && ids.size() > 0) {
			return ids;
		}
		return new ArrayList<>();
	}

	/**
	 * 设置地区名
	 *
	 * @param serviceStation
	 */
	@Override
	public String getStationArea(ServiceStation serviceStation) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(serviceStation.getArea_province())) {
			sb.append(areaService.getAreaByCode(serviceStation.getArea_province()).getName());
		}
		if (StringUtils.isNotBlank(serviceStation.getArea_city())) {
			sb.append(areaService.getAreaByCode(serviceStation.getArea_city()).getName());
		}
		if (StringUtils.isNotBlank(serviceStation.getArea_district())) {
			sb.append(areaService.getAreaByCode(serviceStation.getArea_district()).getName());
		}
		if (StringUtils.isNotBlank(serviceStation.getArea_street())) {
			sb.append(areaService.getAreaByCode(serviceStation.getArea_street()).getName());
		}

		// 获取village
		AreaStationExample example = new AreaStationExample();
		AreaStationExample.Criteria criteria = example.createCriteria();
		criteria.andStationIdEqualTo(serviceStation.getId());
		criteria.andAreaTypeEqualTo(5);
		List<AreaStation> areaStations = areaStationMapper.selectByExample(example);

		for (int i = 0; i < areaStations.size(); i++) {
			if (i >= 1) {
				sb.append(",");
			}
			sb.append(areaService.getAreaByCode(areaStations.get(i).getAreaCode()).getName());
		}

		if (StringUtils.isNotBlank(serviceStation.getAddress())) {
			sb.append(" " + serviceStation.getAddress());
		}
		return sb.toString();
	}

	@Override
	public ServiceStation checkStationName(Map<String, Object> map) {
		return serviceStationMapper.checkStationName(map);
	}

	@Override
	public ServiceStation getServiceStationByOrganizationId(Long organizationId) {
		return serviceStationMapper.getStationByOrganizationId(organizationId);
	}

}
