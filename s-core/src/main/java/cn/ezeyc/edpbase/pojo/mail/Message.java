package cn.ezeyc.edpbase.pojo.mail;



/**
 * 邮件
 * @author wz
 */
public class Message {
    /**
     * 邮件名称
     */
    private String title;
    /**
     * 内容
     */
    private String context;
    /**
     *邮箱地址
     */
    private String email;
    /**
     *附件地址
     */
    private  String filePath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
