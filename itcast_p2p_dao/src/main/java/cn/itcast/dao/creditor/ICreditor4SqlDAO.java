package cn.itcast.dao.creditor;

import java.util.List;
import java.util.Map;

import cn.itcast.domain.creditor.CreditorModel;

public interface ICreditor4SqlDAO {

	List<CreditorModel> findCreditorList(Map<String, Object> map);

	Object[] findCreditorListSum(Map<String, Object> map);

}
