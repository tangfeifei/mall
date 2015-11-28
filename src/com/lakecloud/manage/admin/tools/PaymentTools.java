package com.lakecloud.manage.admin.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IUserService;

@Component
public class PaymentTools {
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IUserService userService;

	public boolean queryPayment(String mark, String type) {
		Map params = new HashMap();
		params.put("mark", mark);
		params.put("type", type);
		List<Payment> objs = this.paymentService
				.query(
						"select obj from Payment obj where obj.mark=:mark and obj.type=:type",
						params, -1, -1);
		if (objs.size() > 0) {
			return objs.get(0).isInstall();
		} else
			return false;
	}

	public Map queryPayment(String mark) {
		Map params = new HashMap();
		params.put("mark", mark);
		params.put("type", "user");
		Long store_id = null;
		store_id = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore().getId();
		params.put("store_id", store_id);
		List<Payment> objs = this.paymentService
				.query(
						"select obj from Payment obj where obj.mark=:mark and obj.type=:type and obj.store.id=:store_id",
						params, -1, -1);
		Map ret = new HashMap();
		if (objs.size() == 1) {
			ret.put("install", objs.get(0).isInstall());
			ret.put("already", true);
		} else {
			ret.put("install", false);
			ret.put("already", false);
		}
		return ret;
	}

	public Map queryStorePayment(String mark, String store_id) {
		Map ret = new HashMap();
		Map params = new HashMap();
		params.put("mark", mark);
		params.put("store_id", CommUtil.null2Long(store_id));
		List<Payment> objs = this.paymentService
				.query(
						"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
						params, -1, -1);
		if (objs.size() == 1) {
			ret.put("install", objs.get(0).isInstall());
			ret.put("content", objs.get(0).getContent());
		} else {
			ret.put("install", false);
			ret.put("content", "");
		}
		return ret;
	}

	public Map queryShopPayment(String mark) {
		Map ret = new HashMap();
		Map params = new HashMap();
		params.put("mark", mark);
		params.put("type", "admin");
		List<Payment> objs = this.paymentService
				.query(
						"select obj from Payment obj where obj.mark=:mark and obj.type=:type",
						params, -1, -1);
		if (objs.size() == 1) {
			ret.put("install", objs.get(0).isInstall());
			ret.put("content", objs.get(0).getContent());
		} else {
			ret.put("install", false);
			ret.put("content", "");
		}
		return ret;
	}
}
