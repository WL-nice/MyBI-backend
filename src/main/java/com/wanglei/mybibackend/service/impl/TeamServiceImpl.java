package com.wanglei.mybibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanglei.mybibackend.commmon.ErrorCode;
import com.wanglei.mybibackend.exception.BusinessException;
import com.wanglei.mybibackend.model.domain.Team;
import com.wanglei.mybibackend.mapper.TeamMapper;
import com.wanglei.mybibackend.model.domain.User;
import com.wanglei.mybibackend.model.domain.UserTeam;
import com.wanglei.mybibackend.model.dto.TeamQuery;
import com.wanglei.mybibackend.model.enums.TeamStatus;
import com.wanglei.mybibackend.model.request.TeamJoinRequest;
import com.wanglei.mybibackend.model.request.TeamQuitRequest;
import com.wanglei.mybibackend.model.request.TeamUpdateRequest;
import com.wanglei.mybibackend.model.vo.TeamUserVo;
import com.wanglei.mybibackend.model.vo.UserVo;
import com.wanglei.mybibackend.service.TeamService;
import com.wanglei.mybibackend.service.UserService;
import com.wanglei.mybibackend.service.UserTeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @description 针对表【team(队伍)】的数据库操作Service实现
 *
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        if (team.getMaxNum() < 1 || team.getMaxNum() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }

        String name = team.getTeamName();
        if (!StringUtils.isNotBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不满足要求");
        }

        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }

        int status = Optional.ofNullable(team.getTeamStatus()).orElse(0);
        TeamStatus teamStatus = TeamStatus.getTeamStatusByValue(status);
        if (teamStatus == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态不满足要求");
        }
        String password = team.getPassword();
        if (TeamStatus.SECRET.equals(teamStatus) && (StringUtils.isNotBlank(password) || password.length() >= 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
        }
        Date expireTime = team.getExpireTime();
        if(expireTime == null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, 1);
            expireTime = calendar.getTime();
        }
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间>当前时间");
        }

        Long userId = loginUser.getId();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamCount = this.count(queryWrapper);
        if (hasTeamCount >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建5个队伍");
        }
        team.setId(null);
        team.setUserId(userId);
        team.setExpireTime(expireTime);
        boolean result = this.save(team);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());


        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }

        return team.getId();
    }

    @Override
    public List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (!CollectionUtils.isEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String teamName = teamQuery.getTeamName();
            if (StringUtils.isNotBlank(teamName)) {
                queryWrapper.like("teamName", teamName);//模糊匹配
            }

            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("description", searchText).or().like("teamName", searchText));
            }

            Integer teamStatus = teamQuery.getTeamStatus();
            TeamStatus teamStatusByValue = TeamStatus.getTeamStatusByValue(teamStatus);
            if (teamStatusByValue == null) {
                teamStatusByValue = TeamStatus.PUBLIC;//默认为公开
            }
            //只有管理员可查询加密队伍
            if (!isAdmin && teamStatusByValue.equals(TeamStatus.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("teamStatus", teamStatusByValue.getValue());

            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }

            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }

            //创建人查询
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
        }
        //不展示过期队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVo> teamUserVoList = new ArrayList<>();
        //关联查询创建人用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            //脱敏
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            if (user != null) {
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user, userVo);
                teamUserVo.setCreateUser(userVo);
            }
            teamUserVoList.add(teamUserVo);
        }
        return teamUserVoList;


    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Date expireTime = teamUpdateRequest.getExpireTime();
        if(expireTime == null){
            expireTime = oldTeam.getExpireTime();
        }
        //只有创建者和管理员可修改
        if (!Objects.equals(oldTeam.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatus teamStatusByValue = TeamStatus.getTeamStatusByValue(teamUpdateRequest.getTeamStatus());
        if (teamStatusByValue.equals(TeamStatus.SECRET) && (!StringUtils.isNotBlank(teamUpdateRequest.getPassword()))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
        }

        Team updateTeam = new Team();

        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getTeamStatus();
        TeamStatus statusByValue = TeamStatus.getTeamStatusByValue(status);
        if (TeamStatus.PRIVATE.equals(statusByValue)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatus.SECRET.equals(statusByValue) && (StringUtils.isBlank(password) || !password.equals(team.getPassword()))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        //加入分布式锁防止用户重复加入队伍
        Long userId = loginUser.getId();
        RLock lock = redissonClient.getLock("bromatch:join_team:" + userId + "_" + teamId + ":lock");
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //该用户已加入的队伍数量

                    QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId", userId);
                    long hasJoinNum = userTeamService.count(queryWrapper);
                    if (hasJoinNum > 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "只能创建和加入5个队伍");
                    }

                    //已加入队伍的人数
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("teamId", teamId);
                    long teamHasJoinNum = userTeamService.count(queryWrapper);
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                    }
                    //不能重复加入
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("teamId", teamId);
                    queryWrapper.eq("userId", userId);
                    long hasUserJoinTeam = userTeamService.count(queryWrapper);
                    if (hasUserJoinTeam > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "已加入该队伍");
                    }
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());


                    return userTeamService.save(userTeam);
                }
            }

        } catch (InterruptedException e) {
            log.error("join error", e);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class) //有多表删除的时候加上事务，防止脏数据
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper1);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        //查询队伍当前人数
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        long teamHasJoinNum = userTeamService.count(queryWrapper);
        //队伍只剩一人，解散
        if (teamHasJoinNum == 1) {
            this.removeById(teamId);
            return userTeamService.remove(queryWrapper);
        } else {
            //队伍还剩至少两人
            //是否为队长
            if (Objects.equals(team.getId(), userId)) {
                //顺位继承队长
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId", teamId);
                queryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextTeamUser = userTeamList.get(1);
                Long nextLeaderId = nextTeamUser.getUserId();
                Team updateTeam = new Team();
                BeanUtils.copyProperties(team, updateTeam);
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长失败");
                }

            }
            //清除用户队伍关系
            return userTeamService.remove(queryWrapper1);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long teamId, User loginUser) {
        //校验队伍是否存在
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        //校验身份
        long userId = loginUser.getId();
        if (userId != team.getUserId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你不是队长");
        }
        //删除用户队伍关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        boolean result = userTeamService.remove(queryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        //删除队伍信息
        return this.removeById(teamId);


    }
}




