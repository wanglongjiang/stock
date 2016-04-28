package wlj.stock;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

	private BigDecimal buyingPrice;
	private Date buyingDate;
	private BigDecimal sellingPrice;
	private Date sellingDate;
	private BigDecimal profit;

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

}
