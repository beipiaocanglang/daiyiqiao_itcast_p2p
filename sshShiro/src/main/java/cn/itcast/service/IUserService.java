package cn.itcast.service;

import java.util.List;

import cn.itcast.domain.Permission;
import cn.itcast.domain.Role;
import cn.itcast.domain.User;

public interface IUserService {

	User login(String username, String password);

	List<Role> findRoleByUser(User user);

	List<Permission> findPermissionByRole(Role r);

}
