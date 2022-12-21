package cn.ezeyc.edpbase.service.impl;

import cn.ezeyc.edpbase.pojo.PicCode.PicCode;
import cn.ezeyc.edpbase.util.RedisUtil;
import cn.ezeyc.edpbase.idgenerator.SnowflakeIdGenerator;
import cn.ezeyc.edpbase.service.LoginUtilService;
import cn.ezeyc.edpbase.util.PicValidateCodeUtil;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.enums.ResultEnum;
import cn.ezeyc.edpcommon.pojo.ResultBody;
import org.apache.commons.lang3.StringUtils;

public class LoginUtilServiceImpl implements LoginUtilService {

    @autowired
    private RedisUtil redisUtil;


    @Override
    public ResultBody image() throws Exception {
        PicCode simple = PicValidateCodeUtil.simpleMath();
        Long key = SnowflakeIdGenerator.nextId();
        // 存入redis并设置过期时间为30分钟
        redisUtil.set(String.valueOf(key), simple.getCode(), 300);
        // 将key和base64返回给前端
        return ResultBody.success().put("key",key).put("image","data:image/jpg;base64,"+simple.getBase64());
    }


    @Override
    public   ResultBody  verify(String picKey,String picCode){
        if(StringUtils.isBlank(picCode) ){
            return  ResultBody.failed(ResultEnum.picNull);
        } else if (!redisUtil.exists(picKey)) {
            return  ResultBody.failed(ResultEnum.picTimeOut);
        }else if(!picCode.equals(redisUtil.get(picKey))){
            return ResultBody.failed(ResultEnum.picError);
        }
        return ResultBody.success();
    }
}
