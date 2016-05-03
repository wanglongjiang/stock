package wlj.stock;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 高于X日平均线时买入，低于X日平均线时卖出，查看最终是否盈利
 * 
 * @author wanglongjiang
 *
 */
public class MaSelector {
	/**
	 * 移动平均价类型
	 */
	private String maType;

	private String startDate;
	private String endDate;

	private Connection conn;

	public MaSelector(String maType, Connection conn) {
		this.maType = maType;
		this.conn = conn;
	}

	/**
	 * 查询符合条件的交易
	 * 
	 * @param stockId
	 * @return
	 * @throws SQLException
	 */
	public List<Transaction> query(String stockId) throws SQLException {
		try (PreparedStatement queryStock = conn.prepareStatement("select closing," + maType
				+ ",stock_date,highest,lowest from stock_daily where stock_id=? order by stock_id,stock_date");) {
			queryStock.setString(1, stockId);
			ResultSet rs = queryStock.executeQuery();
			boolean purchased = false;
			Transaction transaction = null;
			ArrayList<Transaction> transactions = new ArrayList<>();
			BigDecimal closing = null;
			BigDecimal ma = null;
			Date stockDate = null;
			BigDecimal highest = null;
			BigDecimal lowest = null;
			while (rs.next()) {
				closing = rs.getBigDecimal(1);
				ma = rs.getBigDecimal(2);
				stockDate = rs.getDate(3);
				highest = rs.getBigDecimal(4);
				lowest = rs.getBigDecimal(5);
				// 未购买
				if (!purchased) {
					// 收盘价高于X日线，买入
					if (closing.compareTo(ma) > 0) {
						transaction = new Transaction();
						transactions.add(transaction);
						transaction.setBuyingDate(stockDate);
						transaction.setBuyingPrice(closing);

						transaction.setHighestPrice(closing);
						transaction.setLowestPrice(closing);
						purchased = true;
					}
				}
				// 已购买
				else {

					// 设置最高价、最低价
					if (transaction.getHighestPrice().compareTo(highest) < 0) {
						transaction.setHighestPrice(highest);
					}
					if (transaction.getLowestPrice().compareTo(lowest) > 0) {
						transaction.setLowestPrice(lowest);
					}
					// 收盘价低于X日线，卖出
					if (closing.compareTo(ma) < 0) {
						transaction.setSellingDate(stockDate);
						transaction.setSellingPrice(closing);
						transaction.setProfit(transaction.getSellingPrice().subtract(transaction.getBuyingPrice()));
						purchased = false;
					}
				}
			}
			if (purchased) {
				transaction.setSellingDate(stockDate);
				transaction.setSellingPrice(closing);
				transaction.setProfit(transaction.getSellingPrice().subtract(transaction.getBuyingPrice()));
			}
			return transactions;
		}

	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
