package com.salesforce.salesforce_app.repository;

import com.salesforce.salesforce_app.model.User;
import com.salesforce.salesforce_app.model.ValidationRuleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ValidationRuleLogRepository
        extends JpaRepository<
        ValidationRuleLog, Long> {

    List<ValidationRuleLog>
    findByUserOrderByPerformedAtDesc(
            User user);

    List<ValidationRuleLog>
    findByRuleId(String ruleId);
}
