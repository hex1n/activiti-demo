package com.asuka.admin.util;


import com.asuka.admin.realm.AuthRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

/**
 * 管理工具类
 */
public class CommonUtil {

    /**
     * 获取当前用户
     */
    public static AuthRealm.ShiroUser getUser() {
        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        return (AuthRealm.ShiroUser) session.getAttribute("currentUser");
    }
}

/**
 * 获取权限
 *
 * @return
 */
  /*public static List<SysPermission> getPermission(){
    SysUser user=CommonUtil.getUser();
    if(user!=null){

    }
  }

}
*/