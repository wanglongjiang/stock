package wlj.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Queue;

import com.baic.bcl.util.NumberUtils;

/**
 * 计算移动平均价
 * 
 * @author wanglongjiang
 *
 */
public class MaCompute {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement queryStockDaily = conn.prepareStatement(
						"select stock_id,stock_date,closing from stock_daily where ma5 is null order by stock_id,stock_date ");
				PreparedStatement countStockDaily = conn
						.prepareStatement("select count(*) from stock");
				PreparedStatement updateStockDaily = conn.prepareStatement(
						"update stock_daily set ma5=?,ma10=?,ma20=?,ma30=?,ma60=?,ma120=? where stock_id=? and stock_date=? ");) {
			conn.setAutoCommit(true);
			ResultSet rsc = countStockDaily.executeQuery();
			rsc.next();
			long total = rsc.getLong(1);
			System.out.println("开始处理，共" + total + "个股票。");
			ResultSet rs = queryStockDaily.executeQuery();
			String currentStockId = null;
			Ma ma5 = null;
			Ma ma10 = null;
			Ma ma20 = null;
			Ma ma30 = null;
			Ma ma60 = null;
			Ma ma120 = null;
			float i = 0;
			while (rs.next()) {
				String stockId = rs.getString(1);
				Date stockDate = rs.getDate(2);
				BigDecimal closing = rs.getBigDecimal(3);
				if (!stockId.equals(currentStockId)) {
					i += 1;
					String progress = NumberUtils.format2(i / total * 100);
					System.out.println("当前股票：" + stockId + " 进度：" + progress + "%");
					ma5 = new Ma(5);
					ma10 = new Ma(10);
					ma20 = new Ma(20);
					ma30 = new Ma(30);
					ma60 = new Ma(60);
					ma120 = new Ma(120);
					currentStockId = stockId;
				}
				updateStockDaily.setBigDecimal(1, ma5.add(closing));
				updateStockDaily.setBigDecimal(2, ma10.add(closing));
				updateStockDaily.setBigDecimal(3, ma20.add(closing));
				updateStockDaily.setBigDecimal(4, ma30.add(closing));
				updateStockDaily.setBigDecimal(5, ma60.add(closing));
				updateStockDaily.setBigDecimal(6, ma120.add(closing));
				updateStockDaily.setString(7, stockId);
				updateStockDaily.setDate(8, stockDate);
				updateStockDaily.executeUpdate();
			}
			System.out.println("计算完成！");
		}
	}

	private static class Ma {
		private int maxLength;
		private Queue<BigDecimal> queue;
		private BigDecimal total = BigDecimal.ZERO;

		Ma(int maxLength) {
			this.maxLength = maxLength;
			queue = new ArrayDeque<>(maxLength);
		}

		BigDecimal add(BigDecimal val) {
			if (queue.size() == maxLength) {
				BigDecimal first = queue.remove();
				queue.add(val);
				total = total.subtract(first).add(val);
			} else {
				queue.add(val);
				total = total.add(val);
			}
			return total.divide(BigDecimal.valueOf(queue.size()), 3, RoundingMode.HALF_UP);
		}

	}
}
