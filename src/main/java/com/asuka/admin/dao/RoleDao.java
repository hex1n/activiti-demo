package com.asuka.admin.dao;

import com.asuka.admin.entity.Menu;
import com.asuka.admin.entity.Role;
import com.asuka.admin.entity.User;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
public interface RoleDao extends BaseMapper<Role> {

    Role selectRoleById(@Param("id") Long id);

    void saveRoleMenus(@Param("roleId") Long id, @Param("menus") Set<Menu> menus);

    void dropRoleMenus(@Param("roleId") Long roleId);

    void dropRoleUsers(@Param("roleId") Long roleId);

    List<User> getUserByRoleName(String roleName);

    List<String> getRoleByUserId(Long id);
}