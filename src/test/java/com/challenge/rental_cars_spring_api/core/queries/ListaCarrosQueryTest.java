package com.challenge.rental_cars_spring_api.core.queries;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarCarrosQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;

@Tag("Unit")
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class ListaCarrosQueryTest {
    
    @InjectMocks
    private ListarCarrosQuery listarCarrosQuery;

    @Mock
    private CarroRepository carroRepository;

    @Test
    public void listaCarrosTest() {
        Carro carroMock = new Carro();
        carroMock.setId(123l);
        carroMock.setModelo("GOL");

        List<Carro> carros = List.of(carroMock);

        when(carroRepository.findAll()).thenReturn(carros);

        List<ListarCarrosQueryResultItem> result = listarCarrosQuery.execute();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(result.get(0).id(), 123l);
        Mockito.verify(carroRepository, Mockito.times(1)).findAll();
    }
}
