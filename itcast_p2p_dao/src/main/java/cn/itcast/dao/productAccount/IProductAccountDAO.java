package cn.itcast.dao.productAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.itcast.domain.product.ProductAccount;

public interface IProductAccountDAO extends JpaRepository<ProductAccount, Integer>,JpaSpecificationExecutor<ProductAccount> {

}
