package cn.itcast.action.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.userAccount.UserAccountModel;
import cn.itcast.service.userAccount.IUserAccountService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;

@Controller
@Namespace("/account")
@Scope("prototype")
public class PersonCenterAction extends BaseAction {
	@Autowired
	private BaseCacheService baseCacheService;

	@Autowired
	private IUserAccountService userAccountService;

	private Logger log = Logger.getLogger(PersonCenterAction.class);

	// 获取帐户中心信息-----帐户中金额信息
	@Action("accountHomepage")
	public void accountHomepage() {

		// 1.得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());

		try {
			// 2.判断toke不为空
			if (StringUtils.isEmpty(token)) {
				log.error("token为空");
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
				return;
			}
			// 3.token不为空，从readis中获取数据
			Map<String, Object> hmap = baseCacheService.getHmap(token);

			// 4.判断hmap不为空
			if (hmap == null || hmap.size() == 0) {
				log.error("用户没有登录");
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}

			// 5.获取用户id
			int id = (int) hmap.get("id");

			// 6.调用service，根据id查询帐户信息
			UserAccountModel uam = userAccountService.findByUserId(id);
			List<JSONObject> list = new ArrayList<JSONObject>();

			// 7.判断uam不为空
			if (uam != null) {

				// 8.封装响应数据到浏览器
				JSONObject obj = new JSONObject();
				obj.put("u_total", uam.getTotal()); // 总额
				obj.put("u_balance", uam.getBalance()); // 余额
				obj.put("u_interest_a", uam.getInterestA()); // 收益

				list.add(obj);

			} else {
				// 8.封装响应数据到浏览器
				JSONObject obj = new JSONObject();
				obj.put("u_total", 0); // 总额
				obj.put("u_balance", 0); // 余额
				obj.put("u_interest_a", 0); // 收益

				list.add(obj);
				// this.getResponse().getWriter()
				// .write(Response.build().setStatus(FrontStatusConstants.NULL_RESULT).toJSON());
				// return;
			}
			log.info("查询用户帐户信息success");
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
			return;

		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			try {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.SYSTEM_ERROE).toJSON());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

	}
}
