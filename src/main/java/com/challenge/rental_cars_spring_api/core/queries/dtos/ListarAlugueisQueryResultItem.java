package com.challenge.rental_cars_spring_api.core.queries.dtos;

import java.text.SimpleDateFormat;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;

public record ListarAlugueisQueryResultItem (Long id, String dataAluguel, String modelo, Integer km, String cliente, String telefone, String dataDevolucao, String pago, String valor){
    private static String formatarTelefone(String telefone) {
        return "+55 (" + telefone.substring(0, 2) + ") " + telefone.substring(2, 7) + " - " + telefone.substring(7);
    }
    public static ListarAlugueisQueryResultItem from(Aluguel aluguel) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return new ListarAlugueisQueryResultItem(aluguel.getId(), 
        sdf.format(aluguel.getDataAluguel()), 
        aluguel.getCarro().getModelo(), 
        aluguel.getCarro().getKm(),
        aluguel.getCliente().getNome(), 
        formatarTelefone(aluguel.getCliente().getTelefone()),
        sdf.format(aluguel.getDataDevolucao()),
        aluguel.getPago() ? "SIM" : "NAO",
        aluguel.getValor().toString());
    }
}

