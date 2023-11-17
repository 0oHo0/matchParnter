package com.duu.matchPartner.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duu.matchPartner.common.BaseResponse;
import com.duu.matchPartner.common.ErrorCode;
import com.duu.matchPartner.common.ResultUtils;
import com.duu.matchPartner.exception.BusinessException;
import com.duu.matchPartner.model.domain.User;
import com.duu.matchPartner.model.request.UserLoginRequest;
import com.duu.matchPartner.model.request.UserRegisterRequest;
import com.duu.matchPartner.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.duu.matchPartner.contant.UserConstant.USER_LOGIN_STATE;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }


    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request ) {
        User loginUser = userService.getLoginUser(request);
        int res = userService.updateUser(user,loginUser);
        return ResultUtils.success(res);
    }


    @GetMapping("/tag")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam List<String> tags) {
        if(CollectionUtils.isEmpty(tags))
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        List<User> userByTags = userService.searchUserByTags(tags);
        return ResultUtils.success(userByTags);
    }

    @GetMapping("/home")
    public BaseResponse<Page<User>> searchUser(Long pageNum,Long pageSize,HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Page<User> userByTags = userService.searchUsers(pageNum, pageSize,loginUser);
        return ResultUtils.success(userByTags);
    }

    @GetMapping("/match")
    public BaseResponse<List<User>> matchUser(long num,HttpServletRequest request){
        if (num<0||num>=20)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.matchUser(num,request));
    }

    @PostMapping("/login/phone")
    public BaseResponse<User> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest ==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User user = userService.userLoginByPhone(userLoginRequest, request);
        if (user == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"登陆失败");
        return ResultUtils.success(user);
    }
    @GetMapping("/sendMsg")
    public BaseResponse<Boolean> sendMsg(String phoneNumber){
        if(StringUtils.isEmpty(phoneNumber)){
            //生成随机的4位验证码
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean sendMsg = userService.sendMsg(phoneNumber);
        if (!sendMsg)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);

        return ResultUtils.success(sendMsg);
    }
}
