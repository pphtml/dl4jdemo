package org.superbiz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.superbiz.dao.SecurityDAO;
import org.superbiz.dto.SecurityDTO;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class SecuritiesListHandler implements Handler {
    @Inject
    Logger logger;

    @Inject
    SecurityDAO securityDAO;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void handle(Context ctx) throws Exception {
        List<SecurityDTO> securities = securityDAO.findAll();
        String result = OBJECT_MAPPER.writeValueAsString(securities);
        ctx.getResponse().send(result);
    }

//    public static void main(String[] args) throws JsonProcessingException {
//        List<SecurityDTO> securities = Arrays.asList(SecurityDTO.of("AMZN", "am"), SecurityDTO.of("FB", "f"));
//        String result = OBJECT_MAPPER.writeValueAsString(securities);
//        System.out.println(result);
//    }
}
