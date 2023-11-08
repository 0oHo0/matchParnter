package com.duu.matchPartner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.duu.matchPartner.mapper.UserTeamMapper;
import com.duu.matchPartner.model.domain.UserTeam;
import com.duu.matchPartner.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author 47228
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-11-02 20:33:08
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




