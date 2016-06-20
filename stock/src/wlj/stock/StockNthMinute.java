package wlj.stock;

import java.math.BigDecimal;

public class StockNthMinute {

	private String stockId;
	private java.sql.Date stockDate;
	private java.sql.Time stockTime;
	private BigDecimal opening;
	private BigDecimal highest;
	private BigDecimal lowest;
	private BigDecimal closing;
	private Long volume;
	private BigDecimal turnover;

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public java.sql.Date getStockDate() {
		return stockDate;
	}

	public void setStockDate(java.sql.Date stockDate) {
		this.stockDate = stockDate;
	}

	public java.sql.Time getStockTime() {
		return stockTime;
	}

	public void setStockTime(java.sql.Time stockTime) {
		this.stockTime = stockTime;
	}

	public BigDecimal getOpening() {
		return opening;
	}

	public void setOpening(BigDecimal opening) {
		this.opening = opening;
	}

	public BigDecimal getHighest() {
		return highest;
	}

	public void setHighest(BigDecimal highest) {
		this.highest = highest;
	}

	public BigDecimal getLowest() {
		return lowest;
	}

	public void setLowest(BigDecimal lowest) {
		this.lowest = lowest;
	}

	public BigDecimal getClosing() {
		return closing;
	}

	public void setClosing(BigDecimal closing) {
		this.closing = closing;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public BigDecimal getTurnover() {
		return turnover;
	}

	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}
}
