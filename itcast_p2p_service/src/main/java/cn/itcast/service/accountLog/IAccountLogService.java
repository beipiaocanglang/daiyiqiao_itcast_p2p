package cn.itcast.service.accountLog;

import java.util.List;

import cn.itcast.domain.productAcount.WaitMatchMoneyModel;

public interface IAccountLogService {

	List<WaitMatchMoneyModel> findWaitMoneyList();

	WaitMatchMoneyModel findWaitMoneySum();

}
