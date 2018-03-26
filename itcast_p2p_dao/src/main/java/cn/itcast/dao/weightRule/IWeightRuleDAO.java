package cn.itcast.dao.weightRule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.domain.matchManagement.WeigthRule;

public interface IWeightRuleDAO extends JpaRepository<WeigthRule, Integer> {

	@Query("select wr from WeigthRule wr where wr.weigthType=?1")
	WeigthRule findByWeigthType(int i);

}
