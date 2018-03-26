package cn.itcast.service.admin;

import cn.itcast.domain.admin.Admin;

public interface IAdminService {

	public Admin login(String username,String password);
}
