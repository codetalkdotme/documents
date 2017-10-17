package com.newcare.fnd.service.impl;

import com.newcare.cache.service.ICacheService;
import com.newcare.constant.Constants;
import com.newcare.core.pagination.Page;
import com.newcare.core.pagination.PagedData;
import com.newcare.fnd.mapper.CommunityPositionMapper;
import com.newcare.fnd.pojo.community.CommunityPosition;
import com.newcare.fnd.service.community.ICommunityPositionService;
import com.newcare.fnd.service.community.IStaffService;
import com.newcare.select.pojo.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("communityPositionService")
public class CommunityPositionServiceImpl implements ICommunityPositionService {

    @Autowired
    private CommunityPositionMapper communityPositionMapper;

    @Autowired
    private IStaffService staffService;

    @Autowired
    private ICacheService cacheService;

    private static String POSITIONS = "positions";

    private static String POSITION_PREFIX = "position-";

    private static String POSITION_MAPPERS = "position-mappers";

    @Override
    public int add(CommunityPosition communityPosition) {
        int result = communityPositionMapper.add(communityPosition);
        clearPositions(communityPosition.getPosition_id());
        return result;
    }

    /**
     * 0 表示删除失败。 该职务被占用
     *
     * @param id
     * @return
     */
    @Override
    public int delete(Long id) {
        // 等于0 没有被引用过
        if (staffService.countStaffByPositionId(id.intValue()) == 0) {
            CommunityPosition post = getById(id);
            post.setPosition_status(Constants.STATUS_STOP);
            int result = update(post);
            clearPositions(id);
            return result;
        }
        return 0;
    }

    @Override
    public int update(CommunityPosition communityPosition) {
        int result = communityPositionMapper.update(communityPosition);
        clearPositions(communityPosition.getPosition_id());
        return result;
    }

    @Override
    public List<CommunityPosition> getAllCommunityPositions() {
        if (cacheService.exists(POSITIONS)) {
            return (List<CommunityPosition>) cacheService.get(POSITIONS);
        }
        List<CommunityPosition> positions = communityPositionMapper.getAllCommunityPositions();
        cacheService.set(POSITIONS, positions);
        return positions;
    }

    @Override
    public CommunityPosition getById(Long id) {
        if (cacheService.exists(POSITION_PREFIX + id)) {
            return (CommunityPosition) cacheService.get(POSITION_PREFIX + id);
        }
        CommunityPosition position = communityPositionMapper.getById(id);
        cacheService.set(POSITION_PREFIX + id, position);
        return position;
    }

    @Override
    public Page<CommunityPosition> getPagePositions(Page<CommunityPosition> page) {
        new PagedData<CommunityPosition>(page,getAllCommunityPositions());
        return page;
    }

    @Override
    public List<Select> getCommunityPositionMappers() {
        if (cacheService.exists(POSITION_MAPPERS)) {
            return (List<Select>) cacheService.get(POSITION_MAPPERS);
        }
        List<Select> mappers = new ArrayList<Select>();
        List<CommunityPosition> positions = getAllCommunityPositions();
        if (positions != null && positions.size() > 0) {
            for (CommunityPosition position : positions) {
                mappers.add(new Select(String.valueOf(position.getPosition_id()), position.getPosition_name(), null));
            }
        }
        cacheService.set(POSITION_MAPPERS, mappers);
        return mappers;
    }

    private void clearPositions(Long id) {
        if (cacheService.exists(POSITIONS)) {
            cacheService.delete(POSITIONS);
        }

        if (id != null && cacheService.exists(POSITION_PREFIX + id)) {
            cacheService.delete(POSITION_PREFIX + id);
        }
    }

    @Override
    public CommunityPosition getByName(String name) {
        return communityPositionMapper.getByName(name);
    }
}
