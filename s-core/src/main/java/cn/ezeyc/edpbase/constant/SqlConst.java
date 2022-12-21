package cn.ezeyc.edpbase.constant;
/**
 * @author wz
 */
public class SqlConst {
    /**
     * select
     */
    public  static  String select="select ";
    /**
     * 空格
     */
    public  static  String space=" ";
    /**
     * 单引号
     */
    public  static  String single="'";

    /**
     * 左括号
     */
    public  static  String left_kuo="(";
    /**
     * 右括号
     */
    public  static  String right_kuo=")";
    /**
     * 主键
     */
    public  static  String id="id";
    /**
     * 逗号
     */
    public  static  String comma=",";
    /**
     * from
     */
    public  static  String from=" from ";
    /**
     * limit
     */
    public  static  String limit="limit";

    /**
     * where
     */
    public  static  String where=" where 1=1  ";
    /**
     * 是否包含where
     */
    public  static  String hasWhere=" where ";
    public  static  String eq="=";
    public  static  String and=space+"and"+space;
    public  static  String val="?";

    public  static  String ne="!=";
    public  static  String gt=">";
    public  static  String ge=">=";
    public  static  String lt="<";
    public  static  String le="<=";

    public  static  String like=" like ";
    public  static  String likeS="%";
    public  static  String likeE="%";
    public  static  String likeSl="%";
    public  static  String likeEl="";
    public  static  String likeSr="";
    public  static  String likeEr="%";

    public static String noLike ="not like";




    public  static  String between=space+"between"+space;

    public  static  String noBetween=space+"not between"+space;

    public  static  String isNull=" is null ";
    public  static  String isNoNull=" is not null ";

    public  static  String isEmpty="='' ";
    public  static  String isNoEmpty="!='' ";
    public  static  String kuoRight=") ";
    public  static  String kuoLeft="( ";
    public  static  String or=" or ";

    public static String as=" as ";

    public  static  String aliasXing=" a.* ";

    public  static  String join=" join ";
    public  static  String alias=" a ";
    public  static  String aliasJoin="a.";
    public static String update="update ";
    public static String set=" set ";
    public static String delete="update  ";
    public static String reallyDelete="delete from ";
    public static String insert="insert into ";
    public static String values=" values(";
    public static String remove="remove";
    public static String methodUpdateByIdNotNull="updateByIdNotNull";
    public static String methodUpdateNotNull="updateNotNull";
    public static CharSequence orderBy=" order by ";
}
