package com.lakecloud.foundation.dao;
import org.springframework.stereotype.Repository;
import com.lakecloud.core.base.GenericDAO;
import com.lakecloud.foundation.domain.Article;
@Repository("articleDAO")
public class ArticleDAO extends GenericDAO<Article> {

}