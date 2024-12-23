package com.challenge.rental_cars_spring_api.core.queries;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.domain.Cliente;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;

@Tag("Unit")
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class ListaAlugueisQueryTest {

    @InjectMocks
    private ListarAlugueisQuery listarAlugueisQuery;

    @Mock
    private AluguelRepository aluguelRepository;

    @Test
    public void listaAlugueisTest() {
        Aluguel aluguelMock = new Aluguel();
        Carro carroMock = new Carro();
        carroMock.setKm(888);
        carroMock.setModelo("GOL");
        Cliente clienteMock = new Cliente();
        clienteMock.setNome("Walber");
        clienteMock.setTelefone("+55(55)55555-5555");
        aluguelMock.setId(123l);
        aluguelMock.setPago(false);
        aluguelMock.setDataAluguel(new Date());
        aluguelMock.setDataDevolucao(new Date());
        aluguelMock.setValor(new BigDecimal(1000));
        aluguelMock.setCliente(clienteMock);
        aluguelMock.setCarro(carroMock);

        List<Aluguel> alugueis = List.of(aluguelMock);

        when(aluguelRepository.findAll()).thenReturn(alugueis);

        List<ListarAlugueisQueryResultItem> result = listarAlugueisQuery.execute();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(result.get(0).id(), 123l);
        Assertions.assertEquals(result.get(0).pago(), "NAO");
        Mockito.verify(aluguelRepository, Mockito.times(1)).findAll();
    }
}
