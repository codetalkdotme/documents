package com.newcare.fnd.mapper;

import java.util.List;
import java.util.Map;

import com.newcare.fnd.pojo.Memo;

public interface MemoMapper {

	public void insertMemo(Memo memo);
	
	public void updateMemo(Memo memo);
	
	public List<Memo> listMemoByUser(Map<String, Object> params);
	
	public Memo selectMemoByIdAndUser(Map<String, Long> params);
	
}
