package com.newcare.fnd.service.impl;

import com.newcare.cache.service.ICacheService;
import com.newcare.core.pagination.Page;
import com.newcare.core.pagination.PagedData;
import com.newcare.fnd.mapper.LookupMapper;
import com.newcare.fnd.pojo.Lookup;
import com.newcare.fnd.service.ILookupService;
import com.newcare.fnd.service.community.IStaffService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("lookupService")
public class LookupServiceImpl implements ILookupService {

    private static String LOOKUP_PREFIX = "LOOKUP_";

    @Autowired
    private LookupMapper lookupMapper;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IStaffService staffService;

    @Override
    public int add(Lookup lookup) {
        lookup.setLookup_code(UUID.randomUUID().toString());
        int result = lookupMapper.add(lookup);
        List<String> list = new ArrayList<String>();
        list.add(lookup.getLookup_category());
        clearLookups(list);
        return result;
    }

    @Override
    public int update(Lookup lookup) {
        int result = lookupMapper.update(lookup);
        List<String> list = new ArrayList<String>();
        list.add(lookup.getLookup_category());
        clearLookups(list);
        return result;
    }

    @Override
    public List<Lookup> getLookupsByCategory(String category) {
        boolean empty = StringUtils.isBlank(category);
        if (empty) {
            category = "ALL";
        }

        if (cacheService.exists(LOOKUP_PREFIX + category)) {
            return (List<Lookup>) cacheService.get(LOOKUP_PREFIX + category);
        }

        List<Lookup> lookups = lookupMapper.getLookupsByCategory(empty ? null : category);
        cacheService.set(LOOKUP_PREFIX + category, lookups);
        return lookups;
    }

    @Override
    public Lookup getLookupById(Long id) {
        if (id == null) {
            return null;
        }
        List<Lookup> lookups = getLookupsByCategory(null);
        for (Lookup lookup : lookups) {
            if (lookup.getLookup_id().longValue() == id) {
                return lookup;
            }
        }
        return null;
    }

    @Override
    public Page<Lookup> getPageLookups(Page<Lookup> page) {
        List<Lookup> lookups = getLookupsByCategory(String.valueOf(page.getSearchMap().get("lookup_catagory")));
        new PagedData<Lookup>(page, lookups);
        return page;
    }

    @Override
    public List<String> getLookupCategorys() {
        return lookupMapper.getLookupCategorys();
    }

    private void clearLookups() {
        clearLookups(getLookupCategorys());
    }

    private void clearLookups(List<String> categorys) {
        for (String category : categorys) {
            if (cacheService.exists(LOOKUP_PREFIX + category)) {
                cacheService.delete(LOOKUP_PREFIX + category);
            }
        }
        if (cacheService.exists(LOOKUP_PREFIX + "ALL")) {
            cacheService.delete(LOOKUP_PREFIX + "ALL");
        }
    }

    @Override
    public int delete(Long id) {
        Lookup lookup = lookupMapper.getLookupById(id);
        int count = staffService.countStaffBylookupCode(lookup.getLookup_code());
        if (count == 0) {
            int result = lookupMapper.delete(id);
            List<String> categories = new ArrayList<String>();
            categories.add(lookup.getLookup_category());
            clearLookups(categories);
            return result;
        }
        return 0;
    }

    @Override
    public List<Lookup> getLookupByName(String category, String name) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("lookupCategory",category);
        map.put("lookupValue",name);
        return lookupMapper.getLookupByName(map);
    }
}
