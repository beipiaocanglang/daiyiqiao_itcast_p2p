package cn.itcast.service.productAccount.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.dao.accountLog.IAccountLogDAO;
import cn.itcast.dao.fundingNotMatched.IFundingNotMatchedDAO;
import cn.itcast.dao.productAccount.IProductAccountDAO;
import cn.itcast.dao.userAccount.IUserAccountDAO;
import cn.itcast.domain.accountLog.AccountLog;
import cn.itcast.domain.product.ProductAccount;
import cn.itcast.domain.productAcount.FundingNotMatchedModel;
import cn.itcast.domain.userAccount.UserAccountModel;
import cn.itcast.service.productAccount.IProductAccountService;

@Service
public class ProductAccountServiceImpl implements IProductAccountService {
	
	

	@Autowired
	private IProductAccountDAO productAccountDao;

	@Autowired
	private IAccountLogDAO accountLogDao;

	@Autowired
	private IFundingNotMatchedDAO fundingNotMatchedModelDao;

	@Autowired
	private IUserAccountDAO userAccountDao;

	@Transactional
	@Override
	public void addProductAccount(UserAccountModel uam, ProductAccount pa, AccountLog accountLog,
			FundingNotMatchedModel fnmm) {
		// 修改UserAccountModel表中数据
		userAccountDao.updateUserAccountById(uam.getBalance(), uam.getInverstmentW(), uam.getInterestTotal(),
				uam.getRecyclingInterest(), uam.getInverstmentA(), uam.getId());
		// 保存ProductAccount
		productAccountDao.save(pa);
		accountLog.setpId(pa.getpId());
		accountLogDao.save(accountLog);
		fnmm.setfInvestRecordId(pa.getpId());
		fundingNotMatchedModelDao.save(fnmm);
	}

	// 分页
	// @Override
	// public Page<ProductAccount> findProductAccountByPage(int page, int
	// currenCount) {
	// // page代表的是页码 currentCount代表的是每页显示条数
	// Pageable pa = new PageRequest(page - 1, currenCount);
	//
	// Page<ProductAccount> p = productAccountDao.findAll(pa);
	//
	// // long totalElements = p.getTotalElements();//总条数
	// // List content = p.getContent();//当前页要显示的数据
	//
	// return p;
	// }

	// 多条件分页操作
	@Override
	public Page<ProductAccount> findProductAccountByPage(int page, int currenCount, final int uid, final int status,
			final String startDate, final String endDate) {
		// page代表的是页码 currentCount代表的是每页显示条数
		Pageable pa = new PageRequest(page - 1, currenCount);

		Page<ProductAccount> p = productAccountDao.findAll(new Specification<ProductAccount>() {

			@Override
			public Predicate toPredicate(Root<ProductAccount> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 1.设定比较的条件
				Path<Date> pBeginDate = root.get("pBeginDate");
				Path<Long> userId = root.get("pUid");
				Path<String> pStatus = root.get("pStatus");

				// 2.构造比较条件
				List<Predicate> list = new ArrayList<Predicate>();
				Predicate p1 = cb.equal(userId, uid); // pUid=uid
				Predicate p2 = cb.equal(pStatus, status); // pStatus=status
				list.add(p1);
				list.add(p2);
				if (StringUtils.isNotBlank(startDate)) {
					try {
						Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
						Predicate p3 = cb.greaterThanOrEqualTo(pBeginDate, start); // pBeginDate>=start
						list.add(p3);
					} catch (ParseException e) {
						e.printStackTrace();
					}

				}
				if (StringUtils.isNotBlank(endDate)) {
					try {
						Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
						Predicate p4 = cb.lessThanOrEqualTo(pBeginDate, end); // pBeginDate<=end
						list.add(p4);
					} catch (ParseException e) {
						e.printStackTrace();
					}

				}

				query.where(list.toArray(new Predicate[list.size()]));

				return null;
			}
		}, pa);

		return p;
	}

}
