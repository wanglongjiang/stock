package wlj.stock;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.baic.bcl.util.DateUtils;

import wlj.stock.ConnectionManager;
import wlj.stock.MaSelector;
import wlj.stock.Transaction;

/**
 * 单个股票选择程序 高于20日平均线时买入，低于20平均线时卖出，查看最终是否盈利
 * 
 * @author wanglongjiang
 *
 */
public class SingleMaSelector {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		String stockId = "SZ#399395";
		String maType = "ma5";
		String separtor = "\t";
		try (Connection conn = ConnectionManager.getConnection();) {
			List<Transaction> transactions = new MaSelector(maType, conn).query(stockId);
			if (transactions.isEmpty()) {
				System.err.println("没有找到股票！");
				return;
			}
			BigDecimal firstBuyingPrice = transactions.get(0).getBuyingPrice();
			System.out.println("买入日期\t卖出日期\t买入价\t卖出价\t利润\t利润百分比");
			BigDecimal totalProfit = transactions.stream().map(t -> {
				StringBuilder sb = new StringBuilder();
				sb.append(DateUtils.toStr(t.getBuyingDate())).append(separtor);
				sb.append(DateUtils.toStr(t.getSellingDate())).append(separtor);
				sb.append(t.getBuyingPrice()).append(separtor);
				sb.append(t.getSellingPrice()).append(separtor);
				sb.append(t.getProfit()).append(separtor);
				sb.append(
						t.getProfit().multiply(BigDecimal.valueOf(100)).divide(t.getBuyingPrice(), 2, RoundingMode.HALF_UP))
						.append("%").append(separtor);
				System.out.println(sb);
				return t.getProfit();
			}).reduce((a, b) -> {
				return a.add(b);
			}).get();
			System.out.println("总利润：" + totalProfit + ",倍数：" + totalProfit.divide(firstBuyingPrice, RoundingMode.HALF_UP));
			BigDecimal principal = transactions.get(0).getBuyingPrice();
			Date firstBuyingDate = transactions.get(0).getBuyingDate();
			Date lastSellingDate = transactions.get(transactions.size() - 1).getSellingDate();
			BigDecimal returnRate = totalProfit.divide(principal, RoundingMode.HALF_UP);
			BigDecimal ar = Utils.computeAR(firstBuyingDate, lastSellingDate, returnRate);
			System.out.println("年化收益率：" + ar + "%");
		}
	}
}
