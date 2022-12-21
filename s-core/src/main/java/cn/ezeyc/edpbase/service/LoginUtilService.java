package cn.ezeyc.edpbase.service;

import cn.ezeyc.edpcommon.pojo.ResultBody;

public interface LoginUtilService {
    /**
     * 获取图片验证码
     * @return
     * @throws Exception
     */
    public ResultBody image() throws Exception ;
    /**
     * 验证码验证
     * @param picKey
     * @param picCode
     * @return
     */
    public   ResultBody  verify(String picKey,String picCode);
}
