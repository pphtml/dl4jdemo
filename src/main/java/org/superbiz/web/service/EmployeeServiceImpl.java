package org.superbiz.web.service;

import org.superbiz.model.jooq.tables.Employee;
import ratpack.exec.Promise;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;

  public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }


//  @Override
//  public Operation addMeeting(Meeting meeting) {
//    return employeeRepository.addMeeting(meeting);
//  }
//
//  @Override
//  public Operation rateMeeting(String id, String rating) {
//    return ratingRepository.rateMeeting(id, rating);
//  }

  @Override
  public Promise<List<Employee>> getEmployees() {
    return employeeRepository.getEmployees()
        .flatMap(employees -> Promise.value(employees));
  }
}
