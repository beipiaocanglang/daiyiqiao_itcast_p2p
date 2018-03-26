package cn.itcast.action.creditor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.creditor.CreditorModel;
import cn.itcast.domain.creditor.CreditorSumModel;
import cn.itcast.service.creditor.ICreditorService;
import cn.itcast.utils.Response;

@Namespace("/creditor")
@Controller
@Scope("prototype")
public class CreditorAction extends BaseAction {

	@Autowired
	private ICreditorService creditorService;
	
	//债权审核
	@Action("checkCreditor")
	public void checkCreditor(){
		//1.得到请求参数
		String ids=this.getRequest().getParameter("ids");
		
		//2.调用service完成审核操作
		creditorService.checkCreditor(ids);
		
		try {
			this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 查询债权信息
	@Action("getCreditorlist")
	public void getCreditorlist() {

		this.getResponse().setCharacterEncoding("utf-8");
		// 1.获取请求参数
		// dDebtNo=&dContractNo=&dDebtTransferredDateStart=&dDebtTransferredDateEnd=&dDebtStatus=&dMatchedStatus=&offsetnum=1
		String dDebtNo = this.getRequest().getParameter("dDebtNo");
		String dContractNo = this.getRequest().getParameter("dContractNo");
		String dDebtTransferredDateStart = this.getRequest().getParameter("dDebtTransferredDateStart");
		String dDebtTransferredDateEnd = this.getRequest().getParameter("dDebtTransferredDateEnd");
		String dDebtStatus = this.getRequest().getParameter("dDebtStatus");
		String dMatchedStatus = this.getRequest().getParameter("dMatchedStatus");
		String offsetnum = this.getRequest().getParameter("offsetnum");
		// 2.验证请求参数
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(dDebtNo)) {
			map.put("dDebtNo", dDebtNo.trim());
		}
		if (StringUtils.isNotEmpty(dContractNo)) {
			map.put("dContractNo", dContractNo.trim());
		}
		if (StringUtils.isNotEmpty(dDebtTransferredDateStart)) {
			map.put("dDebtTransferredDateStart", dDebtTransferredDateStart.trim());
		}
		if (StringUtils.isNotEmpty(dDebtTransferredDateEnd)) {
			map.put("dDebtTransferredDateEnd", dDebtTransferredDateEnd.trim());
		}
		if (StringUtils.isNotEmpty(dDebtStatus)) {
			map.put("dDebtStatus", Integer.parseInt(dDebtStatus.trim()));
		}
		if (StringUtils.isNotEmpty(dMatchedStatus)) {
			map.put("dMatchedStatus", Integer.parseInt(dMatchedStatus.trim()));
		}
		if (StringUtils.isNotEmpty(offsetnum)) {
			map.put("offsetnum", Integer.parseInt(offsetnum.trim()));
		}

		// 3.调用service完成查询操作
		// 3.1查询债权信息
		List<CreditorModel> cms = creditorService.findCreditorList(map);// 查询债权信息
		// 3.2查询债权统计信息
		Object[] cmsSum = creditorService.findCreditorListSum(map); // 

		CreditorSumModel creditorSum = new CreditorSumModel();
		creditorSum.setdIdCount(Integer.parseInt(cmsSum[0].toString()));
		creditorSum.setdDebtMoneySum(Double.parseDouble(cmsSum[1].toString()));
		creditorSum.setdAvailableMoneySum(Double.parseDouble(cmsSum[2].toString()));

		// 处理显示的状态相关的描述
		// <td>
		// <select id="dDebtStatus" style="width:235px;">
		// <option value="">全部</option>
		// <option value="11301">未审核</option>
		// <option value="11302">已审核</option>
		// <option value="11303">正常还款</option>
		// <option value="11304">已结清</option>
		// <option value="11305">提前结清</option>
		// <option value="11306">结算失败</option>
		// </select>
		// </td>
		// <td class="tr">债权匹配状态：</td>
		// <td>
		// <select id="dMatchedStatus" style="width:235px;">
		// <option value="">全部</option>
		// <option value="11401">部分匹配</option>
		// <option value="11402">完全匹配</option>
		// <option value="11403">未匹配</option>
		// </select>
		// </td>
		for (CreditorModel cm : cms) {
			if (cm.getDebtStatus() == 11301) {
				cm.setDebtStatusDesc("未审核");
			}
			if (cm.getDebtStatus() == 11302) {
				cm.setDebtStatusDesc("已审核");
			}
			if (cm.getDebtStatus() == 11303) {
				cm.setDebtStatusDesc("正常还款");
			}
			if (cm.getDebtStatus() == 11304) {
				cm.setDebtStatusDesc("已结清");
			}
			if (cm.getDebtStatus() == 11305) {
				cm.setDebtStatusDesc("提前结清");
			}
			if (cm.getDebtStatus() == 11306) {
				cm.setDebtStatusDesc("结算失败");
			}

			if (cm.getMatchedStatus() == 11401) {
				cm.setMatchedStatusDesc("部分匹配");
			}
			if (cm.getMatchedStatus() == 11402) {
				cm.setMatchedStatusDesc("完全匹配");
			}
			if (cm.getMatchedStatus() == 11403) {
				cm.setMatchedStatusDesc("未匹配");
			}
		}

		// 4.响应数据到浏览器
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("date", cms);
		data.put("datasum", creditorSum);
		try {
			this.getResponse().getWriter().write(Response.build().setStatus("1").setData(data).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
