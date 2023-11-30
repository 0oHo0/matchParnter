package com.duu.matchPartner.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duu.matchPartner.common.BaseResponse;
import com.duu.matchPartner.common.ErrorCode;
import com.duu.matchPartner.common.ResultUtils;
import com.duu.matchPartner.exception.BusinessException;
import com.duu.matchPartner.model.domain.Team;
import com.duu.matchPartner.model.domain.User;
import com.duu.matchPartner.model.domain.UserTeam;
import com.duu.matchPartner.model.dto.TeamQuery;
import com.duu.matchPartner.model.request.*;
import com.duu.matchPartner.model.vo.TeamUserVO;
import com.duu.matchPartner.service.TeamService;
import com.duu.matchPartner.service.UserService;
import com.duu.matchPartner.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : du u
 * @data : 2023/11/2
 * @from ：https://github.com/0oHo0
 **/
@RestController
@RequestMapping("/team")
@Slf4j
//@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        Long teamId = teamService.addteam(team, request);
        if (teamId < 0)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建失败");
        return ResultUtils.success(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest,
                                            HttpServletRequest request) {
        if (teamDeleteRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        boolean remove = teamService.deleteTeam(teamDeleteRequest, request);
        if (!remove)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        return ResultUtils.success(remove);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,
                                            HttpServletRequest request) {
        if (teamUpdateRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, team);
        boolean update = teamService.updateTeam(team, request);
        if (!update)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询失败");
        return ResultUtils.success(update);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(Long id) {
        if (id <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Team team = teamService.getById(id);
        if (team == null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询失败");
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> getTeamList(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        boolean admin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.getTeamList(teamQuery, admin);
        if (teamList == null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"查询出错");
        else if (teamList.size()==0)
            return ResultUtils.success(teamList);
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        //判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {
        }
        // 3、TODO 查询已加入队伍的人数
//        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
//        userTeamJoinQueryWrapper.in("teamId", teamIdList);
//        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
//        // 队伍 id => 加入这个队伍的用户列表
//        Map<Long, List<UserTeam>> teamIdUserTeamList =
//                userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
//        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamPage(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> teamPage = teamService.page(new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize()));
        if (teamPage == null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询失败");
        return ResultUtils.success(teamPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        boolean joinTeam = teamService.joinTeam(teamJoinRequest, request);
        return ResultUtils.success(joinTeam);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Boolean quitTeam = teamService.quitTeam(teamQuitRequest, request);
        return ResultUtils.success(quitTeam);
    }

    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.getTeamList(teamQuery, true);
        if (CollectionUtils.isEmpty(teamList))
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        QueryWrapper queryWrapper = new QueryWrapper<>();
        User loginUser = userService.getLoginUser(request);
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        List<Long> idList = userTeamList.stream().map(item -> {
            Long teamId = item.getTeamId();
            return teamId;
        }).collect(Collectors.toList());
        teamQuery.setIdList(idList);
        List<TeamUserVO> teamList = teamService.getTeamList(teamQuery, true);
        if (teamList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(teamList);
    }
    @GetMapping("/cat")
    public BaseResponse<List<User>> catTeam(Long teamId, HttpServletRequest request){
        if (teamId<=0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (loginUser==null)
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        List<User> userList = teamService.catTeam(teamId);
        if (userList==null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(userList);
    }

}
