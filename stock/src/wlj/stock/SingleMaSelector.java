package wlj.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.baic.bcl.util.DateUtils;

import wlj.stock.ConnectionManager;
import wlj.stock.MaSelector;
import wlj.stock.Transaction;

/**
 * 高于20日平均线时买入，低于20平均线时卖出，查看最终是否盈利
 * 
 * @author wanglongjiang
 *
 */
public class SingleMaSelector {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		String stockId = "SZ#399395";
		String maType = "ma20";
		String separtor = "\t";
		try (Connection conn = ConnectionManager.getConnection();) {
			List<Transaction> transactions = new MaSelector(maType, conn).query(stockId);
			if (transactions.isEmpty()) {
				System.err.println("没有找到股票！");
				return;
			}
			BigDecimal firstBuyingPrice = transactions.get(0).getBuyingPrice();
			System.out.println("买入日期\t卖出日期\t买入价\t卖出价\t利润");
			BigDecimal totalProfit = transactions.stream().map(t -> {
				StringBuilder sb = new StringBuilder();
				sb.append(DateUtils.toStr(t.getBuyingDate())).append(separtor);
				sb.append(DateUtils.toStr(t.getSellingDate())).append(separtor);
				sb.append(t.getBuyingPrice()).append(separtor);
				sb.append(t.getSellingPrice()).append(separtor);
				sb.append(t.getProfit());
				System.out.println(sb);
				return t.getProfit();
			}).reduce((a, b) -> {
				return a.add(b);
			}).get();
			System.out.println("总利润：" + totalProfit + ",倍数：" + totalProfit.divide(firstBuyingPrice, RoundingMode.HALF_UP));
		}
	}
}
