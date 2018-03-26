package cn.itcast.utils;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.domain.Permission;
import cn.itcast.domain.Role;
import cn.itcast.domain.User;
import cn.itcast.service.IUserService;

public class MyRealm extends AuthorizingRealm {

	@Autowired
	private IUserService userService;

	// 授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
		SimpleAuthorizationInfo aif = new SimpleAuthorizationInfo();

		User user = (User) pc.getPrimaryPrincipal(); // cn.itcast.domain.User@14f1eeb
														// 这个就是当前认证的用户
		if (user != null) {
			// 1.根据用户查询角色
			List<Role> roles = userService.findRoleByUser(user);

			// 2.根据角色查询权限
			for (Role r : roles) {
				List<Permission> ps = userService.findPermissionByRole(r);
				// 添加所有角色名称
				aif.addRole(r.getRoleName());
				System.out.print(user.getUsername() + "具有的角色:" + r.getRoleName() + "\t");

				for (Permission p : ps) {
					// 添加所有权限名称
					aif.addStringPermission(p.getPermissionName());
					System.out.print(r.getRoleName() + "角色具有的权限:" + p.getPermissionName() + "\t");
				}
				System.out.println();
			}

			return aif;
		}

		return null;
	}

	// 认证操作
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		String password = new String((char[]) token.getCredentials());
		// 调用service完成认证操作
		User user = userService.login(username, password);
		if (user != null) {
			return new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
		}
		return null;
	}
}
