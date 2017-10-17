package com.newcare.fnd.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newcare.constant.Constants;
import com.newcare.exception.BizServiceException;
import com.newcare.fnd.mapper.CommentMapper;
import com.newcare.fnd.pojo.Comment;
import com.newcare.fnd.service.ICommentService;
import com.newcare.service.AbstractBizService;
import com.newcare.util.StringUtils;

/**
 * 意见反馈服务
 * @author guobxu
 *
 */
@Service("commentService")
public class CommentServiceImpl extends AbstractBizService implements ICommentService {

	@Autowired
	private CommentMapper cmntMapper;
	
	public static final String URI_ADD_COMMENT = "/hca/api/business/comment";
	
	@Override
	public void addComment(Comment cmnt) {
		cmntMapper.insertComment(cmnt);
	}

	@Override
	public String doPost(String uri, Map<String, Object> data) throws BizServiceException {
		if(URI_ADD_COMMENT.equals(uri)) {
			return addComment(data);
		}
		
		return null;
	}
	
	/**
	 * {
		  "src_type":"xx" //请求源的类型，如"HECadre APP"、"Inhabitant APP"等
		  "pf_type":"xx" //请求源的终端平台类型，如"Android"、"iOS"、"Web"等
		  "user_id":0L //用户ID，u64
		  "auth_str":"xx" //通信认证密文串
		  "comment":"xx" //意见内容
		}

	 * @param params
	 * @return
	 */
	private String addComment(Map<String, Object> params) {
		String srcTypeParam = params.get("src_type").toString(),
				pfTypeParam = params.get("pf_type").toString(),
				contentParam = params.get("comment").toString();
		
		Long userIdParam = Long.parseLong(params.get("user_id").toString());
		
		if(StringUtils.isNull(contentParam)) {
			return errorWithKey("cmnt_error_empty");
		}
		
		// 添加评论
		Comment cmnt = new Comment();
		cmnt.setUserId(userIdParam);
		cmnt.setContent(contentParam);
		cmntMapper.insertComment(cmnt);
		
		return Constants.RESPONSE_SUCCESS;
	}
	
}
