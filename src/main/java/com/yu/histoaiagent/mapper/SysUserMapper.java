package com.yu.histoaiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.histoaiagent.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 根据用户ID查询用户
     */
    @Select("SELECT * FROM sys_user WHERE user_id = #{userId} AND is_del = 0")
    SysUser findByUserId(String userId);
    
    /**
     * 根据角色查询用户列表
     */
    @Select("SELECT * FROM sys_user WHERE role = #{role} AND is_del = 0")
    List<SysUser> findByRole(String role);
    
    /**
     * 查询即将过期的VIP用户（N天内）
     */
    @Select("SELECT * FROM sys_user " +
            "WHERE role = 'VIP' " +
            "AND enabled = 1 " +
            "AND vip_expire_time > NOW() " +
            "AND vip_expire_time <= DATE_ADD(NOW(), INTERVAL #{days} DAY) " +
            "AND is_del = 0")
    List<SysUser> findExpiringVipUsers(int days);
    
    /**
     * 查询已过期的VIP用户
     */
    @Select("SELECT * FROM sys_user " +
            "WHERE role = 'VIP' " +
            "AND enabled = 1 " +
            "AND vip_expire_time < NOW() " +
            "AND is_del = 0")
    List<SysUser> findExpiredVipUsers();
    
    /**
     * 更新用户启用状态
     */
    @Update("UPDATE sys_user SET enabled = #{enabled} WHERE user_id = #{userId}")
    int updateEnabledStatus(String userId, boolean enabled);
    
    /**
     * 更新用户角色
     */
    @Update("UPDATE sys_user SET role = #{role} WHERE user_id = #{userId}")
    int updateRole(String userId, String role);
    
    /**
     * 更新VIP过期时间
     */
    @Update("UPDATE sys_user SET vip_expire_time = #{expireTime} WHERE user_id = #{userId}")
    int updateVipExpireTime(String userId, LocalDateTime expireTime);
}