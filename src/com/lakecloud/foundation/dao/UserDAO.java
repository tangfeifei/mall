package com.lakecloud.foundation.dao;

import org.springframework.stereotype.Repository;

import com.lakecloud.core.base.GenericDAO;
import com.lakecloud.foundation.domain.User;

@Repository("userDAO")
public class UserDAO extends GenericDAO<User> {

}
