package cn.itcast.action.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.action.common.BaseAction;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.product.IProductService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.JsonMapper;
import cn.itcast.utils.ProductStyle;
import cn.itcast.utils.Response;

@Controller
@Namespace("/product")
@Scope("prototype")
public class ProductAction extends BaseAction implements ModelDriven<Product> {

	// 使用log4j日志来记录
	private Logger log = Logger.getLogger(ProductAction.class);
	
	@Autowired
	private BaseCacheService baseCacheService;

	@Autowired
	private IProductService productService;

	private Product p = new Product();

	@Override
	public Product getModel() {

		return p;
	}

	/**
	 * 方法描述：将状态转换为中文
	 * 
	 * @param products
	 *            void
	 */
	private void changeStatusToChinese(List<Product> products) {
		if (null == products)
			return;
		for (Product product : products) {
			int way = product.getWayToReturnMoney();
			// 每月部分回款
			if (ProductStyle.REPAYMENT_WAY_MONTH_PART.equals(String.valueOf(way))) {
				product.setWayToReturnMoneyDesc("每月部分回款");
				// 到期一次性回款
			} else if (ProductStyle.REPAYMENT_WAY_ONECE_DUE_DATE.equals(String.valueOf(way))) {
				product.setWayToReturnMoneyDesc("到期一次性回款");
			}

			// 是否复投 isReaptInvest 136：是、137：否
			// 可以复投
			if (ProductStyle.CAN_REPEAR == product.getIsRepeatInvest()) {
				product.setIsRepeatInvestDesc("是");
				// 不可复投
			} else if (ProductStyle.CAN_NOT_REPEAR == product.getIsRepeatInvest()) {
				product.setIsRepeatInvestDesc("否");
			}
			// 年利率
			if (ProductStyle.ANNUAL_RATE == product.getEarningType()) {
				product.setEarningTypeDesc("年利率");
				// 月利率 135
			} else if (ProductStyle.MONTHLY_RATE == product.getEarningType()) {
				product.setEarningTypeDesc("月利率");
			}

			if (ProductStyle.NORMAL == product.getStatus()) {
				product.setStatusDesc("正常");
			} else if (ProductStyle.STOP_USE == product.getStatus()) {
				product.setStatusDesc("停用");
			}

			// 是否可转让
			if (ProductStyle.CAN_NOT_TRNASATION == product.getIsAllowTransfer()) {
				product.setIsAllowTransferDesc("否");
			} else if (ProductStyle.CAN_TRNASATION == product.getIsAllowTransfer()) {
				product.setIsAllowTransferDesc("是");
			}
		}
	}

	/**
	 * 方法描述：将状态转换为中文
	 * 
	 * @param products
	 *            void
	 */
	private void changeStatusToChinese(Product p) {
		List<Product> ps = new ArrayList<Product>();
		ps.add(p);
		changeStatusToChinese(ps);
	}

	// 修改操作
	@Action("modifyProduct")
	public void modifyProduct() {

		// 1.使用模型驱动将关于产品信息封装到p对象。
		// 2.手动获取利率信息proEarningRates={"16":12.8,"18":13,"20":13.6,"24":14.2,"30":15.6,"12":12,"36":16}
		// 封装成List<ProductEarningRate>
		String proEarningRates = this.getRequest().getParameter("proEarningRates");
		Map<String, Object> map = new JsonMapper().fromJson(proEarningRates, Map.class); //将字符串转换成Map对象

		List<ProductEarningRate> pers = new ArrayList<ProductEarningRate>();
		for (String key : map.keySet()) {//遍历Map
			ProductEarningRate per = new ProductEarningRate();
			per.setProductId((int) p.getProId());//将利率对象的产品设置
			per.setIncomeRate(Double.parseDouble(map.get(key).toString())); //利率信息
			per.setMonth(Integer.parseInt(key)); //月份
			pers.add(per);
		}

		// 3.将利率信息封装到产品对象中
		p.setProEarningRate(pers);
		// 4.调用修改方法
		productService.modifyProduct(p);

		try {
			this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 根据理财产品的id查询利率信息
	@Action("findRates")
	public void findRatesByPid() {
		// 1.得到理财产品的id
		String pid = this.getRequest().getParameter("proId");

		// 2.调用service查询
		List<ProductEarningRate> pers = productService.findRateByPid(pid);
		// 3.响应数据
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (ProductEarningRate per : pers) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("month", per.getMonth());
			map.put("incomeRate", per.getIncomeRate());
			list.add(map);
		}
		try {
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Action("findProductById")
	public void findProductById() {
		// 1.得到理财产品id
		String pid = this.getRequest().getParameter("proId");

		// 2.调用service，根据id查询
		Product p = productService.findById(Long.parseLong(pid));

		// 3.响应数据
		this.getResponse().setCharacterEncoding("utf-8");
		// 数据响应时显示处理问题

		changeStatusToChinese(p);

		try {
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(p).toJSON());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Action("findAllProduct")
	public void findAll() {
		// 处理编码
		this.getResponse().setCharacterEncoding("utf-8");
		// 1.调用service获取所有理财产品信息
		List<Product> ps = productService.findAll();
		try {
			if (ps == null || ps.size() == 0) {
				log.error("查询理财产品失败");
				// 没有查询到数据
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).setData(ps).toJSON());
				return;
			}

			// 需要将Product对象中的状态及回款方式根据其数值转换成对应的文字描述赋值给statusDesc
			// wayToReturnMoneyDesc 在页面上显示信息时获取是描述信息值
			changeStatusToChinese(ps);

			// 2.将ps转换成json响应到浏览器端---使用工具完成

			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(ps).toJSON());
		} catch (IOException e) {
			log.error("服务器出问题......" + e.getMessage());
			log.info("普通信息");
			log.warn("警告信息");
			log.debug("开发阶段调试");
			log.error("");
			log.fatal("致命信息");
			// 没有查询到数据
			try {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.SYSTEM_ERROE).setData(ps).toJSON());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		// Response它是我们自己定义的一个类,这个类中有属性status,Data,最后我们是将Response对象转换成json

	}
}
