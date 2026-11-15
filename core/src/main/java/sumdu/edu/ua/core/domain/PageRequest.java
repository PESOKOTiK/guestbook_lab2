package sumdu.edu.ua.core.domain;

public class PageRequest {
    private final int page;
    private final int size;
    private final String sort;

    public PageRequest(int page, int size, String sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        this.page = page;
        this.size = size;
        this.sort = (sort == null || sort.isBlank()) ? "id" : sort.trim();
    }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public String getSort() { return sort; }
    public int getOffset() { return page * size; }
}
