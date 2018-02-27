package org.superbiz.web.service;

import org.superbiz.model.jooq.tables.Employee;
import ratpack.exec.Promise;

import java.util.List;

public interface EmployeeRepository {
  Promise<List<Employee>> getEmployees();
  //Operation addMeeting(Meeting meeting);
}
