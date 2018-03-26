package cn.itcast.dao.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;

public interface IProductDAO extends JpaRepository<Product, Long>{

	
}
