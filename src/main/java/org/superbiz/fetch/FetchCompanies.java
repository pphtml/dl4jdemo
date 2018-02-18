package org.superbiz.fetch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.superbiz.db.ConnAndDSL;
import org.superbiz.util.DateConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.superbiz.model.jooq.Tables.SECURITY;

public class FetchCompanies {

    public static final String LIST_OF_SP_500_COMPANIES = "https://en.wikipedia.org/wiki/List_of_S%26P_500_companies";

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
//        AsyncHttpClient asyncHttpClient = asyncHttpClient();
//
//        Request request = get(LIST_OF_SP_500_COMPANIES).build();
//        Future<Response> whenResponse = asyncHttpClient.executeRequest(request);
//        System.out.println(whenResponse);
//        Response response = whenResponse.get();
//        System.out.println(response);

        try (ConnAndDSL dsl = ConnAndDSL.create()) {
            String blogUrl = LIST_OF_SP_500_COMPANIES;
            Document doc = Jsoup.connect(blogUrl).get();
            Elements tables = doc.select("table:first-of-type");
            Element table = tables.get(0);
            Elements rows = table.select("tr");
            rows.stream().skip(1).forEach(row -> {
                Elements columns = row.select("td");
                Element titleColumn = columns.get(0);
                String symbol = titleColumn.select("a").first().text();
                Element securityNameColumn = columns.get(1);
                String securityName = securityNameColumn.select("a").first().text();
                Element addedDateColumn = columns.get(6);
                String addedDate = addedDateColumn.text();
                Optional<LocalDate> optionalDate = addedDate.length() == 0 ? Optional.empty() : Optional.of(LocalDate.parse(addedDate));

                dsl.getDsl().insertInto(SECURITY,
                        SECURITY.SYMBOL, SECURITY.NAME, SECURITY.ENLISTED_DATE)
                        .values(symbol, securityName, DateConverter.from(optionalDate))
                        .execute();
            });
        }

    }
}
