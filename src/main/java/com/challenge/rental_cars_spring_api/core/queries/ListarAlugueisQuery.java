package com.challenge.rental_cars_spring_api.core.queries;

import java.util.List;

import org.springframework.stereotype.Service;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarAlugueisQuery {
    
    private final AluguelRepository aluguelRepository;

    public List<ListarAlugueisQueryResultItem> execute() {
        return aluguelRepository.findAll().stream().map(ListarAlugueisQueryResultItem::from).toList();
    }
}
