package cn.itcast.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.dao.admin.IAdminDAO;
import cn.itcast.domain.admin.Admin;
import cn.itcast.service.admin.IAdminService;

@Transactional
@Service
public class AdminServiceImpl implements IAdminService {

	@Autowired
	private IAdminDAO adminDao;
	
	@Override
	public Admin login(String username, String password) {
		return adminDao.login(username,password);
	}

}
