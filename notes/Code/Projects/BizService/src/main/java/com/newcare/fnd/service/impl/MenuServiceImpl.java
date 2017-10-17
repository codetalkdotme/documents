package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.cache.service.ICacheService;
import com.newcare.constant.Constants;
import com.newcare.fnd.mapper.MenuMapper;
import com.newcare.fnd.pojo.Menu;
import com.newcare.fnd.pojo.MenuExample;
import com.newcare.fnd.pojo.Role;
import com.newcare.fnd.pojo.RoleMenu;
import com.newcare.fnd.service.IMenuService;
import com.newcare.fnd.service.IRoleService;

/**
 * Created by Administrator on 2017/5/15.
 */
@Service("menuService")
public class MenuServiceImpl implements IMenuService {

	// 菜单网页
	public static final String MENUHTML = "MENUHTML-";
	// 全部菜单
	private static final String MENUS = "MENUS";
	// 菜单DTO格式
	private static final String MENU_DTOS = "MENU_DTOS";
	// 菜单主键MAP
	private static final String MENU_MAP = "MENU_MAP";

	@Autowired
	private ICacheService cacheService;

	@Autowired
	private MenuMapper menuMapper;

	@Autowired
	private IRoleService roleService;

	private List<Menu> getAllMenus() {
		if (cacheService.exists(MENUS)) {
			return (List<Menu>) cacheService.get(MENUS);
		}
		MenuExample example = new MenuExample();
		example.setOrderByClause("sort asc");
		List<Menu> allMenus = menuMapper.selectByExample(example);
		cacheService.set(MENUS, allMenus);
		return allMenus;
	}

	@Override
	public List<Menu> getMenus() {
		if (cacheService.exists(MENU_DTOS)) {
			return (List<Menu>) cacheService.get(MENU_DTOS);
		}

		List<Menu> allMenus = getAllMenus();
		List<Menu> rootMenus = getMenuByParent(allMenus, 0L);
		// 加载子菜单
		loadSubMenus(rootMenus, allMenus);
		cacheService.set(MENU_DTOS, rootMenus);
		return rootMenus;
	}

	private Map<Long, Menu> setMenuMap(List<Menu> allMenus) {
		if (allMenus != null && allMenus.size() > 0) {
			Map<Long, Menu> menuMap = new HashMap<>();
			for (Menu menu : allMenus) {
				menuMap.put(menu.getId(), menu);
			}
			cacheService.set(MENU_MAP, menuMap);
			return menuMap;
		}
		return null;
	}

	private void loadSubMenus(List<Menu> rootMenus, List<Menu> allMenus) {
		if (rootMenus != null && rootMenus.size() > 0) {
			for (Menu menu : rootMenus) {
				List<Menu> subMenus = getMenuByParent(allMenus, menu.getId());
				if (subMenus.size() > 0) {
					menu.setSubMenus(subMenus);
					loadSubMenus(subMenus, allMenus);
				}
			}
		}
	}

	private List<Menu> getMenuByParent(List<Menu> allMenus, Long parent) {
		List<Menu> menus = new ArrayList<>();
		if (allMenus != null && allMenus.size() > 0) {
			for (Menu menu : allMenus) {
				if (!"none".equalsIgnoreCase(menu.getDisplay()) && menu.getParent().longValue() == parent.longValue()) {
					menus.add(menu);
				}
			}
		}
		return menus;
	}

	/**
	 * 生成页面菜单HTML
	 *
	 * @return
	 */
	@Override
	public String genMenuHTML(Long roleId) {
		Role role = roleService.findRoleById(roleId);
		if (cacheService.exists(MENUHTML + roleId)) {
			return String.valueOf(cacheService.get(MENUHTML + roleId));
		}
		List<RoleMenu> roleMenus = null;
		if (role != null) {
			roleMenus = roleService.getRoleMenusByRoleId(role.getId().longValue());
		}
		StringBuilder sb = new StringBuilder();
		List<Menu> menus = getMenus();
		if (menus != null && menus.size() > 0) {
			for (Menu menu : menus) {
				sb.append(genMenuHtml(menu, roleMenus));
			}
		}
		cacheService.set(MENUHTML + roleId, sb.toString());
		return sb.toString();
	}

	/**
	 * 检测是否有权限
	 *
	 * @param menu
	 * @param roleMenus
	 * @return
	 */
	private boolean checkRight(Menu menu, List<RoleMenu> roleMenus) {
		if (roleMenus != null && roleMenus.size() > 0) {
			for (RoleMenu roleMenu : roleMenus) {
				if (roleMenu.getMenuId().longValue() == menu.getId().longValue()) {
					return true;
				}
			}
		}
		return false;
	}

	private String genMenuHtml(Menu menu, List<RoleMenu> roleMenus) {
		StringBuilder sb = new StringBuilder();
		if ("none".equalsIgnoreCase(menu.getDisplay()) || !checkRight(menu, roleMenus)) {
			return "";
		}
		sb.append("<li  class='treeview' >");
		if (StringUtils.isNotBlank(menu.getDataUri())) {
			sb.append("<a ");
			if (StringUtils.isNotBlank(menu.getDataUri())) {
				sb.append("data-uri='" + Constants.PRJ_PERFIX + "/" + menu.getDataUri() + "' data-key='" + menu.getDataKey() + "' title='"
						+ menu.getDataName() + "' data-name='" + menu.getDataName() + "'>");
			}
			if (StringUtils.isNotBlank(menu.getIconClass())) {
				sb.append("<i class='" + menu.getIconClass() + "'></i>");
			}
			if (StringUtils.isNotBlank(menu.getSpanClass())) {
				sb.append("<span class='" + menu.getSpanClass() + "'>" + menu.getSpanText() + "</span>");
			}
			sb.append("</a>");
		} else {
			sb.append("<a>");
			// <i class="icon icon-baseDate"></i><span
			// class="tree-txt">公共基础数据</span><i class="icon icon-arrow-r
			// pull-right"></i>
			if (StringUtils.isNotBlank(menu.getIconClass())) {
				sb.append("<i class='" + menu.getIconClass() + "'></i>");
			}
			if (StringUtils.isNotBlank(menu.getSpanClass())) {
				sb.append("<span class='" + menu.getSpanClass() + "'>" + menu.getSpanText() + "</span>");
			}
			if (StringUtils.isNotBlank(menu.getIcon2Class())) {
				sb.append("<i class='" + menu.getIcon2Class() + "'></i>");
			}
			sb.append("</a>");
			if (menu.getSubMenus() != null && menu.getSubMenus().size() > 0) {
				sb.append("<ul class='treeview-menu'>");
				for (Menu subMenu : menu.getSubMenus()) {
					sb.append(genMenuHtml(subMenu, roleMenus));
				}
				sb.append("</ul>");
			}
		}
		sb.append("</li>");
		return sb.toString();
	}

	@Override
	public Menu getMenuById(Long id) {
		Map<Long, Menu> menuMap = null;
		if (cacheService.exists(MENU_MAP)) {
			menuMap = (Map<Long, Menu>) cacheService.get(MENU_MAP);
		} else {
			menuMap = setMenuMap(getAllMenus());
		}
		if (menuMap != null) {
			return menuMap.get(id);
		}
		return null;
	}

	@Override
	public String genInitMenuHtml() {
		StringBuilder sb = new StringBuilder();
		MenuExample example = new MenuExample();
		MenuExample.Criteria criteria = example.createCriteria();
		criteria.andDataUriEqualTo("serviceStation");
		List<Menu> menus = menuMapper.selectByExample(example);
		if (menus != null && menus.size() > 0) {
			Menu menu = menus.get(0);
			sb.append("<li  class='treeview' >");
			if (StringUtils.isNotBlank(menu.getDataUri())) {
				sb.append("<a ");
				if (StringUtils.isNotBlank(menu.getDataUri())) {
					sb.append("data-uri='" + Constants.PRJ_PERFIX + "/" + menu.getDataUri() + "' data-key='" + menu.getDataKey() + "' title='"
							+ menu.getDataName() + "' data-name='" + menu.getDataName() + "'>");
				}
				if (StringUtils.isNotBlank(menu.getIconClass())) {
					sb.append("<i class='" + menu.getIconClass() + "'></i>");
				}
				if (StringUtils.isNotBlank(menu.getSpanClass())) {
					sb.append("<span class='" + menu.getSpanClass() + "'>" + menu.getSpanText() + "</span>");
				}
				sb.append("</a>");
			}
			sb.append("</li>");
		}
		return sb.toString();
	}
}
