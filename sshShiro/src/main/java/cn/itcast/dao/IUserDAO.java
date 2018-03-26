package cn.itcast.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.domain.Permission;
import cn.itcast.domain.Role;
import cn.itcast.domain.User;

public interface IUserDAO extends JpaRepository<User, Integer> {

	User findByUsernameAndPassword(String username, String password);

	@Query("select r from Role r inner join r.users ru where ru=?1")
	List<Role> findRoleByUser(User user);

	@Query("select p from Permission p inner join p.roles pr where pr=?1")
	List<Permission> findPermissionByRole(Role r);

}
