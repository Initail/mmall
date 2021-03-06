package com.dry3.service.Impl;


import com.dry3.common.Const;
import com.dry3.common.ResponseCode;
import com.dry3.common.ServerResponse;
import com.dry3.common.TokenCache;
import com.dry3.dao.UserMapper;
import com.dry3.pojo.User;
import com.dry3.service.IUserService;
import com.dry3.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //先校验用户名是否存在
        int count = userMapper.checkUsername(username);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("账号或密码错误");
        }
        // MD5解密
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, MD5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("账号或密码错误");
        }
        //对查询到的密码进行置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessMessage("登录成功", user);
    }

    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        //普通用户注册
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        if (userMapper.insert(user) == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");

    }

    public ServerResponse checkValid(String str, String type) {
        //后端不对type进行校验是否非法输入,默认为正确输入
        if (StringUtils.isNotBlank(type)) {
            //开始校验
            if (Const.USERNAME.equals(type)) {
                if (userMapper.checkUsername(str) > 0) {
                    return ServerResponse.createByErrorMessage("账号已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                if (userMapper.checkEmail(str) > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数输入错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");

    }

    public ServerResponse<String> forgetGetQuestion(String username) {
        ServerResponse<String> response = checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("账号不存在");
        }
        String question = userMapper.forgetGetQuestion(username);
        if (StringUtils.isBlank(question)) {
            return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
        }
        return ServerResponse.createBySuccess(question);
    }

    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        //默认question是从forgetGetQuestion中获取, 则不存在参数错误情况
        int resultCount = userMapper.forgetCheckAnswer(username, question, answer);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("账号验证失败,答案错误");
        }
        String forgetToken = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
        return ServerResponse.createBySuccess(forgetToken);
    }

    public ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken) {
        ServerResponse<String> response = checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("账号不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token失效或不存在");
        }
        if (StringUtils.equals(token, forgetToken)) {
            TokenCache.invalidate(TokenCache.TOKEN_PREFIX + username);
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int updateCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (updateCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
            return ServerResponse.createByErrorMessage("修改密码失败");
        }
        return ServerResponse.createByErrorMessage("token验证错误");
    }

    public ServerResponse resetPassword(User user, String passwordOld, String passwordNew) {
        //防止横向越权必须得校验当前传入旧密码是当前用户的
        int checkCount = userMapper.checkPasswordByUserId(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (checkCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        //更新新密码
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createByErrorMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user) {
        //不能更新Username
        //先校验email是否被其他用户使用
        int checkResult = userMapper.checkEmailExceptUserId(user.getEmail(), user.getId());
        if (checkResult > 0) {
            return ServerResponse.createByErrorMessage("email已存在,请更换email");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("更新用户信息失败", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新用户信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId) {
        User userInfo = userMapper.selectByPrimaryKey(userId);
        if (userInfo == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        //需要置空查询出来的密码
        userInfo.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(userInfo);
    }
    //backend

    /***
     * 校验管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user) {
        if (user.getRole() != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
