package wlj.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.baic.bcl.util.DateUtils;
import com.baic.bcl.util.NumberUtils;

/**
 * 高于X日平均线时买入，低于X平均线时卖出，查看最终是否盈利
 * 
 * @author wanglongjiang
 *
 */
public class MulitMaSelector {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		String stockIdLike = "29#%";
		String maType = "ma20";
		String separtor = "\t";
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement queryStock = conn
						.prepareStatement("select stock_id,stock_name from stock where stock_id like ?");) {
			queryStock.setString(1, stockIdLike);
			ResultSet rs = queryStock.executeQuery();
			boolean hasStock = false;
			System.out.println("股票名称\t股票代码\t买入日期\t卖出日期\t持有天数\t最开始买入价\t最终卖出价\t交易次数\t总利润\t倍数\t年化收益率");
			while (rs.next()) {
				hasStock = true;
				String stockId = rs.getString(1);
				String stockName = rs.getString(2);
				List<Transaction> transactions = new MaSelector(maType, conn).query(stockId);
				if (transactions.isEmpty()) {
					System.err.println("没有找到股票符合条件的交易记录\t" + stockId + " " + stockName);
					return;
				}
				BigDecimal principal = transactions.get(0).getBuyingPrice();
				Date firstBuyingDate = transactions.get(0).getBuyingDate();
				BigDecimal lastSellingPrice = transactions.get(transactions.size() - 1).getSellingPrice();
				Date lastSellingDate = transactions.get(transactions.size() - 1).getSellingDate();
				BigDecimal totalProfit = transactions.stream().map(t -> {
					return t.getProfit();
				}).reduce((a, b) -> {
					return a.add(b);
				}).get();
				StringBuilder sb = new StringBuilder();
				sb.append(stockName).append(separtor);
				sb.append(stockId).append(separtor);
				sb.append(DateUtils.toStr(firstBuyingDate)).append(separtor);
				sb.append(DateUtils.toStr(lastSellingDate)).append(separtor);
				sb.append(DateUtils.daysOfBetween2(firstBuyingDate, lastSellingDate)).append(separtor);
				sb.append(principal).append(separtor);
				sb.append(lastSellingPrice).append(separtor);
				sb.append(transactions.size()).append(separtor);
				sb.append(totalProfit).append(separtor);
				if (!NumberUtils.eq(principal.floatValue(), 0)) {
					BigDecimal returnRate = totalProfit.divide(principal, RoundingMode.HALF_UP);
					sb.append(returnRate).append(separtor);
					BigDecimal ar = computeAR(firstBuyingDate, lastSellingDate, returnRate);
					if (ar == null) {
						sb.append("计算错误").append(separtor);
					} else {
						sb.append(ar).append("%").append(separtor);
					}
				} else {
					sb.append(0).append(separtor);
					sb.append(0).append("%").append(separtor);
				}
				System.out.println(sb);
			}
			if (!hasStock) {
				System.err.println("没有找到任何符合条件的股票。");
			}
		}
	}

	/**
	 * 计算年化收益率
	 * 
	 * @param firstBuyingDate
	 * @param lastSellingDate
	 * @param returnRate
	 * @return
	 */
	private static BigDecimal computeAR(Date firstBuyingDate, Date lastSellingDate, BigDecimal returnRate) {
		int days = DateUtils.daysOfBetween2(firstBuyingDate, lastSellingDate);
		// 每年投资次数=365/投资周期
		BigDecimal invOfYear = BigDecimal.valueOf(365).divide(BigDecimal.valueOf(days), 4, RoundingMode.HALF_UP);
		// 年化收益率=(1+收益率)^每年投资次数-1
		double pow = Math.pow(returnRate.add(BigDecimal.valueOf(1)).doubleValue(), invOfYear.doubleValue());
		if (Double.isNaN(pow)) {
			return null;
		}
		BigDecimal ar = BigDecimal.valueOf(pow).subtract(BigDecimal.valueOf(1));
		return ar.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	}
}
