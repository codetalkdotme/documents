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

import com.newcare.auth.exception.AuthServiceException;
import com.newcare.auth.pojo.User;
import com.newcare.auth.service.IAuthService;
import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.doc.mapper.StaffMapper;
import com.newcare.doc.pojo.Staff;
import com.newcare.doc.pojo.StaffExample;
import com.newcare.fnd.pojo.Lookup;
import com.newcare.fnd.pojo.Role;
import com.newcare.fnd.service.ILookupService;
import com.newcare.fnd.service.IRoleService;
import com.newcare.fnd.service.community.ICommunityPositionService;
import com.newcare.fnd.service.community.IStaffService;
import com.newcare.inter.mapper.InterHospitalDepartmentMapper;
import com.newcare.inter.vo.CascadeVO;
import com.newcare.select.pojo.Select;
import com.newcare.util.SelectUtil;

@Service("staffService")
public class StaffServiceImpl implements IStaffService {

	@Autowired
	private StaffMapper staffMapper;

	@Autowired
	private ILookupService lookupService;

	@Autowired
	private ICommunityPositionService communityPositionService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IAuthService authService;

	@Autowired
	private InterHospitalDepartmentMapper interHospitalDepartmentMapper;

	@Override
	public List<Staff> getOrganizationStaffs(Long loginOrganizationId) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.LOGIN_ORIZATION_ID, loginOrganizationId);
		return staffMapper.searchByCondition(map);
	}

	@Override
	@Transactional
	public Staff save(Staff staff) throws AuthServiceException {
		// 如果有user没staff。 就修改user新增staff
		User existUser = null;
		if (StringUtils.isNotBlank(staff.getStaffIdcard())) {
			existUser = authService.getUser(staff.getStaffIdcard());
		}
		if (existUser != null) {
			// 修改
			// 修改user
			existUser.setRealName(staff.getStaffName());
			existUser.setMobile(staff.getStaffMobile());
			if (staff.getStaffSex() != null) {
				existUser.setSex(staff.getStaffSex());
			}
			existUser.setPasswd(staff.getUser().getPasswd());
			authService.updateUser(existUser);
			staff.setUserId(existUser.getId().intValue());
		} else {
			// 新增
			// 添加用户, 性别，手机，姓名，身份证。 登录名在页面user对象
			staff.getUser().setSex(staff.getStaffSex() != null ? staff.getStaffSex() : Constants.SEX_MALE);
			staff.getUser().setMobile(staff.getStaffMobile());
			staff.getUser().setRealName(staff.getStaffName());
			staff.getUser().setPersonId(staff.getStaffIdcard());
			staff.getUser().setCreateDate(new Date());
			// 如果是健教专干
			Role role = roleService.findRoleById(staff.getStaffRole().longValue());
			// if (role.getCode().equalsIgnoreCase(Constants.ROLE_CODE_HECADRE))
			// {
			// staff.getUser().setSourceType(Constants.SOURCE_TYPE_HECADRE);
			// } else if
			// (role.getCode().equalsIgnoreCase(Constants.ROLE_CODE_DOCTOR)) {
			// staff.getUser().setSourceType(Constants.SOURCE_TYPE_DOCTOR);
			// }
			Long userId = authService.addUser(staff.getUser());
			staff.setUserId(userId.intValue());
		}

		staff.setStaffStatus(Constants.STATUS_ACTIVE);
		staff.setStaffRegisterDate(new Date());
		staffMapper.insert(staff);

		return staff;
	}

	@Override
	@Transactional
	public int update(Staff staff) throws AuthServiceException {
		// 修改用户，性别，手机，姓名
		User user = getUserByUserId(staff.getUserId().longValue());
		user.setRealName(staff.getStaffName());
		user.setMobile(staff.getStaffMobile());
		if (staff.getStaffSex() != null) {
			user.setSex(staff.getStaffSex());
		}
		// 用户没有修改密码
		if (user.getPasswd().equalsIgnoreCase(staff.getUser().getPasswd())) {
			user.setPasswd(null);
		} else {
			user.setPasswd(staff.getUser().getPasswd());
		}
		authService.updateUser(user);
		int result = staffMapper.updateByPrimaryKey(staff);
		return result;
	}

	@Override
	@Transactional
	public int updateAccount(Staff staff) throws AuthServiceException {
		// 只修改密码
		User user = getUserByUserId(staff.getUser().getId());
		// 用户没有修改密码
		if (user.getPasswd().equalsIgnoreCase(staff.getUser().getPasswd())) {
			user.setPasswd(null);
		} else {
			user.setPasswd(staff.getUser().getPasswd());
		}
		authService.updateUser(user);
		int result = staffMapper.updateByPrimaryKey(staff);
		return result;
	}

	@Override
	public int delete(Long staffId) {
		Staff staff = getStaffById(staffId);
		staff.setStaffStatus(Constants.STATUS_STOP);
		return staffMapper.updateByPrimaryKey(staff);
	}

	@Override
	public Page<Staff> getPageStaffs(Page<Staff> page) {
		// 查询数据库, 分页
		if (page.getPageSize() != -1) {
			page.getSearchMap().put("offset", page.getPageOffset());
			page.getSearchMap().put("limit", page.getPageSize());
		}
		page.setContent(staffMapper.searchByCondition(page.getSearchMap()));
		page.setTotal(staffMapper.countByCondition(page.getSearchMap()));
		return page;
	}

	/**
	 * 返回1 则表示至少有一个 返回0 表示该职务没有被使用， 可以被删除
	 *
	 * @param positionId
	 * @return
	 */
	@Override
	public int countStaffByPositionId(Integer positionId) {
		StaffExample example = new StaffExample();
		StaffExample.Criteria criteria = example.createCriteria();
		criteria.andStaffPostEqualTo(positionId);
		criteria.andStaffStatusEqualTo(Constants.STATUS_ACTIVE);
		return (int) staffMapper.countByExample(example);
	}

	@Override
	public int countStaffByTitleCode(String titleCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("staffTitle", titleCode);
		return staffMapper.countByCondition(map);
	}

	@Override
	public int countStaffByDepartmentCode(String departmentCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("staffDepartment", departmentCode);
		return staffMapper.countByCondition(map);
	}

	/**
	 * 如果返回0. 则表示没有被引用。
	 *
	 * @param roleId
	 * @return
	 */
	@Override
	public int countStaffByRoleId(Integer roleId) {
		StaffExample example = new StaffExample();
		StaffExample.Criteria criteria = example.createCriteria();
		criteria.andStaffRoleEqualTo(roleId);
		criteria.andStaffStatusEqualTo(Constants.STATUS_ACTIVE);
		return (int) staffMapper.countByExample(example);
	}

	@Override
	public Staff getStaffById(Long id) {
		return staffMapper.selectByPrimaryKey(id.intValue());
	}

	@Override
	public int unbindUser(Long userId) {
		authService.unbindDevice(userId);
		return 1;
	}

	@Override
	public User getUserByUserId(Long userId) {
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		List<User> users = authService.getUsers(userIds);
		if (users != null && users.size() > 0) {
			return users.get(0);
		}
		return null;
	}

	@Override
	public List<User> getUsersByUserIds(List<Long> userIds) {
		return authService.getUsers(userIds);
	}

	@Override
	public int updateUser(User user) throws AuthServiceException {
		authService.updateUser(user);
		return 0;
	}

	@Override
	public int countStaffsByCondition(Map<String, Object> params) {
		// 这里是根据职务查询的。 类型固定。
		params.put("lookupCategory", Constants.LOOKUPS_WORKING_TYPE);
		return staffMapper.countStaffsByCondition(params);
	}

	@Override
	public Staff resetStaff(Staff staff, Long loginOrganizationId) {
		return resetStaff(staff, getOrganizationStaffs(loginOrganizationId), null);
	}

	/**
	 * @param staff
	 * @param allStaffs
	 *            避免集合反复查询
	 * @param users
	 *            避免集合反复查询
	 * @return
	 */
	public Staff resetStaff(Staff staff, List<Staff> allStaffs, List<User> users) {
		List<CascadeVO> cascadeVOs = interHospitalDepartmentMapper.getDepartmentList(1);
		List<Lookup> cascadeLookups = new ArrayList<Lookup>();
		for (CascadeVO cascadeVO : cascadeVOs) {
			Lookup lookup = new Lookup();
			lookup.setLookup_code(cascadeVO.getCode());
			lookup.setLookup_value(cascadeVO.getName());
			cascadeLookups.add(lookup);
		}
		staff.setDepartments(cascadeLookups);
		staff.setEducations(lookupService.getLookupsByCategory(Constants.LOOKUPS_EDUCATION));
		staff.setFulltimeWorks(lookupService.getLookupsByCategory(Constants.LOOKUPS_FULLTME_WORK));
		staff.setPositions(communityPositionService.getAllCommunityPositions());
		// 专干
		staff.setRoles(roleService.findAllRoles(false));
		staff.setSexs(SelectUtil.getSexs());
		staff.setTitles(lookupService.getLookupsByCategory(Constants.LOOKUPS_WORKING_TITLE));
		// 排除自己
		if (staff.getStaffId() != null && allStaffs != null && allStaffs.size() > 0) {
			for (Staff parentStaff : allStaffs) {
				if (parentStaff.getStaffId().longValue() == staff.getStaffId().longValue()) {
					allStaffs.remove(parentStaff);
					break;
				}
			}
		}
		staff.setParentStaffs(allStaffs);
		if (staff.getUserId() != null) {
			if (users != null) {
				for (User user : users) {
					if (user.getId().longValue() == staff.getUserId().longValue()) {
						staff.setUser(user);
					}
				}
			} else {
				User user = getUserByUserId(staff.getUserId().longValue());
				if (user != null) {
					staff.setUser(user);
				}
			}
		}
		return staff;
	}

	@Override
	public Page<Staff> resetPage(Page<Staff> pages) {
		if (pages.getContent() != null && pages.getContent().size() > 0) {
			List<Staff> allStaffs = getOrganizationStaffs(Long.parseLong(pages.getSearchMap().get(Constants.LOGIN_ORIZATION_ID).toString()));
			List<Long> userIds = new ArrayList<Long>();
			for (Staff staff : pages.getContent()) {
				userIds.add(staff.getUserId().longValue());
			}
			List<User> users = authService.getUsers(userIds);
			for (Staff staff : pages.getContent()) {
				resetStaff(staff, allStaffs, users);
			}
		}
		return pages;
	}

	/**
	 * 获取服务站角色。 医生/护士/健教专干 这里没有排除分区被用的
	 *
	 * @param stationId
	 * @param roleCode
	 * @return
	 */
	@Override
	public List<Staff> getStaffByStationRole(Long stationId, String roleCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleCode", roleCode);
		params.put("stationId", stationId);
		return staffMapper.getStaffByStationRole(params);
	}

	@Override
	public List<Select> getStaffSelects(List<Staff> staffs) {
		List<Select> selects = new ArrayList<>();
		if (staffs != null && staffs.size() > 0) {
			for (Staff staff : staffs) {
				selects.add(new Select(staff.getStaffId() + "", staff.getStaffName(), ""));
			}
		}
		return selects;
	}

	@Override
	public List<User> getUsersByStaffIds(List<Long> staffIds) {
		List<Long> userIds = new ArrayList<>();
		if (staffIds != null && staffIds.size() > 0) {
			for (Long staffId : staffIds) {
				Staff staff = getStaffById(staffId);
				if (staff != null) {
					userIds.add(staff.getUserId().longValue());
				}
			}
			if (userIds.size() > 0) {
				return getUsersByUserIds(userIds);
			}
		}
		return null;
	}

	@Override
	public List<Staff> getStaffsByStaffIds(List<Long> staffIds) {
		if (staffIds != null && staffIds.size() > 0) {
			return staffMapper.getStaffsByStaffIds(staffIds);
		}
		return null;
	}

	@Override
	public Staff getStaffByUserId(Long userId) {
		StaffExample example = new StaffExample();
		StaffExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId.intValue());
		criteria.andStaffStatusEqualTo(Constants.STATUS_ACTIVE);
		List<Staff> staffs = staffMapper.selectByExample(example);
		return staffs != null && staffs.size() > 0 ? staffs.get(0) : null;
	}

	@Override
	public int countStaffBylookupCode(String lookupCategory) {
		return staffMapper.countStaffBylookupCode(lookupCategory);
	}

	@Override
	public int hasAllRightsByStaffId(Long staffId) {
		return staffMapper.hasAllRightsByStaffId(staffId);
	}

	@Override
	public List<String> getRoleCodeByUserId(Long userId) {
		List<String> codeList = staffMapper.selectRoleCodeByUserId(userId);

		return codeList;
	}

	@Override
	public Page<Staff> getPageAccounts(Page<Staff> page) {
		// 查询数据库, 分页
		if (page.getPageSize() != -1) {
			page.getSearchMap().put("offset", page.getPageOffset());
			page.getSearchMap().put("limit", page.getPageSize());
		}
		page.setContent(staffMapper.searchAccount(page.getSearchMap()));
		page.setTotal(staffMapper.countAccount(page.getSearchMap()));
		return page;
	}

	@Override
	public Staff getOrganizationAdmin(Long organizationId) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(organizationId);
		List<Staff> staffs = staffMapper.getOrganizationAdmins(ids);
		return staffs != null && staffs.size() == 1 ? staffs.get(0) : null;
	}

	@Override
	public List<Staff> getOrganizationAdmins(List<Long> organizationIds) {
		return staffMapper.getOrganizationAdmins(organizationIds);
	}

}
