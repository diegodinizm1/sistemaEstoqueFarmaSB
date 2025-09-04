package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.relatorios.RelatorioSaidaDiariaDTO;
import com.diego.sistemafarmaciasb.repository.MovimentacaoRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final MovimentacaoRepository movimentacaoRepository;

    public RelatorioService(MovimentacaoRepository movimentacaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
    }


    @Cacheable(value = "relatoriosSaidasDiarias", key = "#data")
    public byte[] gerarRelatorioSaidasDiarias(LocalDate data) {
        LocalDateTime inicioDoDia = data.atStartOfDay();
        LocalDateTime fimDoDia = data.atTime(23, 59, 59);

        // 1. Busca os dados brutos, agrupados por item e setor.
        List<RelatorioSaidaDiariaDTO> dadosDoBanco = movimentacaoRepository.findSaidasDiariasAgrupadas(inicioDoDia, fimDoDia);

        // 2. Identifica os cabeçalhos das colunas (todos os setores únicos do dia).
        List<String> nomesDosSetores = dadosDoBanco.stream()
                .map(RelatorioSaidaDiariaDTO::nomeSetor)
                .distinct()
                .sorted()
                .toList();

        // 3. Agrupa os dados por item para criar as linhas da tabela dinâmica.
        // A estrutura será: Map<Nome do Item, List<Saídas para aquele item>>
        Map<String, List<RelatorioSaidaDiariaDTO>> dadosAgrupadosPorItem = dadosDoBanco.stream()
                .collect(Collectors.groupingBy(RelatorioSaidaDiariaDTO::nomeItem, LinkedHashMap::new, Collectors.toList()));

        // --- Início da Geração do PDF ---
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        document.add(new Paragraph("Relatório de Saídas Diárias por Item e Setor").setBold().setFontSize(18));
        document.add(new Paragraph("Data: " + data.format(formatter)).setFontSize(12));
        document.add(new Paragraph("\n"));

        if (dadosDoBanco.isEmpty()) {
            document.add(new Paragraph("Nenhuma movimentação de saída registrada para esta data."));
            document.close();
            return baos.toByteArray();
        }

        // 4. Cria a tabela com colunas dinâmicas
        // Número de colunas = 1 (para o Item) + número de setores + 1 (para o Total)
        float[] columnWidths = new float[nomesDosSetores.size() + 2];
        columnWidths[0] = 3; // Coluna do item é mais larga
        for (int i = 1; i < columnWidths.length - 1; i++) {
            columnWidths[i] = 1; // Colunas dos setores
        }
        columnWidths[columnWidths.length - 1] = 1; // Coluna total

        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // 5. Adiciona os cabeçalhos dinâmicos
        table.addHeaderCell(new Cell().add(new Paragraph("Item")).setBold());
        for (String nomeSetor : nomesDosSetores) {
            table.addHeaderCell(new Cell().add(new Paragraph(nomeSetor)).setBold().setTextAlignment(TextAlignment.CENTER));
        }
        table.addHeaderCell(new Cell().add(new Paragraph("Total")).setBold().setTextAlignment(TextAlignment.CENTER));

        // 6. Preenche a tabela com os dados da tabela dinâmica
        for (Map.Entry<String, List<RelatorioSaidaDiariaDTO>> entry : dadosAgrupadosPorItem.entrySet()) {
            String nomeItem = entry.getKey();
            List<RelatorioSaidaDiariaDTO> saidasDoItem = entry.getValue();
            long totalItem = 0;

            // Adiciona a célula do nome do item
            table.addCell(nomeItem);

            // Para cada setor (coluna), encontra a quantidade correspondente
            for (String nomeSetor : nomesDosSetores) {
                long quantidadeParaSetor = saidasDoItem.stream()
                        .filter(s -> s.nomeSetor().equals(nomeSetor))
                        .mapToLong(RelatorioSaidaDiariaDTO::quantidadeTotal)
                        .findFirst()
                        .orElse(0); // Se não houve saída para este setor, a quantidade é 0

                table.addCell(new Cell().add(new Paragraph(String.valueOf(quantidadeParaSetor))).setTextAlignment(TextAlignment.CENTER));
                totalItem += quantidadeParaSetor;
            }

            // Adiciona a célula do total
            table.addCell(new Cell().add(new Paragraph(String.valueOf(totalItem))).setBold().setTextAlignment(TextAlignment.CENTER));
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }
}