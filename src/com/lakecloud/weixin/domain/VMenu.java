package com.lakecloud.weixin.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;
import com.lakecloud.foundation.domain.Store;

/**
 * @info 微信商城菜单管理类，绑定微信服务账号，生成微信App菜单
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "vmenu")
public class VMenu extends IdEntity {
	private String menu_type;// click或者view
	private String menu_name;// 菜单名称
	private String menu_key;// 菜单key
	@Column(columnDefinition = "LongText")
	private String menu_key_content;// 菜单key对应的文字内容
	private String menu_url;// 菜单url
	@Column(columnDefinition = "int default 0")
	private int menu_sequence;// 菜单序号，正序排列
	@ManyToOne
	private VMenu parent;// 父级菜单
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@OrderBy(value = "menu_sequence")
	private List<VMenu> childs = new ArrayList<VMenu>();// 子菜单
	@ManyToOne
	private Store store;// 菜单对应的店铺
	@Column(columnDefinition = "varchar(255) default 'user'")
	private String menu_cat;// 菜单类型，默认为user，表示卖家店铺的菜单，admin表示大平台的微商城菜单

	public String getMenu_cat() {
		return menu_cat;
	}

	public void setMenu_cat(String menu_cat) {
		this.menu_cat = menu_cat;
	}

	public String getMenu_key_content() {
		return menu_key_content;
	}

	public void setMenu_key_content(String menu_key_content) {
		this.menu_key_content = menu_key_content;
	}

	public int getMenu_sequence() {
		return menu_sequence;
	}

	public void setMenu_sequence(int menu_sequence) {
		this.menu_sequence = menu_sequence;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getMenu_type() {
		return menu_type;
	}

	public void setMenu_type(String menu_type) {
		this.menu_type = menu_type;
	}

	public String getMenu_name() {
		return menu_name;
	}

	public void setMenu_name(String menu_name) {
		this.menu_name = menu_name;
	}

	public String getMenu_key() {
		return menu_key;
	}

	public void setMenu_key(String menu_key) {
		this.menu_key = menu_key;
	}

	public String getMenu_url() {
		return menu_url;
	}

	public void setMenu_url(String menu_url) {
		this.menu_url = menu_url;
	}

	public VMenu getParent() {
		return parent;
	}

	public void setParent(VMenu parent) {
		this.parent = parent;
	}

	public List<VMenu> getChilds() {
		return childs;
	}

	public void setChilds(List<VMenu> childs) {
		this.childs = childs;
	}

}
