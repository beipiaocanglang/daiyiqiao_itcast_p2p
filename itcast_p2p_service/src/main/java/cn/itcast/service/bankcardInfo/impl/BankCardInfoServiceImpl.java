package cn.itcast.service.bankcardInfo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.dao.bankcardInfo.IBankCardInfoDAO;
import cn.itcast.domain.bankCardInfo.BankCardInfo;
import cn.itcast.service.bankcardInfo.IBankCardInfoService;

@Service
public class BankCardInfoServiceImpl implements IBankCardInfoService {

	@Autowired
	private IBankCardInfoDAO bankCardInfoDao;

	@Override
	public BankCardInfo findByUserId(int userid) {
		return bankCardInfoDao.findByUserId(userid);
	}

	@Override
	public void save(BankCardInfo bci) {

		bankCardInfoDao.save(bci);
	}
}
