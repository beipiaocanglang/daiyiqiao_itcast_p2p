package cn.itcast.action.verification;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;

@Namespace("/verification")
@Controller
@Scope("prototype")
public class VerificationAction extends BaseAction {

	@Autowired
	private BaseCacheService baseCacheService;

	// 验证手机短信码
	@Action("validateSMS")
	public void validateSMS() {
		// 1.获取token
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

			// 2.获取请求参数
			String phone = this.getRequest().getParameter("phone");
			String code = this.getRequest().getParameter("code");

			// 3.从redis中获取验证码，判断
			String _code = baseCacheService.get(phone);
			if (!code.equals(_code)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
				return;
			}

			this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
