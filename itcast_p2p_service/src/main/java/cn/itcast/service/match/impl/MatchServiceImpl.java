package cn.itcast.service.match.impl;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.dao.creditor.ICreditorDAO;
import cn.itcast.dao.fundingNotMatched.IFundingNotMatchedDAO;
import cn.itcast.domain.creditor.CreditorModel;
import cn.itcast.domain.productAcount.FundingNotMatchedModel;
import cn.itcast.service.match.IMatchService;

@Service
@Transactional
public class MatchServiceImpl implements IMatchService {

	@Autowired
	private EntityManagerFactory factory;

	@Autowired
	private IFundingNotMatchedDAO fundingNotMatchedDao;
	@Autowired
	private ICreditorDAO creditorDao;

	@Override
	public void startMatch() {
		// 1.查询所有的资金List<FundingNotMatchedModel>
		List<FundingNotMatchedModel> fnms = fundingNotMatchedDao.findNotMatchedList();
		// 2.查询所有的债权 需要审核过并且是没有匹配或部分匹配债权
		List<CreditorModel> cms = creditorDao.findNotMatchedList();

		match(fnms, cms);

	}

	// 真正匹配操作
	private void match(List<FundingNotMatchedModel> fnms, List<CreditorModel> cms) {
		// 3.得到所有的待匹配资金金额
		BigDecimal investMoney = getInvestMoney(fnms);
		// 4.得到所有的待匹配债权金额
		BigDecimal creditorMoney = getCreditorMoney(cms);

		// 5.根据资金与债权的金额大小来进行匹配
		BigDecimal sub = investMoney.subtract(creditorMoney); // 资金-债权
		// 注意：资金>债权 所有债权匹配 资金<债权 所有资金匹配 资金=债权，它两全部匹配
		if (sub.doubleValue() > 0) { // 资金>债权
			// 6.根据对比来处理资金它的匹配问题
			// 6.1将所有有资金与债权装入到两个队列中
			LinkedList<FundingNotMatchedModel> fnmQueue = new LinkedList<FundingNotMatchedModel>();
			LinkedList<CreditorModel> cmQueue = new LinkedList<CreditorModel>();
			fnmQueue.addAll(fnms);
			cmQueue.addAll(cms);
			// 6.2因为现在资金是大于债权的，所有我们可以明确债权队列会为空.
			while (!cmQueue.isEmpty()) {
				// 6.3进行手动匹配操作--从两个队列中分别获取资金与债权
				FundingNotMatchedModel fnm = fnmQueue.removeFirst(); // 从队列中获取一个资金对象
				CreditorModel cm = cmQueue.removeFirst();// 从队列中获取一个债权对象

				// 6.4对比资金与债权对象的金额大小
				BigDecimal bd = new BigDecimal(fnm.getfNotMatchedMoney())
						.subtract(new BigDecimal(cm.getAvailableMoney()));
				if (bd.doubleValue() > 0) {
					// 资金>债权 -----资金要拆分,重新将资金装入到队列中
					fnm.setfNotMatchedMoney(bd.abs().doubleValue());
					fnmQueue.addFirst(fnm);

				} else if (bd.doubleValue() < 0) {
					// 资金<债权-----债权要拆分，重新将债权装入到队列中
					cm.setAvailableMoney(bd.abs().doubleValue());
					cmQueue.addFirst(cm);
				}

			}
			// 7.处理资金队列中的资金对象
			// 7.1 判断在资金的集合中存在，而资金队列中不存在的资金对象，它们是完全匹配的。
			fnms.removeAll(fnmQueue);
			investMatched(fnms);
			// 7.2判断队列中的剩下的资金对象，它们有可能是未匹配，有可能是部分匹配
			investBodyMatched(fnmQueue);

			creditorMatched(cms); // 所有债权匹配

		} else if (sub.doubleValue() < 0) { // 资金<债权

			// 6.根据对比来处理债权它的匹配问题
			investMatched(fnms);// 所有资金匹配
		} else { // 资金==债权
			investMatched(fnms);
			creditorMatched(cms);
		}

	}

	// 处理资金队列中剩余的资金对象，判断它们是部分匹配还是未匹配，进行处理
	private void investBodyMatched(LinkedList<FundingNotMatchedModel> fnmQueue) {

		for (FundingNotMatchedModel fnm : fnmQueue) {
			// 判断fnm的中的未匹配金额是否与数据库中的对应的记录中的金额一致
			Double notMatchMoney = fnm.getfNotMatchedMoney();
			int fnm_id = fnm.getfId(); // 资金对象的id
			// 重新开启了一次会话，它不会使用已经存在的会话，也就是不会走一级缓存
			EntityManager em = factory.createEntityManager();
			FundingNotMatchedModel _fnm = em.find(FundingNotMatchedModel.class, fnm_id);// findOne也是从一级缓存查询
			BigDecimal cha = new BigDecimal(notMatchMoney).subtract(new BigDecimal(_fnm.getfNotMatchedMoney()));
			if (cha.doubleValue()!=0) {
				// 部分匹配
				fnm.setfMatchedMoney(_fnm.getfNotMatchedMoney() - fnm.getfNotMatchedMoney()); // 已经匹配的金额
			}
			em.close();

		}

	}

	// 获取债权可以金额总和
	private BigDecimal getCreditorMoney(List<CreditorModel> cms) {
		BigDecimal money = new BigDecimal(0);
		for (CreditorModel cm : cms) {
			money = money.add(new BigDecimal(cm.getAvailableMoney()));
		}
		return money;
	}

	// 获取资金可以进行匹配的金额总和
	private BigDecimal getInvestMoney(List<FundingNotMatchedModel> fnms) {
		BigDecimal money = new BigDecimal(0);
		for (FundingNotMatchedModel fnm : fnms) {
			money = money.add(new BigDecimal(fnm.getfNotMatchedMoney()));
		}
		return money;
	}

	// 处理资金匹配
	private void investMatched(List<FundingNotMatchedModel> invests) {
		for(FundingNotMatchedModel fnm:invests){
			fundingNotMatchedDao.match(fnm.getfId(),fnm.getfNotMatchedMoney());
		}
	}
	// 处理债权匹配

	private void creditorMatched(List<CreditorModel> cms) {
		for(CreditorModel cm:cms){
			creditorDao.match(cm.getId(),cm.getAvailableMoney());
		}
	}

}
