package cn.itcast.service.weigthRule.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.dao.weightRule.IWeightRuleDAO;
import cn.itcast.domain.matchManagement.WeigthRule;
import cn.itcast.service.weigthRule.IWeigthRuleService;

@Service
public class WeigthRuleServiceImpl implements IWeigthRuleService {

	@Autowired
	private IWeightRuleDAO weightRuleDao;

	@Override
	public WeigthRule findByWeigthType(int i) {
		return weightRuleDao.findByWeigthType(i);
	}

}
