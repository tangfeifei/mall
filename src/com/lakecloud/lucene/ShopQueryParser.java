package com.lakecloud.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.util.Version;

import com.lakecloud.core.tools.CommUtil;

/**
 * 自定义lucene查询分析器，支持区间查询
 * 
 * @author erikchang
 * 
 */
public class ShopQueryParser extends QueryParser {
	public ShopQueryParser(Version matchVersion, String f, Analyzer a) {
		super(matchVersion, f, a);
	}

	@Override
	protected org.apache.lucene.search.Query getRangeQuery(String field,
			String part1, String part2, boolean inclusive)
			throws ParseException {
		if ("store_price".equals(field)) {
			return NumericRangeQuery.newDoubleRange(field, CommUtil
					.null2Double(part1), CommUtil.null2Double(part2),
					inclusive, inclusive);
		}
		if ("add_time".equals(field)) {
			return NumericRangeQuery.newLongRange(field, Long.parseLong(part1),
					Long.parseLong(part2), inclusive, inclusive);
		}
		return super.newRangeQuery(field, part1, part2, inclusive);
	}
}
