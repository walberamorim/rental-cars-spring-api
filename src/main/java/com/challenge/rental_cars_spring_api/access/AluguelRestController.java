package com.challenge.rental_cars_spring_api.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ResultadoImportacaoAluguelDTO;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ResultadoListagemAluguelDTO;
import com.challenge.rental_cars_spring_api.core.services.AluguelService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.InputMismatchException;
import java.util.Objects;

@RestController
@RequestMapping("/alugueis")
public class AluguelRestController {

    @Autowired
    private AluguelService aluguelService;

    @PostMapping(value = "/importar-arquivo", consumes = "multipart/form-data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arquivo importado com sucesso.", content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResultadoImportacaoAluguelDTO.class))}),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor ao importar arquivo.", content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<ResultadoImportacaoAluguelDTO> importarArquivo(@RequestParam("file") MultipartFile file) {

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".rtn")) {
            throw new InputMismatchException("Tipo de arquivo inválido. O arquivo de ser do tido .rtn.");
        }

        ResultadoImportacaoAluguelDTO resultado = aluguelService.processarArquivo(file);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resultado);
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a lista com os alugueis encontrados.", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResultadoListagemAluguelDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor ao consultar lista de alugueis.", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<ResultadoListagemAluguelDTO> listarAlugueis() {
        return new ResponseEntity<>(aluguelService.consultarAluguel(), HttpStatus.OK);
    }

}