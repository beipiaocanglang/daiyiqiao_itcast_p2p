package cn.itcast.service.accountLog.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.dao.accountLog.IAccountLogDAO;
import cn.itcast.domain.productAcount.WaitMatchMoneyModel;
import cn.itcast.service.accountLog.IAccountLogService;

@Service
public class AccountLogServiceImpl implements IAccountLogService {

	@Autowired
	private IAccountLogDAO accountLogDao;

	// 待匹配资金队列信息
	@Override
	public List<WaitMatchMoneyModel> findWaitMoneyList() {
		
		//
		
		
		List<WaitMatchMoneyModel> wmms = null;
		// 1.查询待匹配资金
		List<Object[]> list = accountLogDao.findWaitMoneyList();
		// 2.将List<Object[]>转换成List<WaitMatchMoneyModel>
		if (list != null && list.size() > 0) {
			wmms = new ArrayList<WaitMatchMoneyModel>();

			// 3.封装数据
			for (Object[] obj : list) {
				WaitMatchMoneyModel wmm = new WaitMatchMoneyModel();

				// obj中封装的数据就是我们在执行sql时select后面查询的内容，我们需要将obj中每一个元素封装到wmm对象对应的属性中。
				wmm.setFundWeight((Integer) obj[0]);
				wmm.setUserName((String) obj[1]);
				wmm.setpSerialNo((String) obj[2]);
				wmm.setProductName((String) obj[3]);
				wmm.setDate((Date) obj[4]);
				wmm.setDeadline((Integer) obj[5]);
				wmm.setCurrentMonth((int) obj[6]);
				wmm.setAmountWait((Double) obj[7]);
				wmm.setInvestType((Integer) obj[8]);

				if (wmm.getInvestType() == 124) {
					wmm.setInvestTypeDescrible("新增投资");
				}
				if (wmm.getInvestType() == 125) {
					wmm.setInvestTypeDescrible("回款再投资");
				}
				if (wmm.getInvestType() == 126) {
					wmm.setInvestTypeDescrible("到期结清");
				}
				if (wmm.getInvestType() == 127) {
					wmm.setInvestTypeDescrible("提前结清");
				}

				// 处理时间显示
				wmm.setDateDescrible(new SimpleDateFormat("yyyy-MM-dd").format(wmm.getDate()));

				wmms.add(wmm);

			}

		}
		return wmms;
	}

	@Override
	public WaitMatchMoneyModel findWaitMoneySum() {
		List<Object[]> objs = accountLogDao.findWaitMoneySum();
		if (objs == null || objs.size() == 0) {
			return null;
		}
		// 将object[]转换成WaitMatchMoneyModel
		WaitMatchMoneyModel wmm = new WaitMatchMoneyModel();
		wmm.setCount(Integer.parseInt(objs.get(0)[0].toString()));
		wmm.setSum(Double.parseDouble(objs.get(0)[1].toString()));
		return wmm;
	}
}
