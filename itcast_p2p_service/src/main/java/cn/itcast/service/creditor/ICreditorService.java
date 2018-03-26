package cn.itcast.service.creditor;

import java.util.List;
import java.util.Map;

import cn.itcast.domain.creditor.CreditorModel;

public interface ICreditorService {

	void addMultiple(List<CreditorModel> cms);

	List<CreditorModel> findCreditorList(Map<String, Object> map);

	Object[] findCreditorListSum(Map<String, Object> map);

	void checkCreditor(String ids);

}
