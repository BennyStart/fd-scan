package com.fardo.common.exception;

import com.fardo.common.api.vo.Result;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.enums.IndexUniqEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 * 
 * @Author maozf
 * @Date 20-11-21
 */
@RestControllerAdvice
@Slf4j
public class FdExceptionHandler {

    /**
     * 接口异常
     */
    @ExceptionHandler(ApiException.class)
    public ResultVo handleRRException(ApiException e){
        log.error(e.getMessage(), e);
        return ResultVo.getResultVo(e.getStatus(), e.getMessage());
    }

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(FdException.class)
	public Result<?> handleRRException(FdException e){
		log.error(e.getMessage(), e);
		return Result.error(e.getMessage());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public Result<?> handlerNoFoundException(Exception e) {
		log.error(e.getMessage(), e);
		return Result.error(404, "路径不存在，请检查路径是否正确");
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public ResultVo handleDuplicateKeyException(DuplicateKeyException e){
		log.error(e.getMessage(), e);
		for (IndexUniqEnum uniqEnum : IndexUniqEnum.values()) {
			if(e.getMessage().contains(uniqEnum.getUniqKey())) {
				return ResultVo.getResultVo(ResultCode.DUPLICATE_KEY_ERROR.getResultCode(),ResultCode.DUPLICATE_KEY_ERROR.getResultMsg()+uniqEnum.getUniqText());
			}
		}
		return  ResultVo.getResultVo(ResultCode.DUPLICATE_KEY_ERROR);
	}

	@ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
	public Result<?> handleAuthorizationException(AuthorizationException e){
		log.error(e.getMessage(), e);
		return Result.noauth("没有权限，请联系管理员授权");
	}

	@ExceptionHandler(Exception.class)
	public ResultVo handleException(Exception e){
		log.error(e.getMessage(), e);
		return ResultVo.getResultVo(ResultCode.ERROR);
	}
	
	/**
	 * @Author 政辉
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Result<?> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
		StringBuffer sb = new StringBuffer();
		sb.append("不支持");
		sb.append(e.getMethod());
		sb.append("请求方法，");
		sb.append("支持以下");
		String [] methods = e.getSupportedMethods();
		if(methods!=null){
			for(String str:methods){
				sb.append(str);
				sb.append("、");
			}
		}
		log.error(sb.toString(), e);
		//return Result.error("没有权限，请联系管理员授权");
		return Result.error(405,sb.toString());
	}
	
	 /** 
	  * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException 
	  */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
    	log.error(e.getMessage(), e);
        return Result.error("文件大小超出10MB限制, 请压缩或降低文件质量! ");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResultVo handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    	log.error(e.getMessage(), e);
        return ResultVo.getResultVo(ResultCode.DB_EXECUTE_ERROR.getResultCode(), e.getMessage());
    }

    @ExceptionHandler(PoolException.class)
    public Result<?> handlePoolException(PoolException e) {
    	log.error(e.getMessage(), e);
        return Result.error("Redis 连接异常!");
    }

}
