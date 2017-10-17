package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newcare.auth.exception.AuthServiceException;
import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.doc.pojo.Staff;
import com.newcare.fnd.mapper.OrganizationMapper;
import com.newcare.fnd.mapper.OrganizationStationMapper;
import com.newcare.fnd.mapper.ServiceStationMapper;
import com.newcare.fnd.pojo.Organization;
import com.newcare.fnd.pojo.OrganizationExample;
import com.newcare.fnd.pojo.OrganizationStation;
import com.newcare.fnd.pojo.community.ServiceStation;
import com.newcare.fnd.service.IOrganizationService;
import com.newcare.fnd.service.community.IStaffService;
import com.newcare.select.pojo.Select;

/**
 * Created by Administrator on 2017/6/13.
 */
@Service("organizationService")
public class OrganizationServiceImpl implements IOrganizationService {

	@Autowired
	private OrganizationMapper organizationMapper;

	@Autowired
	private ServiceStationMapper serviceStationMapper;

	@Autowired
	private OrganizationStationMapper organizationStationMapper;

	@Autowired
	private IStaffService staffService;

	@Override
	@Transactional
	public int save(Organization organization, Staff staff) throws AuthServiceException {
		organization.setOrganizationStatus(true);
		organization.setCreateDate(new Date());
		int count = organizationMapper.insert(organization);
		// 如果是卫生站组织，则建立卫生站
		if (organization.getIsStation()) {
			ServiceStation serviceStation = createStationByOrganization(organization);
			// 添加站和机构的关系
			addOrganizationStationRelations(organization, serviceStation);
		}
		// 添加站管理员
		staff.setOrganizationId(organization.getOrganizationId());
		staffService.save(staff);
		return count;
	}

	/**
	 * 添加组织和站的关系
	 * 
	 * @param organization
	 */
	private void addOrganizationStationRelations(Organization organization, ServiceStation serviceStation) {
		OrganizationStation organizationStation = new OrganizationStation();
		organizationStation.setOrganizationId(organization.getOrganizationId());
		organizationStation.setStationId(serviceStation.getId());
		organizationStationMapper.insert(organizationStation);
		if (organization.getParentId() != null) {
			Organization parentOrganization = organizationMapper.getOrganizationById(organization.getParentId());
			if (parentOrganization != null) {
				addOrganizationStationRelations(parentOrganization, serviceStation);
			}
		}
	}

	// 根据机构创建卫生站
	private ServiceStation createStationByOrganization(Organization organization) {
		ServiceStation serviceStation = new ServiceStation();
		serviceStation.setOrganization_id(organization.getOrganizationId());
		serviceStation.setName(organization.getOrganizationName());
		serviceStation.setStatus(Constants.STATUS_ACTIVE);
		serviceStationMapper.save(serviceStation);
		return serviceStation;
	}

	@Override
	@Transactional
	public int delete(Organization organization) {
		// 获取对应的站
		ServiceStation serviceStation = serviceStationMapper.getStationByOrganizationId(organization.getOrganizationId());
		organization.setUpdateDate(new Date());
		organization.setOrganizationStatus(false);
		int count = organizationMapper.updateByPrimaryKeySelective(organization);
		// 删除卫生站
		serviceStationMapper.deleteStationByOrganizationId(organization.getOrganizationId());
		// 删除所有有该卫生站的关系
		if (serviceStation != null) {
			organizationStationMapper.deleteOrganizationsByStationId(serviceStation.getId());
		}
		// 删除管理员
		Staff staff = staffService.getOrganizationAdmin(organization.getOrganizationId());
		if (staff != null) {
			staffService.delete(staff.getStaffId().longValue());
		}
		return count;
	}

	@Override
	@Transactional
	public int update(Organization organization, Staff staff) throws AuthServiceException {
		// 获取原数据
		Organization organizationDB = organizationMapper.getOrganizationById(organization.getOrganizationId());
		organization.setUpdateDate(new Date());
		int count = organizationMapper.updateByPrimaryKey(organization);
		// 修改站名
		if (organization.getIsStation()) {
			ServiceStation serviceStation = serviceStationMapper.getStationByOrganizationId(organization.getOrganizationId());
			serviceStation.setName(organization.getOrganizationName());
			serviceStationMapper.update(serviceStation);
		}
		// 如果修改了上级服务站， 就更新
		if (organization.getParentId() != null && organizationDB.getParentId().longValue() != organization.getParentId().longValue()) {
			// 删除老的关系
			loopDeleteParentOrganizationStations(organization.getOrganizationId(), organizationDB.getParentId());
			// 添加新的关系
			loopInsertParentOrganizationStations(organization.getOrganizationId(), organization.getParentId());
		}
		if (staff != null) {
			// 管理员
			staff.setOrganizationId(organization.getOrganizationId());
			if (staff.getStaffId() == null) {
				staffService.save(staff);
			} else {
				staffService.update(staff);
			}
		}
		return count;
	}

	/**
	 * 递归添加新的关系
	 * 
	 * @param organizationId
	 */
	private void loopInsertParentOrganizationStations(Long currentOrganizationId, Long targetOrganizationId) {
		// 当前的
		// 如果有上级
		if (targetOrganizationId != null && targetOrganizationId.longValue() != 0) {
			Organization targetOrganization = organizationMapper.getOrganizationById(targetOrganizationId);
			Map<String, Long> map = new HashMap<String, Long>();
			map.put("currentOrganizationId", currentOrganizationId);
			map.put("targetOrganizationId", targetOrganizationId);
			// 先删除老的关系
			organizationStationMapper.insertParentOrganizationsByOrganizationId(map);
			loopInsertParentOrganizationStations(currentOrganizationId, targetOrganization.getParentId());
		}
	}

	/**
	 * 递归删除老的关系
	 * 
	 * @param organizationId
	 */
	private void loopDeleteParentOrganizationStations(Long currentOrganizationId, Long targetOrganizationId) {
		Organization organization = organizationMapper.getOrganizationById(targetOrganizationId);
		// 删除当前的上级
		Map<String, Long> map = new HashMap<String, Long>();
		map.put("currentOrganizationId", currentOrganizationId);
		map.put("targetOrganizationId", targetOrganizationId);
		organizationStationMapper.deleteParentOrganizationsByOrganizationId(map);
		// 如果还有上级
		if (organization.getParentId() != null && organization.getParentId().longValue() != 0) {
			// 删除上级
			loopDeleteParentOrganizationStations(currentOrganizationId, organization.getParentId());
		}
	}

	@Override
	public Organization get(Long id) {
		return organizationMapper.selectByPrimaryKey(id);
	}

	@Override
	public Page<Organization> getPageOrganizations(Page<Organization> page) {
		// 查询数据库, 分页
		if (page.getPageSize() != -1) {
			page.getSearchMap().put("offset", page.getPageOffset());
			page.getSearchMap().put("limit", page.getPageSize());
		}
		page.setContent(organizationMapper.search(page.getSearchMap()));
		page.setTotal(organizationMapper.count(page.getSearchMap()));
		return page;
	}

	/**
	 * 获取当前站的下拉框
	 */
	@Override
	public List<Select> getOrganizationMappers(Long organizationId) {
		List<Select> mappers = new ArrayList<Select>();
		Organization organization = get(organizationId);
		if (organization != null) {
			mappers.add(new Select(String.valueOf(organization.getOrganizationId()), organization.getOrganizationName(), null));
		}
		return mappers;
	}

	@Override
	public List<Organization> getOrganizationsByName(String name) {
		return organizationMapper.getOrganizationsByName(name);
	}

	/**
	 * 转成下拉框
	 */
	@Override
	public List<Select> getOrganizationSelects(List<Organization> organizations) {
		List<Select> selects = new ArrayList<Select>();
		if (organizations != null && organizations.size() > 0) {
			for (Organization organization : organizations) {
				selects.add(new Select(organization.getOrganizationId() + "", organization.getOrganizationName(), ""));
			}
		}
		return selects;
	}

	/**
	 * 获取子机构
	 */
	@Override
	public List<Organization> getOrganizationsByParentId(Long parentId) {
		OrganizationExample example = new OrganizationExample();
		OrganizationExample.Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		criteria.andOrganizationStatusEqualTo(true);
		return organizationMapper.selectByExample(example);
	}

	@Override
	public List<Select> getOrganizationSelect(Organization organization) {
		List<Select> selects = new ArrayList<Select>();
		if (organization != null) {
			selects.add(new Select(organization.getOrganizationId() + "", organization.getOrganizationName(), ""));
		}
		return selects;
	}

	@Override
	public List<Organization> getOrganizationsByLevel(Integer level) {
		return organizationMapper.getOrganizationsByLevel(level);
	}

}
