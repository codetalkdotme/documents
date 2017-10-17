package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.fnd.mapper.AreaStationMapper;
import com.newcare.fnd.pojo.area.Area;
import com.newcare.fnd.pojo.community.AreaStation;
import com.newcare.fnd.pojo.community.ServiceStation;
import com.newcare.fnd.service.area.IAreaService;
import com.newcare.fnd.service.community.IAreaStationService;
import com.newcare.fnd.service.community.IServiceStationService;
import com.newcare.select.pojo.Cascade;

@Service("areaStationService")
public class AreaStationServiceImpl implements IAreaStationService {

	@Autowired
	private AreaStationMapper areaStationMapper;

	@Autowired
	private IServiceStationService serviceStationService;

	@Autowired
	private IAreaService areaService;

	@Override
	public List<AreaStation> getAreaStationsByParentAreaCode(String code) {
		return areaStationMapper.getAreaStationsByParentAreaCode(code);
	}

	/**
	 * 需要2个参数 areaType stationId
	 */
	@Override
	public List<AreaStation> getAreaStationsByStationAndLevel(Integer areaType, Long stationId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("areaType", areaType);
		map.put("stationId", stationId);
		return areaStationMapper.getAreaStationsByStationAndLevel(map);
	}

	/**
	 * 返回2个集合 stations commAreaCodes
	 */
	@Override
	public Map<String, List<Cascade>> getStationAreaCascades(Long loginStaffId) {
		List<Cascade> stations = new ArrayList<Cascade>();
		List<Cascade> commAreaCodes = new ArrayList<Cascade>();

		List<ServiceStation> serviceStations = serviceStationService.getServiceStationsByStaffId(loginStaffId.intValue());
		for (ServiceStation serviceStation : serviceStations) {
			stations.add(new Cascade(serviceStation.getId() + "", serviceStation.getName(), ""));
			List<AreaStation> areaStations = getAreaStationsByStationAndLevel(5, serviceStation.getId());
			for (AreaStation areaStation : areaStations) {
				Area area = areaService.getAreaByCode(areaStation.getAreaCode());
				commAreaCodes.add(new Cascade(areaStation.getAreaCode(), area.getName(), areaStation.getStationId() + ""));
			}
		}
		Map<String, List<Cascade>> map = new HashMap<String, List<Cascade>>();
		map.put("stations", stations);
		map.put("commAreaCodes", commAreaCodes);
		return map;
	}

}
