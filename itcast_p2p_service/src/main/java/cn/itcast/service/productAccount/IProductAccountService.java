package cn.itcast.service.productAccount;

import org.springframework.data.domain.Page;

import cn.itcast.domain.accountLog.AccountLog;
import cn.itcast.domain.product.ProductAccount;
import cn.itcast.domain.productAcount.FundingNotMatchedModel;
import cn.itcast.domain.userAccount.UserAccountModel;

public interface IProductAccountService {

	void addProductAccount(UserAccountModel uam, ProductAccount pa, AccountLog accountLog, FundingNotMatchedModel fnmm);

	
	//分页操作
	Page<ProductAccount> findProductAccountByPage(int page,int currenCount,int uid,int status,String startDate,String endDate);

}
