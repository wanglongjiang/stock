package wlj.stock;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

	/**
	 * 买入价
	 */
	private BigDecimal buyingPrice;

	/**
	 * 买入日期
	 */
	private Date buyingDate;

	/**
	 * 卖出价
	 */
	private BigDecimal sellingPrice;

	/**
	 * 卖出日期
	 */
	private Date sellingDate;

	/**
	 * 利润
	 */
	private BigDecimal profit;

	/**
	 * 最高价
	 */
	private BigDecimal highestPrice;

	/**
	 * 最低价
	 */
	private BigDecimal lowestPrice;

	/**
	 * @return the buyingPrice
	 */
	public BigDecimal getBuyingPrice() {
		return buyingPrice;
	}

	/**
	 * @param buyingPrice
	 *            the buyingPrice to set
	 */
	public void setBuyingPrice(BigDecimal buyingPrice) {
		this.buyingPrice = buyingPrice;
	}

	/**
	 * @return the buyingDate
	 */
	public Date getBuyingDate() {
		return buyingDate;
	}

	/**
	 * @param buyingDate
	 *            the buyingDate to set
	 */
	public void setBuyingDate(Date buyingDate) {
		this.buyingDate = buyingDate;
	}

	/**
	 * @return the sellingPrice
	 */
	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}

	/**
	 * @param sellingPrice
	 *            the sellingPrice to set
	 */
	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	/**
	 * @return the sellingDate
	 */
	public Date getSellingDate() {
		return sellingDate;
	}

	/**
	 * @param sellingDate
	 *            the sellingDate to set
	 */
	public void setSellingDate(Date sellingDate) {
		this.sellingDate = sellingDate;
	}

	/**
	 * @return the profit
	 */
	public BigDecimal getProfit() {
		return profit;
	}

	/**
	 * @param profit
	 *            the profit to set
	 */
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	/**
	 * @return the highestPrice
	 */
	public BigDecimal getHighestPrice() {
		return highestPrice;
	}

	/**
	 * @param highestPrice the highestPrice to set
	 */
	public void setHighestPrice(BigDecimal highestPrice) {
		this.highestPrice = highestPrice;
	}

	/**
	 * @return the lowestPrice
	 */
	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}

	/**
	 * @param lowestPrice the lowestPrice to set
	 */
	public void setLowestPrice(BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}
}
