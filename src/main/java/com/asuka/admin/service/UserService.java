package com.asuka.admin.service;

import com.baomidou.mybatisplus.service.IService;
import com.asuka.admin.entity.Role;
import com.asuka.admin.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2017-10-31
 */
public interface UserService extends IService<User> {

	User findUserByLoginName(String name);

	User findUserById(Long id);

	User saveUser(User user);

	User updateUser(User user);

	void saveUserRoles(Long id,Set<Role> roleSet);

	void dropUserRolesByUserId(Long id);

	int userCount(String param);

	void deleteUser(User user);

	Map selectUserMenuCount();

    List<String> findUserRoleNameById(Long id);

    List<User> findUserByNickName(String taskUser);
}
