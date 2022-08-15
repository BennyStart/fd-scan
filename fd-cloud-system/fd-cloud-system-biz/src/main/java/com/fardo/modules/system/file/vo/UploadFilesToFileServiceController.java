package com.fardo.modules.system.file.vo;

import com.fardo.common.api.vo.Result;
import com.fardo.common.exception.FdException;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.utils.FileServiceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = "上传文件至文件服务")
@RequestMapping("/sys/fileService")
@Slf4j
public class UploadFilesToFileServiceController {

    @ApiOperation(value = "文件上传(目前只支持单个)", notes = "文件上传(目前只支持单个)")
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Result uploadFile(@ApiParam(name = "file", value = "文件",required=true)  @RequestParam(value="file") MultipartFile file) throws Exception {
        Result<UploadFilesVo> result = new Result<>();
        if(file !=null){
            String fileName = FileServiceUtils.saveFile(file);
            UploadFilesVo vo = new UploadFilesVo();
            vo.setFileUrl(fileName);
            result.setResult(vo);
            result.setSuccess(true);
            result.setMessage("上传成功");
            return result;
        }
        return result.error500("上传失败");
    }

    @ApiOperation("文件下载,返回流")
    @RequestMapping(value = "/getFile", method = RequestMethod.GET)
    public void getPic(@ApiParam(name = "fileUrl", value = "文件存储key", required = true) @RequestParam(name = "fileUrl") String fileUrl,
                       HttpServletResponse response) throws Exception {
        if (StringUtil.isEmpty(fileUrl)) {
            throw new FdException("请求参数缺失");
        }
        FileServiceUtils.downLoadFile(fileUrl,response);
    }

}
