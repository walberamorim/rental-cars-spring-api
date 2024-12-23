package com.challenge.rental_cars_spring_api.core.queries.dtos;

import java.util.List;

public record ResultadoImportacaoAluguelDTO(
        String nomeArquivo,
        Integer totalRegistros,
        Integer totalRegistrosProcessados,
        Integer totalRegistrosComErro,
        List<String> erros) {

}