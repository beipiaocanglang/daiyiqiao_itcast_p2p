package cn.itcast.dao.expectedReturn;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.itcast.domain.productAcount.ExpectedReturn;

public interface IExpectedReturnDAO extends JpaRepository<ExpectedReturn, Integer> {

}
