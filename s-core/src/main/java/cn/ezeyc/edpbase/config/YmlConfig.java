package cn.ezeyc.edpbase.config;


import cn.ezeyc.edpcommon.annotation.framework.bean;
import cn.ezeyc.edpcommon.annotation.framework.value;
import cn.ezeyc.edpcommon.pojo.ZdConst;

/**
 * @author wz
 */
@bean
public class YmlConfig  {
    @value("edp.config.uploadPath")
    private String uploadPath;
    @value("edp.config.uploadWinPath")
    private String uploadWinPath;

    public String getUploadPath() {
        //获得系统属性集
        String props = System.getProperties().getProperty("os.name");
        if(props.toLowerCase().contains(ZdConst.windows)){
            return uploadWinPath!=null?uploadWinPath: ZdConst.WIN_UPLOAD;
        }else {
            return   uploadPath!=null?uploadPath: ZdConst.UPLOAD;
        }
    }
}
