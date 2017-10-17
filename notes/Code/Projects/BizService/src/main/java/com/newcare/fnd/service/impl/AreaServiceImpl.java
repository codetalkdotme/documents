package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.cache.service.ICacheService;
import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.core.pagination.PagedData;
import com.newcare.doc.mapper.RelationMapper;
import com.newcare.exception.BizServiceException;
import com.newcare.fnd.mapper.AreaMapper;
import com.newcare.fnd.mapper.AreaStationMapper;
import com.newcare.fnd.pojo.area.Area;
import com.newcare.fnd.pojo.community.AreaStation;
import com.newcare.fnd.pojo.community.AreaStationExample;
import com.newcare.fnd.service.area.IAreaService;
import com.newcare.service.AbstractBizService;
import com.newcare.util.StringUtils;

@Service("areaService")
public class AreaServiceImpl extends AbstractBizService implements IAreaService {

    private static final String AREA_PROVINCES = "AREA_PROVINCES";
    private static final String AREA_CITIES = "AREA_CITIES";
    private static final String AREA_DISTRICTS = "AREA_DISTRICTS";

    // 单个的map
    private static final String AREA_CODE_MAP = "AREA_CODE_MAP";

    // list
    private static final String AREA_PARENT_CODE_MAP = "AREA_PARENT_CODE_MAP";

    public static final String URI_AREALIST_BYPARENT = "/hca/api/business/getarealist";

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private RelationMapper relationMapper;

    @Autowired
    private ICacheService cacheService;
    
    @Autowired
    private AreaStationMapper areaStationMapper;

    private List<Area> loadProvinces() {
        if (cacheService.exists(AREA_PROVINCES)) {
            return (List<Area>) cacheService.get(AREA_PROVINCES);
        }
        List<Area> areas = areaMapper.getTypeAreas(1);
        cacheService.set(AREA_PROVINCES, areas);
        return areas;
    }

    private List<Area> loadCities() {
        if (cacheService.exists(AREA_CITIES)) {
            return (List<Area>) cacheService.get(AREA_CITIES);
        }
        List<Area> areas = areaMapper.getTypeAreas(2);
        cacheService.set(AREA_CITIES, areas);
        return areas;
    }

    private List<Area> loadDistricts() {
        if (cacheService.exists(AREA_DISTRICTS)) {
            return (List<Area>) cacheService.get(AREA_DISTRICTS);
        }
        List<Area> areas = areaMapper.getTypeAreas(3);
        cacheService.set(AREA_DISTRICTS, areas);
        return areas;
    }

    @Override
    public Area getAreaByCode(String code) {
        if (!cacheService.exists(AREA_CODE_MAP)) {
            Map<String, Area> areaMap = new HashMap<>();
            cacheService.set(AREA_CODE_MAP, areaMap);
        }

        Map<String, Area> areaMap = (Map<String, Area>) cacheService.get(AREA_CODE_MAP);

        if (areaMap.containsKey(code)) {
            return areaMap.get(code);
        } else {
            Area area = areaMapper.getAreaByCode(code);
            if (area != null) {
                areaMap.put(code, area);
                cacheService.set(AREA_CODE_MAP, areaMap);
            }
            return area;
        }
    }

    @Override
    public List<Area> getAreasByParentCode(String code) {
        if (!cacheService.exists(AREA_PARENT_CODE_MAP)) {
            Map<String, Area> areaMap = new HashMap<>();
            cacheService.set(AREA_PARENT_CODE_MAP, areaMap);
        }

        Map<String, List<Area>> areaMap = (Map<String, List<Area>>) cacheService.get(AREA_PARENT_CODE_MAP);

        if (areaMap.containsKey(code)) {
            return areaMap.get(code);
        } else {
            List<Area> areas = areaMapper.getAreasByParentCode(code);
            areaMap.put(code, areas);
            return areas;
        }
    }

    @Override
    public Page<Area> getPageAreasByParent(Page<Area> page) {
        String parent = String.valueOf(page.getSearchMap().get("parent"));
        new PagedData<Area>(page, getAreasByParentCode(parent));
        return page;
    }

    @Override
    public Map<String, Object> cascadeList() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("provinceList", areaMapper.provinceList());
        map.put("cityList", areaMapper.cityList());
        return map;
    }


    /**
     * 按照从大到小的范围获取
     *
     * @param code
     * @return
     */
    @Override
    public List<Area> getAbsoluteAreasByCode(String code) {
        List<Area> areas = new ArrayList<Area>();
        Area area = getAreaByCode(code);
        if (area != null) {
            areas.add(0, area);
            // 如果不是
            if (Constants.AREA_PROVINCE_TYPE != area.getType()) {
                areas.addAll(0, getAbsoluteAreasByCode(area.getParent()));
            }
        }
        return areas;
    }

    @Override
    public List<Area> getCityList() {
        List<Area> cityList = new ArrayList<>();
        List<Area> provincesList = getAreasByParentCode(Constants.AREA_COUNTRY_CODE);

        if (provincesList != null && provincesList.size() > 0) {
            for (Area area : provincesList) {
                cityList.addAll(getAreasByParentCode(area.getCode()));
            }
        }

        return cityList;
    }

    @Override
    public List<Area> listAreaByParent(String parentCode) {
        if (parentCode == null) {
            // 加载省
            parentCode = Constants.AREA_COUNTRY_CODE;
        }
        return getAreasByParentCode(parentCode);
    }

    @Override
	public List<Area> listAreaByParentId(Long parentId) {
		if(parentId == null) {
			return loadProvinces();
		} else {
			String cacheKey = "AREA-CHILDOF-" + parentId;
			if (cacheService.exists(cacheKey)) {
	            return (List<Area>)cacheService.get(cacheKey);
	        } else {
	        	List<Area> areas = areaMapper.listAreaByParentId(parentId); 
	        	cacheService.set(cacheKey, areas);
	        	
	        	return areas;
	        }
		}
	}
	
	@Override
	public String doPost(String uri, Map<String, Object> data) throws BizServiceException {
		if(URI_AREALIST_BYPARENT.equals(uri)) {
			Object areaIdObj = data.get("area_id_higher");
			Long parentId = (areaIdObj == null ? null : Long.parseLong(areaIdObj.toString()));
			
			List<Area> areas = listAreaByParentId(parentId);
			List<Map<String, Object>> rtData = new ArrayList<Map<String, Object>>();
			if(areas != null && areas.size() > 0) {
				for(Area area : areas) {
					Map<String, Object> areaMap = new HashMap<String, Object>();
					areaMap.put("area_id", area.getId());
					areaMap.put("area_name", area.getName());
					
					rtData.add(areaMap);
				}
			}
				
			return successWithObject(rtData);
		}
		
		return null;
	}

    /**
     * 获取前3个下拉框
     *
     * @return
     */
    @Override
    public Map<String, List<Area>> getAreaSelect() {
        Map<String, List<Area>> areaMap = new HashMap<>();
        areaMap.put("provinces", loadProvinces());
        areaMap.put("cities", loadCities());
        areaMap.put("districts", loadDistricts());
        return areaMap;
    }

    @Override
    public Map<String,Area> getAreaListByCodes(Set<String> codeSets) {
        if(codeSets == null || codeSets.size() == 0){
            return null;
        }

        Map map = new HashMap();
        map.put("list",codeSets);
        List<Area> areaList = areaMapper.getAreaListByCodes(map);
        Map<String,Area> resultMap = new HashMap();

        for(Area area:areaList){
            resultMap.put(area.getCode(),area);
        }
        return resultMap;
    }

    @Override
    public Area getAreaByCodeNoCache(String areaCode) {
        if(!StringUtils.isEmpty(areaCode)){
            return areaMapper.getAreaByCode(areaCode);
        }
        return null;
    }

    @Override
    public Area getAreaById(int areaId) {
        if(areaId == 0){
            return null;
        }
        return areaMapper.getAreaById(areaId);
    }

	@Override
	public List<Area> getStationVillageAreas(Long stationId) {
		// 获取village
		AreaStationExample example = new AreaStationExample();
		AreaStationExample.Criteria criteria = example.createCriteria();
		criteria.andStationIdEqualTo(stationId);
		criteria.andAreaTypeEqualTo(5);
		List<AreaStation> areaStations = areaStationMapper.selectByExample(example);
		List<Area> areas = new ArrayList<Area>();
		for (AreaStation areaStation : areaStations) {
			areas.add(getAreaByCode(areaStation.getAreaCode()));
		}
		return areas;
	}
    
    

}
