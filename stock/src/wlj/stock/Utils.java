package wlj.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.baic.bcl.util.DateUtils;

public class Utils {
	/**
	 * 计算年化收益率
	 * 
	 * @param firstBuyingDate
	 * @param lastSellingDate
	 * @param returnRate
	 * @return
	 */
	public static BigDecimal computeAR(Date firstBuyingDate, Date lastSellingDate, BigDecimal returnRate) {
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
