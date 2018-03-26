package cn.itcast.action.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
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
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.user.IUserService;
import cn.itcast.utils.CommonUtil;
import cn.itcast.utils.ConfigurableConstants;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.ImageUtil;
import cn.itcast.utils.MD5Util;
import cn.itcast.utils.Response;
import cn.itcast.utils.SMSUtils;
import cn.itcast.utils.TokenUtil;

@Controller
@Namespace("/user")
@Scope("prototype")
public class UserAction extends BaseAction implements ModelDriven<UserModel> {

	@Autowired
	private BaseCacheService baseCacheService;

	@Autowired
	private IUserService userService;

	private UserModel user = new UserModel();

	@Override
	public UserModel getModel() {
		return user;
	}

	public String generateUserToken(String userName) {

		try {
			// 生成令牌
			String token = TokenUtil.generateUserToken(userName);// 这个加密操作得到的token中包含了用户名。这个加密是可逆的，也就是说可以从token中解出用户名。

			// 根据用户名获取用户
			UserModel user = userService.findByUsername(userName);
			// 将用户信息存储到map中。
			Map<String, Object> tokenMap = new HashMap<String, Object>();
			tokenMap.put("id", user.getId());
			tokenMap.put("userName", user.getUsername());
			tokenMap.put("phone", user.getPhone());
			tokenMap.put("userType", user.getUserType());
			tokenMap.put("payPwdStatus", user.getPayPwdStatus());
			tokenMap.put("emailStatus", user.getEmailStatus());
			tokenMap.put("realName", user.getRealName());
			tokenMap.put("identity", user.getIdentity());
			tokenMap.put("realNameStatus", user.getRealNameStatus());
			tokenMap.put("payPhoneStatus", user.getPhoneStatus());

			baseCacheService.del(token);
			baseCacheService.setHmap(token, tokenMap); // 将信息存储到redis中

			// 获取配置文件中用户的生命周期，如果没有，默认是30分钟
			String tokenValid = ConfigurableConstants.getProperty("token.validity", "30");
			tokenValid = tokenValid.trim();
			baseCacheService.expire(token, Long.valueOf(tokenValid) * 60);

			return token;
		} catch (Exception e) {
			e.printStackTrace();
			return Response.build().setStatus("-9999").toJSON();
		}
	}

	// 发送短信验证码
	@Action("sendMessage")
	public void sendMessage() {
		// 0.获取token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		Map<String, Object> hmap = baseCacheService.getHmap(token);
		try {
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}
			// 1.得到请求参数 phone
			String phone = this.getRequest().getParameter("phone");

			// 2.得到一个手机验证码
			String phoneCode = RandomStringUtils.randomNumeric(4);// 得到一个4位的随机数字

			// 3.发送
			String sendMsg = "P2P手机认证操作,请在3分钟内录入验证码:" + phoneCode;
			//SMSUtils.SendSms(phone, sendMsg);
			System.out.println(sendMsg);
			// 4.存储到redis
			baseCacheService.set(phone, phoneCode);
			baseCacheService.expire(phone, 3 * 60);
			// 5.响应
			this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 绑定手机
	@Action("addPhone")
	public void addPhone() {
		// 0.获取token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		Map<String, Object> hmap = baseCacheService.getHmap(token);
		try {
			if (hmap == null || hmap.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
				return;
			}
			// 1.获取请求参数 手机号与录入的验证码
			String phone = this.getRequest().getParameter("phone");
			String phoneCode = this.getRequest().getParameter("phoneCode");

			// 2.判断验证码是否正确
			String _phoneCode = baseCacheService.get(phone);
			if (!_phoneCode.equals(phoneCode)) {
				this.getResponse().getWriter().write(Response.build().setStatus("27").toJSON()); // 验证码不正确
				return;
			}
			// 3.判断用记是否已经绑定手机
			UserModel um = userService.findByPhone(phone);
			if (um.getPhoneStatus() == 1) {
				// 已经绑定
				this.getResponse().getWriter().write(Response.build().setStatus("67").toJSON()); // 已经绑定了
				return;
			}

			// 4.没有绑定进行绑定操作
			int userid=(int) hmap.get("id");
			userService.updatePhoneStatus(phone,userid);

			// 5.响应
			this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取用户信息及认证状态
	@Action("userSecureDetailed")
	public void userSecureDetailed() {
		// 1. 得到token
		// String token = this.getRequest().getHeader("token");
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		// 2. 根据token从redis中获取用户id
		Map<String, Object> hmap = baseCacheService.getHmap(token);
		int userid = (int) hmap.get("id");
		// 3. 调用service根据id查询用户
		UserModel um = userService.findById(userid);
		// 4. 将浏览器端需要的数据封装响应到浏览器端。
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("realNameStatus", um.getRealNameStatus());
		map.put("phoneStatus", um.getPhoneStatus());
		map.put("payPwdStatus", um.getPayPwdStatus());
		map.put("emailStatus", um.getEmailStatus());
		map.put("username", um.getUsername());
		map.put("phone", um.getPhone());
		list.add(map);

		try {
			this.getResponse().getWriter().write(Response.build().setStatus("1").setData(list).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取用户安全级别
	@Action("userSecure")
	public void userSecure() {
		// 1. 得到token
		// String token = this.getRequest().getHeader("token");
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		// 2. 根据token从redis中获取用户id
		Map<String, Object> hmap = baseCacheService.getHmap(token);
		int userid = (int) hmap.get("id");
		// 3. 调用service根据id查询用户
		UserModel um = userService.findById(userid);
		// 4. 将浏览器端需要的数据封装响应到浏览器端。
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("realNameStatus", um.getRealNameStatus());
		map.put("phoneStatus", um.getPhoneStatus());
		map.put("payPwdStatus", um.getPayPwdStatus());
		map.put("emailStatus", um.getEmailStatus());
		list.add(map);

		try {
			this.getResponse().getWriter().write(Response.build().setStatus("1").setData(list).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 退出
	@Action("logout")
	public void logout() {
		// 从redis中将用户信息删除
		// 获取token
		String token = this.getRequest().getHeader("token");
		baseCacheService.del(token);
		try {
			this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 登录操作
	@Action("login")
	public void login() {
		// a. 获取请求参数
		String username = this.getRequest().getParameter("username");
		String password = this.getRequest().getParameter("password");
		String signUuid = this.getRequest().getParameter("signUuid");
		String signCode = this.getRequest().getParameter("signCode");
		// b. 校验
		// c. 判断验证码
		String _signCode = baseCacheService.get(signUuid);
		try {
			if (!_signCode.equalsIgnoreCase(signCode)) {
				// 验证码不下确
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
				return;
			}
			// d. 调用service通过username,password判断用户是否存在

			// 处理可以使用电话号码登录
			boolean flag = CommonUtil.isMobile(username);
			String uname = "";
			if (flag) {
				UserModel um = userService.findByPhone(username);
				uname = um.getUsername();
			} else {
				uname = username;
			}

			String pwd = MD5Util.md5(uname.toLowerCase() + password.toLowerCase());

			UserModel um = userService.login(uname, pwd);
			if (um != null) {
				// e. 将用户存储到redis中(就是以前类似于session操作)
				// 1.得到一个token,将用户um信息封装到map中，以token为key 以map为value存储到redis中。
				String token = generateUserToken(um.getUsername());
				// f. 向浏览器响应数据
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("userName", um.getUsername());
				data.put("id", um.getId());
				this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS)
						.setData(data).setToken(token).toJSON());
			} else {
				// 登录失败
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).toJSON());
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 注册操作
	@Action("signup")
	public void regist() {
		// 1.使用模型驱动完成请求参数封装.
		// 2.有些参数封装不上，例如验证码----必须手动获取

		// 3.完成添加用户操作

		// 处理密码加密
		String pwd = MD5Util.md5(user.getUsername().toLowerCase() + user.getPassword().toLowerCase());
		user.setPassword(pwd);

		boolean flag = userService.addUser(user);

		// 4.响应数据
		try {
			if (flag) {
				String token = generateUserToken(user.getUsername());// 得到token将用户存储到redis

				Map<String, Object> data = new HashMap<String, Object>();
				data.put("userName", user.getUsername());
				data.put("id", user.getId());
				this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS)
						.setData(data).setToken(token).toJSON());
			} else {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.REGISTER_LOSED).toJSON());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 验证验证码是否正确
	@Action("codeValidate")
	public void codeValidate() {
		// 1.得到请求参数
		String signUuid = this.getRequest().getParameter("signUuid");
		String signCode = this.getRequest().getParameter("signCode");
		try {
			if (StringUtils.isBlank(signUuid)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).toJSON());
				return;
			}
			if (StringUtils.isBlank(signCode)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).toJSON());
				return;
			}
			// 2.判断验证码是否正确
			String _signCode = baseCacheService.get(signUuid);
			if (StringUtils.isBlank(_signCode)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).toJSON());
				return;
			}
			// 3.响应数据到浏览器
			if (signCode.equalsIgnoreCase(_signCode)) {
				// 正确
				this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
			} else {
				// 不正确
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 验证手机号是否可用
	@Action("validatePhone")
	public void validatePhone() {
		// 1.获取请求参数
		String phone = this.getRequest().getParameter("phone");

		// 2.调用service判断用户是否存在
		UserModel user = userService.findByPhone(phone);
		// 3.响应数据
		try {
			if (user == null) {
				// 没有占用
				this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
			} else {
				// 被占用

				this.getResponse().getWriter().write(Response.build().setStatus("67").toJSON());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 验证用户名是否可用
	@Action("validateUserName")
	public void validateUserName() {
		// 1.获取请求参数
		String username = this.getRequest().getParameter("username");

		// 2.调用service判断用户是否存在
		UserModel user = userService.findByUsername(username);
		// 3.响应数据
		try {
			if (user == null) {
				// 没有占用
				this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
			} else {
				// 被占用

				this.getResponse().getWriter().write(Response.build().setStatus("0").toJSON());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取uuid
	@Action("uuid")
	public void uuid() {
		// 1.得到一个uuid
		String uuid = UUID.randomUUID().toString();

		// 2.存储到redis
		baseCacheService.set(uuid, uuid);
		baseCacheService.expire(uuid, 60 * 3);
		// 3.响应到浏览器
		try {
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setUuid(uuid).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取验证码
	@Action("validateCode")
	public void validateCode() {

		// 1.得到请求参数uuid
		String tokenUuid = this.getRequest().getParameter("tokenUuid");

		// 2.判断请求参数uuid是否正确
		String value = baseCacheService.get(tokenUuid);
		if (StringUtils.isNotBlank(value)) {// 如果值为false说明uuid错误或者过期,就不返回任何结果

			// 3.如果没有问题，生成图片验证码响应回去,并且要保存到redis中
			String str = ImageUtil.getRundomStr(); // 获取图片验证码上的信息

			baseCacheService.set(tokenUuid, str);// 将验证码内容存储到redis中，以uuid为key,内容为value
			baseCacheService.expire(tokenUuid, 3 * 60);

			try {
				ImageUtil.getImage(str, this.getResponse().getOutputStream());// 将图片验证码响应到浏览器
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
