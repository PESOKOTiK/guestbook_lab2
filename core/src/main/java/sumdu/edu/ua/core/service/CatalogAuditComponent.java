package sumdu.edu.ua.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CatalogAuditComponent {
    private static final Logger log = LoggerFactory.getLogger(CatalogAuditComponent.class);

    private final String applicationTitle;
    private final int maxDeleteHours;

    public CatalogAuditComponent(
            @Value("${spring.application.name:book-catalog-spring-mvc}") String applicationTitle,
            @Value("${catalog.comment.delete-window-hours:24}") int maxDeleteHours) {
        this.applicationTitle = applicationTitle;
        this.maxDeleteHours = maxDeleteHours;
    }

    public void recordAccess(String operation, String details) {
        log.info("[AuditComponent | app={}] Operation='{}', details='{}', deleteWindowHours={}",
                applicationTitle, operation, details, maxDeleteHours);
    }

    public int getMaxDeleteHours() {
        return maxDeleteHours;
    }
}
