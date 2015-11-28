package com.lakecloud.view.web.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserService;

/**
 * @info 店铺工具类
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Component
public class StoreViewTools {
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreClassService storeClassService;

	/**
	 * 根据店铺等级属性，生成文字说明
	 * 
	 * @param grade
	 * @return
	 */
	public String genericFunction(StoreGrade grade) {
		String fun = "";
		if (grade.getAdd_funciton().equals(""))
			fun = "无";
		String[] list = grade.getAdd_funciton().split(",");
		for (String s : list) {
			if (s.equals("editor_multimedia")) {
				fun = "富文本编辑器" + fun;
			}
		}
		return fun;
	}

	/**
	 * 转换商品后缀名
	 * 
	 * @param imageSuffix
	 * @return
	 */
	public String genericImageSuffix(String imageSuffix) {
		String suffix = "";
		String[] list = imageSuffix.split("\\|");
		for (String l : list) {
			suffix = "*." + l + ";" + suffix;
		}
		return suffix.substring(0, suffix.length() - 1);
	}

	/**
	 * 计算店铺信用等级
	 * 
	 * @param id
	 * @return
	 */
	public int generic_store_credit(String id) {
		int credit = 0;
		String sys_credit = this.configService.getSysConfig().getCreditrule();
		Map map = Json.fromJson(HashMap.class, sys_credit);
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			list.add(Integer.parseInt(map.get(key).toString()));
		}
		Integer[] ints = list.toArray(new Integer[list.size()]);
		Arrays.sort(ints, new Comparator() {
			@Override
			public int compare(Object obj1, Object obj2) {
				// TODO Auto-generated method stub
				int a = CommUtil.null2Int(obj1);
				int b = CommUtil.null2Int(obj2);
				if (a == b) {
					return 0;
				} else {
					return a > b ? 1 : -1;
				}
			}
		});
		Store store = this.storeService.getObjById(Long.parseLong(id));
		for (int i = 0; i < ints.length - 1; i++) {
			if (ints[i] <= store.getStore_credit()
					&& ints[i + 1] >= store.getStore_credit()) {
				credit = i + 1;
				break;
			}
		}
		if (store.getStore_credit() >= ints[ints.length - 1]) {
			credit = ints.length;
		}
		return credit;
	}

	public int generic_user_credit(String id) {
		int credit = 0;
		String user_credit = this.configService.getSysConfig()
				.getUser_creditrule();
		Map map = Json.fromJson(HashMap.class, user_credit);
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			list.add(Integer.parseInt(map.get(key).toString()));
		}
		Integer[] ints = list.toArray(new Integer[list.size()]);
		Arrays.sort(ints, new Comparator() {
			@Override
			public int compare(Object obj1, Object obj2) {
				// TODO Auto-generated method stub
				int a = CommUtil.null2Int(obj1);
				int b = CommUtil.null2Int(obj2);
				if (a == b) {
					return 0;
				} else {
					return a > b ? 1 : -1;
				}
			}
		});
		User user = this.userService.getObjById(CommUtil.null2Long(id));
		for (int i = 0; i < ints.length - 1; i++) {
			if (ints[i] <= user.getUser_credit()
					&& ints[i + 1] >= user.getUser_credit()) {
				credit = i + 1;
				break;
			}
		}
		if (user.getUser_credit() >= ints[ints.length - 1]) {
			credit = ints.length;
		}
		return credit;
	}

	/**
	 * 按照数量查询推荐店铺(明星店铺)
	 * 
	 * @param count
	 * @return
	 */
	public List<Store> query_recommend_store(int count) {
		List<Store> list = new ArrayList<Store>();
		Map params = new HashMap();
		params.put("recommend", true);
		list = this.storeService
				.query("select obj from Store obj where obj.store_recommend=:recommend order by obj.store_recommend_time desc",
						params, 0, count);
		return list;
	}

	/**
	 * 搜索店铺时在店铺列表页显示相应店铺的推荐商品,不足5个自动补5个Null值
	 * 
	 * @param begin
	 *            :推荐商品查询开始位置
	 * @param max
	 *            查询商品数量
	 * @return 商品列表
	 */
	public List<Goods> query_recommend_store_goods(Store store, int begin,
			int max) {
		Map params = new HashMap();
		params.put("recommend", true);
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_recommend=:recommend and obj.goods_status=:goods_status",
						params, begin, max);
		if (goods.size() < 5) {
			int count = 5 - goods.size();
			for (int i = 0; i < count; i++) {
				goods.add(null);
			}
		}
		return goods;
	}

	/**
	 * 查询店铺评价
	 * 
	 * @param store_id
	 * @param evaluate_val
	 * @param type
	 * @param date_symbol
	 * @param date_count
	 * @return
	 */
	public int query_evaluate(String store_id, int evaluate_val, String type,
			String date_symbol, int date_count) {
		Calendar cal = Calendar.getInstance();
		if (type.equals("date")) {
			cal.add(Calendar.DAY_OF_YEAR, date_count);
		}
		if (type.equals("week")) {
			cal.add(Calendar.WEEK_OF_YEAR, date_count);
		}
		if (type.equals("month")) {
			cal.add(Calendar.MONTH, date_count);
		}
		String symbol = ">=";
		if (date_symbol.equals("before")) {
			symbol = "<=";
		}
		Map params = new HashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("addTime", cal.getTime());
		params.put("evaluate_buyer_val", CommUtil.null2Int(evaluate_val));
		List<Evaluate> evas = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_goods.goods_store.id=:store_id and obj.evaluate_buyer_val=:evaluate_buyer_val and obj.addTime"
						+ symbol + ":addTime", params, -1, -1);
		return evas.size();
	}

	/**
	 * @param store
	 *            进行同行评分比较的店铺
	 * @return Map 评分信息，包括description_css:better，lower
	 *         样式名称，description_type：better为高出或者持平
	 *         lower为低于，description_result:比较得出的数值
	 */
	public Map query_point(Store store) {
		Map map = new HashMap();
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		if (store.getSc() != null) {
			StoreClass sc = this.storeClassService.getObjById(store.getSc()
					.getId());
			float description_evaluate = CommUtil.null2Float(sc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(sc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
			if (store.getPoint() != null) {
				float store_description_evaluate = CommUtil.null2Float(store
						.getPoint().getDescription_evaluate());
				float store_service_evaluate = CommUtil.null2Float(store
						.getPoint().getService_evaluate());
				float store_ship_evaluate = CommUtil.null2Float(store
						.getPoint().getShip_evaluate());
				// 计算和同行比较结果
				description_result = CommUtil.div(store_description_evaluate
						- description_evaluate, description_evaluate);
				service_result = CommUtil.div(store_service_evaluate
						- service_evaluate, service_evaluate);
				ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate,
						ship_evaluate);
			}
		}
		if (description_result > 0) {
			map.put("description_css", "better");
			map.put("description_type", "高于");
			map.put("description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100))
							+ "%");
		}
		if (description_result == 0) {
			map.put("description_css", "better");
			map.put("description_type", "持平");
			map.put("description_result", "-----");
		}
		if (description_result < 0) {
			map.put("description_css", "lower");
			map.put("description_type", "低于");
			map.put("description_result",
					CommUtil.null2String(CommUtil.mul(-description_result, 100))
							+ "%");
		}
		if (service_result > 0) {
			map.put("service_css", "better");
			map.put("service_type", "高于");
			map.put("service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			map.put("service_css", "better");
			map.put("service_type", "持平");
			map.put("service_result", "-----");
		}
		if (service_result < 0) {
			map.put("service_css", "lower");
			map.put("service_type", "低于");
			map.put("service_result",
					CommUtil.null2String(CommUtil.mul(-service_result, 100))
							+ "%");
		}
		if (ship_result > 0) {
			map.put("ship_css", "better");
			map.put("ship_type", "高于");
			map.put("ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100)) + "%");
		}
		if (ship_result == 0) {
			map.put("ship_css", "better");
			map.put("ship_type", "持平");
			map.put("ship_result", "-----");
		}
		if (ship_result < 0) {
			map.put("ship_css", "lower");
			map.put("ship_type", "低于");
			map.put("ship_result",
					CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
		return map;
	}
}
