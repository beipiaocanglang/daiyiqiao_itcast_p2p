package cn.itcast.service.product.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.dao.product.IProductDAO;
import cn.itcast.dao.product.IProductRateDAO;
import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.product.IProductService;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

	@Autowired
	private IProductDAO productDao;

	@Autowired
	private IProductRateDAO productRateDao;

	// 查询所有理财产品方法
	public List<Product> findAll() {

		return productDao.findAll();

	}

	@Override
	public Product findById(long id) {
		return productDao.findOne(id);
	}

	// 根据理财产品的id查询利率信息
	@Override
	public List<ProductEarningRate> findRateByPid(String pid) {
		return productRateDao.findByProductId(Integer.parseInt(pid));
	}

	//修改操作
	@Override
	public void modifyProduct(Product p) {
		// 1. 根据理财产品的id将利率信息删除
		productRateDao.deletByProId((int) p.getProId());
		// 2. 添加新的利率信息
		productRateDao.save(p.getProEarningRate());
		// 3. 修改理财产品信息
		productDao.save(p);
	}

	@Override
	public Product findById(String pProductId) {
		return productDao.findOne(Long.parseLong(pProductId));
	}
}
