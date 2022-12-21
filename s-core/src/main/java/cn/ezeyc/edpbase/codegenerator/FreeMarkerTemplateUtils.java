package cn.ezeyc.edpbase.codegenerator;

import cn.ezeyc.edpcommon.error.ExRuntimeException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;

/**
 * FreeMarkerTemplateUtils：
 *
 * @author Administrator
 * @date 2020年11月30日, 0030 17:28:38
 */
public class FreeMarkerTemplateUtils {
    private FreeMarkerTemplateUtils(){}
    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);

    static{
        //这里比较重要，用来指定加载模板所在的路径
        CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtils.class, "/template"));
        CONFIGURATION.setDefaultEncoding("UTF-8");
    }

    public static Template getTemplate(String templateName)  {
        try {
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw new ExRuntimeException("报错");
        }
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }
}
