package com.duu.matchPartner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.duu.matchPartner.common.ErrorCode;
import com.duu.matchPartner.exception.BusinessException;
import com.duu.matchPartner.mapper.TeamMapper;
import com.duu.matchPartner.model.domain.Team;
import com.duu.matchPartner.model.domain.User;
import com.duu.matchPartner.model.domain.UserTeam;
import com.duu.matchPartner.model.dto.TeamQuery;
import com.duu.matchPartner.model.enums.TeamStatusEnum;
import com.duu.matchPartner.model.request.*;
import com.duu.matchPartner.model.vo.TeamUserVO;
import com.duu.matchPartner.model.vo.UserVO;
import com.duu.matchPartner.service.TeamService;
import com.duu.matchPartner.service.UserService;
import com.duu.matchPartner.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

/**
 * @author 47228
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-11-02 20:33:08
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addteam(Team team, HttpServletRequest request) {

//        1. 请求参数是否为空？ 外层已判定
//        2. 是否登录，未登录不允许创建
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null)
            throw new BusinessException(ErrorCode.NO_AUTH, "用户未登录不能创建");
//        3. 校验信息
//        a. 队伍人数 > 1 且 <= 20
        Integer num = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (num < 1 || num > 20)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建人数不符合");
//        b. 队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
//        c. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isBlank(description) || description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不满足要求");
        }
//        d. status 是否公开（int）不传默认为 0（公开）
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusEnum(status);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
//        e. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
//        f. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
//        g. 校验用户最多创建 5 个队伍
        Long userId = loginUser.getId();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建 5 个队伍");
        }
//        4. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        Long teamId = team.getId();
        if (!save || teamId == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
//        5. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        save = userTeamService.save(userTeam);
        Long id = userTeam.getId();
        if (!save || id < 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        return teamId;
    }

    @Override
    public List<TeamUserVO> getTeamList(TeamQuery teamQuery, Boolean admin) {
        if (teamQuery == null)
            return null;
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        1. 从请求参数中取出队伍名称等查询条件，如果存在则作为查询条件
        String name = teamQuery.getName();
        if (StringUtils.isNotBlank(name))
            queryWrapper.like("name", name);
        Long id = teamQuery.getId();
        if (id != null && id > 0) {
            queryWrapper.eq("id", id);
        }
        List<Long> idList = teamQuery.getIdList();
        if (CollectionUtils.isNotEmpty(idList)) {
            queryWrapper.in("id", idList);
        }
        String searchText = teamQuery.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
        }
        String description = teamQuery.getDescription();
        if (StringUtils.isNotBlank(description)) {
            queryWrapper.like("description", description);
        }
        Integer maxNum = teamQuery.getMaxNum();
        if (maxNum != null && maxNum > 0) {
            queryWrapper.eq("maxNum", maxNum);
        }
        Long userId = teamQuery.getUserId();
        if (userId != null && userId > 0) {
            queryWrapper.eq("userId", userId);
        }
        Integer status = teamQuery.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusEnum(status);
        if (teamStatusEnum == null) {
            teamStatusEnum = TeamStatusEnum.PUBLIC;
        }
        if (!admin && TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        queryWrapper.eq("status", teamStatusEnum.getValue());
        //        2. 不展示已过期的队伍（根据过期时间筛选）
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList))
            return new ArrayList<>();
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        for (Team team : teamList) {
            userId = team.getUserId();
            if (userId == null)
                continue;
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            userTeamQueryWrapper.eq("teamId",team.getId());
            int count = (int)userTeamService.count(userTeamQueryWrapper);
            //设置已加入数
            teamUserVO.setHasJoinNum(count);
            userTeamQueryWrapper.clear();
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;

    }

    @Override
    public boolean updateTeam(Team team, HttpServletRequest request) {
//        1. 判断请求参数是否为空
        if (team == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        2. 查询队伍是否存在
        Long id = team.getId();
        Team teamById = this.getById(id);
        if (teamById == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        //  3. 只有管理员或者队伍的创建者可以修改
        boolean admin = userService.isAdmin(request);
        User loginUser = userService.getLoginUser(request);
        if (!admin && !teamById.getUserId().equals(loginUser.getId()))
            throw new BusinessException(ErrorCode.NO_AUTH);
//        4. 如果用户传入的新值和老值一致，就不用 update 了（可自行实现，降低数据库使用次数）
        if (team.equals(teamById)) {
            return true;
        }
//        5. 如果队伍状态改为加密，必须要有密码
        String password = team.getPassword();
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusEnum(status);
        if (teamStatusEnum.equals(TeamStatusEnum.PRIVATE) && StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密状态请设置密码");
        }
//        6. 更新成功
        return this.updateById(team);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
//        1. 用户最多加入 5 个队伍
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUserId);
        if (userTeamService.count(queryWrapper) > 5)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多只能加入 5 个队伍");
//        2. 队伍必须存在，只能加入未满、未过期的队伍
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getById(teamId);
        if (team == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
//        3. 不能加入自己的队伍，不能重复加入已加入的队伍（幂等性）
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        List<Long> joinTeamList = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toList());
        if (joinTeamList.contains(teamId))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入已加入的队伍");
//        4. 禁止加入私有的队伍
        Integer teamStatus = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusEnum(teamStatus);
        if (teamStatusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有的队伍");
        }
//        5. 如果加入的队伍是加密的，必须密码匹配才可以
        String password = teamJoinRequest.getPassword();
        if (teamStatusEnum.equals(TeamStatusEnum.SECRET)) {
            if (!team.getPassword().equals(password))
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码错误");
        }
//        6. 新增队伍 - 用户关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUserId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());

        return userTeamService.save(userTeam);
    }

    /**
     * @param request
     * @description:
     * @author: duu
     * @date: 2023/11/7 21:31
     * @param: teamQuitRequest
     * @return: Boolean
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean quitTeam(TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
//        1. 校验请求参数
        if (teamQuitRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        2. 校验队伍是否存在
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getById(teamId);
        if (team == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        3. 校验我是否已加入队伍
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUserId);
        userTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<UserTeam>(userTeam);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count == 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        4. 如果队伍
//          a. 只剩一人，队伍解散
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        long count1 = userTeamService.count(queryWrapper);
        if (count1 == 1) {
            boolean remove = this.removeById(team);
            if (!remove)
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出失败");
        } else {
            //         b. 还有其他人
//              ⅰ. 如果是队长退出队伍，权限转移给第二早加入的用户 —— 先来后到只用取 id 最小的 2 条数据
            if (loginUserId.equals(team.getUserId())) {
                // userTeamService.remove(queryWrapper);
                queryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1)
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                UserTeam nextUserTeam = userTeamList.get(1);
                Team newTeam = new Team();
                Long userId = nextUserTeam.getUserId();
                newTeam.setUserId(userId);
                newTeam.setId(teamId);
                boolean update = this.updateById(newTeam);
                if (!update)
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更换队长失败");
            }
        }
        return userTeamService.remove(userTeamQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request) {
        //        1. 校验请求参数
        if (teamDeleteRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        //        2. 校验队伍是否存在
        Long id = teamDeleteRequest.getId();
        Team team = this.getById(id);
        if (team == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        3. 校验你是不是队伍的队长
        if (!Objects.equals(loginUserId, team.getUserId()))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不是队长不能解散队伍");
//        4. 移除所有加入队伍的关联信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("teamId", id);
        boolean remove = userTeamService.remove(queryWrapper);
        if (!remove)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户关联表失败");
//        5. 删除队伍
        return this.removeById(id);
    }

    @Override
    public List<User> catTeam(Long teamId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        List<Long> userIdList = userTeamService.list(queryWrapper).stream().map(UserTeam::getUserId).collect(Collectors.toList());
        return userService.listByIds(userIdList);
    }
}




