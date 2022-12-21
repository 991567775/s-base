package cn.ezeyc.edpcommon.pojo;




import java.io.Serializable;
import java.util.List;

/**
 * Page：
 *
 * @author: Administrator
 * @date: 2020年11月23日, 0023 13:23:07
 */
public class Page<T>  implements Serializable {
    private static final long serialVersionUID=1L;
    /**
     * 如果指定的分页大小小于等于0，则义默认分页为20条数据
     */
    public static final int PAGE_SIZES = 15;

    /**
     * 如果指定的分页页码小于等于0，则默认为第一页。
     */
    public static final int CURRENT_PAGE = 1;
    /**
     * 当前页
     */
    private int pageNo=CURRENT_PAGE;
    /**
     * 每页数
     */
    private int pageSize=PAGE_SIZES;
    /**
     * 总记录数
     */
    private long total=-1;
    /**
     * 总页数
     */
    private long totalPage=-1;
    /**
     * 起始页
     */
    private int startIndex;
    /**
     * 结束页
     */
    private int endIndex;
    /**
     * 分页结果
     */
    private List<T> results;

    private void calculateTotalPage() {
        if ( this.total < 0 ) {
            this.total = 0;
        }
        if ( this.total % this.pageSize == 0 ) {
            this.totalPage = this.total / this.pageSize;
        } else {
            this.totalPage = this.total / this.pageSize + 1;
        }
    }

    /**
     * 该方法不推荐使用，使用{@code Page(int PageNo, int pageSize)}进行替换
     */
    public Page() {
        super();
    }

    private void calculatorPageNo() {
        this.startIndex = pageNo > 0 ? ((pageNo - 1) * pageSize) : 0;
        this.endIndex = pageNo * pageSize;
    }

    /**
     * 构造一个Page对象
     * @param pageNo 当前页码
     * @param pageSize 页面大小
     */
    public Page(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        calculatorPageNo();
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        if ( pageNo <= 0 ) {
            this.pageNo = CURRENT_PAGE;
        }
        this.pageNo = pageNo;
        calculatorPageNo();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if ( pageSize <= 0 ) {
            this.pageSize = PAGE_SIZES;
        }
        this.pageSize = pageSize;
        calculatorPageNo();
    }

    public long getTotal() {
        return total;
    }

    public void setTotalCount(long totalCount) {
        this.total = totalCount;

        // 去计算总页数
        calculateTotalPage();
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
    /**
     * 分页结果
     */
    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
    /**
     * 分页条件
     */
    public  String getPageSql(){
        return  " limit "+(pageNo-1)*pageSize+","+pageSize;
    }


}
