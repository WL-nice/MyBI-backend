package com.wanglei.mybibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanglei.mybibackend.model.domain.Team;
import com.wanglei.mybibackend.model.domain.User;
import com.wanglei.mybibackend.model.dto.TeamQuery;
import com.wanglei.mybibackend.model.request.TeamJoinRequest;
import com.wanglei.mybibackend.model.request.TeamQuitRequest;
import com.wanglei.mybibackend.model.request.TeamUpdateRequest;
import com.wanglei.mybibackend.model.vo.TeamUserVo;

import java.util.List;

/**
 * @author admin
 * @description 针对表【team(队伍)】的数据库操作Service
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);


    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @return
     */
    List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 修改队伍信息
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     *
     * @param teamId
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long teamId, User loginUser);
}
