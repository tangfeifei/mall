package com.lakecloud.view.web.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GoodsSpecification;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IUserGoodsClassService;

@Component
public class GoodsViewTools {
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;

	/**
	 * 将商品属性归类,便于前台显示
	 * 
	 * @param id
	 * @return
	 */
	public List<GoodsSpecification> generic_spec(String id) {
		List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();
		if (id != null && !id.equals("")) {
			Goods goods = this.goodsService.getObjById(Long.parseLong(id));
			for (GoodsSpecProperty gsp : goods.getGoods_specs()) {
				GoodsSpecification spec = gsp.getSpec();
				if (!specs.contains(spec)) {
					specs.add(spec);
				}
			}
		}
		java.util.Collections.sort(specs, new Comparator<GoodsSpecification>() {

			@Override
			public int compare(GoodsSpecification gs1, GoodsSpecification gs2) {
				// TODO Auto-generated method stub
				return gs1.getSequence() - gs2.getSequence();
			}
		});

		return specs;
	}

	/**
	 * 查询用户商品分类信息
	 * 
	 * @param pid
	 * @return
	 */
	public List<UserGoodsClass> query_user_class(String pid) {
		List<UserGoodsClass> list = new ArrayList<UserGoodsClass>();
		if (pid == null || pid.equals("")) {
			Map map = new HashMap();
			map.put("uid", SecurityUserHolder.getCurrentUser().getId());
			list = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.parent.id is null and obj.user.id = :uid order by obj.sequence asc",
							map, -1, -1);
		} else {
			Map params = new HashMap();
			params.put("pid", Long.parseLong(pid));
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			list = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.parent.id=:pid and obj.user.id = :uid order by obj.sequence asc",
							params, -1, -1);
		}
		return list;
	}

	/**
	 * 根据商城分类查询对应的商品
	 * 
	 * @param gc_id
	 *            商城分类id
	 * @param count
	 *            需要查询的数量
	 * @return
	 */
	public List<Goods> query_with_gc(String gc_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		if (gc != null) {
			Set<Long> ids = this.genericIds(gc);
			Map params = new HashMap();
			params.put("ids", ids);
			params.put("goods_status", 0);
			list = this.goodsService
					.query("select obj from Goods obj where obj.gc.id in (:ids) and obj.goods_status=:goods_status order by obj.goods_click desc",
							params, 0, count);
		}
		return list;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	public List<Goods> sort_sale_goods(String store_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("goods_status", 0);
		list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, 0, count);
		return list;
	}

	public List<Goods> sort_collect_goods(String store_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("goods_status", 0);
		list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
						params, 0, count);
		return list;
	}

	public List<Goods> query_combin_goods(String id) {
		return this.goodsService.getObjById(CommUtil.null2Long(id))
				.getCombin_goods();
	}
}
