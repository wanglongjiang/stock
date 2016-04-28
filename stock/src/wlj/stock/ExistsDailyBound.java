package wlj.stock;

import java.util.Date;

/**
 * 已有的日线数据上下限
 * 
 * @author wanglongjiang
 *
 */
public class ExistsDailyBound {

	private String stockId;
	private Date upperBound;
	private Date lowerBound;

	public ExistsDailyBound(String stockId, Date upperBound, Date lowerBound) {
		this.stockId = stockId;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	/**
	 * @return the stockId
	 */
	public String getStockId() {
		return stockId;
	}

	/**
	 * @param stockId
	 *            the stockId to set
	 */
	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	/**
	 * @return the upperBound
	 */
	public Date getUpperBound() {
		return upperBound;
	}

	/**
	 * @param upperBound
	 *            the upperBound to set
	 */
	public void setUpperBound(Date upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * @return the lowerBound
	 */
	public Date getLowerBound() {
		return lowerBound;
	}

	/**
	 * @param lowerBound
	 *            the lowerBound to set
	 */
	public void setLowerBound(Date lowerBound) {
		this.lowerBound = lowerBound;
	}
}
