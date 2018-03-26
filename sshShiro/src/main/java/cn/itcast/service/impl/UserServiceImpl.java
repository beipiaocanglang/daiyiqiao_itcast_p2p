package cn.itcast.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.dao.IUserDAO;
import cn.itcast.domain.Permission;
import cn.itcast.domain.Role;
import cn.itcast.domain.User;
import cn.itcast.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserDAO userDao;

	public User login(String username, String password) {
		return userDao.findByUsernameAndPassword(username,password);
	}

	public List<Role> findRoleByUser(User user) {
		return userDao.findRoleByUser(user);
	}

	public List<Permission> findPermissionByRole(Role r) {
		return userDao.findPermissionByRole(r);
	}
}
