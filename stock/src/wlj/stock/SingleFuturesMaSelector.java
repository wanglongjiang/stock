package wlj.stock;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baic.bcl.util.DateUtils;

import wlj.stock.ConnectionManager;
import wlj.stock.MaSelector;
import wlj.stock.Transaction;

/**
 * 单个期货选择程序，高于20日平均线时买入，低于20平均线时卖出，查看最终是否盈利
 * 
 * @author wanglongjiang
 *
 */
public class SingleFuturesMaSelector {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		String stockId = "SH#600887";
		int lever = 20; // 杠杆倍数
		String maType = "ma5";
		String separtor = "\t";
		BigDecimal lev = BigDecimal.valueOf(lever);
		ArrayList<Date> loseDate = new ArrayList<>();
		System.err.println("单个期货选择程序，买多。");
		try (Connection conn = ConnectionManager.getConnection();) {
			List<Transaction> transactions = new MaSelector(maType, conn).query(stockId);
			if (transactions.isEmpty()) {
				System.err.println("没有找到股票！");
				return;
			}
			BigDecimal firstBuyingPrice = transactions.get(0).getBuyingPrice();
			System.out.println("买入日期\t卖出日期\t买入价\t卖出价\t利润\t利润百分比\t是否爆仓\t最低");
			BigDecimal totalProfit = transactions.stream().map(t -> {
				StringBuilder sb = new StringBuilder();
				sb.append(DateUtils.toStr(t.getBuyingDate())).append(separtor);
				sb.append(DateUtils.toStr(t.getSellingDate())).append(separtor);
				sb.append(t.getBuyingPrice()).append(separtor);
				sb.append(t.getSellingPrice()).append(separtor);
				sb.append(t.getProfit().multiply(lev)).append(separtor);
				BigDecimal profitRate = t.getProfit().multiply(BigDecimal.valueOf(100 * lever)).divide(t.getBuyingPrice(), 2,
						RoundingMode.HALF_UP);
				sb.append(profitRate).append("%").append(separtor);
				// 是否爆仓，如果买多，为：(买入价-最低)*杠杆>买入价
				boolean lose = false;
				if (t.getBuyingPrice().subtract(t.getLowestPrice()).multiply(lev).compareTo(t.getBuyingPrice()) > 0) {
					lose = true;
					loseDate.add(t.getBuyingDate());
				}
				if (lose) {
					sb.append("是").append(separtor);
					sb.append(t.getLowestPrice()).append(separtor);
					System.err.println(sb);
				} else {
					sb.append("").append(separtor);
					System.out.println(sb);
				}
				return t.getProfit().multiply(lev);
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
			if (!loseDate.isEmpty()) {
				System.err.println("发生爆仓！！！爆仓交易的买入日期为：");
				for (Date date : loseDate) {
					System.err.println(DateUtils.toStr(date));
				}
			}
		}
	}
}
