package com.newcare.fnd.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.fnd.mapper.FundMapper;
import com.newcare.fnd.pojo.community.Fund;
import com.newcare.fnd.pojo.community.ServiceStation;
import com.newcare.fnd.service.community.IFundService;
import com.newcare.fnd.service.community.IServiceStationService;
import com.newcare.select.pojo.Select;

@Service("fundService")
public class FundServiceImpl implements IFundService {

	@Autowired
	private FundMapper fundMapper;

	@Autowired
	private IServiceStationService serviceStationService;

	@Override
	public Page<Fund> getPageFunds(Page<Fund> page) {
		// 查询数据库, 分页
		if (page.getPageSize() != -1) {
			page.getSearchMap().put("offset", page.getPageOffset());
			page.getSearchMap().put("limit", page.getPageSize());
		}
		page.setContent(fundMapper.search(page.getSearchMap()));
		page.setTotal(fundMapper.count(page.getSearchMap()));
		return page;
	}

	@Override
	public Fund get(Long id) {
		return fundMapper.selectByPrimaryKey(id);
	}

	@Override
	public int add(Fund fund) {
		fund.setFundStatus(true);
		return fundMapper.insert(fund);
	}

	@Override
	public int update(Fund fund) {
		return fundMapper.updateByPrimaryKey(fund);
	}

	@Override
	public int delete(Long fundId) {
		Fund fund = get(fundId);
		fund.setFundStatus(false);
		return fundMapper.updateByPrimaryKey(fund);
	}

	@Override
	public Page<Fund> resetPage(Page<Fund> pages) {
		if (pages.getContent() != null && pages.getContent().size() > 0) {
			Long organizationId = Long.parseLong(pages.getSearchMap().get(Constants.LOGIN_ORIZATION_ID).toString());
			List<ServiceStation> serviceStations = serviceStationService.getAll(organizationId);
			for (Fund fund : pages.getContent()) {
				fund.setServiceStations(serviceStations);
			}
		}
		return pages;
	}

	@Override
	public List<Select> getYears() {
		List<Long> years = fundMapper.getYears();
		List<Select> selects = new ArrayList<Select>();
		if (years.size() > 0) {
			for (Long year : years) {
				selects.add(new Select(year + "", year + "", ""));
			}
		}
		return selects;
	}
}
