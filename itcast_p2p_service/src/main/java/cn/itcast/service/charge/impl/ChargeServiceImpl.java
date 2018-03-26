package cn.itcast.service.charge.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.dao.userAccount.IUserAccountDAO;
import cn.itcast.service.charge.IChargeService;
import cn.itcast.utils.HttpClientUtil;

@Service
@Transactional
public class ChargeServiceImpl implements IChargeService {

	@Autowired
	private IUserAccountDAO userAccountDao;

	// 充值操作
	// 为什么传递userid,充值成功后要修改当前p2p项目中用户帐户中的金额，需要用户id
	@Override
	public boolean charge(double money, String bankCardNum, int userid) {

		// 1.调用httpclient向银行发送请求,进行充值操作
		// 参数map就是发送post请求时的参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("money", money);
		map.put("bankCardNum", bankCardNum);
		// 在模拟的银行端得到请求参数时 money=xx&bankCardNum=xxx

		// 参数server_url 它是请求的资源路径,只不过它是在properties文件中定义的一个key
		String returnValue = HttpClientUtil.visitWebService(map, "bank_url");

		// 2.根据响应回来的数据，判断银行帐户中金额是否ok,修改p2p平台上用户帐户信息
		// returnValue就是银行服务器端响应回来的数据
		boolean flag = Boolean.parseBoolean(returnValue);
		if (flag) {
			// 充值成功
			userAccountDao.updateAccount(money, userid);
			return true;
		} else {
			// 充值失败
			return false;
		}

	}

}
