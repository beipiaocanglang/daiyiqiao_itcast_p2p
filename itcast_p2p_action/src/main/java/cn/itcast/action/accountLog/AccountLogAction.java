package cn.itcast.action.accountLog;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.productAcount.WaitMatchMoneyModel;
import cn.itcast.service.accountLog.IAccountLogService;
import cn.itcast.utils.Response;

@Namespace("/accountLog")
@Controller
@Scope("prototype")
public class AccountLogAction extends BaseAction {

	@Autowired
	private IAccountLogService accountLogService;

	// 查询待匹配资金队列
	@Action("selectWaitMoney")
	public void selectWaitMoney() {
		this.getResponse().setCharacterEncoding("utf-8");
		// 1.请求参数
		// 2.处理请求参数
		// 3.调用service完成查询操作
		List<WaitMatchMoneyModel> list = accountLogService.findWaitMoneyList();
		WaitMatchMoneyModel wmm = accountLogService.findWaitMoneySum();
		// 4.响应数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("listMatch", list);
		map.put("waitMatchCount", wmm);
		try {
			this.getResponse().getWriter().write(Response.build().setStatus("1").setData(map).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
