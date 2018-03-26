package cn.itcast.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.dao.user.IUserDAO;
import cn.itcast.dao.userAccount.IUserAccountDAO;
import cn.itcast.domain.user.UserModel;
import cn.itcast.domain.userAccount.UserAccountModel;
import cn.itcast.service.user.IUserService;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserDAO userDao;

	@Autowired
	private IUserAccountDAO userAccountDao;

	@Override
	public UserModel findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public UserModel findByPhone(String phone) {
		return userDao.findByPhone(phone);
	}

	@Override
	public boolean addUser(UserModel user) {
		// 添加用户
		UserModel returnvalue1 = userDao.save(user);
		if (returnvalue1 == null) {
			return false;
		}
		// 只要一save，user对象就有了oid
		// 添加帐户
		UserAccountModel uam = new UserAccountModel();
		uam.setUserId(user.getId());
		UserAccountModel returvalue2 = userAccountDao.save(uam);
		if (returvalue2 == null) {
			return false;
		}
		return true;

	}

	@Override
	public UserModel login(String username, String password) {
		
		return userDao.findByUsernameAndPassword(username,password);
	}

	//根据id查询
	@Override
	public UserModel findById(int userid) {
		return userDao.findOne(userid);
	}

	//修改手机号及状态
	@Override
	public void updatePhoneStatus(String phone, int userid) {
		userDao.updatePhoneStatus(phone,userid);
	}

	@Override
	public void addEmail(String email, int userid) {
		userDao.addEmail(email,userid);
	}

	@Override
	public void updateEmailStatus(int parseInt) {
		userDao.addEmailStatus(parseInt);
	}

}
