package com.lakecloud.view.web.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.service.IOrderFormService;

@Component
public class OrderViewTools {
	@Autowired
	private IOrderFormService orderFormService;

	public int query_user_order(String order_status) {
		Map params = new HashMap();
		int status = -1;
		if (order_status.equals("order_submit")) {// 已经提交
			status = 10;
		}
		if (order_status.equals("order_pay")) {// 已经付款
			status = 20;
		}
		if (order_status.equals("order_shipping")) {// 已经发货
			status = 30;
		}
		if (order_status.equals("order_receive")) {// 已经收货
			status = 40;
		}
		if (order_status.equals("order_finish")) {// 已经完成
			status = 60;
		}
		if (order_status.equals("order_cancel")) {// 已经取消
			status = 0;
		}
		params.put("status", status);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<OrderForm> ofs = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.order_status=:status and obj.user.id=:user_id",
						params, -1, -1);
		return ofs.size();
	}

	public int query_store_order(String order_status) {
		if (SecurityUserHolder.getCurrentUser().getStore() != null) {
			Map params = new HashMap();
			int status = -1;
			if (order_status.equals("order_submit")) {// 已经提交
				status = 10;
			}
			if (order_status.equals("order_pay")) {// 已经付款
				status = 20;
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				status = 30;
			}
			if (order_status.equals("order_receive")) {// 已经收货
				status = 40;
			}
			if (order_status.equals("order_finish")) {// 已经完成
				status = 60;
			}
			if (order_status.equals("order_cancel")) {// 已经取消
				status = 0;
			}
			params.put("status", status);
			params.put("store_id", SecurityUserHolder.getCurrentUser()
					.getStore().getId());
			List<OrderForm> ofs = this.orderFormService
					.query(
							"select obj from OrderForm obj where obj.order_status=:status and obj.store.id=:store_id",
							params, -1, -1);
			return ofs.size();
		} else
			return 0;
	}
}
