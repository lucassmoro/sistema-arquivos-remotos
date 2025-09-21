/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ine5418;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @author Lucas
 */


public class ServidorSistemaArquivos extends SistemaArquivosGrpc.SistemaArquivosImplBase {
    
    private final ConcurrentHashMap<Integer, String> tabelaArquivos = new ConcurrentHashMap<>(); //mapper thread-safe que mapeia um descritor do cliente para um arquivo do servidor
    private final AtomicInteger contadorDescritor = new AtomicInteger(1); // variavel atomica que garante o incremento com concorrencia, gera descritores unicos pro cliente
    
    @Override
    public void abre(SistemaArquivosProto.AbreRequest req, //AbreRequest é uma classe dentro da classe SistemaArquivosProto gerada no protobuf
                     StreamObserver<SistemaArquivosProto.AbreReply> responseObserver){
        
        String nome_arquivo = req.getNomeArquivo(); // retorna o nome do arquivo do servidor
        int descritor = contadorDescritor.incrementAndGet(); // gera um descritor pro cliente que requisitou a abertura do arquivo
        
        tabelaArquivos.put(descritor, nome_arquivo); // mapeia o descritor pro arquivo
        
        //falta implementar a resposta
        
    }
}
