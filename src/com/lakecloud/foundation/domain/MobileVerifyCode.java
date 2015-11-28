package com.lakecloud.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 手机验证码保存,该实体不需要缓存，存在即时性,系统开通短信发送后，用户修改手机号码，可以使用手机短信验证用户的合法性
 
 * 
 */
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "mobileverifycode")
public class MobileVerifyCode extends IdEntity {
	private String mobile;
	private String code;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
