package com.lakecloud.foundation.dao;

import org.springframework.stereotype.Repository;

import com.lakecloud.core.base.GenericDAO;
import com.lakecloud.foundation.domain.Role;

@Repository("roleDAO")
public class RoleDAO extends GenericDAO<Role> {
}
