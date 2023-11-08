package com.duu.matchPartner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duu.matchPartner.model.domain.Team;
import com.duu.matchPartner.model.dto.TeamQuery;
import com.duu.matchPartner.model.request.*;
import com.duu.matchPartner.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author 47228
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-11-02 20:33:08
*/
public interface TeamService extends IService<Team> {

    Long addteam(Team team, HttpServletRequest request);

    List<TeamUserVO> getTeamList(TeamQuery teamQuery, HttpServletRequest request);

    boolean updateTeam(Team team, HttpServletRequest request);

    boolean joinTeam(TeamJoinRequest teamJoinRequest,HttpServletRequest request);

    Boolean quitTeam(TeamQuitRequest teamQuitRequest,HttpServletRequest request);

    boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, HttpServletRequest request);
}
