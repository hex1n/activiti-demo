package com.asuka.admin.base;

import com.asuka.admin.entity.User;
import com.asuka.admin.realm.AuthRealm.ShiroUser;
import com.asuka.admin.service.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    public User getCurrentUser() {
        ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
        if (shiroUser == null) {
            return null;
        }
        User loginUser = userService.selectById(shiroUser.getId());
        return loginUser;
    }

    @Autowired
    protected UserService userService;

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected RoleService roleService;


}
