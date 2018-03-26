package cn.itcast.action.email;

import java.io.IOException;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.email.IEmailService;
import cn.itcast.service.user.IUserService;
import cn.itcast.utils.EmailUtils;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import cn.itcast.utils.SecretUtil;

@Namespace("/emailAuth")
@Controller
@Scope("prototype")
public class EmailAuthAction extends BaseAction {

	@Autowired
	private BaseCacheService baseCacheService;

	@Autowired
	private IEmailService emailService;

	@Autowired
	private IUserService userService;

	// 激活邮箱，并修改邮箱状态
	@Action("emailactivation")
	public void emailactivation() {
		this.getResponse().setContentType("text/html;charset=utf-8");
		// 1.得到请求参数
		String us = this.getRequest().getParameter("us");
		// 2.将请求参数解密得到userid
		try {
			String userid = SecretUtil.decode(us);

			// 3.查询用户
			UserModel um = userService.findById(Integer.parseInt(userid));
			// 4.修改状态
			if (um != null) {
				// um.setEmailStatus(1);
				userService.updateEmailStatus(Integer.parseInt(userid));
				this.getResponse().getWriter().write("激活成功");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.getResponse().getWriter().write("激活失败");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 绑定邮箱，并发送邮件
	@Action("auth")
	public void sendEmail() {

		// 0.获取token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		Map<String, Object> hmap = baseCacheService.getHmap(token);
		try {
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}

			// 1.得到请求参数
			String email = this.getRequest().getParameter("email");

			// 校验邮箱
			if (EmailUtils.checkEmail(email)) {
				// 2.发邮件
				// 参数email 就是发送的邮箱地址 enc是加密后的id username是用户名
				int userid = (int) hmap.get("id");
				String enc = SecretUtil.encrypt(String.valueOf(userid)); // 将userid加密
				// SecretUtil.decode(encryptedStr) //解密
				String username = (String) hmap.get("userName");
				String content = EmailUtils.getMailCapacity(email, enc, username);// 获取邮件内容

				emailService.sendEmail(email, content);

				// 绑定邮箱操作---应该先查询用户是否已经绑定邮箱
							
				userService.addEmail(email, userid);
				// 3.响应
				this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
			} else {
				this.getResponse().getWriter().write(Response.build().setStatus("174").toJSON());
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
