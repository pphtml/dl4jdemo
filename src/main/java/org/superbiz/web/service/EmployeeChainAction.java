package org.superbiz.web.service;

import org.superbiz.model.jooq.tables.Employee;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.jackson.Jackson;

public class EmployeeChainAction implements Action<Chain> {
    @Override
    public void execute(Chain chain) throws Exception {
//        chain
//            .path(ctx -> {
//                EmployeeService service = ctx.get(EmployeeService.class);
//                ctx
//                    .byMethod(method -> method
//                        .get(() -> service
//                            .getEmployees()
//                            .map(Jackson::json)
//                            .then(ctx::render)
//                        )
////                        .post(() -> ctx
////                                .parse(Jackson.fromJson(Meeting.class))
////                                .nextOp(service::addMeeting)
////                                .map(m -> "Added meeting for " + m.getOrganizer())
////                                .then(ctx::render)
////                        )
//                    );
//            }).get("publ", ctx -> {
//                EmployeeServicePublisher service = ctx.get(EmployeeServicePublisher.class);
//                TransformablePublisher<Employee> employees = service.getEmployees();
//                employees
//                        .filter(employee -> employee.getName().startsWith("P"))
//                        .bindExec()
//                        .toList()
//                        .map(Jackson::json)
//                        .then(ctx::render);
//            }).get("rx", ctx -> {
//                EmployeeServiceRx service = ctx.get(EmployeeServiceRx.class);
//                final Observable<Employee> employees = service.getEmployees();
//                RxRatpack.promise(employees).then(users -> ctx.render(Jackson.json(users)));
//        });
////            .get(":id:\\d+/rate/:rating:[1-5]", ctx -> {
////                EmployeeService service = ctx.get(EmployeeService.class);
////                PathTokens pathTokens = ctx.getPathTokens();
////                service
////                        .rateMeeting(pathTokens.get("id"), pathTokens.get("rating"))
////                        .then(() -> ctx.redirect("/meeting"));
////            });
    }
}
