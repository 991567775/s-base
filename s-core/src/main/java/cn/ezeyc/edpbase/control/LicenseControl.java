package cn.ezeyc.edpbase.control;


import cn.ezeyc.edpbase.core.license.create.*;
import cn.ezeyc.edpcommon.annotation.framework.value;
import cn.ezeyc.edpcommon.annotation.mvc.body;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpcommon.pojo.ResultBody;

/**
 *
 * 用于生成证书文件
 * @author zifangsky
 * @date 2018/4/26
 * @since 1.0.0
 */
public class  LicenseControl {
    /**
     * 证书生成路径
     */
    @value("edp.config.licensePath")
    private String licensePath;

    /**
     * 获取服务器硬件信息
     */
    /**
     * showdoc
     * @catalog 通用/证书
     * @title 获取服务器硬件信息
     * @description
     * @method post
     * @url 地址/license/getServerInfos
     * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
     * @return_param code int 状态码
     * @return_param message string 提示信息
     * @return_param timestamp long 服务器时间戳
     * @return_param extra map 额外信息
     * @return_param data Object 数据
     * @remark
     * @number 1
     */
    public LicenseCheckModel getServerInfos() {
        //操作系统类型
         String   osName = System.getProperty("os.name");
        osName = osName.toLowerCase();
        AbstractServerInfos abstractServerInfos ;
        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.toLowerCase().startsWith(ZdConst.windows)) {
            abstractServerInfos = new WindowsServerInfos();
        } else{
            abstractServerInfos = new LinuxServerInfos();

        }
        return abstractServerInfos.getServerInfos();
    }

    /**
     * 证书实体
     * @param param
     * @return
     */
    public ResultBody generateLicense( @body LicenseCreatorParam param) {
        Boolean result = LicenseCreator.licenseInit(param);
        if(result){
            return     ResultBody.success("ok");
        }else{
            return ResultBody.failed("证书文件生成失败");
        }
    }
    /**
     * 证书实体
     * @param param
     * @return
     */
    public ResultBody generateLicenseInstall( @body LicenseCreatorParam param)  {
        if(licensePath!=null){
            Boolean result = LicenseCreator.licenseInit(param);
            if(result){
                try {
                    LicenseCreator.licenseInstall(licensePath);
                } catch (Exception e) {
                    return ResultBody.failed("证书文件安装失败");
                }
                return null;
            }else{
                return ResultBody.failed("证书文件生成失败");
            }
        }else{
            return ResultBody.failed("证书位置未配置");
        }

    }
}

