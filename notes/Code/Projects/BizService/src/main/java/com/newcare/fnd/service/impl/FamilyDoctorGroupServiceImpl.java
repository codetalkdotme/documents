package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newcare.auth.pojo.User;
import com.newcare.auth.service.IAuthService;
import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.fnd.dto.household.GroupArea;
import com.newcare.fnd.dto.household.GroupBuilding;
import com.newcare.fnd.dto.household.GroupHecadre;
import com.newcare.fnd.dto.household.GroupHouse;
import com.newcare.fnd.dto.household.GroupUnit;
import com.newcare.fnd.mapper.FamilyDoctorGroupMapper;
import com.newcare.fnd.pojo.community.FamilyDoctorGroup;
import com.newcare.fnd.service.community.IFamilyDoctorGroupService;
import com.newcare.fnd.service.community.IStaffService;
import com.newcare.select.pojo.Select;
import com.newcare.util.StringUtils;

@Service("familyDoctorGroupService")
public class FamilyDoctorGroupServiceImpl implements IFamilyDoctorGroupService {

	@Autowired
	private IAuthService authService;

	@Autowired
	private IStaffService staffService;

	@Autowired
	private FamilyDoctorGroupMapper familyDoctorGroupMapper;

	@Override
	public Page<FamilyDoctorGroup> getPageDataList(Page<FamilyDoctorGroup> page) {
		page.getSearchMap().put("offset", page.getPageOffset());
		page.getSearchMap().put("limit", page.getPageSize());
		List<FamilyDoctorGroup> familyDoctorGroups = familyDoctorGroupMapper.search(page.getSearchMap());
		for (FamilyDoctorGroup familyDoctorGroup : familyDoctorGroups) {
			familyDoctorGroup.setDoctor_name(getNameByUserId(familyDoctorGroup.getDoctor_id()));
			familyDoctorGroup.setNurse_name(getNameByUserId(familyDoctorGroup.getNurse_id()));
			familyDoctorGroup.setHecadre_names(getNamesByUserIds(familyDoctorGroup.getHecadre_ids()));
		}
		page.setContent(familyDoctorGroups);
		page.setTotal(familyDoctorGroupMapper.count(page.getSearchMap()));
		return page;
	}

	/**
	 * 根据userId获取姓名
	 * 
	 * @param userId
	 * @return
	 */
	public String getNameByUserId(Long userId) {
		if (userId != null) {
			User user = authService.getUserById(userId);
			if (user != null) {
				return user.getRealName();
			}
		}
		return null;
	}

	/**
	 * 根据ids获取姓名，并拼接成字符串
	 * 
	 * @param userIds
	 * @return
	 */
	public String getNamesByUserIds(String userIds) {
		if (userIds != null && !"".equals(userIds)) {
			String[] userIdArr = userIds.split(",");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < userIdArr.length; i++) {
				User user = authService.getUserById(Long.parseLong(userIdArr[i]));
				if (user != null) {
					sb.append(user.getRealName());
				} else {
					continue;
				}
				if (i != userIdArr.length - 1) {
					sb.append(",");
				}
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public List<Select> getSelectList(Map<String, Object> map) {
		List<Select> mappers = new ArrayList<Select>();
		List<Long> userIdList = familyDoctorGroupMapper.getSelectList(map);
		if (userIdList.size() > 0) {
			List<User> userList = authService.getUsers(userIdList);
			if (userList.size() > 0) {
				for (User user : userList) {
					mappers.add(new Select(String.valueOf(user.getId()), user.getRealName(), null));
				}
			}
		}
		return mappers;
	}

	@Override
	public List<GroupArea> getHouseholdList(Long stationId) {
		List<GroupArea> areaList = new ArrayList<GroupArea>();
		// b.building_id, b.building_name, hh.header_building_no,
		// hh.header_building_unit, hd.detail_household_no, detail_id
		List<Map<String, Object>> houseList = familyDoctorGroupMapper.selectHouseListNotAssigned(stationId);
		String buildingId = null, buildingNo = null, buildingUnit = null;
		GroupArea area = null; // 小区
		GroupBuilding building = null; // 楼栋
		GroupUnit unit = null; // 单元
		for (Map<String, Object> houseMap : houseList) {
			String tmpBuildingId = houseMap.get("building_id").toString(),
					tmpBuildingName = houseMap.get("building_name").toString(),
					tmpBuildingNo = houseMap.get("header_building_no").toString(),
					tmpBuildingUnit = houseMap.get("header_building_unit").toString(),
					tmpHouseNo = houseMap.get("detail_household_no").toString();
			Long houseId = Long.parseLong(houseMap.get("detail_id").toString());
			// 小区
			if (!tmpBuildingId.equals(buildingId)) {
				area = new GroupArea();
				area.setName(tmpBuildingName);
				areaList.add(area);
				area.setBuildingList(new ArrayList<GroupBuilding>());
				if(buildingId != null) {
					buildingNo = null;
					buildingUnit = null;
				}
				buildingId = tmpBuildingId;
			}
			// 楼栋
			if (!tmpBuildingNo.equals(buildingNo)) {
				building = new GroupBuilding();
				building.setName(tmpBuildingNo);
				area.addBuilding(building);
				building.setUnitList(new ArrayList<GroupUnit>());
				if(buildingNo != null) {
					buildingUnit = null;
				}
				buildingNo = tmpBuildingNo;
			}
			// 单元
			if (!tmpBuildingUnit.equals(buildingUnit)) {
				unit = new GroupUnit();
				unit.setName(tmpBuildingUnit);
				building.addUnit(unit);
				unit.setHouseList(new ArrayList<GroupHouse>());
				buildingUnit = tmpBuildingUnit;
			}
			GroupHouse house = new GroupHouse();
			house.setId(houseId);
			house.setHouse(tmpHouseNo);
			unit.addHouse(house);
		}
		
		/*for(String id:distinctList(houseList,"building_id")){
			GroupArea groupArea = new GroupArea();
			groupArea.setName("");
			List<Map<String,Object>> buildingNos = search(houseList, id, null, null, null);
		}*/
		return areaList;
	}
	
	/*private List<Map<String,Object>> search(List<Map<String, Object>> houseList,String building_id,String header_building_no,String header_building_unit,String detail_id){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Set<Map<String,Object>> set = new HashSet<Map<String,Object>>();
		for(Map<String, Object> data:houseList){
			boolean flag = true;
			if(building_id!=null && !data.get("building_id").toString().equalsIgnoreCase(building_id)){
				flag = false;
			}
			if(building_id!=null && !data.get("header_building_no").toString().equalsIgnoreCase(header_building_no)){
				flag = false;
			}
			if(building_id!=null && !data.get("header_building_unit").toString().equalsIgnoreCase(header_building_unit)){
				flag = false;
			}
			if(building_id!=null && !data.get("detail_id").toString().equalsIgnoreCase(detail_id)){
				flag = false;
			}
			if(flag){
				set.add(data);
			}
			
		}
		result.addAll(set);
		return result;
		
	}
	
	
	private List<String> distinctList(List<Map<String, Object>> houseList,String key){
		Set<String> idSet = new HashSet<String>();
		for(Map<String, Object> map:houseList){
			idSet.add(map.get(key).toString());
		}
		List<String> idList = new ArrayList<String>();
		idList.addAll(idSet);
		return idList;
	}

	
	
	// 找到building
	private GroupArea fetchBuildings(String buildingId, List<Map<String, Object>> allData) {
		GroupArea groupArea = new GroupArea();
		List<String> buildingNames = search(allData, "building_name", buildingId, null, null, null);
		return groupArea;
	}

	private List<GroupBuilding> getBuildingNosByBuildingId(List<Map<String, Object>> allData, String buildingId) {
		List<GroupBuilding> groupBuildings = new ArrayList<GroupBuilding>();
		// 找到不重复的
		
		
		for (Map<String, Object> data : allData) {
			if (data.get("building_id").toString().equalsIgnoreCase(buildingId)) {
				GroupBuilding groupBuilding = new GroupBuilding();
				groupBuilding.setName(data.get("header_building_no").toString());
				groupBuilding.setUnitList(getUnitsByBuildingId(allData, buildingId));
				groupBuildings.add(groupBuilding);
			}
		}
		return groupBuildings;
	}

	private List<GroupUnit> getUnitsByBuildingId(List<Map<String, Object>> allData, String buildingId) {
		List<GroupUnit> units = new ArrayList<GroupUnit>();
		for (Map<String, Object> data : allData) {
			if (data.get("building_id").toString().equalsIgnoreCase(buildingId)) {
				GroupUnit unit = new GroupUnit();
				unit.setName(data.get("header_building_unit").toString());
				unit.setHouseList(getHousesByBuildingId(allData, buildingId));
				units.add(unit);
			}
		}
		return units;
	}
	
	private List<GroupHouse> getHousesByBuildingId(List<Map<String, Object>> allData, String buildingId) {
		List<GroupHouse> houses = new ArrayList<GroupHouse>();
		for (Map<String, Object> data : allData) {
			if (data.get("building_id").toString().equalsIgnoreCase(buildingId)) {
				GroupHouse house = new GroupHouse();
				house.setId(Long.parseLong(data.get("detail_id").toString()));
				house.setHouse(data.get("detail_household_no").toString());
				houses.add(house);
			}
		}
		return houses;
	}*/

	public List<GroupHecadre> getSeletedHouseHoldList(Map<String, Object> map) {
		List<GroupHecadre> hecadreList = new ArrayList<GroupHecadre>();
		List<Map<String, Object>> seletedHouseHoldList = familyDoctorGroupMapper.getSeletedHouseHoldList(map);
		String hecadreId = null, buildingId = null, buildingNo = null, buildingUnit = null;
		GroupHecadre hecadre = null;// 专干
		GroupArea area = null;// 小区
		GroupBuilding building = null;// 楼栋
		GroupUnit unit = null;// 单元
		for (Map<String, Object> selectedHouseMap : seletedHouseHoldList) {
			String tmpHecadreId = selectedHouseMap.get("hecadre_uid").toString(),
					tmpBuildingId = selectedHouseMap.get("building_id").toString(),
					tmpBuildingName = selectedHouseMap.get("building_name").toString(),
					tmpBuildingNo = selectedHouseMap.get("header_building_no").toString(),
					tmpBuildingUnit = selectedHouseMap.get("header_building_unit").toString(),
					tmpHouseNo = selectedHouseMap.get("detail_household_no").toString();
			Long houseId = Long.parseLong(selectedHouseMap.get("detail_id").toString());
			// 专干
			if (!tmpHecadreId.equals(hecadreId)) {
				hecadre = new GroupHecadre();
				hecadre.setName(tmpHecadreId);
				hecadreList.add(hecadre);
				hecadre.setAreaList(new ArrayList<GroupArea>());
				if (hecadreId != null) {
					buildingId = null;
					buildingNo = null;
					buildingUnit = null;
				}
				hecadreId = tmpHecadreId;
			}
			// 小区
			if (!tmpBuildingId.equals(buildingId)) {
				area = new GroupArea();
				area.setName(tmpBuildingName);
				hecadre.addArea(area);
				area.setBuildingList(new ArrayList<GroupBuilding>());
				if (buildingId != null) {
					buildingNo = null;
					buildingUnit = null;
				}
				buildingId = tmpBuildingId;
			}
			// 楼栋
			if (!tmpBuildingNo.equals(buildingNo)) {
				building = new GroupBuilding();
				building.setName(tmpBuildingNo);
				area.addBuilding(building);
				building.setUnitList(new ArrayList<GroupUnit>());
				if (buildingNo != null) {
					buildingUnit = null;
				}
				buildingNo = tmpBuildingNo;
			}
			// 单元
			if (!tmpBuildingUnit.equals(buildingUnit)) {
				unit = new GroupUnit();
				unit.setName(tmpBuildingUnit);
				building.addUnit(unit);
				unit.setHouseList(new ArrayList<GroupHouse>());
				buildingUnit = tmpBuildingUnit;
			}
			GroupHouse house = new GroupHouse();
			house.setId(houseId);
			house.setHouse(tmpHouseNo);
			unit.addHouse(house);
		}
		return hecadreList;
	}

	// @Override
	// public Map<String, List<GroupArea>> getHouseholdList(Map<String, Object>
	// map) {
	// Map<String, List<GroupArea>> areaMap = new HashMap<>();
	// List<GroupArea> groupAreaList =
	// familyDoctorGroupMapper.getGroupAreaList(map);
	// for (GroupArea groupArea : groupAreaList) {
	// map.put("header_building_name", groupArea.getName());
	// List<GroupBuilding> groupBuildingList =
	// familyDoctorGroupMapper.getGroupBuildingList(map);
	// for (GroupBuilding groupBuilding : groupBuildingList) {
	// map.put("header_building_no", groupBuilding.getName());
	// List<GroupUnit> groupUnitList =
	// familyDoctorGroupMapper.getGroupUnitList(map);
	// for (GroupUnit groupUnit : groupUnitList) {
	// map.put("header_building_unit", groupUnit.getName());
	// List<GroupHouse> groupHouseList =
	// familyDoctorGroupMapper.getGroupHouseList(map);
	// String[] housesArray = new String[groupHouseList.size()];
	// for (int i = 0; i < groupHouseList.size(); i++) {
	// housesArray[i] = groupHouseList.get(i).getHouse().toString();
	// }
	// groupUnit.setHouse(housesArray);
	// }
	// groupBuilding.setUnit(groupUnitList);
	// }
	// groupArea.setBuild(groupBuildingList);
	// }
	// areaMap.put("groupAreaList", groupAreaList);
	// return areaMap;
	// }

	@Transactional
	@Override
	public void save(FamilyDoctorGroup familyDoctorGroup) {
		// 保存组
		familyDoctorGroupMapper.save(familyDoctorGroup);
		// 保存组包含的医生、护士、专干
		List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
		if (familyDoctorGroup.getDoctor_id() != null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id", familyDoctorGroup.getId());
			paramMap.put("user_id", familyDoctorGroup.getDoctor_id());

			paramMap.put("staff_id", staffService.getStaffByUserId(familyDoctorGroup.getDoctor_id()).getStaffId());
			paramMap.put("member_type", Constants.FD_MEMBER_DOCTOR);
			paramList.add(paramMap);
		}
		if (familyDoctorGroup.getNurse_id() != null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id", familyDoctorGroup.getId());
			paramMap.put("user_id", familyDoctorGroup.getNurse_id());
			paramMap.put("staff_id", staffService.getStaffByUserId(familyDoctorGroup.getNurse_id()).getStaffId());
			paramMap.put("member_type", Constants.FD_MEMBER_NURSE);
			paramList.add(paramMap);
		}
		if (familyDoctorGroup.getHecadrehouseList() != null) {
			List<Map<String, Object>> houseList = familyDoctorGroup.getHecadrehouseList();
			for (Map<String, Object> map : houseList) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				Long hecadreUserId = StringUtils.objToLong(map.get("hecadre_id"));
				paramMap.put("id", familyDoctorGroup.getId());
				paramMap.put("user_id", hecadreUserId);
				paramMap.put("staff_id", staffService.getStaffByUserId(hecadreUserId).getStaffId());
				paramMap.put("member_type", Constants.FD_MEMBER_HECADRE);
				paramList.add(paramMap);
			}
		}

		/*
		 * List<Map<String,Object>> hecadreHouseList = new
		 * ArrayList<Map<String,Object>>(); if (familyDoctorGroup.getHousejson()
		 * != null) { // 专干 List<Map<String,String>> list =
		 * JsonUtils.json2Bean(familyDoctorGroup.getHousejson(), List.class);
		 * for (Map<String, String> map : list) { Map<String, Object> subMap =
		 * new HashMap<String, Object>(); subMap.put("id",
		 * familyDoctorGroup.getId());
		 * subMap.put("user_id",map.get("officerId")); subMap.put("staff_id",
		 * authService.getUserById(StringUtils.objToLong(map.get("officerId"))).
		 * getStaffId()); subMap.put("member_type",
		 * Constants.FD_MEMBER_HECADRE);
		 * 
		 * subMap.put("hecadre_id",
		 * StringUtils.objToLong(map.get("officerId")));
		 * subMap.put("household_ids", map.get("houseNo"));
		 * hecadreHouseList.add(subMap); paramList.add(subMap); } }
		 */

		familyDoctorGroupMapper.saveInfo(paramList);
		// 保存专干对应网格住户
		// familyDoctorGroupMapper.updateHouseHold(familyDoctorGroup.getHecadrehouseList());
		List<Map<String, Object>> hhouseList = familyDoctorGroup.getHecadrehouseList();
		for (Map<String, Object> hmap : hhouseList) {
			familyDoctorGroupMapper.updateHouseHold(hmap);
		}
	}

	@Override
	public FamilyDoctorGroup getDataById(Map<String, Object> map) {
		FamilyDoctorGroup familyDoctorGroup = familyDoctorGroupMapper.searchView(map);
		return familyDoctorGroup;
	}

	@Transactional
	@Override
	public void update(FamilyDoctorGroup familyDoctorGroup) {
		// 更新专干对应住户前，先清掉当前组专干已分配的住户
		Map<String, Object> paMap = new HashMap<String, Object>();
		paMap.put("hecadreType", Constants.FD_MEMBER_HECADRE);
		paMap.put("id", familyDoctorGroup.getId());
		familyDoctorGroupMapper.deleteHouseHold(paMap);
		// 重新设置当前组专干对应网格住户
		// familyDoctorGroupMapper.updateHouseHold(familyDoctorGroup.getHecadrehouseList());
		List<Map<String, Object>> hhouseList = familyDoctorGroup.getHecadrehouseList();
		for (Map<String, Object> hmap : hhouseList) {
			familyDoctorGroupMapper.updateHouseHold(hmap);
		}

		// 更新组包含的医生、护士、专干
		List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
		if (familyDoctorGroup.getDoctor_id() != null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id", familyDoctorGroup.getId());
			paramMap.put("user_id", familyDoctorGroup.getDoctor_id());
			paramMap.put("staff_id", staffService.getStaffByUserId(familyDoctorGroup.getDoctor_id()).getStaffId());
			paramMap.put("member_type", Constants.FD_MEMBER_DOCTOR);
			paramList.add(paramMap);
		}
		if (familyDoctorGroup.getNurse_id() != null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id", familyDoctorGroup.getId());
			paramMap.put("user_id", familyDoctorGroup.getNurse_id());
			paramMap.put("staff_id", staffService.getStaffByUserId(familyDoctorGroup.getNurse_id()).getStaffId());
			paramMap.put("member_type", Constants.FD_MEMBER_NURSE);
			paramList.add(paramMap);
		}
		if (familyDoctorGroup.getHecadrehouseList() != null) {
			List<Map<String, Object>> houseList = familyDoctorGroup.getHecadrehouseList();
			for (Map<String, Object> map : houseList) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				Long hecadreUserId = StringUtils.objToLong(map.get("hecadre_id"));
				paramMap.put("id", familyDoctorGroup.getId());
				paramMap.put("user_id", hecadreUserId);
				paramMap.put("staff_id", staffService.getStaffByUserId(hecadreUserId).getStaffId());
				paramMap.put("member_type", Constants.FD_MEMBER_HECADRE);
				paramList.add(paramMap);
			}
		}
		// 更新组内医生、护士、专干前，先清掉已分配的数据
		familyDoctorGroupMapper.deleteGroupDetail(paMap);
		// 重新分配当前组的医生、护士、专干
		familyDoctorGroupMapper.saveInfo(paramList);
	}

	@Transactional
	@Override
	public void delete(Map<String, Object> map) {
		familyDoctorGroupMapper.deleteHouseHold(map);
		familyDoctorGroupMapper.deleteGroupDetail(map);
		familyDoctorGroupMapper.deleteGroup(map);
	}

	@Override
	public List<Map<String, Object>> getSelectZGList(Map<String, Object> map) {
		List<Long> userIdList = familyDoctorGroupMapper.getSelectList(map);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		if (userIdList.size() > 0) {
			List<User> userList = authService.getUsers(userIdList);
			if (userList.size() > 0) {
				for (User user : userList) {
					Map<String, Object> remap = new HashMap<String, Object>();
					remap.put("id", user.getId());
					remap.put("name", user.getRealName());
					resultList.add(remap);
				}
			}
		}
		return resultList;
	}

	@Override
	public List<Map<String, Object>> getSeletedHecadreList(Map<String, Object> map) {
		List<Map<String, Object>> resultList = familyDoctorGroupMapper.selectedHecadreList(map);
		for (Map<String, Object> hecadreMap : resultList) {
			hecadreMap.put("hecadre_name", getNameByUserId(Long.parseLong(hecadreMap.get("hecadre_id").toString())));
		}
		return resultList;
	}

	@Override
	public Integer isStation(Map<String, Object> map) {
		return familyDoctorGroupMapper.isStation(map);
	}

	@Override
	public List<Map<String, Object>> hecadreSelectedList(Map<String, Object> map) {
		return familyDoctorGroupMapper.hecadreSelectedList(map);
	}

}
