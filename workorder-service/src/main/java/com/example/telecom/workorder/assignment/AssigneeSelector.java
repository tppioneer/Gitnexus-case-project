package com.example.telecom.workorder.assignment;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.WorkOrder;

import java.util.List;

public interface AssigneeSelector {
    OperatorUser select(WorkOrder workOrder, List<OperatorUser> candidates);
}
