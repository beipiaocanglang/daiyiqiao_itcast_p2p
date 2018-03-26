package cn.itcast.action.bankcardInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.bankCardInfo.Bank;
import cn.itcast.domain.bankCardInfo.BankCardInfo;
import cn.itcast.domain.city.City;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.bank.IBankService;
import cn.itcast.service.bankcardInfo.IBankCardInfoService;
import cn.itcast.service.city.ICityService;
import cn.itcast.service.user.IUserService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;

@Namespace("/bankCardInfo")
@Controller
@Scope("prototype")
public class BankCardInfoAction extends BaseAction implements ModelDriven<BankCardInfo> {

	@Autowired
	private BaseCacheService baseCacheService;

	@Autowired
	private IBankCardInfoService bankCardInfoService;

	@Autowired
	private IBankService bankService;

	@Autowired
	private ICityService cityService;

	@Autowired
	private IUserService userService;

	private BankCardInfo bci = new BankCardInfo();

	@Override
	public BankCardInfo getModel() {

		return bci;
	}

	// 绑定银行卡操作
	@Action("addBankCardInfo")
	public void addBankCardInfo() {
		// 1.得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		try {
			if (StringUtils.isEmpty(token)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
				return;
			}
			Map<String, Object> hmap = baseCacheService.getHmap(token);
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}
			// 2.使用模型驱动将数据封装到BankCardInfo对象

			// 必须封装用户id到bci中。
			bci.setUserId((Integer) hmap.get("id"));
			bankCardInfoService.save(bci);

			this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询联动信息
	@Action("findCity")
	public void findCity() {
		this.getResponse().setCharacterEncoding("utf-8");
		// 1.得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		try {
			if (StringUtils.isEmpty(token)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
				return;
			}
			Map<String, Object> hmap = baseCacheService.getHmap(token);
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}

			// 2.得到参数
			String cityAreaNum = this.getRequest().getParameter("cityAreaNum");

			// 3.调用service查询
			List<City> citys = cityService.findByParentCityAreaNum(cityAreaNum);

			// 4.响应
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(citys).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 查询用户信息
	@Action("findUserInfo")
	public void findUserInfo() {
		// 1.得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		try {
			if (StringUtils.isEmpty(token)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
				return;
			}
			Map<String, Object> hmap = baseCacheService.getHmap(token);
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}

			// 2.得到用户名,在一次判断用户
			String username = this.getRequest().getParameter("username");
			// 3.根据用户id去查询用户的信息
			int userid = (int) hmap.get("id");
			UserModel user = userService.findById(userid);

			this.getResponse().getWriter().write(Response.build().setStatus("1").setData(user).toJSON());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询所有省份信息
	@Action("findProvince")
	public void findProvince() {

		this.getResponse().setCharacterEncoding("utf-8");
		// 1.得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		try {
			if (StringUtils.isEmpty(token)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
				return;
			}
			Map<String, Object> hmap = baseCacheService.getHmap(token);
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}

			// 2.查询所有省份信息
			List<City> citys = cityService.findProvinces();

			// 3.响应
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(citys).toJSON());
			return;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询所有银行卡
	@Action("findAllBanks")
	public void findAllBanks() {
		this.getResponse().setCharacterEncoding("utf-8");
		// 1.得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		try {
			if (StringUtils.isEmpty(token)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
				return;
			}
			Map<String, Object> hmap = baseCacheService.getHmap(token);
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}

			// 2.查询所有银行卡信息
			List<Bank> banks = bankService.findAll();

			// 3.响应数据
			this.getResponse().getWriter().write(Response.build().setStatus("1").setData(banks).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 查询用户的银行卡信息
	@Action("findBankInfoByUsername")
	public void findBankInfoByUsername() {
		// 1.得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		try {
			if (StringUtils.isEmpty(token)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
				return;
			}
			Map<String, Object> hmap = baseCacheService.getHmap(token);
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}

			// 2.得到用户名,在一次判断用户
			String username = this.getRequest().getParameter("username");
			// 3.根据用户id去查询用户的银行卡信息
			int userid = (int) hmap.get("id");
			BankCardInfo bci = bankCardInfoService.findByUserId(userid);
			// 4.响应数据
			if (bci == null) {
				// 没有查询到
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_RESULT).toJSON());
				return;
			} else {
				// 查询到，返回1
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(bci).toJSON());
				return;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
