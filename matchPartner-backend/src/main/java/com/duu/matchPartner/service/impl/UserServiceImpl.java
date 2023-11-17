package com.duu.matchPartner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duu.matchPartner.common.BaseResponse;
import com.duu.matchPartner.common.ErrorCode;
import com.duu.matchPartner.contant.UserConstant;
import com.duu.matchPartner.exception.BusinessException;
import com.duu.matchPartner.mapper.UserMapper;
import com.duu.matchPartner.model.domain.User;
import com.duu.matchPartner.model.request.UserLoginRequest;
import com.duu.matchPartner.service.UserService;
import com.duu.matchPartner.utils.AlgorithmUtils;
import com.duu.matchPartner.utils.SMSUtils;
import com.duu.matchPartner.utils.ValidateCodeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.models.auth.In;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.duu.matchPartner.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "duu";

    /**
     * 判断管理员
     * @param loginUser
     * @return
     */

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserCode(originUser.getUserCode());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUserByTags(List<String> tags) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tag:tags) {
            queryWrapper = queryWrapper.like("tags", tag);
        }
        List<User> usersList = userMapper.selectList(queryWrapper);
        usersList.stream().map(this::getSafetyUser).collect(Collectors.toList());
        return usersList;
    }

    @Override
    public int updateUser(User user,User loginUser) {
        Long userId = user.getId();
        if (userId < 0)
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        if (!isAdmin(loginUser) && !userId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request== null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        return  (User) userObj;
    }

    @Override
    public Page<User> searchUsers(Long PageNum,Long PageSize,User loginUser) {
        Long id = loginUser.getId();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = String.format("match-duu-recommend:%s",id);
        Page<User> userPage = (Page<User>)valueOperations.get(key);
        if(userPage!=null)
            return  userPage;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("and id != "+id.toString());
        Page<User> Page = new Page<>(PageNum,PageSize);
        userPage = this.page(Page,queryWrapper);
        try {
            valueOperations.set(key,userPage,60, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("redis set error");
        }
        return userPage;
    }

    @Override
    public List<User> matchUser(long num, HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        Gson gson = new Gson();
        List<String> loginUserTagList = gson.fromJson(loginUser.getTags(), new TypeToken<List<String>>() {
        }.getType());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        List<Pair<User,Integer>> list = new ArrayList<>();
        userList.forEach(user -> {
            List<String> tagList = gson.fromJson(user.getTags(), new TypeToken<List<String>>() {
            }.getType());
            int minDistance = AlgorithmUtils.minDistance(loginUserTagList, tagList);
            if(!user.getId().equals(loginUser.getId()))
                list.add(new Pair<>(user,minDistance));
        });
        List<Long> userIdList =
                list.stream().sorted((a, b) -> a.getValue() - b.getValue()).limit(num).collect(Collectors.toList())
                .stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> finalQueryWrapper = new QueryWrapper<>();
        finalQueryWrapper.in("id",userIdList);
        Map<Long, List<User>> listMap = this.list(finalQueryWrapper)
                .stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> resList = new ArrayList<>();
        userIdList.forEach(userId -> resList.add(listMap.get(userId).get(0)));
        return resList;
    }

    @Override
    public User userLoginByPhone(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

            //获取手机号
            String phone = userLoginRequest.getPhoneNumber();

            //获取验证码
            String code = userLoginRequest.getCode();

            String codeKey = String.format("duu-matchPartner-userLoginByPhone:%s", phone);
        //从Session中获取保存的验证码
            // Object codeInSession = session.getAttribute(phone);
            //从缓存中获取验证码
            Object codeInRedis =  redisTemplate.opsForValue().get(codeKey);
            //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
            if(codeInRedis != null && codeInRedis.equals(code)){
                //如果能够比对成功，说明登录成功

                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getPhone,phone);

                User user = this.getOne(queryWrapper);
                if(user == null){
                    //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                    user = new User();
                    user.setPhone(phone);
                    this.save(user);
                }
                // 3. 用户脱敏
                User safetyUser = getSafetyUser(user);
                // 4. 记录用户的登录态
                request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
                try {
                    redisTemplate.delete(codeKey);
                }catch (Exception e){
                    log.error("redis delete error");
                }
                return user;
            }
            return null;
    }

    @Override
    public Boolean sendMsg(String phoneNumber) {

        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("code={}",code);
        //调用腾讯云提供的短信服务API完成发送短信
        SMSUtils.sendSms(phoneNumber,code);

        //需要将生成的验证码保存到Redis
        String codeKey = String.format("duu-matchPartner-userLoginByPhone:%s", phoneNumber);
        try {
            redisTemplate.opsForValue().set(codeKey,code,5, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("redis set error");
            return false;
        }
        return true;
    }
}