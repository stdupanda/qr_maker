package cn.xz.qrmaker.entity;

/**
 * 对应 sqlite 中的 log 表实体
 * @author gsx
 *
 */
public class UrlLog {
	
	private Integer id;
	private String url;
	
	public UrlLog() {
		//
	}
	public UrlLog(Integer id, String url) {
		this.id = id;
		this.url = url;
	}
	
	/**
	 * 为了在 JComboBox 中显示而调整 2017-4-9 21:58:37
	 */
	@Override
	public String toString() {
		//return "UrlLog [id=" + id + ", url=" + url + "]";
		return url;
	}

	// get set
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
