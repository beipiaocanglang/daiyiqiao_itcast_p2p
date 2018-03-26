package cn.itcast.service.product;

import java.util.List;

import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;

public interface IProductService {

	public List<Product> findAll();

	public Product findById(long parseLong);

	public List<ProductEarningRate> findRateByPid(String pid);
	
	public void modifyProduct(Product p);

	public Product findById(String pProductId);
}
