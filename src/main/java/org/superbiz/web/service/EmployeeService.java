package org.superbiz.web.service;

import org.superbiz.model.jooq.tables.Employee;
import ratpack.exec.Promise;

import java.util.List;

public interface EmployeeService {
  Promise<List<Employee>> getEmployees();
//  Operation addMeeting(Employee meeting);
//  Operation rateMeeting(String id, String rating);
}
