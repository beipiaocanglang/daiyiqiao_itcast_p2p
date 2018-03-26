package cn.itcast.service.user;

import cn.itcast.domain.user.UserModel;

public interface IUserService {

	UserModel findByUsername(String username);

	UserModel findByPhone(String phone);

	boolean addUser(UserModel user);

	UserModel login(String username, String password);

	UserModel findById(int userid);

	void updatePhoneStatus(String phone, int userid);

	void addEmail(String email, int userid);

	void updateEmailStatus(int parseInt);

}
