package cn.ezeyc.edpbase.control;

import cn.ezeyc.edpbase.config.YmlConfig;
import cn.ezeyc.edpbase.pojo.base.ControlBase;
import cn.ezeyc.edpbase.util.FileUtil;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.mvc.get;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpcommon.pojo.ResultBody;

import java.io.*;
import java.net.URLEncoder;

/**
* 描述：附件上传
* @author wz
* @date 2021-05-12 16:17:25
*/
public class FileControl extends ControlBase {
    @autowired
    private YmlConfig config;
    /**
     * showdoc
     * @catalog 通用/附件
     * @title 附件上传
     * @description
     * @method post
     * @url 地址/file/upload
     * @header Authorization 必选 string  token认证
     * @param file 必选 File  附件
     * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
     * @return_param code int 状态码
     * @return_param message string 提示信息
     * @return_param timestamp long 服务器时间戳
     * @return_param extra map 额外信息
     * @return_param data Object 数据
     * @remark
     * @number 1
     */
    public ResultBody upload(File file){
        return ResultBody.success(FileUtil.upload(file,config.getUploadPath()));

    }
   /**
     * showdoc
     * @catalog 通用/附件
     * @title 附件下载
     * @description
     * @method get
     * @url 地址/file/down
     * @header Authorization 必选 string  token认证
     * @param fileName 必选 string  文件名称
     * @param path 必选 string  文件路径
     * @json_param  {"fileName":"","path":""}
     * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
     * @return_param code int 状态码
     * @return_param message string 提示信息
     * @return_param timestamp long 服务器时间戳
     * @return_param extra map 额外信息
     * @return_param data Object 数据
     * @remark
     * @number 1
     */
    @get
    public void down(String fileName, String path) throws IOException {
        if (fileName != null){
            File file = new File(config.getUploadPath()+path.replace("/upload",""));
            if (file.exists()){
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                response.setContentType("application/octet-stream");
                response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try{
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while(i != -1){
                        os.write(buffer,0,i);
                        i = bis.read(buffer);
                    }
                }catch (Exception e){
                    throw new ExRuntimeException(e.getMessage());
                }finally {
                    if (bis != null){
                        bis.close();
                    }
                    if (fis != null){
                        fis.close();
                    }
                }
            }
        }else {
            throw new ExRuntimeException("文件不存在");
        }

    }
}
