package cn.itcast.dao.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.domain.admin.Admin;

public interface IAdminDAO extends JpaRepository<Admin, Integer>{

	@Query("select admin from Admin admin where admin.username=?1 and admin.password=?2")
	Admin login(String username, String password);
	
	
	Admin findByUsernameAndPassword(String username,String password);

}
