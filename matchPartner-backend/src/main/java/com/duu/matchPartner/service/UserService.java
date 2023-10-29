package com.duu.matchPartner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duu.matchPartner.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param userCode    用户编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userCode);

    /**
     * 判断是否是管理员
     * @param loginUser
     * @return
     */
    public boolean isAdmin(User loginUser);

    /**
     * 判断是否是管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);


    List<User> searchUserByTags(List<String> tags);

    int updataUser(User user,User loginUser);

    User getLoginUser(HttpServletRequest request);

    List<User> getUserByTags(List<String> tags);
}
