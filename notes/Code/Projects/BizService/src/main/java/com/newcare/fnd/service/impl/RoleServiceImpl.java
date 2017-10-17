package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.newcare.cache.service.ICacheService;
import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.core.pagination.PagedData;
import com.newcare.doc.mapper.StaffMapper;
import com.newcare.doc.pojo.StaffExample;
import com.newcare.fnd.mapper.RoleMapper;
import com.newcare.fnd.mapper.RoleMenuMapper;
import com.newcare.fnd.mapper.RoleTypeMapper;
import com.newcare.fnd.pojo.Role;
import com.newcare.fnd.pojo.RoleMenu;
import com.newcare.fnd.pojo.RoleMenuExample;
import com.newcare.fnd.pojo.RoleType;
import com.newcare.fnd.service.IRoleService;
import com.newcare.select.pojo.Select;

/**
 * Created by guobxu on 30/3/2017.
 */
@Service("roleService")
public class RoleServiceImpl implements IRoleService {

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private RoleTypeMapper rtMapper;

	@Autowired
	private ICacheService cacheService;

	@Autowired
	private StaffMapper staffMapper;

	@Autowired
	private RoleMenuMapper roleMenuMapper;

	// cache key prefix
	public static final String CACHE_KEY_PREFIX = "ROLE-";

	public static final String CACHE_TYPE_ADMIN = "ROLE-ADMIN-LIST";

	public static final String CACHE_TYPE_NOT_ADMIN = "ROLE-NOT-ADMIN-LIST";

	public static final String CACHE_TYPE_ALL = "ROLE-ALL";

	public static final String ROLE_MAPPERS = "ROLE-MAPPERS";

	public List<Role> findAllRoles() {
		return findAllRoles(null);
	}

	public List<Role> findAllRoles(Boolean isAdmin) {
		String cacheKey = CACHE_TYPE_ALL;
		if (isAdmin != null) {
			cacheKey = isAdmin ? CACHE_TYPE_ADMIN : CACHE_TYPE_NOT_ADMIN;
		}
		if (cacheService.exists(cacheKey)) {
			return (List<Role>) cacheService.get(cacheKey);
		} else {
			List<Role> roles = roleMapper.findAllRoles(isAdmin);
			cacheService.set(cacheKey, roles);
			return roles;
		}
	}

	public Role findRoleById(Long id) {
		if (id == null)
			return null;
		String cacheKey = CACHE_KEY_PREFIX + id;
		if (cacheService.exists(cacheKey)) {
			return (Role) cacheService.get(cacheKey);
		} else {
			Role role = roleMapper.findRoleById(id);
			cacheService.set(cacheKey, role);
			return role;

		}
	}

	@Transactional
	public int insertRole(Role role) {

		int i = roleMapper.insertRole(role);

		saveRoleTypes(role);

		// 添加新的
		addRoleMenus(role);
		clearRoles(null);
		return i;
	}

	@Override
	public int deleteRole(Long id) {
		StaffExample example = new StaffExample();
		StaffExample.Criteria criteria = example.createCriteria();
		criteria.andStaffRoleEqualTo(id.intValue());
		criteria.andStaffStatusEqualTo(Constants.STATUS_ACTIVE);
		long count = staffMapper.countByExample(example);
		if (count == 0) {
			int result = roleMapper.deleteRole(id);
			rtMapper.deleteByRoleId(id);

			clearRoles(id);
			return result;
		}
		return 0;
	}

	@Transactional
	public int updateRole(Role role) {
		// 删掉老的权限。 添加新的
		deleteRoleMenus(role);
		addRoleMenus(role);
		int i = roleMapper.updateRole(role);

		// 更新role types
		rtMapper.deleteByRoleId(role.getId());
		saveRoleTypes(role);

		clearRoles(role.getId().longValue());
		return i;
	}

	private void saveRoleTypes(Role role) {
		List<RoleType> typeList = role.getTypeList();

		if (typeList != null && typeList.size() > 0) {
			for (RoleType type : typeList) {
				type.setRoleId(role.getId());
			}

			rtMapper.insertRoleTypes(typeList);
		}
	}

	/**
	 * 更新角色的菜单权限
	 *
	 * @param role
	 */
	private void addRoleMenus(Role role) {
		if (role.getRoleMenus() != null && role.getRoleMenus().size() > 0) {
			for (RoleMenu menu : role.getRoleMenus()) {
				if (menu.getMenuId() == null)
					continue;
				menu.setRoleId(role.getId().longValue());
				resetRoleAction(menu);
				roleMenuMapper.insert(menu);
			}
		}
	}

	private void resetRoleAction(RoleMenu roleMenu) {
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(Constants.MENU_VIEW);
		jsonArray.add(Constants.MENU_CREATE);
		jsonArray.add(Constants.MENU_UPDATE);
		jsonArray.add(Constants.MENU_DELETE);
		roleMenu.setActions(jsonArray.toString());
	}

	private void deleteRoleMenus(Role role) {
		RoleMenuExample example = new RoleMenuExample();
		RoleMenuExample.Criteria criteria = example.createCriteria();
		criteria.andRoleIdEqualTo(role.getId().longValue());
		roleMenuMapper.deleteByExample(example);
	}

	@Override
	public Page<Role> getPageRoles(Page<Role> page) {
		List<Role> roles = findAllRoles();
		new PagedData<Role>(page, roles);
		return page;
	}

	@Override
	public List<RoleMenu> getRoleMenusByRoleId(Long roleId) {

		RoleMenuExample example = new RoleMenuExample();
		RoleMenuExample.Criteria criteria = example.createCriteria();
		criteria.andRoleIdEqualTo(roleId);
		return roleMenuMapper.selectByExample(example);
	}

	@Override
	public List<Select> getRoleMappers() {
		if (cacheService.exists(ROLE_MAPPERS)) {
			return (List<Select>) cacheService.get(ROLE_MAPPERS);
		}
		List<Select> mappers = new ArrayList<Select>();
		List<Role> roles = findAllRoles();
		if (roles != null && roles.size() > 0) {
			for (Role role : roles) {
				mappers.add(new Select(String.valueOf(role.getId()), role.getName(), null));
			}
		}
		cacheService.set(ROLE_MAPPERS, mappers);
		return mappers;
	}

	private void clearRoles(Long roleId) {
		if (cacheService.exists(CACHE_TYPE_ADMIN)) {
			cacheService.delete(CACHE_TYPE_ADMIN);
		}
		if (cacheService.exists(CACHE_TYPE_NOT_ADMIN)) {
			cacheService.delete(CACHE_TYPE_NOT_ADMIN);
		}
		if (cacheService.exists(CACHE_TYPE_ALL)) {
			cacheService.delete(CACHE_TYPE_ALL);
		}
		if (cacheService.exists(ROLE_MAPPERS)) {
			cacheService.delete(ROLE_MAPPERS);
		}
		if (roleId != null) {
			if (cacheService.exists(CACHE_KEY_PREFIX + roleId)) {
				cacheService.delete(CACHE_KEY_PREFIX + roleId);
			}
			if (cacheService.exists(MenuServiceImpl.MENUHTML + roleId)) {
				cacheService.delete(MenuServiceImpl.MENUHTML + roleId);
			}
		}
	}

	@Override
	public Role getByName(String name) {
		return roleMapper.getByName(name);
	}

	@Override
	public boolean hasTopestAdmin(int level) {
		return roleMapper.hasTopestAdmin(level);
	}

	@Override
	public Role getLevelTopestAdminRole(int level) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("level", level);
		map.put("weight", 1);
		List<Role> roles = roleMapper.getRoleByLevelAndWeight(map);
		if (roles != null && roles.size() > 0) {
			return roles.get(0);
		}
		return null;
	}

	@Override
	public List<Role> getLevelOtherAdminRoles(int level) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("level", level);
		map.put("weight", 2);
		return roleMapper.getRoleByLevelAndWeight(map);
	}
}
