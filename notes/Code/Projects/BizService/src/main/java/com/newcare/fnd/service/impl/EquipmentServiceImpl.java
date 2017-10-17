package com.newcare.fnd.service.impl;

import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.fnd.mapper.EquipmentMapper;
import com.newcare.fnd.pojo.Lookup;
import com.newcare.fnd.pojo.community.Equipment;
import com.newcare.fnd.pojo.community.ServiceStation;
import com.newcare.fnd.service.ILookupService;
import com.newcare.fnd.service.community.IEquipmentService;
import com.newcare.fnd.service.community.IServiceStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("equipmentService")
public class EquipmentServiceImpl implements IEquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private IServiceStationService serviceStationService;

    @Autowired
    private ILookupService lookupService;

    @Override
    public Page<Equipment> getPageEquipment(Page<Equipment> page) {
        // 查询数据库, 分页
        if (page.getPageSize() != -1) {
            page.getSearchMap().put("offset", page.getPageOffset());
            page.getSearchMap().put("limit", page.getPageSize());
        }
        page.setContent(equipmentMapper.search(page.getSearchMap()));
        page.setTotal(equipmentMapper.count(page.getSearchMap()));
        return page;
    }

    @Override
    public Equipment get(Long id) {
        return equipmentMapper.selectByPrimaryKey(id.intValue());
    }

    @Override
    public int save(Equipment equipment) {
        equipment.setEquStatus(true);
        return equipmentMapper.insert(equipment);
    }

    @Override
    public int delete(Long id) {
        Equipment equipment = get(id);
        equipment.setEquStatus(false);
        return equipmentMapper.updateByPrimaryKey(equipment);
    }

    @Override
    public int update(Equipment equipment) {
        return equipmentMapper.updateByPrimaryKey(equipment);
    }

    @Override
    public Page<Equipment> resetPage(Page<Equipment> pages) {
        if (pages.getContent() != null && pages.getContent().size() > 0) {
            List<ServiceStation> serviceStations = serviceStationService.getAll(Long.parseLong(pages.getSearchMap().get(Constants.LOGIN_ORIZATION_ID).toString()));
            List<Lookup> equTypes = lookupService.getLookupsByCategory(Constants.LOOKUPS_EQU_TYPE);
            for (Equipment equ : pages.getContent()) {
                equ.setServiceStations(serviceStations);
                equ.setEquTypes(equTypes);
            }
        }
        return pages;
    }
}
