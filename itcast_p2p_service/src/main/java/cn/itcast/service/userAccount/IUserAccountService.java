package cn.itcast.service.userAccount;

import cn.itcast.domain.userAccount.UserAccountModel;

public interface IUserAccountService {

	UserAccountModel findByUserId(int id);

}
