package com.newcare.fnd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.constant.Constants;
import com.newcare.exception.BizServiceException;
import com.newcare.fnd.mapper.MemoMapper;
import com.newcare.fnd.pojo.Memo;
import com.newcare.fnd.service.IMemoService;
import com.newcare.service.AbstractBizService;

/**
 * 每日总结服务实现
 * @author guobxu
 * 
 * TODO: cache
 *
 */
@Service("memoService")
public class MemoServiceImpl extends AbstractBizService implements IMemoService {

	@Autowired
	private MemoMapper memoMapper;
	
	public static final String URI_MEMO_LIST = "/hca/api/business/hecadre/getmemolist";
	
	public static final String URI_MEMO_MOD = "/hca/api/business/hecadre/modmemo";
	
	
	@Override
	public void addMemo(Memo memo) {
		memoMapper.insertMemo(memo);
	}

	@Override
	public void updateMemo(Memo memo) {
		memoMapper.updateMemo(memo);
	}
	
	@Override
	public Memo findMemoByIdAndUser(Long memoId, Long userId) {
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("memoId", memoId);
		params.put("userId", userId);
		
		return memoMapper.selectMemoByIdAndUser(params);
	}
	
	@Override
	public List<Memo> listMemoByUser(Long userId, int begin, int count) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("begin", begin);
		params.put("count", count);
		
		return memoMapper.listMemoByUser(params);
	}
	

	@Override
	public String doPost(String uri, Map<String, Object> data) throws BizServiceException {
		if(URI_MEMO_LIST.equals(uri)) {
			return memoList(data);
		} else if(URI_MEMO_MOD.equals(uri)) {
			return memoMod(data);
		}
		
		return null;
	}

	/**
	 * 获取总结列表
	 * Params:
	 * {
		  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
		  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
		  "user_id":0L //用户ID，u64
		  "auth_str":"xx" //通信认证密文串
		}
 	 * 
 	 * Return:
 	 * {
		  "ret_code":0 //返回码，u16
		  "ret_msg":"xx" //可选，返回错误时的错误描述
		  "ret_data": //响应数据
		  [
		    {
		      "memo_id":0L //备忘ID，u64
		      "title":"xx" //标题
		      "content":"xx" //内容
		      "update_time":0L //最后更新时间，u64
		    }
		    ...
		  ]
		}
 	 * 
	 * @param data
	 * @return
	 */
	private String memoList(Map<String, Object> params) {
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString();
		
		Long userIdParam = Long.parseLong(params.get("user_id").toString());
		Integer beginParam = Integer.parseInt(params.get("begin").toString()),
				countParam = Integer.parseInt(params.get("count").toString());
		
		List<Memo> memoList = listMemoByUser(userIdParam, beginParam, countParam);
		
		return successWithObject(memoList);
	}

	/**
	 * 新增或者修改备忘
	 * Params:
	 * {
		  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
		  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
		  "user_id":0L //用户ID，u64
		  "auth_str":"xx" //通信认证密文串
		  "memo_id":0L //备忘ID，u64，可选，不携带则为新增备忘
		  //以下为要新增或修改的备忘信息
		  "title":"xx" //标题
		  "content":"xx" //内容
		}
	 * 
	 * @param data
	 * @return
	 */
	private String memoMod(Map<String, Object> params) {
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString();
		
		Long userIdParam = Long.parseLong(params.get("user_id").toString());
		Object titleObj = params.get("title"), contentObj= params.get("content");
		
		String titleStr = null, contentStr = null;
		if(titleObj != null) titleStr = titleObj.toString();
		if(contentObj != null) contentStr = contentObj.toString();
		
		if(titleStr != null && titleStr.trim().length() == 0) {
			return errorWithKey("memo_error_title_empty");
		}
		if(contentStr != null && contentStr.trim().length() == 0) {
			return errorWithKey("memo_error_content_empty");
		}
		
		Object memoIdObj = params.get("memo_id");
		if(memoIdObj != null) { // 修改
			Long memoIdParam = Long.parseLong(memoIdObj.toString());
			
			if(titleStr == null && contentStr == null) {
				return errorWithKey("memo_error_both_empty");
			}
			
			Memo memo = this.findMemoByIdAndUser(memoIdParam, userIdParam);
			if(memo == null) {
				return errorWithKey("memo_error_notfound_byuser");
			}
			
			Memo memoForUpdate = new Memo();
			memoForUpdate.setId(memoIdParam);
			memoForUpdate.setTitle(titleStr);
			memoForUpdate.setContent(contentStr);
			updateMemo(memoForUpdate);
		} else {
			if(titleStr == null || contentStr == null) {
				return errorWithKey("memo_error_either_empty");
			}
			
			Memo memoForInsert = new Memo();
			memoForInsert.setTitle(titleStr);
			memoForInsert.setContent(contentStr);
			memoForInsert.setUserId(userIdParam);
			addMemo(memoForInsert);
		}
		
		return Constants.RESPONSE_SUCCESS;
	}



	
}
