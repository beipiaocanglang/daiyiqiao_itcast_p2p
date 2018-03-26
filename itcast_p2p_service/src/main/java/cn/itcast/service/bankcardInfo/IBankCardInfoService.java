package cn.itcast.service.bankcardInfo;

import cn.itcast.domain.bankCardInfo.BankCardInfo;

public interface IBankCardInfoService {

	BankCardInfo findByUserId(int userid);

	void save(BankCardInfo bci);

}
