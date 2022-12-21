package cn.ezeyc.edpbase.core.license.verity;


import cn.ezeyc.edpbase.core.license.create.CustomKeyStoreParam;
import de.schlichtherle.license.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

/**
 * License校验类
 *
 * @author zifangsky
 * @date 2018/4/20
 * @since 1.0.0
 */
public class LicenseVerify {
    private Logger logger=  LoggerFactory.getLogger(LicenseVerify.class);

    /**
     * 安装License证书
     * @author zifangsky
     * @date 2018/4/20 16:26
     * @since 1.0.0
     */
    public synchronized LicenseContent install(LicenseVerifyParam param) throws Exception {
        LicenseContent result = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("++++++++ 开始安装证书 ++++++++");
        //1. 安装证书
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(initLicenseParam(param));
        licenseManager.uninstall();
        final File file = new File(param.getLicensePath());
        logger.info("证书位置:"+file.getPath());
        if(file.exists()){
            result = licenseManager.install(file);
            logger.info(MessageFormat.format("证书安装成功，证书有效期：{0} - {1}",format.format(result.getNotBefore()),format.format(result.getNotAfter())));
        }else{
            logger.error("未发现证书,系统暂时不能使用!请联系管理员安装证书");
        }
        logger.info("++++++++ 安装证书结束 ++++++++");
        return result;
    }

    /**
     * 校验License证书
     * @author zifangsky
     * @date 2018/4/20 16:26
     * @since 1.0.0
     * @return boolean
     */
    public boolean verify(){
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //2. 校验证书
        try {
            LicenseContent licenseContent = licenseManager.verify();
            if(licenseContent!=null){
                return true;
            }
            return false;
        }catch (Exception e){
            logger.error("证书校验失败！",e);
            return false;
        }
    }

    /**
     * 初始化证书生成参数
     * @param param License校验类需要的参数
     * @return de.schlichtherle.license.LicenseParam
     */
    private LicenseParam initLicenseParam(LicenseVerifyParam param){
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);

        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

        KeyStoreParam publicStoreParam = new CustomKeyStoreParam(LicenseVerify.class
                ,param.getPublicKeysStorePath()
                ,param.getPublicAlias()
                ,param.getStorePass()
                ,null);

        return new DefaultLicenseParam(param.getSubject()
                ,preferences
                ,publicStoreParam
                ,cipherParam);
    }

}


