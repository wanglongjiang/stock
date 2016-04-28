package wlj.stock;

import java.math.BigDecimal;

public class StockDaily {

	private String stockId;
	private java.sql.Date stockDate;
	private BigDecimal opening;
	private BigDecimal highest;
	private BigDecimal lowest;
	private BigDecimal closing;
	private Long volume;
	private BigDecimal turnover;

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
	 * @return the stockDate
	 */
	public java.sql.Date getStockDate() {
		return stockDate;
	}

	/**
	 * @param stockDate
	 *            the stockDate to set
	 */
	public void setStockDate(java.sql.Date stockDate) {
		this.stockDate = stockDate;
	}

	/**
	 * @return the opening
	 */
	public BigDecimal getOpening() {
		return opening;
	}

	/**
	 * @param opening
	 *            the opening to set
	 */
	public void setOpening(BigDecimal opening) {
		this.opening = opening;
	}

	/**
	 * @return the highest
	 */
	public BigDecimal getHighest() {
		return highest;
	}

	/**
	 * @param highest
	 *            the highest to set
	 */
	public void setHighest(BigDecimal highest) {
		this.highest = highest;
	}

	/**
	 * @return the lowest
	 */
	public BigDecimal getLowest() {
		return lowest;
	}

	/**
	 * @param lowest
	 *            the lowest to set
	 */
	public void setLowest(BigDecimal lowest) {
		this.lowest = lowest;
	}

	/**
	 * @return the closing
	 */
	public BigDecimal getClosing() {
		return closing;
	}

	/**
	 * @param closing
	 *            the closing to set
	 */
	public void setClosing(BigDecimal closing) {
		this.closing = closing;
	}

	/**
	 * @return the volume
	 */
	public Long getVolume() {
		return volume;
	}

	/**
	 * @param volume
	 *            the volume to set
	 */
	public void setVolume(Long volume) {
		this.volume = volume;
	}

	/**
	 * @return the turnover
	 */
	public BigDecimal getTurnover() {
		return turnover;
	}

	/**
	 * @param turnover
	 *            the turnover to set
	 */
	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}

}
