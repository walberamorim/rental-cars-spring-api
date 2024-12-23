package com.challenge.rental_cars_spring_api.core.queries.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ResultadoListagemAluguelDTO(
        BigDecimal valorTotalNaoPago,
        List<ListarAlugueisQueryResultItem> lista
        ) {

}