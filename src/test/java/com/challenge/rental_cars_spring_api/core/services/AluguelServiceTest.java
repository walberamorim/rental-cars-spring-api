package com.challenge.rental_cars_spring_api.core.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.domain.Cliente;
import com.challenge.rental_cars_spring_api.core.queries.ListarAlugueisQuery;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ResultadoImportacaoAluguelDTO;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ResultadoListagemAluguelDTO;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.ClienteRepository;

@Tag("Unit")
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class AluguelServiceTest {

    @InjectMocks
    private AluguelService aluguelService;

    @Mock
    private AluguelRepository aluguelRepository;
    @Mock
    private CarroRepository carroRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ListarAlugueisQuery listarAlugueisQuery;

    @Test
    public void processarArquivoTest() {
        Carro carroMock = new Carro();
        carroMock.setId(123l);
        carroMock.setModelo("GOL");
        carroMock.setVlrDiaria(new BigDecimal(200));

        Cliente clienteMock = new Cliente();
        clienteMock.setNome("Walber");
        clienteMock.setTelefone("+55(55)55555-5555");

        when(carroRepository.findById(03l)).thenReturn(Optional.of(carroMock));
        when(clienteRepository.findById(15l)).thenReturn(Optional.of(clienteMock));
        when(aluguelRepository.save(any())).thenReturn(null);

        MultipartFile arquivo = new MockMultipartFile("alugueis.rtn", "alugueis.rtn", "application/octet-stream",
                "03152024010220240105\n".getBytes(StandardCharsets.UTF_8));

        ResultadoImportacaoAluguelDTO resultado = aluguelService.processarArquivo(arquivo);
        Assertions.assertEquals("alugueis.rtn", resultado.nomeArquivo());
        Assertions.assertEquals(1, resultado.totalRegistros());
        Assertions.assertEquals(0, resultado.totalRegistrosComErro());
        Assertions.assertEquals(1, resultado.totalRegistrosProcessados());
        Mockito.verify(aluguelRepository, Mockito.times(1)).save(Mockito.any(Aluguel.class));
    }

    @Test
    public void consultarAlugueisTest() {

        ListarAlugueisQueryResultItem item1 = new ListarAlugueisQueryResultItem(1l, "12/12/2022", "GOL", 55, "Walber", "+55(55)55555-5555", "14/12/2022", "NAO", "1000");
        ListarAlugueisQueryResultItem item2 = new ListarAlugueisQueryResultItem(1l, "12/12/2022", "GOL", 55, "Walber", "+55(55)55555-5555", "14/12/2022", "NAO", "1000");
        ListarAlugueisQueryResultItem item3 = new ListarAlugueisQueryResultItem(1l, "12/12/2022", "GOL", 55, "Walber", "+55(55)55555-5555", "14/12/2022", "SIM", "1000");
        List<ListarAlugueisQueryResultItem> lista = new ArrayList<>();
        lista.add(item1);
        lista.add(item2);
        lista.add(item3);
        when(listarAlugueisQuery.execute()).thenReturn(lista);

        ResultadoListagemAluguelDTO result = aluguelService.consultarAluguel();

        Assertions.assertEquals(3, result.lista().size());
        Assertions.assertEquals(result.valorTotalNaoPago(), new BigDecimal(2000));
    }

}