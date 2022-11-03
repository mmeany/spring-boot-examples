package net.mmeany.play.quote.controller.service;

import net.datafaker.Faker;
import net.mmeany.play.quote.controller.model.QuoteDto;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    Faker faker = new Faker();

    public QuoteDto nextQuote() {
        return new QuoteDto(faker.babylon5().quote());
    }
}
