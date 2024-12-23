package com.challenge.rental_cars_spring_api.core.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CarroRepository carroRepository;
    private final ClienteRepository clienteRepository;


    private final ListarAlugueisQuery listarAlugueisQuery;

    public ResultadoImportacaoAluguelDTO processarArquivo(MultipartFile file) {
        try {

            log.info("Processando arquivo: {}", file.getOriginalFilename());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    file.getInputStream(),
                    StandardCharsets.UTF_8));
                    
            String linha;
            Integer totalRegistros = 0;
            Integer totalRegistrosProcessados = 0;
            Integer totalRegistrosComErro = 0;
            List<String> erros = new ArrayList<>();

            while ((linha = reader.readLine()) != null) {
                totalRegistros++;

                if (linha.length() != 20) {
                    log.error("Linha inválida: {}", linha);
                    totalRegistrosComErro++;
                    erros.add("Linha inválida: " + linha);
                    continue;
                }

                Long carroId = null;
                Long clienteId = null;
                Date dataAluguel = null;
                Date dataDevolucao = null;

                try {
                    carroId = Long.parseLong(linha.substring(0, 2));
                } catch (NumberFormatException e) {
                    log.error("Carro com id inválido: {}", carroId);
                    totalRegistrosComErro++;
                    erros.add("Carro com id inválido: " + carroId);
                    continue;
                }
                try {
                    clienteId = Long.parseLong(linha.substring(2, 4));
                } catch (NumberFormatException e) {
                    log.error("Cliente com id inválido: {}", carroId);
                    totalRegistrosComErro++;
                    erros.add("Cliente com id inválido: " + carroId);
                    continue;
                }
                try {
                    dataAluguel = converterData(linha.substring(4, 12));
                } catch (ParseException e) {
                    log.error("Data de aluguel inválida: {}", carroId);
                    totalRegistrosComErro++;
                    erros.add("Data de aluguel inválida: " + carroId);
                    continue;
                }
                try {
                    dataDevolucao = converterData(linha.substring(12, 20));
                } catch (ParseException e) {
                    log.error("Data de devolução inválida: {}", carroId);
                    totalRegistrosComErro++;
                    erros.add("Data de devolução inválida: " + carroId);
                    continue;
                }

                Optional<Carro> carroOptional = carroRepository.findById(carroId);
                if (carroOptional.isEmpty()) {
                    log.error("Carro não encontrado: {}", carroId);
                    totalRegistrosComErro++;
                    erros.add("Carro não encontrado: " + carroId);
                    continue;
                }

                Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);
                if (clienteOptional.isEmpty()) {
                    log.error("Cliente não encontrado: {}", clienteId);
                    totalRegistrosComErro++;
                    erros.add("Cliente não encontrado: " + clienteId);
                    continue;
                }

                BigDecimal valor = calcularValor(
                        carroOptional.get().getVlrDiaria(),
                        dataAluguel,
                        dataDevolucao);

                Aluguel aluguel = new Aluguel(null, carroOptional.get(), clienteOptional.get(), dataAluguel,dataDevolucao,valor,false);
                totalRegistrosProcessados++;
                aluguelRepository.save(aluguel);
            }
            return new ResultadoImportacaoAluguelDTO(file.getOriginalFilename(), totalRegistros, totalRegistrosProcessados, totalRegistrosComErro, erros);

        } catch (Exception ex) {
            log.error("Erro ao processar arquivo", ex);
            return null;
        }
    }

    private Date converterData(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.parse(dateStr);
    }

    private BigDecimal calcularValor(BigDecimal valorDiaria, Date dataAluguel, Date dataDevolucao) {
        long diferencaEmMilisegundos = Math.abs(dataDevolucao.getTime() - dataAluguel.getTime());
        long qtdDiasAluguel = diferencaEmMilisegundos / (1000 * 60 * 60 * 24);
        return valorDiaria.multiply(BigDecimal.valueOf(qtdDiasAluguel));
    }

    public ResultadoListagemAluguelDTO consultarAluguel(){
        List<ListarAlugueisQueryResultItem> lista = listarAlugueisQuery.execute();
        BigDecimal valorTotalNaoPago = new BigDecimal(0);
        for (ListarAlugueisQueryResultItem item : lista) {
            if(item.pago().equals("NAO")){
                valorTotalNaoPago = valorTotalNaoPago.add(new BigDecimal(item.valor()));
            }
        }
        return new ResultadoListagemAluguelDTO(valorTotalNaoPago, lista);
    }
}
