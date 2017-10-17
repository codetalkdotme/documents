package com.newcare.service;

import java.util.HashMap;
import java.util.Map;

import com.newcare.util.ObjectNoNullMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newcare.auth.service.IAuthService;
import com.newcare.constant.Constants;
import com.newcare.doc.util.DocUriMap;
import com.newcare.exception.BizServiceException;
import com.newcare.fnd.service.IWhitelistFileService;
import com.newcare.mesg.MessageService;

/**
 * Created by guobxu on 2017/4/1.
 *
 * 抽象服务类
 *
 */
public abstract class AbstractBizService implements IBizService {

	@Autowired
	private IWhitelistFileService wlFileService;
	
	@Autowired
	protected MessageService messageService;
	
	@Autowired
	protected IAuthService authService;
	
    // URL 和 Bean id映射关系
    public static final Map<String, String> URI_MAP = new HashMap<String, String>();
    static {
        URI_MAP.putAll(DocUriMap.URI_DOC);

        URI_MAP.put("/hca/api/business/role/add", "roleService");
        URI_MAP.put("/hca/api/business/role/update", "roleService");
        URI_MAP.put("/hca/api/business/role/list", "roleService");
        URI_MAP.put("/hca/api/business/role", "roleService");
        URI_MAP.put("/hca/api/business/hecadre/getactivity","actActivitiesService");
        URI_MAP.put("/hca/api/business/hecadre/startactivity","actActivitiesService");
        URI_MAP.put("/hca/api/business/hecadre/submitactivitysum","actActivitiesService");
        URI_MAP.put("/hca/api/business/hecadre/activityaccredit","actActivitiesService");
        URI_MAP.put("/hca/api/business/hecadre/signinactivity","actActivitiesService");
        URI_MAP.put("/hca/api/business/hecadre/getactivitystate","actActivitiesService");
        URI_MAP.put("/hca/api/business/hecadre/enteractivitysum","actActivitiesService");
        URI_MAP.put("/hca/api/business/hecadre/addactivityuser","actActivitiesService");
        URI_MAP.put("/hca/api/business/inhabitant/getappohospitalillnesslist","interService");
        URI_MAP.put("/hca/api/business/inhabitant/getappodoctorlist","interService");
        URI_MAP.put("/hca/api/business/inhabitant/getGoodsType","interService");
        URI_MAP.put("/hca/api/business/inhabitant/getGoodsPage","interService");
        URI_MAP.put("/hca/api/business/inhabitant/getGoodsDetails","interService");
        URI_MAP.put("/hca/api/business/modapporeg","interAppointmentService");
        URI_MAP.put("/hca/api/business/getapporeglist","interAppointmentService");
        URI_MAP.put("/hca/api/business/hecadre/modcircle","interCircleService");
        URI_MAP.put("/hca/api/business/getcirclelist","interCircleService");
        URI_MAP.put("/hca/api/business/getcirclepostlist","interCirclePostsService");
        URI_MAP.put("/hca/api/business/submitcirclepost","interCirclePostsService");
        URI_MAP.put("/hca/api/business/delcirclepost","interCirclePostsService");
        URI_MAP.put("/hca/api/business/hecadre/getgrid","GridMangerService");
        URI_MAP.put("/hca/api/business/hecadre/gettasksum","taskPlanService");
        URI_MAP.put("/hca/api/business/hecadre/addtask","taskPlanService");

        // 通知
        URI_MAP.put("/hca/api/business/hecadre/getnotice","noticeService");
        URI_MAP.put("/hca/api/business/inhabitant/getnotice","noticeService");
        URI_MAP.put("/hca/api/business/inhabitant/replynotice","noticeService");
        URI_MAP.put("/hca/api/business/getpushmsg", "noticeService");
        
        //预约免疫相关
        URI_MAP.put("/hca/api/business/getappoimmunelist","docVaccineRecordService");
        URI_MAP.put("/hca/api/business/modappoimmune","docVaccineRecordService");

        // 家庭医生签约
        URI_MAP.put("/hca/api/business/hecadre/getfdcontractlist","fdContractService");
        URI_MAP.put("/hca/api/business/hecadre/getfdcontract","fdContractService");
        URI_MAP.put("/hca/api/business/hecadre/modfdcontract","fdContractService");
        URI_MAP.put("/hca/api/business/inhabitant/getfdcontractstate","fdContractService");
        URI_MAP.put("/hca/api/business/inhabitant/signfdcontract","fdContractService");
        
        // 意见反馈
        URI_MAP.put("/hca/api/business/comment", "commentService");
        
        // 居民
        URI_MAP.put("/hca/api/business/inhabitant/gethecadre", "residentService");
        URI_MAP.put("/hca/api/business/hecadre/getinhabitant", "residentService");
        
        // 每日总结
        URI_MAP.put("/hca/api/business/hecadre/getmemolist", "memoService");
        URI_MAP.put("/hca/api/business/hecadre/modmemo", "memoService");
        
        // 行政区域
        URI_MAP.put("/hca/api/business/getarealist", "areaService");
        
        //专家讲堂居民端
        URI_MAP.put("/hca/api/business/inhabitant/gethomearticlelist","articleService");
        URI_MAP.put("/hca/web/inhabitant/getarticlelist", "articleService");
        URI_MAP.put("/hca/web/inhabitant/getarticledetail", "articleService");
        //居民端文章收藏
        URI_MAP.put("/hca/web/inhabitant/getarticlefavoritlist", "articleService");
        URI_MAP.put("/hca/web/inhabitant/submitarticlefavorite", "articleService");
        URI_MAP.put("/hca/web/inhabitant/canclearticlefavorite", "articleService");
        //专家讲堂健教端
        URI_MAP.put("/hca/web/hecadre/getarticlelist", "articleService");
        URI_MAP.put("/hca/web/hecadre/getarticlecategory", "articleService");
        URI_MAP.put("/hca/web/hecadre/submitarticle", "articleService");
        URI_MAP.put("/hca/web/hecadre/getarticlemod", "articleService");
        URI_MAP.put("/hca/web/hecadre/modarticle", "articleService");

        // 医生端  大肠癌,冠心病,糖尿病,乳腺癌,健康建议
        URI_MAP.put("/hca/web/doctor/evaluate/create","evaluateService");
        URI_MAP.put("/hca/web/doctor/evaluate/search","evaluateService");
        URI_MAP.put("/hca/web/doctor/evaluate/document","evaluateService");
        
        //app内容管理
        URI_MAP.put("/hca/api/business/inhabitant/gethomepic", "carouselRecordService");
        URI_MAP.put("/hca/api/business/inhabitant/getcompanyinfo", "companyIntroService");
        
        
    }

    // 服务登录
    static {
    	URI_MAP.put("/hca/api/business/login","bizAuthService");
    }
    
    private static ObjectMapper NEWMAPPER = new ObjectNoNullMapper();

    private static ObjectMapper MAPPER = new ObjectMapper();

    @Override
	public boolean addFileToWhitelist(String fdfsUrl, String name, String type, Long length) {
		return wlFileService.addFile(fdfsUrl, name, type, length);
	}

	@Override
	public boolean isWhitelistFile(String encodeUrl) {
		return wlFileService.isWhitelistFile(encodeUrl);
	}
    
    public String successWithObject(Object obj){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
        map.put(Constants.KEY_DATA, obj);

        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String successWithObjectNull(Object obj){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
        map.put(Constants.KEY_DATA, obj);

        try {
            return NEWMAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String successWithKey(String key){
    	String msg = messageService.get(key);
    	
    	return successWithMsg(msg);
    }
    
    public String successWithMsg(String msg){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
        map.put(Constants.KEY_MSG, msg);

        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String errorWithObject(Object obj){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, Constants.CODE_ERROR);
        map.put(Constants.KEY_DATA, obj);

        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String errorWithMsg(String message){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, Constants.CODE_ERROR);
        map.put(Constants.KEY_MSG, message);

        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String errorWithMsgAndRetCode(int retCode,String message){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_CODE, retCode);
        map.put(Constants.KEY_MSG, message);

        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String errorWithKey(String messageKey) {
		return String.format(Constants.RESPONSE_ERROR_COMMON_TMPL, messageService.get(messageKey));
	}
    
    public String errorWithCodeKey(int errcode, String key) {
		return String.format(Constants.RESPONSE_ERROR_TMPL, errcode, messageService.get(key));
	}
    
    @Override
    public String doGet(String uri, Map<String, String[]> params) throws BizServiceException {
    	throw new UnsupportedOperationException("Not implemented!");
    }
    
    @Override
    public String doUploadFile(String uri, String uid, String authStr) throws BizServiceException {
    	throw new UnsupportedOperationException("Not implemented!");
    }
    
    @Override
    public String doGetFile(String uri, String uid, String authStr) throws BizServiceException {
    	throw new UnsupportedOperationException("Not implemented!");
    }
    
    @Override
    public String doServiceAuth(String uri, Map<String, Object> data) throws BizServiceException {
    	throw new UnsupportedOperationException("Not implemented!");
    }

}













