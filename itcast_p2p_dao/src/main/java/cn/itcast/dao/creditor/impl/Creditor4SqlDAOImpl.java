package cn.itcast.dao.creditor.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import cn.itcast.dao.creditor.ICreditor4SqlDAO;
import cn.itcast.domain.creditor.CreditorModel;

@Repository
public class Creditor4SqlDAOImpl implements ICreditor4SqlDAO {

	@PersistenceContext
	private EntityManager em;

	// 多条件查询债权信息
	@Override
	public List<CreditorModel> findCreditorList(Map<String, Object> map) {

		// 1.生成一个基础 的sql语句
		String sql = "select a.* from t_debt_info a,(select rownum rn,d_id from t_debt_info) b where 1=1 ";

		// 2.判断条件，拼接sql
		// dDebtNo=&dContractNo=&dDebtTransferredDateStart=&dDebtTransferredDateEnd=&dDebtStatus=&dMatchedStatus=&offsetnum=1
		// 2.1标的编号
		String dDebtNo = (String) map.get("dDebtNo");
		if (StringUtils.isNotBlank(dDebtNo)) {
			sql += " and d_debt_no='" + dDebtNo + "'";
		}
		// 2.2合同编号
		String dContractNo = (String) map.get("dContractNo");
		if (StringUtils.isNotBlank(dContractNo)) {
			sql += " and d_contract_No='" + dContractNo + "'";
		}
		// 2.3债权转入日期

		String dDebtTransferredDateStart = (String) map.get("dDebtTransferredDateStart");
		String dDebtTransferredDateEnd = (String) map.get("dDebtTransferredDateEnd");
		if (StringUtils.isNotBlank(dDebtTransferredDateStart) && StringUtils.isNotBlank(dDebtTransferredDateEnd)) {
			sql += " and d_debt_Transferred_Date between to_date('" + dDebtTransferredDateStart
					+ "','yyyy-mm-dd') and to_date('" + dDebtTransferredDateEnd + "','yyyy-mm-dd')";
		}

		// 2.4债权状态
		Integer dDebtStatus = (Integer) map.get("dDebtStatus");
		if (dDebtStatus != null && dDebtStatus != 0) {
			sql += " and d_debt_Status='" + dDebtStatus + "'";
		}

		// 2.4债权匹配状态
		Integer dMatchedStatus = (Integer) map.get("dMatchedStatus");
		if (dMatchedStatus != null && dMatchedStatus != 0) {
			sql += " and d_matched_Status='" + dMatchedStatus + "'";
		}
		int offsetnum = (int) map.get("offsetnum"); // 当前页码
		int start = (offsetnum - 1) * 3;
		int end = start + 3;
		sql += " and a.d_id=b.d_id and b.rn>'" + start + "' and b.rn<='" + end + "'";
		// System.out.println(sql);
		// 3.执行sql
		Query query = em.createNativeQuery(sql, CreditorModel.class);

		List<CreditorModel> list = query.getResultList();

		return list;
	}

	// 查询债权统计信息
	@Override
	public Object[] findCreditorListSum(Map<String, Object> map) {
		// 1.生成一个基础 的sql语句
		String sql = "select count(d_id),sum(d_debt_Money),sum(d_available_Money) from t_debt_info a where 1=1 ";

		// 2.判断条件，拼接sql
		// dDebtNo=&dContractNo=&dDebtTransferredDateStart=&dDebtTransferredDateEnd=&dDebtStatus=&dMatchedStatus=&offsetnum=1
		// 2.1标的编号
		String dDebtNo = (String) map.get("dDebtNo");
		if (StringUtils.isNotBlank(dDebtNo)) {
			sql += " and d_debt_no='" + dDebtNo + "'";
		}
		// 2.2合同编号
		String dContractNo = (String) map.get("dContractNo");
		if (StringUtils.isNotBlank(dContractNo)) {
			sql += " and d_contract_No='" + dContractNo + "'";
		}
		// 2.3债权转入日期

		String dDebtTransferredDateStart = (String) map.get("dDebtTransferredDateStart");
		String dDebtTransferredDateEnd = (String) map.get("dDebtTransferredDateEnd");
		if (StringUtils.isNotBlank(dDebtTransferredDateStart) && StringUtils.isNotBlank(dDebtTransferredDateEnd)) {
			sql += " and d_debt_Transferred_Date between to_date('" + dDebtTransferredDateStart
					+ "','yyyy-mm-dd') and to_date('" + dDebtTransferredDateEnd + "','yyyy-mm-dd')";
		}

		// 2.4债权状态
		Integer dDebtStatus = (Integer) map.get("dDebtStatus");
		if (dDebtStatus != null && dDebtStatus != 0) {
			sql += " and d_debt_Status='" + dDebtStatus + "'";
		}

		// 2.4债权匹配状态
		Integer dMatchedStatus = (Integer) map.get("dMatchedStatus");
		if (dMatchedStatus != null && dMatchedStatus != 0) {
			sql += " and d_matched_Status='" + dMatchedStatus + "'";
		}

		// 3.执行sql
		Query query = em.createNativeQuery(sql);

		Object[] obj = (Object[]) query.getSingleResult();

		return obj;
	}
}
