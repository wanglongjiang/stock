package wlj.stock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baic.BaicException;
import com.baic.bcl.Filter;
import com.baic.bcl.util.BigDecimalUtil;
import com.baic.bcl.util.DateUtils;
import com.baic.bcl.util.FileUtils;
import com.baic.bcl.util.NumberUtils;

/**
 * 通达信数据导入
 * 
 * @author wanglongjiang
 *
 */
public class TdxImporter {

	private static final Pattern tdxFileName = Pattern.compile("^(?<id>(?<exchange>[A-Z]{2})#(?<code>\\d{6}))\\.txt$");
	private static final Pattern tdxFileData = Pattern.compile(
			"(?<date>[^,]{10}),(?<opening>[^,]+),(?<highest>[^,]+),(?<lowest>[^,]+),(?<closing>[^,]+),(?<volume>[^,]+),(?<turnover>[^,]+)");

	private String folderPath;
	private Map<String, ExistsDailyBound> boundMap = new TreeMap<>();

	public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
		TdxImporter importer = new TdxImporter("/Users/wanglongjiang/Desktop/export");
		importer.startImport();
	}

	public TdxImporter(String folderPath) {
		this.folderPath = folderPath;
	}

	private void startImport()
			throws SQLException, IOException, UnsupportedEncodingException, FileNotFoundException, ClassNotFoundException {
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement queryStock = conn.prepareStatement("select 1 from stock where stock_id=?");
				PreparedStatement insertStock = conn
						.prepareStatement("insert into stock(stock_id,stock_name,stock_code,exchange) values(?,?,?,?) ");
				PreparedStatement updateStock = conn.prepareStatement("update stock set stock_name=? where stock_id=? ");
				PreparedStatement insertDaily = conn.prepareStatement(
						"insert into stock_daily(stock_id,stock_date,opening,closing,highest,lowest,volume,turnover) values(?,?,?,?,?,?,?,?)");
				PreparedStatement queryDailyBound = conn.prepareStatement(
						"select stock_id,max(stock_date),min(stock_date) from stock_daily group by stock_id");) {

			conn.setAutoCommit(true);
			initBoundMap(queryDailyBound);

			Collection<File> files = getFiles();

			System.out.println("开始处理，共" + files.size() + "个文件。");

			importFiles(queryStock, insertStock, updateStock, insertDaily, files);
			System.out.println("导入完成!");
		}
	}

	private void importFiles(PreparedStatement queryStock, PreparedStatement insertStock, PreparedStatement updateStock,
			PreparedStatement insertDaily, Collection<File> files)
					throws IOException, SQLException, UnsupportedEncodingException, FileNotFoundException {
		float i = 0;
		for (File file : files) {
			i += 1;
			String progress = NumberUtils.format2(i / files.size() * 100);
			System.out.println("当前文件：" + file.getName() + " 进度：" + progress + "%");

			Matcher matcher = tdxFileName.matcher(file.getName());
			if (matcher.find()) {
				Stock stock = new Stock();
				stock.setId(matcher.group("id"));
				stock.setExchange(matcher.group("exchange"));
				stock.setCode(matcher.group("code"));
				try (LineNumberReader reader = new LineNumberReader(
						new InputStreamReader(new FileInputStream(file), "GBK"))) {
					String line = null;
					for (int lineNum = 0; (line = reader.readLine()) != null; lineNum++) {
						if (lineNum == 0) {
							saveOrUpdateStock(queryStock, updateStock, insertStock, file, stock, line);
						} else {
							StockDaily stockDaily = parseDailyData(stock, line);
							if (stockDaily != null) {
								saveStockDaily(insertDaily, stockDaily);
							}
						}
					}
					// conn.commit();
				}
			}
		}
	}

	private Collection<File> getFiles() {
		Collection<File> files = FileUtils.listFiles(new File(folderPath), new Filter<File>() {
			@Override
			public boolean accept(File e) {
				return tdxFileName.matcher(e.getName()).matches();
			}
		});
		return files;
	}

	private void initBoundMap(PreparedStatement queryDailyBound) throws SQLException {
		ResultSet rs = queryDailyBound.executeQuery();
		while (rs.next()) {
			ExistsDailyBound bound = new ExistsDailyBound(rs.getString(1), rs.getDate(2), rs.getDate(3));
			boundMap.put(bound.getStockId(), bound);
		}
	}

	private void saveStockDaily(PreparedStatement insertDaily, StockDaily stockDaily) throws SQLException {
		insertDaily.setString(1, stockDaily.getStockId());
		insertDaily.setDate(2, stockDaily.getStockDate());
		insertDaily.setBigDecimal(3, stockDaily.getOpening());
		insertDaily.setBigDecimal(4, stockDaily.getClosing());
		insertDaily.setBigDecimal(5, stockDaily.getHighest());
		insertDaily.setBigDecimal(6, stockDaily.getLowest());
		insertDaily.setLong(7, stockDaily.getVolume());
		insertDaily.setBigDecimal(8, stockDaily.getTurnover());
		insertDaily.executeUpdate();
	}

	private StockDaily parseDailyData(Stock stock, String line) {
		Matcher m = tdxFileData.matcher(line);
		if (m.find()) {
			StockDaily stockDaily = new StockDaily();
			stockDaily.setStockId(stock.getId());
			Date stockDate = DateUtils.toDate(m.group("date"));
			if (dailyExists(stock.getId(), stockDate)) {
				return null;
			}
			stockDaily.setStockDate(new java.sql.Date(stockDate.getTime()));
			stockDaily.setOpening(BigDecimalUtil.parse(m.group("opening")));
			stockDaily.setHighest(BigDecimalUtil.parse(m.group("highest")));
			stockDaily.setLowest(BigDecimalUtil.parse(m.group("lowest")));
			stockDaily.setClosing(BigDecimalUtil.parse(m.group("closing")));
			stockDaily.setVolume(Long.valueOf(m.group("volume")));
			stockDaily.setTurnover(BigDecimalUtil.parse(m.group("turnover")));
			return stockDaily;
		} else {
			return null;
		}
	}

	private boolean dailyExists(String id, Date stockDate) {
		ExistsDailyBound dailyBound = boundMap.get(id);
		if (dailyBound != null) {
			if (dailyBound.getLowerBound().compareTo(stockDate) <= 0
					&& dailyBound.getUpperBound().compareTo(stockDate) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 保存或更新股票
	 * 
	 * @param queryStock
	 * @param insertStock
	 * @param file
	 * @param line
	 * @throws SQLException
	 */
	private void saveOrUpdateStock(PreparedStatement queryStock, PreparedStatement updateStock,
			PreparedStatement insertStock, File file, Stock stock, String line) throws SQLException {
		String[] stockInfo = line.split("\\s");
		String code2 = stockInfo[0];
		if (!stock.getCode().equals(code2)) {
			throw new BaicException("文件内部的股票代码与文件名中的股票代码不一致。内部股票代码：" + code2 + " 文件名：" + file.getName());
		}
		if (!"日线".equals(stockInfo[2])) {
			throw new BaicException("不是日线数据。内部股票代码：" + code2 + " 文件名：" + file.getName());
		}
		if (!"前复权".equals(stockInfo[3])) {
			throw new BaicException("不是前复权数据。内部股票代码：" + code2 + " 文件名：" + file.getName());
		}
		stock.setName(stockInfo[1]);
		queryStock.setString(1, stock.getId());
		ResultSet rs = queryStock.executeQuery();
		if (!rs.next()) {
			insertStock.setString(1, stock.getId());
			insertStock.setString(2, stock.getName());
			insertStock.setString(3, stock.getCode());
			insertStock.setString(4, stock.getExchange());
			insertStock.executeUpdate();
		} else {
			updateStock.setString(1, stock.getName());
			updateStock.setString(2, stock.getId());
			updateStock.executeUpdate();
		}
	}
}
