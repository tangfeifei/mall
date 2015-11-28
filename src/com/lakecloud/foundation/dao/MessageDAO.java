package com.lakecloud.foundation.dao;
import org.springframework.stereotype.Repository;
import com.lakecloud.core.base.GenericDAO;
import com.lakecloud.foundation.domain.Message;
@Repository("messageDAO")
public class MessageDAO extends GenericDAO<Message> {

}