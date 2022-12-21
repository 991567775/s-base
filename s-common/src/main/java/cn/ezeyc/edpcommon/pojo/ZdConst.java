package cn.ezeyc.edpcommon.pojo;

import java.io.File;
import java.net.URL;

/**
 * Zd：
 *
 * @author Administrator
 * @date 2020年8月10日, 0010 09:50:30
 */
public class ZdConst {
    /**
     * 代理类包含字符
     */
    public  static  CharSequence proxy="Proxy";
    /**
     *分页
     */
    public  static  CharSequence page="page";
    /**
     * 逗号
     */
    public  static  String comma=",";
    /**
     * 等于号
     */
    public  static  String eq="=";
    /**
     * 点
     */
    public  static  CharSequence dot=".";
    /**
     * 下划线
     */
    public  static  CharSequence uLine="_";

    /**
     *斜杠
     */
    public  static  CharSequence slanting="/";
    /**
     * 请求
     */
    public  static  CharSequence post="POST";
    /**
     * 请求
     */
    public  static  CharSequence get="GET";

    /**
     * 默认token不拦截地址
     */
    public  static String[] tokenIgnore=new String[]{"/error",

            "/swagger-ui.html",
            "/swagger-ui/",
            "/*.html",
            "/favicon.ico",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/loginUtil/**",
            "/login/**","/login","/file/**","/upload/**", "/images/**",
            "/license/**","/sysLog/update","sysMonitor/**"};
    /**
     * 默认证书不拦截地址
     */
    public  static String[] licenseIgnore=new String[]{
            "/sysCompany/selectList","/login/login","/license/**","/sysLog/update"};
    /**
     * 是否开启文档key
     */
    public  static String swapper="edp.config.swapper";
    /**
     * 是否开启数据库连接池监控key
     */
    public  static String showMonitor="edp.db.showMonitor";
    /**
     * win附件位置key
     */
    public  static String uploadWinPath="edp.config.uploadWinPath";
    /**
     * win附件位置key
     */
    public  static String applicationName="spring.application.name";
    /**
     * 非win附件位置key
     */
    public  static String uploadPath="edp.config.uploadPath";
    /**
     * 是否开启日志记录
     */
    public static String enableLog="edp.log.enable";
    /**
     * 日志请求服务
     */
    public static String   logService=  "edp.log.service";
    /**
     * 日志请求地址
     */
    public static String logUrl="edp.log.url";
    /**
     * 远程请求
     */
    public  static  String client="Client";



    /**
     * 空格
     */
    public  static  CharSequence space=" ";
    /**
     * 单引号
     */
    public  static  CharSequence single="'";
    /**
     * 横杠
     */
    public  static  CharSequence colon="-";
    /**
     * 左括号
     */
    public  static  CharSequence left_kuo="(";
    /**
     * 右括号
     */
    public  static  CharSequence right_kuo=")";
    /**
     * 主键
     */
    public  static  String id="id";


    /**
     *以Dao结尾的类
     */
    public  static  String end_with_Dao="Dao";

    /**
     *以Service结尾的类
     */
    public  static  String end_with_service="Service";
    /**
     *以Service实现类结尾的类
     */
    public  static  String end_with_service_impl="ServiceImpl";
    /**
     *以Control结尾的类
     */
    public  static  String end_with_control="Control";
    /**
     * model 范型
     */
    public  static  CharSequence T="T";

    /**
     *总页数
     */
    public  static  String total="total";
    /**
     *model包
     */
    public  static  String package_path="cn.ezeyc";
    /**
     *model包
     */
    public  static  CharSequence package_model="model";
    /**
     *dao包
     */
    public  static  CharSequence package_dao="dao";
    /**
     *service
     */
    public  static  CharSequence package_service="service";
    /**
     *serviceimpl
     */
    public  static  CharSequence package_service_impl="service/impl";
    /**
     *service
     */
    public  static  CharSequence package_control="control";
    /**
     * java文件后缀
     */
    public  static  CharSequence suffix_java=".java";
    /**
     *freemarker模板文件后缀
     */
    public  static  CharSequence suffix_ftl=".ftl";
    /**
     * 作者
     */
    public final static  CharSequence AUTHOR="wz";
    /**
     * 当前操作用户id
     *
     */
    public final static  String  CURRENT_USER_ID="currentUserId";
    /**
     * 创建用户
     *
     */
    public final static  CharSequence  CREATE_USER="createUser";
    /**
     * 修改用户
     *
     */
    public final static  CharSequence  UPDATE_USER="updateUser";
    /**
     * 权限
     *
     */
    public final static  CharSequence DATA_CODE="dataCode";
    /**
     * 创建时间
     */
    public final static  CharSequence  CREATE_DATE="createDate";


    /**
     * 更新时间
     */
    public final  static CharSequence  UPDATE_DATE="updateDate";
    /**
     * win上传文件默认路径
     */
    public final  static String  WIN_UPLOAD=" C:/data/upload/";
    /**
     * 上传文件默认路径
     */
    public final  static String  UPLOAD="/root/upload/";
    /**
     * 上传请求路径
     */
    public final  static String  UPLOAD_URL="/upload/**";

    /**
     * 配置属性
     */
    public  final  static String EDP_LOG_RECORD="edp.config.logRecord";
    //********************************项目各种路径*******************************
    /**
     * 项目路径
     */
    public final  static CharSequence PROJECT_PATH=System.getProperty("user.dir")+ File.separator;
    /**
     * toString 方法
     */
    public static CharSequence toString="toString";

    /**
     * 系统
     */
    public static String windows="windows";
    /**
     * 系统
     */
    public static String linux="linux";
    /**
     * 登录方式：微信登录
     */
    public static String grantTypeQQ="4";
    /**
     * 登录方式：qq登录
     */
    public static String grantTypeWeChat="3";
    /**
     * 登录方式：手机号登录
     */
    public static String grantTypePhone="2";
    /**
     * 登录方式：邮箱登录
     */
    public static String grantTypeMail="1";


    /**
     *模块路径
     */
    public final static  String getModulePath(Class o){
        URL resource = o.getResource(File.separator);
        if(resource!=null&&resource.getPath()!=null){
            return  File.separator+resource.getPath().replace("/target/classes/", "").replace("/target/test-classes/", "").replaceFirst(File.separator, "")+ File.separator;
        }
        return  null;
    }

    /**
     * 模块资源路径
     * @param o
     * @return
     */
    public final static  String getModuleResourcePath(Class o){
        return  getModulePath(o)+"target/classes/";
    }
    /**
     * 输出模块java路径
     */
    public final static  String outJavaPath(String modulePath){
        return  PROJECT_PATH+modulePath+"/src/main/java";
    }
    public final static  String outJavaWithRootPath(String root,String modulePath){
        return  root+modulePath+"/src/main/java";
    }
    /**
     * ui模板位置
     */
    public final static CharSequence UI_LIST="/template/list.vm";
    public final static CharSequence UI_INPUT="/template/input.vm";
    /**
     * 输出ui路径
     */
    public final static  String outUiPath(String modulePath,String name){
        return  PROJECT_PATH+"wz-ui/src/page/module/"+modulePath+"/"+name+".vue";
    }

    //********************************项目各种路径*******************************



}
