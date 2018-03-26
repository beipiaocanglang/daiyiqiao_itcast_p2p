package cn.itcast.service.creditor.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.dao.creditor.ICreditor4SqlDAO;
import cn.itcast.dao.creditor.ICreditorDAO;
import cn.itcast.domain.creditor.CreditorModel;
import cn.itcast.service.creditor.ICreditorService;
import cn.itcast.util.constant.ClaimsType;

@Service
@Transactional
public class CreditorServiceImpl implements ICreditorService {

	@Autowired
	private ICreditorDAO creditorDao;

	@Autowired
	private ICreditor4SqlDAO creditor4SqlDao;

	@Override
	public void addMultiple(List<CreditorModel> cms) {

		creditorDao.save(cms);

	}

	@Override
	public List<CreditorModel> findCreditorList(Map<String, Object> map) {
		return creditor4SqlDao.findCreditorList(map);
	}

	@Override
	public Object[] findCreditorListSum(Map<String, Object> map) {
		return creditor4SqlDao.findCreditorListSum(map);
	}

	@Override
	public void checkCreditor(String ids) {
		
		//1.根据id查询债权
		//2.修改债权状态
		String[] str = ids.split(",");
		
		for(String st:str){
			CreditorModel cm = creditorDao.findOne(Integer.parseInt(st.trim()));
			
			cm.setDebtStatus(ClaimsType.CHECKED);
		}
		
		
	}
}
