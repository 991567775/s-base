package cn.ezeyc.edpbase.control;


import cn.ezeyc.edpbase.pojo.base.ControlBase;
import cn.ezeyc.edpbase.service.LoginUtilService;

import cn.ezeyc.edpcommon.annotation.framework.autowired;

import cn.ezeyc.edpcommon.pojo.ResultBody;


/**
* 描述：登录相关工具
* @author wz
* @date 2021-05-12 16:17:25
*/
public class LoginUtilControl extends ControlBase {
    @autowired
    private LoginUtilService service;

    /**
     * showdoc
     * @catalog 通用/登录相关工具
     * @title 图片验证码获取
     * @description
     * @method post
     * @url 地址/loginUtil/image
     * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
     * @return_param code int 状态码
     * @return_param message string 提示信息
     * @return_param timestamp long 服务器时间戳
     * @return_param extra map 额外信息
     * @return_param data Object 数据
     * @remark
     * @number 1
     */
    public ResultBody image() throws Exception {
        return service.image();
    }
    /**
     * showdoc
     * @catalog 通用/登录相关工具
     * @title 图片验证码验证
     * @description
     * @method post
     * @url 地址/loginUtil/verify
     * @param picKey 必选 string  秘钥key
     * @param picCode 必选 string  验证码code
     * @json_param  {"picKey":"","picCode":""}
     * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
     * @return_param code int 状态码
     * @return_param message string 提示信息
     * @return_param timestamp long 服务器时间戳
     * @return_param extra map 额外信息
     * @return_param data Object 数据
     * @remark
     * @number 2
     */
    public   ResultBody  verify(String picKey, String picCode){
        return service.verify(picKey,picCode);
    }

}
