package org.superbiz.web.service;

import org.jooq.DSLContext;
import org.superbiz.model.jooq.tables.Employee;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import javax.inject.Inject;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {
  private final DSLContext context;

  @Inject
  public EmployeeRepositoryImpl(DSLContext context) {
    this.context = context;
  }

//  @Override
//  public Promise<List<Meeting>> getMeetings() {
//    return Blocking.get(() ->
//      context
//        .select()
//        .from(MEETING)
//        .fetchInto(Meeting.class)
//    );
//  }
//
//  @Override
//  public Operation addMeeting(Meeting meeting) {
//    return Blocking.op(() ->
//      context.newRecord(MEETING, meeting).store()
//    );
//  }

  @Override
  public Promise<List<Employee>> getEmployees() {
    return Blocking.get(() ->
        context
            .select()
            .from(org.superbiz.model.jooq.tables.Employee.EMPLOYEE)
            .fetchInto(Employee.class)
    );
  }
}
